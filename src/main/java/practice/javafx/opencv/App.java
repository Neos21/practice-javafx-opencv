package practice.javafx.opencv;

import org.opencv.core.Core;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/** エントリポイント */
public final class App extends Application {
  /** ウィンドウの幅 */
  public static final int WINDOW_WIDTH = 1080;
  /** ウィンドウの高さ */
  public static final int WINDOW_HEIGHT = 720;
  
  /**
   * アプリを起動する
   * 
   * @param args 引数
   */
  public static void main(final String... args) {
    // OpenCV ライブラリを読み込む
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    // GUI を起動する
    launch(args);
  }
  
  /**
   * アプリを起動する : launch() メソッドからコールバックされる
   * 
   * @param stage Scene を表示するウィンドウ
   */
  @Override
  public void start(final Stage stage) {
    try {
      // ウィンドウを準備する
      final FXMLLoader loader = new FXMLLoader(getClass().getResource("Root.fxml"));
      final Pane root = loader.load();
      final Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
      stage.setTitle("Practice JavaFX OpenCV");
      stage.setScene(scene);
      stage.show();
      
      // ウィンドウを閉じる時にコントローラで実行するイベントを設定する
      final RootController rootController = loader.getController();
      stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
        /**
         * ウィンドウを閉じる時の処理
         * 
         * @param windowEvent 未使用
         */
        public void handle(final WindowEvent windowEvent) {
          rootController.onStopCamera();
        }
      });
    }
    catch(final Exception exception) {
      exception.printStackTrace();
      Platform.exit();
    }
  }
}
