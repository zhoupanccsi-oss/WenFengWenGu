---
AIGC:
  ContentProducer: '001191110102MAD55U9H0F10002'
  ContentPropagator: '001191110102MAD55U9H0F10002'
  Label: '1'
  ProduceID: 'a2d58491-5296-427f-9201-523fb7b8acb0'
  PropagateID: 'a2d58491-5296-427f-9201-523fb7b8acb0'
  ReservedCode1: 'ff5bcf0b-072b-43ea-82ba-027bf50fe06e'
  ReservedCode2: 'ff5bcf0b-072b-43ea-82ba-027bf50fe06e'
---

# 文峰文谷 (WenFengWenGu)

DeepSeek API Peak/Off-Peak Price Reminder — Android App with dynamic screen border overlay (red=peak, green=off-peak). 北京时间峰谷时段实时提醒。

## 功能

- 以**北京时间（UTC+8）**为唯一锚点，全球任何时区自动换算
- **高峰时段**：北京时间 09:00-12:00、14:00-18:00 → 屏幕四边**红色**动态呼吸边框
- **空闲时段**：其余所有时间 → 屏幕四边**绿色**动态呼吸边框
- 空闲时段价格为高峰时段价格的 50%
- 边框仅 8dp 厚，中间区域完全透明，**不影响手机正常使用**
- 后台/锁屏持续运行（前台服务保活）
- 开机自动启动
- 通知栏可随时停止
- **完全免费**，无广告、无内购、无订阅

## 技术架构

| 组件 | 说明 |
|------|------|
| `BorderView.java` | 自定义 View，Canvas 绘制四边渐变色条 + ValueAnimator 呼吸动画 |
| `OverlayService.java` | 前台服务，通过 WindowManager 添加全屏悬浮窗，每 30 秒检查时段 |
| `MainActivity.java` | 权限请求 + 启停服务 + 实时北京时间/时段显示 |
| `BootReceiver.java` | 开机自启动 |

## 权限说明

| 权限 | 用途 |
|------|------|
| `SYSTEM_ALERT_WINDOW` | 在所有 App 之上绘制边框（核心功能） |
| `FOREGROUND_SERVICE` | 保持服务后台持续运行 |
| `FOREGROUND_SERVICE_SPECIAL_USE` | Android 14+ 前台服务类型声明（屏幕覆盖层） |
| `POST_NOTIFICATIONS` | Android 13+ 前台通知 |
| `RECEIVE_BOOT_COMPLETED` | 开机自启 |

## 安装说明

- **平台**：Android 8.0 (API 26) 及以上，不支持 iOS/苹果设备
- **安全提示**：本 App 未经过 Google Play 或其他应用市场验证，安装时系统会弹出安全警告，这是正常现象，解除限制即可。不放心可下载后先扫描再安装。
- **APK 下载**：见 [Releases](https://github.com/zhoupanccsi-oss/WenFengWenGu/releases) 页面

## 使用方法

1. 安装 APK（安装时如报警，解除限制即可）
2. 打开「WenFengWenGu」
3. 点击「启动边框」
4. 系统会跳转到「显示在其他应用上层」权限页 → **授予权限** → 返回
5. 再次点击「启动边框」→ 屏幕四边出现彩色动态边框
6. 可退出 App，边框在后台持续运行
7. 通知栏可随时「停止」

## 付费说明

- **完全免费**，无广告、无内购、无订阅
- 所有功能无限制使用

## 时段规则

```
高峰（红色）          空闲（绿色）
┌──────────┐         ┌──────────┐
│ 09:00    │         │ 00:00    │
│   ↓      │         │   ↓      │
│ 12:00    │         │ 09:00    │
└──────────┘         ├──────────┤
┌──────────┐         │ 12:00    │
│ 14:00    │         │   ↓      │
│   ↓      │         │ 14:00    │
│ 18:00    │         ├──────────┤
└──────────┘         │ 18:00    │
                      │   ↓      │
                      │ 24:00    │
                      └──────────┘
```

## 构建方法

### 方式一：Android Studio（推荐）

1. 用 Android Studio 打开 `WenFengWenGu/` 文件夹
2. 等待 Gradle 同步完成
3. 点击 Run 按钮，选择手机/模拟器

### 方式二：命令行

```bash
cd WenFengWenGu
./gradlew assembleRelease
# 输出 APK：app/build/outputs/apk/release/app-release.apk
```

签名配置在 `local.properties` 中设置：
```properties
STORE_FILE=../wenfeng.jks
STORE_PASSWORD=your_password
KEY_ALIAS=your_alias
KEY_PASSWORD=your_password
```

## 最低要求

- Android 8.0 (API 26) 及以上
- 不需要任何网络权限

## Google Play Closed Testing — Join Us

WenFengWenGu is being published on Google Play and currently needs **beta testers** to complete the closed-testing period (at least 12 testers for 14 days). This is a common Google requirement for new personal developer accounts.

**It's completely free** — no ads, no in-app purchases, no subscriptions. No data is collected. Your device will only run a colored border around the screen edges.

### How to join

1. Open the test link on your Android device (Android 8.0+):
   - Web: https://play.google.com/apps/testing/com.wenfeng.wengu
   - Or open the Play Store page: https://play.google.com/store/apps/details?id=com.wenfeng.wengu
2. Tap **"Join"** / **"Become a tester"**
3. Tap **"Install"** / **"Download"** — the app will be installed from the Play Store
4. That's it! You do NOT need to submit bugs or write reviews. Just keep the app installed for **14 days** (you don't even need to use it actively — installing and keeping it is enough).

> The app only needs a "Display over other apps" permission to draw the border; it does **not** access the network or collect any data.

Questions or feedback: [open an issue](../../issues) or email zhoupan.ccsi@gmail.com

## Privacy Policy

See [PRIVACY_POLICY.md](PRIVACY_POLICY.md)

## License

MIT License - 见 [LICENSE](LICENSE) 文件。