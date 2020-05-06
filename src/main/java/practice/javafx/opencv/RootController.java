package practice.javafx.opencv;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/** ルート・コントローラ */
public final class RootController implements Initializable {
  /** キャプチャする間隔 (ミリ秒) : 24fps = 41ms ・ 30fps = 33ms ・ 60fps = 16ms */
  private static final int GRABBER_PERIOD = 33;
  /** カメラのフレームレート : キャプチャする間隔と合わせておく */
  private static final int CAMERA_FPS = 30;
  /** カメラデバイス ID */
  private static final int CAMERA_DEVICE_ID = 0;
  
  /** カメラプレビュー */
  @FXML
  private ImageView imageView;
  /** 起動ボタン */
  @FXML
  private Button buttonStart;
  /** 停止ボタン */
  @FXML
  private Button buttonStop;
  
  /** ビデオキャプチャ */
  private final VideoCapture videoCapture = new VideoCapture();
  /** ビデオキャプチャから取得したフレーム */
  private final Mat mat = new Mat();
  
  /** キャプチャ映像からプレビュー画像を切り出す間隔を決めるタイマー */
  private ScheduledExecutorService timer;
  
  /**
   * 初期化処理 : ボタン状況を変更する
   * 
   * @param url 未使用
   * @param resourceBundle 未使用
   */
  @Override
  public void initialize(final URL url, final ResourceBundle resourceBundle) {
    this.buttonStart.setDisable(false);
    this.buttonStop.setDisable(true);
  }
  
  /** カメラを起動する */
  @FXML
  public void onStartCamera() {
    // ボタンの二度押し防止
    this.buttonStart.setDisable(true);
    this.buttonStop.setDisable(true);
    
    try {
      // カメラを起動する
      this.videoCapture.open(CAMERA_DEVICE_ID);
      
      // カメラが起動できなかった場合は中止する
      if(!this.videoCapture.isOpened()) {
        throw new Exception("Failed To Open The Camera Connection");
      }
      
      // FPS を設定する
      this.videoCapture.set(Videoio.CAP_PROP_FPS, CAMERA_FPS);
      
      // キャプチャ映像からプレビュー画像を切り出し、カメラプレビューに描画する
      this.timer = Executors.newSingleThreadScheduledExecutor();
      this.timer.scheduleAtFixedRate(new Runnable() {
        /** タイマー起動後1回だけ実行する */
        private boolean isFirstTimeExecuted = true;
        
        /** プレビューを表示する */
        @Override
        public void run() {
          RootController.this.previewLoop();
          
          // 起動処理ができたらボタン状況を変更する
          if(isFirstTimeExecuted) {
            RootController.this.buttonStart.setDisable(true);
            RootController.this.buttonStop.setDisable(false);
            this.isFirstTimeExecuted = false;
          }
        }
      }, 0, GRABBER_PERIOD, TimeUnit.MILLISECONDS);
    }
    catch(final Exception exception) {
      exception.printStackTrace();
      // アラートダイアログを表示する
      new Alert(AlertType.WARNING, "Failed To Start Camera").show();
      // 起動に失敗したら停止処理を呼んでおく
      this.onStopCamera();
    }
  }
  
  /** カメラを停止する */
  @FXML
  public void onStopCamera() {
    // ボタンの二度押し防止
    this.buttonStart.setDisable(true);
    this.buttonStop.setDisable(true);
    
    // タイマーを止める
    if(this.timer != null && !this.timer.isShutdown()) {
      try {
        this.timer.shutdown();
        this.timer.awaitTermination(GRABBER_PERIOD, TimeUnit.MILLISECONDS);
      }
      catch(final InterruptedException interruptedException) {
        interruptedException.printStackTrace();
      }
    }
    
    // カメラを解放する
    if(this.videoCapture.isOpened()) {
      this.videoCapture.release();
    }
    
    // ボタン状況を変更する
    this.buttonStart.setDisable(false);
    this.buttonStop.setDisable(true);
  }
  
  /** キャプチャ映像からプレビュー画像を切り出し、カメラプレビューに描画する */
  private void previewLoop() {
    try {
      // キャプチャ映像からプレビュー画像を切り出す
      this.videoCapture.read(this.mat);
      // 切り出したプレビュー画像 (Mat イメージ) をカメラプレビュー用に変換する
      final Image image = this.matToImage();
      // 変換した Image を ImageView に描画する
      this.repaintPreview(image);
    }
    catch(final Exception exception) {
      exception.printStackTrace();
    }
  }
  
  /**
   * Mat イメージから Image に変換する
   * 
   * @return Image
   */
  private Image matToImage() {
    final int width = this.mat.width();
    final int height = this.mat.height();
    final int channels = this.mat.channels();
    final byte[] sourcePixels = new byte[width * height * channels];
    this.mat.get(0, 0, sourcePixels);
    final BufferedImage bufferedImage = new BufferedImage(width, height, (channels > 1) ? BufferedImage.TYPE_3BYTE_BGR : BufferedImage.TYPE_BYTE_GRAY);
    final byte[] targetPixels = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
    System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);
    return SwingFXUtils.toFXImage(bufferedImage, null);
  }
  
  /**
   * JavaFX スレッドでプレビューを描画する
   * 
   * @param image 描画するイメージ
   */
  private void repaintPreview(final Image image) {
    Platform.runLater(() -> {
      this.imageView.imageProperty().set(image);
    });
  }
}
