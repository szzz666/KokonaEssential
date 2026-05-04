package top.szzz666.KokonaEssential.apis;

import cn.hutool.cron.CronUtil;


public class CronTaskApi {
    // Cron 定时任务
    public static String Cron(Runnable logic, String cron) {
        return CronUtil.schedule(cron, logic);
    }
}
