# Practice JavaFX OpenCV

Gradle Based JavaFX App Using OpenCV.


## Recommended Environments

- OS : Windows 10・MacOS Catalina・Ubuntu 18.04
- [JDK : v1.8.0_65](https://www.oracle.com/java/technologies/javase/javase8-archive-downloads.html) (Includes JavaFX)
    - Windows 10 : `jdk-8u65-windows-x64.exe`
    - MacOS Catalina : `jdk-8u65-macosx-x64.dmg`
    - Ubuntu 18.04 : `jdk-8u65-linux-x64.tar.gz`
- [OpenCV : v3.2.0](https://github.com/opencv/opencv/releases/tag/3.2.0)
    - [Windows 10](https://sourceforge.net/projects/opencvlibrary/files/opencv-win/3.2.0/) : `opencv-3.2.0-vc14.exe`
    - MacOS Catalina・Ubuntu 18.04 : Build From Source
    - Then Get `opencv-320.jar` And Copy To `./lib/` Directory


## On Windows

```sh
# Run : Set `java.library.path` In `build.gradle`
$ ./gradlew run

# Build And Exec JAR
$ ./gradlew build
$ java -Djava.library.path='C:\PATH\TO\opencv\build\java\x64' -jar ./build/libs/practice-javafx-opencv.jar
```


## Links

- [Neo's World](https://neos21.net/)
