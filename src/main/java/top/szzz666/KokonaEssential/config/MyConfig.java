package top.szzz666.KokonaEssential.config;

import top.szzz666.config.ConfigItem;
import top.szzz666.config.EasyConfig;

import java.util.List;
import java.util.Map;

import static top.szzz666.KokonaEssential.KokonaEssentialMain.plugin;

public class MyConfig {
    @ConfigItem(key = "command.names", comment = "命令名")
    public static List<String> commands = List.of("菜单", "帮助", "功能");

    @ConfigItem(key = "command.description", comment = "描述")
    public static String description = "显示可用功能列表";

    @ConfigItem(key = "extraCommands", comment = "额外命令列表（仅用于HTML菜单展示，不注册实际执行器）")
    public static List<Map<String, Object>> extraCommands = List.of();

    public static EasyConfig ec;

    public static void initConfig() {
        ec = new EasyConfig(plugin.getConfigFolderPath() + "/config.yml");
        ec.loadFromClass(MyConfig.class);
        ec.load();
    }
}
