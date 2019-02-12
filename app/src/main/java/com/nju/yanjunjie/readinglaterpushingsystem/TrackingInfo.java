package com.nju.yanjunjie.readinglaterpushingsystem;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TrackingInfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_info);

        boolean isaaa = TrackingInfo.checkUsagePermission(this);
        if (!isaaa) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        Log.d("TimeTest", "endTime " + endTime);
        calendar.add(Calendar.HOUR_OF_DAY, -1);
        long beginTime = calendar.getTimeInMillis();
        Log.d("TimeTest", "startTime " + (beginTime));


        List<String> eventInfo = new ArrayList<>();
        UsageEvents.Event event = new UsageEvents.Event();
        UsageEvents usageEvents = usm.queryEvents(beginTime, endTime);
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            String packageName = event.getPackageName();
            long timeStamp = event.getTimeStamp();
            String eInfo = "包名：" + packageName + " " + "发生时间: " + stampToDate(timeStamp);
            eventInfo.add(eInfo);

        }

        List<UsageStats> list = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, beginTime, endTime);
        List<String> usageInfo = new ArrayList<>();
        if (list == null || list.isEmpty()) {
            // 当没有权限时的处理
            try {
                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            for (UsageStats usageStats : list) {
                String name = "";
                try {
                    PackageManager packageManager = getApplicationContext().getPackageManager();
                    PackageInfo packageInfo = packageManager.getPackageInfo(
                            usageStats.getPackageName(), 0);
                    int labelRes = packageInfo.applicationInfo.labelRes;
                    name = getApplicationContext().getResources().getString(labelRes);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                String packageName = usageStats.getPackageName();//获取包名
                long firstTimeStamp = usageStats.getFirstTimeStamp();//获取第一次运行的时间
                long lastTimeStamp = usageStats.getLastTimeStamp();//获取最后一次运行的时间
                long lastTimeUsed = usageStats.getLastTimeUsed();//获取上一次运行的时间
                long totalTimeInForeground = usageStats.getTotalTimeInForeground();//获取总共运行的时间
                int launchCount = 0;
                try {
                    Field field = usageStats.getClass().getDeclaredField("mLaunchCount");//获取应用启动次数，UsageStats未提供方法来获取，只能通过反射来拿到
                    if (field != null) {
                        launchCount = field.getInt(usageStats);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (totalTimeInForeground != 0 && launchCount != 0) {
                    String info = "包名：" + packageName
//                            + "app name " + name
                            + "      第一次运行时间：" + stampToDate(firstTimeStamp) + "\n"
                            + " 最后一次运行时间：" + stampToDate(lastTimeStamp)
                            + " 上一次运行时间：" + lastTimeUsed / 1000 / 60 + "--" + stampToDate(lastTimeUsed)
                            + " 总共运行时间：" + totalTimeInForeground / 1000 / 60 + "==" + totalTimeInForeground
                            + " 运行次数： " + launchCount;
                    usageInfo.add(info);
                    eventInfo.add(info);
                    Log.d("TrackingInfo",
                            info
                    );
                }
            }
        }
        Gson gson = new Gson();
        ListView recyclerView = (ListView) findViewById(R.id.trackinfo2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TrackingInfo.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
//        recyclerView.setLayoutManager(linearLayoutManager);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TrackingInfo.this, android.R.layout.simple_list_item_1
                , usageInfo);
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TrackingInfo.this, android.R.layout.simple_list_item_1
//                , eventInfo);
        recyclerView.setAdapter(arrayAdapter);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static boolean checkUsagePermission(Context context) {
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, android.os.Process.myUid(),
                context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    public static String stampToDate(long s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        long lt = new Long(s);
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }

//    //UsageStatsManager
//    public static void checkUsageStateAccessPermission(Context context) {
//        if(!AppUsageUtil.checkAppUsagePermission(context)) {
//            AppUsageUtil.requestAppUsagePermission(context);
//        }
//    }
//
//    public static boolean checkAppUsagePermission(Context context) {
//        UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
//        if(usageStatsManager == null) {
//            return false;
//        }
//        long currentTime = System.currentTimeMillis();
//        // try to get app usage state in last 1 min
//        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 60 * 1000, currentTime);
//        if (stats.size() == 0) {
//            return false;
//        }
//
//        return true;
//    }
//
//    public static void requestAppUsagePermission(Context context) {
//        Intent intent = new Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        try {
//            context.startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            Log.i(TAG,"Start usage access settings activity fail!");
//        }
//    }

}

