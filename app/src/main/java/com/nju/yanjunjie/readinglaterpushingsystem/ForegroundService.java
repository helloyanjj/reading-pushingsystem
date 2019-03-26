package com.nju.yanjunjie.readinglaterpushingsystem;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import java.util.List;

import static com.igexin.sdk.GActivity.TAG;

public class ForegroundService extends Service {
    public ForegroundService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Handler mH = new Handler();
        Runnable r = new Runnable() {
            UsageStats recentStats = null;
            @Override
            public void run() {
                long ts = System.currentTimeMillis();
                UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
                List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, ts - 20000, ts);
                if (queryUsageStats == null || queryUsageStats.isEmpty()) {
//                    return null;
                }

                for (UsageStats usageStats : queryUsageStats) {
                    if (recentStats == null || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                        recentStats = usageStats;
                    }
                }
                String packageName = recentStats.getPackageName();
                System.out.println("-----------" + packageName);
            }
        };
        int n = 10;
        while (n>0) {
            mH.postDelayed(r, 1000 * 10 );
            n--;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
