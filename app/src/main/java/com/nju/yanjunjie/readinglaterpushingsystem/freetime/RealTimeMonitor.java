package com.nju.yanjunjie.readinglaterpushingsystem.freetime;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;

import java.util.List;
import java.util.Timer;

public class RealTimeMonitor extends Timer {

    public void run() {
//        long ts = System.currentTimeMillis();
//        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
//        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 20000, ts);
//        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
////                return null;
//        }
//        UsageStats recentStats = null;
//        for (UsageStats usageStats : queryUsageStats) {
//            if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
//                recentStats = usageStats;
//            }
//        }
//        String packageName = recentStats.getPackageName();
//        System.out.println(packageName);
    }
}
