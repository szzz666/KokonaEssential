package top.szzz666.KokonaEssential.config;

import top.szzz666.config.ConfigItem;
import top.szzz666.config.EasyConfig;

import java.util.List;

public class MyConfig {
    @ConfigItem(key = "command.names", comment = "命令名")
    public static List<String> commands = List.of("菜单", "帮助", "功能");

    @ConfigItem(key = "command.description", comment = "描述")
    public static String description = "显示可用功能列表";

    @ConfigItem(key = "prefix", comment = "前缀")
    public static String prefix = "";

    public static EasyConfig ec;

    public static void initConfig() {
        ec = new EasyConfig("plugins/KokonaEssential/config.yml");
        ec.loadFromClass(MyConfig.class);
        ec.load();
    }
}
