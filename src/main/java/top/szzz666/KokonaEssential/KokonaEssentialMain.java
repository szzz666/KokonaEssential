package top.szzz666.KokonaEssential;


import top.szzz666.KokonaEssential.config.MyConfig;
import top.szzz666.KokonaEssential.tools.HtmlImageUtil;
import top.szzz666.command.Command;
import top.szzz666.command.CommandManage;
import top.szzz666.plugin.KokonaPlugin;
import top.szzz666.plugin.PluginBase;
import top.szzz666.qq.bot.MsgBuilder;
import top.szzz666.qq.entity.Event;

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
        logger.info("{} 插件读取...", this.getName());
    }

    @Override
    public void onEnable() {
//        QQEventManage.registerListener(new QQListeners());
        CommandManage.register(commands, description, "基础", PERM_ALL, SCOPE_BOTH, KokonaEssentialMain::onHelp);
        logger.info("{}  插件已启用", this.getName());
    }

    @Override
    public void onDisable() {
        logger.info("{} 插件已关闭", this.getName());
    }

    // ==================== 命令实现 ====================

    private static void onHelp(Event event, String commandName, String[] args) {
        String html = buildHelpHtml();
        new Thread(() -> {
            String base64Img = HtmlImageUtil.htmlToBase64(html, 600);
            if (base64Img != null) {
                replyImage(event, base64Img);
            } else {
                replyText(event, buildHelpText());
            }
        }).start();
    }

    // ==================== HTML 构建 ====================

    private static String buildHelpHtml() {
        String header = """
                <html><body style="\
                background-color:#f5f7fa;\
                color:#2c3e50;\
                font-family:Microsoft YaHei,SimHei,Dialog,sans-serif;\
                padding:20px;margin:0;">\
                <div style="\
                text-align:center;font-size:22px;font-weight:bold;\
                color:#3498db;padding:12px;\
                background-color:#ffffff;\
                border-radius:12px;\
                margin-bottom:15px;\
                box-shadow:0 2px 8px rgba(0,0,0,0.08);">\
                📋 功能列表</div>""";

        Map<String, List<Command>> grouped = new LinkedHashMap<>();
        for (Command cmd : CommandManage.getCommands()) {
            if (cmd.permission() < PERM_CONSOLE) {
                grouped.computeIfAbsent(cmd.category(), k -> new ArrayList<>()).add(cmd);
            }
        }

        StringBuilder commandHtml = new StringBuilder();
        for (Map.Entry<String, List<Command>> entry : grouped.entrySet()) {
            commandHtml.append(String.format("""
                    <div style="\
                    color:#e67e22;font-size:16px;font-weight:bold;\
                    margin:14px 0 8px 0;padding:6px 12px;\
                    background-color:#fff8f0;\
                    border-radius:8px;\
                    border-left:4px solid #e67e22;">📁 %s</div>""", escapeHtml(entry.getKey())));

            for (Command cmd : entry.getValue()) {
                String cmdName = escapeHtml(prefix + cmd.names().get(0));
                String aliases = cmd.names().size() > 1
                        ? "<span style=\"color:#7f8c8d;font-size:13px;\">("
                        + escapeHtml(String.join("/", cmd.names().subList(1, cmd.names().size())))
                        + ")</span>"
                        : "";
                String desc = escapeHtml(cmd.description());
                String perm = permLabel(cmd.permission());

                commandHtml.append(String.format("""
                        <div style="\
                        padding:10px 14px;\
                        border-left:4px solid #3498db;\
                        background-color:#ffffff;\
                        margin-bottom:8px;\
                        border-radius:10px;\
                        box-shadow:0 1px 4px rgba(0,0,0,0.06);">\
                        <span style="color:#2980b9;font-weight:bold;font-size:16px;">%s</span>%s<br>\
                        <span style="color:#555555;font-size:14px;">%s</span>\
                        <span style="color:#e67e22;font-size:13px;font-weight:bold;"> [%s]</span>\
                        </div>""", cmdName, aliases, desc, perm));
            }
        }
        return header + commandHtml + "</body></html>";
    }

    private static String buildHelpText() {
        StringBuilder sb = new StringBuilder("===== 命令列表 =====\n");

        Map<String, List<Command>> grouped = new LinkedHashMap<>();
        for (Command cmd : CommandManage.getCommands()) {
            grouped.computeIfAbsent(cmd.category(), k -> new java.util.ArrayList<>()).add(cmd);
        }

        for (Map.Entry<String, List<Command>> entry : grouped.entrySet()) {
            sb.append("\n【").append(entry.getKey()).append("】\n");
            for (Command cmd : entry.getValue()) {
                sb.append("  ").append(prefix).append(cmd.names().get(0));
                if (cmd.names().size() > 1) {
                    sb.append("(").append(String.join("/", cmd.names().subList(1, cmd.names().size()))).append(")");
                }
                sb.append(" - ").append(cmd.description());
                sb.append(" [").append(permLabel(cmd.permission())).append("]");
                sb.append("\n");
            }
        }
        return sb.toString().trim();
    }

    private static String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
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
