这是一个针对Android和iOS的Kotlin多平台项目。

* [/composeApp](./composeApp/src) 用于将在Compose多平台应用程序之间共享的代码。
  它包含几个子文件夹：
  - [commonMain](./composeApp/src/commonMain/kotlin) 用于所有目标通用的代码。
  - 其他文件夹用于仅针对文件夹名称中指示的平台编译的Kotlin代码。
    例如，如果你想在Kotlin应用程序的iOS部分使用Apple的CoreCrypto，
    [iosMain](./composeApp/src/iosMain/kotlin) 文件夹将是进行此类调用的合适位置。
    同样，如果你想编辑桌面（JVM）特定部分，[jvmMain](./composeApp/src/jvmMain/kotlin)
    文件夹是适当的位置。

* [/iosApp](./iosApp/iosApp) 包含iOS应用程序。即使你要与Compose多平台共享UI，
  你仍然需要这个iOS应用程序的入口点。这也是你应该为项目添加SwiftUI代码的地方。

### 构建和运行Android应用程序

要构建和运行Android应用程序的开发版本，请使用IDE工具栏中的运行配置中的运行控件，
或直接从终端构建：
- 在macOS/Linux上
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- 在Windows上
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### 构建和运行iOS应用程序

要构建和运行iOS应用程序的开发版本，请使用IDE工具栏中的运行配置中的运行控件，
或在Xcode中打开 [/iosApp](./iosApp) 目录并从那里运行。

---

了解更多关于 [Kotlin多平台](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html) 的信息…
