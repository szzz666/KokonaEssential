# KokonaEssential

Kokona 框架的基础功能插件，提供帮助菜单和命令展示功能。

## 功能

- **帮助菜单**：以图片或文本形式展示所有可用命令，按分类分组显示
- **额外命令配置**：支持通过配置文件添加额外命令（仅展示，不注册执行器）
- **隐藏命令**：支持在菜单中隐藏指定命令

## 命令

| 命令 | 描述 | 权限 |
|------|------|------|
| 菜单 / 帮助 / 功能 | 显示可用功能列表 | 所有人 |

## 配置

配置文件位于 `config/config.yml`，首次运行自动生成。

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `command.names` | 命令名 | `[菜单, 帮助, 功能]` |
| `command.description` | 命令描述 | `显示可用功能列表` |
| `extraCommands` | 额外命令列表（仅用于HTML菜单展示） | 示例条目 |
| `hiddenCommands` | 隐藏命令列表 | `[]` |

### extraCommands 条目格式

```yaml
extraCommands:
  - names:
      - "示例命令"
    description: "这是一个示例额外命令"
    category: "其他"
    permission: 0
    scopes: "both"
```

| 字段 | 说明 |
|------|------|
| `names` | 命令名列表 |
| `description` | 命令描述 |
| `category` | 所属分类 |
| `permission` | 权限等级（0=所有人, 1=群管理, 2=群主, 3=超管） |
| `scopes` | 作用域（`both`/`group`/`private`） |

## 自定义帮助页样式

帮助页模板位于 `config/help.html`，首次运行自动从插件释放。可自行修改 HTML/CSS 来自定义样式，模板中 `<!--%cards%-->` 处会被替换为命令卡片内容。

## 构建

```bash
mvn package
```

构建产物位于 `target/` 目录。

## 依赖

- Java 21+
- [Kokona](https://github.com/szzz666/Kokona) 框架
- Lombok
