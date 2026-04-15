# 连连看游戏 - Android App

## 项目说明

这是一个专为 Android 7 (API 24+) 开发的经典连连看游戏。

## 功能特点

- ✅ 8x6 游戏棋盘，48张卡片
- ✅ 24种不同表情包图案
- ✅ 支持直线、一折、两折连线消除
- ✅ 3分钟倒计时挑战
- ✅ 计分系统
- ✅ 提示功能（消耗3分）
- ✅ 洗牌功能（消耗5分）
- ✅ 自动检测无解时重新洗牌
- ✅ 通关/时间到弹窗提示

## 系统要求

- Android 7.0 (API 24) 或更高版本
- 最低 SDK: 24
- 目标 SDK: 28

## 项目结构

```
LianliankanApp/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/lianliankan/
│   │   │   │   ├── MainActivity.java    # 主活动
│   │   │   │   └── GameView.java        # 游戏视图
│   │   │   ├── res/
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── card_bg.xml
│   │   │   │   │   ├── card_selected.xml
│   │   │   │   │   ├── ic_launcher_background.xml
│   │   │   │   │   └── ic_launcher_foreground.xml
│   │   │   │   ├── layout/
│   │   │   │   │   └── activity_main.xml
│   │   │   │   ├── mipmap-anydpi-v26/
│   │   │   │   │   ├── ic_launcher.xml
│   │   │   │   │   └── ic_launcher_round.xml
│   │   │   │   └── values/
│   │   │   │       ├── colors.xml
│   │   │   │       ├── strings.xml
│   │   │   │       └── styles.xml
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                         # 单元测试
│   │   └── androidTest/                  # 仪器测试
│   ├── build.gradle
│   └── proguard-rules.pro
├── .github/workflows/
│   └── android.yml                         # GitHub Actions自动构建
├── build.gradle
├── settings.gradle
└── README.md
```

## 如何构建

### 自动构建（GitHub Actions）

每次推送到 main 分支时，GitHub Actions 会自动构建 APK 并发布到 Releases。

### 使用 Android Studio

1. 打开 Android Studio
2. 选择 "Open an existing Android Studio project"
3. 选择 `LianliankanApp` 文件夹
4. 等待 Gradle 同步完成
5. 点击 Run 按钮或使用 Shift+F10

### 使用命令行

```bash
cd LianliankanApp
./gradlew assembleDebug
```

APK 文件将生成在 `app/build/outputs/apk/debug/app-debug.apk`

## 安装 APK

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## 游戏玩法

1. 点击两张相同的卡片
2. 如果可以用不超过2个转折的直线连接，则消除
3. 消除所有卡片即可通关
4. 在时间耗尽前完成挑战！

## 技术要点

- 自定义 View 实现游戏棋盘
- Pathfinding 算法实现连线检测（支持0-2折）
- Handler 实现倒计时
- AlertDialog 实现游戏结束弹窗
- 自动检测死局并重洗牌

## 文件清单

| 文件路径 | 说明 |
|---------|------|
| `app/src/main/java/com/example/lianliankan/MainActivity.java` | 主活动，处理UI和计时器 |
| `app/src/main/java/com/example/lianliankan/GameView.java` | 自定义游戏视图，核心游戏逻辑 |
| `app/src/main/res/layout/activity_main.xml` | 主界面布局 |
| `app/src/main/res/values/colors.xml` | 颜色定义 |
| `app/src/main/res/values/strings.xml` | 字符串资源 |
| `app/src/main/res/values/styles.xml` | 主题样式 |
| `app/src/main/res/drawable/*.xml` | 图形资源 |
| `app/src/main/AndroidManifest.xml` | 应用清单 |
| `app/build.gradle` | 应用级Gradle配置 |
| `build.gradle` | 项目级Gradle配置 |
| `.github/workflows/android.yml` | GitHub Actions自动构建配置 |
