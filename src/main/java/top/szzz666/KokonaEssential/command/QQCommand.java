package top.szzz666.KokonaEssential.command;

import top.szzz666.qq.entity.Event;

import java.util.List;

/**
 * QQ 命令数据模型
 *
 * @param names       命令名称列表（支持多个别名）
 * @param description 命令描述
 * @param category    命令分类（用于帮助列表分组显示）
 * @param permission  命令权限等级（0=所有人, 1=群管理, 2=群主, 3=超级管理员）
 * @param scopes      命令有效范围（"group"=群聊, "private"=私聊, "both"=两者皆可）
 * @param executor    命令执行器
 */
public record QQCommand(
        List<String> names,
        String description,
        String category,
        int permission,
        String scopes,
        CommandExecutor executor
) {

    public static final int PERM_ALL = 0;
    public static final int PERM_GROUP_ADMIN = 1;
    public static final int PERM_GROUP_OWNER = 2;
    public static final int PERM_SUPER_ADMIN = 3;

    public static final String SCOPE_GROUP = "group";
    public static final String SCOPE_PRIVATE = "private";
    public static final String SCOPE_BOTH = "both";

    @FunctionalInterface
    public interface CommandExecutor {
        void execute(Event event, String commandName, String[] args);
    }
}
