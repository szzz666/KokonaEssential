# KokonaEssential

基于 [Kokona](https://github.com/szzz666/Kokona) 框架的 QQ 机器人基础功能插件，提供命令系统、权限管理与 HTML 渲染帮助菜单。

## 功能特性

- **命令系统** — 支持多别名命令注册、分类展示、自动分发与权限校验
- **权限管理** — 四级权限：所有人 / 群管理 / 群主 / 超级管理员
- **作用域控制** — 命令可限定为群聊、私聊或两者皆可
- **HTML 渲染帮助** — 使用 `JEditorPane` 将 HTML 渲染为图片发送，降级时回退到纯文本
- **可配置前缀** — 通过配置文件自定义命令前缀

## 命令

| 命令 | 别名 | 描述 | 权限 | 作用域 |
|------|------|------|------|--------|
| 菜单 | 帮助、功能 | 显示可用功能列表 | 所有人 | 群聊+私聊 |

> 命令前缀可在配置文件中设置，默认为空（无前缀）。

## 项目结构

```
src/main/java/top/szzz666/KokonaEssential/
├── KokonaEssentialMain.java    # 插件主类，注册命令与监听器
├── command/
│   ├── QQCommand.java          # 命令数据模型（record）
│   └── QQCommandManage.java    # 命令注册、分发与权限校验
├── config/
│   └── MyConfig.java           # 插件配置（命令名、前缀等）
├── listener/
│   └── QQListeners.java        # QQ 消息事件监听
└── tools/
    └── HtmlImageUtil.java      # HTML 转图片工具类
```

## 配置

配置文件位于 `plugins/KokonaEssential/config.yml`：

```yaml
command:
  names:
    - "菜单"
    - "帮助"
    - "功能"
  description: "显示可用功能列表"
prefix: ""
```

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `command.names` | 帮助命令的触发名称列表 | `["菜单", "帮助", "功能"]` |
| `command.description` | 帮助命令的描述 | `显示可用功能列表` |
| `prefix` | 命令前缀 | `""` (无前缀) |

## 环境要求

- Java 17+
- Maven
- Kokona 框架 (`Kokona-1.0-SNAPSHOT.jar`，放置于 `lib/` 目录)

## 构建

```bash
mvn package
```

构建产物位于 `target/` 目录，将生成的 JAR 放入 Kokona 的 `plugins/` 目录即可。

## 权限等级

| 等级 | 常量 | 说明 |
|------|------|------|
| 0 | `PERM_ALL` | 所有人可用 |
| 1 | `PERM_GROUP_ADMIN` | 群管理及以上 |
| 2 | `PERM_GROUP_OWNER` | 群主及以上 |
| 3 | `PERM_SUPER_ADMIN` | 超级管理员 |

## 开发

基于此插件扩展新命令示例：

```java
QQCommandManage.register(
    List.of("ping"),
    "测试命令",
    "基础",
    QQCommand.PERM_ALL,
    QQCommand.SCOPE_BOTH,
    (event, cmdName, args) -> {
        // 命令逻辑
    }
);
```

## 许可证

请参阅项目许可证文件。
