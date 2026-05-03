package top.szzz666.KokonaEssential.command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.szzz666.qq.bot.MsgBuilder;
import top.szzz666.qq.entity.Event;

import java.util.*;

import static top.szzz666.KokonaEssential.config.MyConfig.prefix;


public class QQCommandManage {

    private static final Logger log = LoggerFactory.getLogger(QQCommandManage.class);
    private static final Map<String, QQCommand> commandMap = new LinkedHashMap<>();
    private static final List<QQCommand> commandList = new ArrayList<>();

    // ==================== 注册 ====================

    public static void register(String name, String description, String category, int permission,
                                String scopes, QQCommand.CommandExecutor executor) {
        register(List.of(name), description, category, permission, scopes, executor);
    }

    public static void register(List<String> names, String description, String category, int permission,
                                String scopes, QQCommand.CommandExecutor executor) {
        QQCommand command = new QQCommand(names, description, category, permission, scopes, executor);
        commandList.add(command);
        for (String name : names) {
            String lowerName = name.toLowerCase();
            if (commandMap.containsKey(lowerName)) {
                log.warn("命令名 \"{}\" 已被注册，覆盖旧注册", name);
            }
            commandMap.put(lowerName, command);
        }
    }

    // ==================== 查找 ====================

    public static QQCommand getCommand(String name) {
        return commandMap.get(name.toLowerCase());
    }

    public static List<QQCommand> getCommands() {
        return Collections.unmodifiableList(commandList);
    }

    /**
     * 处理消息事件，尝试匹配并执行命令
     */
    public static boolean dispatch(Event event) {
        String raw = event.getText();
        if (raw == null || raw.isEmpty()) return false;
        raw = raw.trim();

        if (!raw.startsWith(prefix)) return false;

        String content = raw.substring(prefix.length()).trim();
        if (content.isEmpty()) return false;

        String[] parts = content.split("\\s+");
        String cmdName = parts[0].toLowerCase();
        String[] args = parts.length > 1 ? Arrays.copyOfRange(parts, 1, parts.length) : new String[0];

        QQCommand command = commandMap.get(cmdName);
        if (command == null) return false;

        if (!checkScope(command, event)) {
            sendScopeTip(event, command);
            return true;
        }

        if (!checkPermission(command, event)) {
            sendPermissionTip(event, command);
            return true;
        }

        try {
            command.executor().execute(event, cmdName, args);
        } catch (Exception e) {
            log.warn("命令 \"{}\" 执行异常: {}", cmdName, e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    // ==================== 校验 ====================

    private static boolean checkScope(QQCommand command, Event event) {
        String scopes = command.scopes();
        if (QQCommand.SCOPE_BOTH.equals(scopes)) return true;

        boolean isGroup = event.isGroupMessage();
        boolean isPrivate = event.isPrivateMessage();

        if (isGroup && QQCommand.SCOPE_GROUP.equals(scopes)) return true;
        if (isPrivate && QQCommand.SCOPE_PRIVATE.equals(scopes)) return true;

        return false;
    }

    private static boolean checkPermission(QQCommand command, Event event) {
        int perm = command.permission();
        if (perm == QQCommand.PERM_ALL) return true;
        if (perm == QQCommand.PERM_GROUP_ADMIN) {
            return event.isGroupAdminMessage() || event.isAdminMessage();
        }
        if (perm == QQCommand.PERM_GROUP_OWNER) {
            return event.isGroupOwnerMessage() || event.isAdminMessage();
        }
        if (perm == QQCommand.PERM_SUPER_ADMIN) {
            return event.isAdminMessage();
        }
        return false;
    }

    // ==================== 提示消息 ====================

    private static void sendScopeTip(Event event, QQCommand command) {
        String tip = "该命令仅在" +
                (QQCommand.SCOPE_GROUP.equals(command.scopes()) ? "群聊" : "私聊") +
                "中可用";
        replyText(event, tip);
    }

    private static void sendPermissionTip(Event event, QQCommand command) {
        String permDesc = switch (command.permission()) {
            case QQCommand.PERM_GROUP_ADMIN -> "群管理";
            case QQCommand.PERM_GROUP_OWNER -> "群主";
            case QQCommand.PERM_SUPER_ADMIN -> "超级管理员";
            default -> "未知权限";
        };
        replyText(event, "权限不足，该命令需要 " + permDesc + " 权限");
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
}
