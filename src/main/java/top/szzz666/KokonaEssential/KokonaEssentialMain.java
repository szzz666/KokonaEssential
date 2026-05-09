package top.szzz666.KokonaEssential;


import top.szzz666.KokonaEssential.config.MyConfig;
import top.szzz666.command.Command;
import top.szzz666.command.CommandManage;
import top.szzz666.plugin.KokonaPlugin;
import top.szzz666.plugin.PluginBase;
import top.szzz666.qq.bot.MsgBuilder;
import top.szzz666.qq.entity.Event;
import top.szzz666.tools.FileUtil;
import top.szzz666.tools.HtmlToImageUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static top.szzz666.KokonaEssential.config.MyConfig.*;
import static top.szzz666.command.Command.*;


@KokonaPlugin(name = "KokonaEssential")
public class KokonaEssentialMain extends PluginBase {
    public static PluginBase plugin;


    @Override
    public void onLoad() {
        plugin = this;
        MyConfig.initConfig();
        plugin.getLogger().info("{} 插件读取...", this.getName());
    }

    @Override
    public void onEnable() {
//        QQEventManage.registerListener(new QQListeners());
        CommandManage.register(commands, description, "基础", PERM_ALL, SCOPE_BOTH, KokonaEssentialMain::onHelp);
        plugin.getLogger().info("{}  插件已启用", this.getName());
    }

    @Override
    public void onDisable() {
        plugin.getLogger().info("{} 插件已关闭", this);
    }

    // ==================== 命令实现 ====================

    private static void onHelp(Event event, String commandName, String[] args) {
        new Thread(() -> {
            String html = buildHelpHtml();
            String base64Img = null;
            try {
                base64Img = HtmlToImageUtil.convertToDataUri(html,
                        new HtmlToImageUtil.HtmlToImageOptions()
                                .setWidth(520)
                                .setQuality(95));
//                plugin.getLogger().info("帮助图片生成成功: {}", base64Img);
            } catch (Exception e) {
                plugin.getLogger().warn("帮助图片生成失败，回退到文本模式: {}", e.getMessage());
            }
            if (base64Img != null) {
                replyImage(event, base64Img);
            } else {
                replyText(event, buildHelpText());
            }
        }).start();
    }

    private static String buildHelpHtml() {
        Map<String, List<Command>> grouped = getGroupedCommands();
        StringBuilder cards = new StringBuilder();
        for (Map.Entry<String, List<Command>> entry : grouped.entrySet()) {
            cards.append("<div class=\"card\">");
            cards.append("<div class=\"card-title\">").append(entry.getKey()).append("</div>");
            for (Command cmd : entry.getValue()) {
                String names = String.join(" / ", cmd.names());
                cards.append("<div class=\"cmd-row\">");
                cards.append("<span class=\"cmd-name\">").append(names).append("</span>");
                cards.append("<span class=\"cmd-perm\">").append(permLabel(cmd.permission())).append("</span>");
                cards.append("</div>");
                cards.append("<div class=\"cmd-desc\">").append(cmd.description()).append("</div>");
            }
            cards.append("</div>");
        }
        Path htmlPath = Path.of(plugin.getConfigFolderPath(), "help.html");
        if (!htmlPath.toFile().exists()) {
            try {
                FileUtil.loadRecourseFromJar("/help.html", plugin.getConfigFolderPath(), KokonaEssentialMain.class);
            } catch (IOException e) {
                plugin.getLogger().warn("默认help.html复制失败: {}", e.getMessage());
            }
        }
        String htmlTemplate = FileUtil.readFileAsString(htmlPath.toString());
        return htmlTemplate.replace("<!--%cards%-->", cards.toString());
    }

    private static String buildHelpText() {
        Map<String, List<Command>> grouped = getGroupedCommands();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Kokona Essential 帮助 ===\n\n");
        for (Map.Entry<String, List<Command>> entry : grouped.entrySet()) {
            sb.append("【").append(entry.getKey()).append("】\n");
            for (Command cmd : entry.getValue()) {
                String names = String.join(" / ", cmd.names());
                sb.append("  ").append(names).append(" — ").append(cmd.description())
                  .append(" [").append(permLabel(cmd.permission())).append("]\n");
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }



    // ==================== 额外命令加载 ====================


    private static List<Command> loadExtraCommands() {
        List<Command> extra = new ArrayList<>();
        if (extraCommands == null) return extra;
        for (Map<String, Object> item : extraCommands) {
            try {
                List<String> names = item.get("names") instanceof List<?> list
                        ? list.stream().map(Object::toString).toList()
                        : List.of(item.getOrDefault("names", "").toString());
                String desc = item.getOrDefault("description", "").toString();
                String category = item.getOrDefault("category", "其他").toString();
                int perm = item.get("permission") instanceof Number n ? n.intValue() : PERM_ALL;
                String scopes = item.getOrDefault("scopes", SCOPE_BOTH).toString();
                extra.add(new Command(names, desc, category, perm, scopes, (evt, name, args) -> {
                }));
            } catch (Exception e) {
                plugin.getLogger().warn("加载额外命令配置失败: {}", item);
            }
        }
        return extra;
    }

    


    private static Map<String, List<Command>> getGroupedCommands() {
        Map<String, List<Command>> grouped = new LinkedHashMap<>();
        for (Command cmd : CommandManage.getCommands()) {
            if (cmd.permission() < PERM_CONSOLE) {
                grouped.computeIfAbsent(cmd.category(), k -> new ArrayList<>()).add(cmd);
            }
        }
        return grouped;
    }
    

    // ==================== 工具方法 ====================

    private static String permLabel(int perm) {
        return switch (perm) {
            case PERM_ALL -> "所有人";
            case Command.PERM_GROUP_ADMIN -> "群管理";
            case Command.PERM_GROUP_OWNER -> "群主";
            case Command.PERM_SUPER_ADMIN -> "超管";
            default -> "未知";
        };
    }

    private static void replyText(Event event, String text) {
        if (event.isGroupMessage()) {
            event.getQq_bot().sendGroupMsg(event.getGroup_id(),
                    MsgBuilder.builder().text(text).build());
        } else if (event.isPrivateMessage()) {
            event.getQq_bot().sendPrivateMsg(event.getUser_id(),
                    MsgBuilder.builder().text(text).build());
        }
    }

    private static void replyImage(Event event, String base64Img) {
        if (event.isGroupMessage()) {
            event.getQq_bot().sendGroupMsg(event.getGroup_id(),
                    MsgBuilder.builder().image(base64Img).build());
        } else if (event.isPrivateMessage()) {
            event.getQq_bot().sendPrivateMsg(event.getUser_id(),
                    MsgBuilder.builder().image(base64Img).build());
        }
    }
}
