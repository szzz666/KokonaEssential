package top.szzz666.KokonaEssential.config;

import top.szzz666.config.ConfigItem;
import top.szzz666.config.EasyConfig;

import java.util.List;

import static top.szzz666.KokonaEssential.KokonaEssentialMain.plugin;

public class MyConfig {
    @ConfigItem(key = "command.names", comment = "命令名")
    public static List<String> commands = List.of("菜单", "帮助", "功能");

    @ConfigItem(key = "command.description", comment = "描述")
    public static String description = "显示可用功能列表";

    @ConfigItem(key = "matchSecond", comment = "是否匹配秒")
    public static boolean matchSecond = true;

    public static EasyConfig ec;

    public static void initConfig() {
        ec = new EasyConfig(plugin + "/config.yml");
        ec.loadFromClass(MyConfig.class);
        ec.load();
    }
}
