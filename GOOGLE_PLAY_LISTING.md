---
AIGC:
  ContentProducer: '001191110102MAD55U9H0F10002'
  ContentPropagator: '001191110102MAD55U9H0F10002'
  Label: '1'
  ProduceID: '76f50d26-4966-443f-b12b-9fc362b60874'
  PropagateID: '76f50d26-4966-443f-b12b-9fc362b60874'
  ReservedCode1: '261ce477-1f80-4f20-9119-670cca5a31bc'
  ReservedCode2: '261ce477-1f80-4f20-9119-670cca5a31bc'
---

# Google Play 上架素材清单

## 1. 基本信息

| 项目 | 内容 |
|------|------|
| 应用名称 | WenFengWenGu |
| 开发者名称 | zhoupanccsi-oss |
| 应用类型 | 工具 (Tools) |
| 定价 | 免费 |
| 是否含广告 | 否 |
| 是否含内购 | 否 |

## 2. 简短描述 (80字符以内)

**英文:**
DeepSeek API Peak/Off-Peak Price Reminder — screen border color indicator.

**中文:**
DeepSeek API峰谷价格提醒，屏幕边框颜色实时显示高峰/空闲时段。

## 3. 完整描述

**英文:**

WenFengWenGu is a lightweight, ad-free utility that reminds you of DeepSeek API peak and off-peak pricing periods through a dynamic screen border overlay.

★ How It Works
- During peak hours (Beijing Time 09:00–12:00, 14:00–18:00), a red breathing border appears on all four edges of your screen.
- During off-peak hours (all other times), the border turns green.
- Off-peak pricing is 50% of peak pricing.

★ Key Features
- 100% Free — No ads, no in-app purchases, no subscriptions
- Works worldwide — automatically converts to Beijing Time (UTC+8) regardless of your timezone
- Minimal footprint — only 8dp border thickness, center area is fully transparent, does not interfere with normal phone use
- Background persistence — runs as a foreground service, survives screen lock
- Auto-restart on boot — border resumes after device reboot
- One-tap stop — notification bar provides a quick stop action
- No data collection — the app does not access the network or collect any personal information

★ How to Use
1. Install and open WenFengWenGu
2. Tap "Start Border"
3. Grant "Display over other apps" permission when prompted
4. Return to the app and tap "Start Border" again
5. A colored border appears on all four screen edges
6. The border runs in the background — you can exit the app
7. Tap the notification to stop at any time

★ Permissions Explained
- SYSTEM_ALERT_WINDOW: Draw the border on top of other apps (core feature)
- FOREGROUND_SERVICE: Keep the border running in the background
- POST_NOTIFICATIONS: Show the running-status notification (Android 13+)
- RECEIVE_BOOT_COMPLETED: Auto-restart after device reboot

★ Privacy
This app does NOT collect, store, transmit, or share any personal data. No network access, no analytics, no tracking. See our Privacy Policy: https://zhoupanccsi-oss.github.io/WenFengWenGu/

---

**中文:**

文峰文谷是一款轻量级、无广告的工具应用，通过屏幕边框动态颜色提醒 DeepSeek API 的高峰和空闲价格时段。

★ 工作原理
- 高峰时段（北京时间 09:00–12:00、14:00–18:00），屏幕四边显示红色呼吸边框
- 空闲时段（其余所有时间），边框变为绿色
- 空闲时段价格为高峰时段的50%

★ 主要特点
- 完全免费 — 无广告、无内购、无订阅
- 全球通用 — 无论你在哪个时区，自动换算为北京时间（UTC+8）
- 极简设计 — 边框仅8dp厚，中间区域完全透明，不影响手机正常使用
- 后台持续运行 — 作为前台服务保活，锁屏不断
- 开机自启 — 设备重启后自动恢复边框
- 一键停止 — 通知栏提供快捷停止按钮
- 零数据采集 — 不联网、不收集任何个人信息

★ 使用方法
1. 安装并打开 WenFengWenGu
2. 点击"启动边框"
3. 在系统提示中授予"显示在其他应用上层"权限
4. 返回应用，再次点击"启动边框"
5. 屏幕四边出现彩色边框
6. 边框在后台运行，可退出应用
7. 点击通知可随时停止

★ 隐私
本应用不收集、存储、传输或分享任何个人数据。不联网、无分析、无追踪。隐私政策：https://zhoupanccsi-oss.github.io/WenFengWenGu/

## 4. 隐私政策 URL

https://zhoupanccsi-oss.github.io/WenFengWenGu/

## 5. 上传文件

| 文件 | 路径 | 用途 |
|------|------|------|
| AAB | app/build/outputs/bundle/release/app-release.aab | Google Play 上传包 |
| Feature Graphic | WenFengWenGu/feature_graphic.png | 1024x500 商店横幅 |
| 应用图标 | app/src/main/res/mipmap-xxxhdpi/ic_launcher.png | 512x512 图标 |

## 6. 内容分级 (IARC问卷回答)

- 是否含暴力内容: 否
- 是否含性内容: 否
- 是否含脏话: 否
- 是否含恐惧内容: 否
- 是否含付费元素: 否（无内购）
- 是否收集个人信息: 否
- 是否含广告: 否

目标分级: 所有人 (Everyone)

## 7. 数据安全表单回答

- 是否收集任何数据: 否
- 是否加密传输数据: 不适用
- 是否可以删除用户数据: 不适用
- 是否遵守儿童政策: 是（不面向儿童，但也不收集任何信息）

## 8. 权限用途声明 (Google Play 审核用)

| 权限 | 用途说明 |
|------|---------|
| SYSTEM_ALERT_WINDOW | 在屏幕四边绘制彩色边框，这是应用的核心功能（显示高峰/空闲时段提醒） |
| FOREGROUND_SERVICE | 维持边框服务在后台持续运行，确保用户退出应用后边框仍可见 |
| FOREGROUND_SERVICE_SPECIAL_USE | Android 14+要求的前台服务类型声明，类型为screenoverlay（屏幕覆盖层） |
| POST_NOTIFICATIONS | 在Android 13+设备上显示前台服务运行状态通知 |
| RECEIVE_BOOT_COMPLETED | 设备重启后自动恢复边框服务，用户无需手动重新启动 |

> AI生成