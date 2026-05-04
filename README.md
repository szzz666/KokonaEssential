# KokonaEssential

Kokona 框架的基础功能插件，提供命令帮助菜单的精美图片渲染与文本回退展示。

## 功能

- 📋 **帮助菜单** — 自动收集已注册命令，按分类生成精美图片菜单
- 🎨 **HTML 渲染** — 将 HTML 转为图片发送，支持群聊与私聊
- 📝 **文本回退** — 图片渲染失败时自动降级为纯文本菜单
- ⚙️ **可配置** — 命令名、描述、前缀均可通过 `config.yml` 自定义
- 🔐 **权限标签** — 自动标注命令权限等级（所有人/群管理/群主/超管）

## 环境要求

- Java 17+
- Maven
- [Kokona](https://github.com/szzz666/Kokona) 框架

## 构建

```bash
mvn package
```

构建产物位于 `target/` 目录下。

## 安装

将构建生成的 jar 文件放入 Kokona 的插件目录即可。

## 配置

插件首次加载后会自动生成 `config.yml`：

```yaml
# 命令触发名
command:
  names:
    - "菜单"
    - "帮助"
    - "功能"
  description: "显示可用功能列表"

# 命令前缀
prefix: ""
```

## 项目结构

```
src/main/java/top/szzz666/KokonaEssential/
├── KokonaEssentialMain.java   # 插件主类，命令注册与帮助菜单生成
├── config/
│   └── MyConfig.java          # 配置管理
├── listener/
│   └── QQListeners.java       # QQ 事件监听器
└── tools/
    └── HtmlImageUtil.java     # HTML 转图片工具类
```

## 许可证

MIT
