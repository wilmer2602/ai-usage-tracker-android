# AI Usage Tracker

一个 Android 应用，记录你在日常生活中接触 AI 的情况，并生成随 AI 使用增长而逐渐机械化的用户头像。

## 功能

- **记录 AI 使用事件**：阅读文章、观看视频、使用 AI 工具、命令行、浏览器对话、与人谈论 AI 等
- **统计仪表盘**：查看总使用时长、近期趋势
- **动态用户画像**：3D 可旋转头像，大脑功能区根据使用类型和时长动态标记，反映 AI 化程度
- **数据备份**：支持导出/导入 JSON，保护隐私（数据仅存本地）

## 技术栈

- Kotlin + Jetpack Compose
- Room (SQLite)
- ViewModel + Coroutines + Flow
- Material Design 3
- GitHub Actions 自动构建

## 构建

GitHub Actions 会自动构建 debug APK 并发布在仓库的 Releases 页面（或 Actions  artifact）。下载 `app-debug.apk` 即可安装测试。

## 开发状态

开发进行中，首版预计很快完成。

## 截图

（完成后补充）

## 许可证

MIT
