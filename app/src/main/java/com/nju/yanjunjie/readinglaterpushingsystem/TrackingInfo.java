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
import android.content.pm.ResolveInfo;
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

        boolean isUsagePermission = TrackingInfo.checkUsagePermission(this);
        if (!isUsagePermission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        UsageStatsManager usm = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        int day = 0;
        List<String> eventFor = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        while (day <= 7) {
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            long beginTime = calendar.getTimeInMillis();
//            calendar.add(Calendar.HOUR_OF_DAY, 1);
//            long endTime2 = calendar.getTimeInMillis();
            System.out.println("startTime " + (beginTime));
            System.out.println("endTime " + (endTime));
//            System.out.println("endTime2 " + stampToDate(endTime2));

//        List<UsageStats> list = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginTime, endTime2);
//        List<String> usageInfo = new ArrayList<>();
//        if (list == null || list.isEmpty()) {
//            // 当没有权限时的处理
//            try {
//                startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            for (UsageStats usageStats : list) {
//
//                String packageName = usageStats.getPackageName();//获取包名
//                long firstTimeStamp = usageStats.getFirstTimeStamp();//获取第一次运行的时间
//                long lastTimeStamp = usageStats.getLastTimeStamp();//获取最后一次运行的时间
//                long lastTimeUsed = usageStats.getLastTimeUsed();//获取上一次运行的时间
//                long totalTimeInForeground = usageStats.getTotalTimeInForeground();//获取总共运行的时间
//                int launchCount = 0;
//                try {
//                    Field field = usageStats.getClass().getDeclaredField("mLaunchCount");//获取应用启动次数，UsageStats未提供方法来获取，只能通过反射来拿到
//                    if (field != null) {
//                        launchCount = field.getInt(usageStats);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (totalTimeInForeground != 0 && launchCount != 0) {
//                    String info = "包名：" + packageName
//                            + "      第一次运行时间：" + stampToDate(firstTimeStamp) + "\n"
//                            + " 最后一次运行时间：" + stampToDate(lastTimeStamp)
//                            + " 上一次运行时间：" + lastTimeUsed / 1000 / 60 + "--" + stampToDate(lastTimeUsed)
//                            + " 总共运行时间：" + totalTimeInForeground / 1000 / 60 + "==" + totalTimeInForeground
//                            + " 运行次数： " + launchCount;
//                    usageInfo.add(info);
//                }
//            }
//        }


            UsageEvents.Event event = new UsageEvents.Event();
            UsageEvents usageEvents = usm.queryEvents(beginTime, endTime);
            endTime = beginTime;
            day++;

            long time = 0;
            int n = 1;
            String eventInfo = "";
            String initName = "";
            long initTime = 0, firstTime = 0;
            while (usageEvents.hasNextEvent()) {
                usageEvents.getNextEvent(event);
                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                    if (n == 1) {
                        initName = event.getPackageName();
                        initTime = event.getTimeStamp();

                        firstTime = initTime;
                        n = 2;
                        continue;
                    } else {
                        String packageName = event.getPackageName();
                        long timeStamp = event.getTimeStamp();
                        if (packageName == initName) {
                            time = time + timeStamp - initTime;
                            initTime = timeStamp;
                            eventInfo = packageName + "---" + stampToDate(firstTime) + "---" + time / 1000 / 60;
                        } else {
                            if (time / 1000 / 60 >= 5) {
                                eventFor.add(eventInfo);
                            }

                            initName = packageName;
                            initTime = timeStamp;
                            firstTime = initTime;
                            time = 0;
                        }
                    }
//                String s = packageName + "时间:" + stampToDate(timeStamp) + " ---" + type;
//                eventFor.add(s);
                }


            }

        }




        ListView recyclerView = (ListView) findViewById(R.id.trackinfo2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(TrackingInfo.this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
//        recyclerView.setLayoutManager(linearLayoutManager);

        System.out.println(eventFor);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TrackingInfo.this, android.R.layout.simple_list_item_1
                , eventFor);
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
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }
}


//    /**
//     * 获取栈顶运行的进程 * @param context * @return
//     */
//    public static String getLauncherTopApp(Context context) {
//        UsageStatsManager sUsageStatsManager = null;
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//            List<ActivityManager.RunningTaskInfo> appTasks = activityManager.getRunningTasks(1);
//            if (null != appTasks && !appTasks.isEmpty()) {
//                return appTasks.get(0).topActivity.getPackageName();
//            }
//        } else {
//            long endTime = System.currentTimeMillis();
//            long beginTime = endTime - 1000;
//            if (sUsageStatsManager == null) {
//                sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
//            }
//            String result = "";
//            UsageEvents.Event event = new UsageEvents.Event();
//            UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
//            while (usageEvents.hasNextEvent()) {
//                usageEvents.getNextEvent(event);
//                //监测app由后台转前台
//                if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
//                    result = event.getPackageName();
//                }
//                //监测app由前台转后台//
//                if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
//                    result = event.getPackageName();
//
//                }
//            }
//            if (!android.text.TextUtils.isEmpty(result)) {
//                return result;
//            }
//        }
//        return "";
//    }
//
//
//    /**
//     * 判断指定进程是否由前台转后台 * @param context * @return
//     */
//    public static boolean IsLauncherToBack(Context context, String pkgName) {
//        boolean isRunning = false;
//        long endTime = System.currentTimeMillis();
//        long beginTime = endTime - 1000;
//        if (sUsageStatsManager == null) {
//            sUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
//        }
//        String result = "";
//        UsageEvents.Event event = new UsageEvents.Event();
//        UsageEvents usageEvents = sUsageStatsManager.queryEvents(beginTime, endTime);
//        while (usageEvents.hasNextEvent()) {
//            usageEvents.getNextEvent(event);
//
//            //监测app由前台转后台
//            if (event.getEventType() == UsageEvents.Event.MOVE_TO_BACKGROUND) {
//                result = event.getPackageName();
//            }
//        }
//        if (!android.text.TextUtils.isEmpty(result) && pkgName.equals(result)) {
//            isRunning = true;
//        }
//        return isRunning;
//    }
//
//    //4. 获取指定应用运行的时间
//    // 查找资料发现Android本身提供了获取应用使用时间的api
//    // (来源：http://blog.csdn.net/pierce0young/article/details/22292603)，
//    // 但是自己试了发现两个主要的类都找不到，不知道为什么，如果大家发现什么，请一定给我留下解释，谢谢！
//    // 那么接下来我就是间接的获取时间，即监听指定app的安装成功及启动，开始计时，
//    // 再配合判断应用在前台以及后台，计算时间的长短来获取运行时间。
//    // 5.允许获取应用使用情况
//    // 以上步骤大多基于用户已打开，
//    // 允许该应用获取其他应用使用情况，
//    // 这个前提，接下来是判断手机上是否存在这个打开界面、是否已打开这个开关以及打开这个界面的代码：
//    // /** * 监测手机上是否存在允许查看应用使用情况
//    // * @return */
//    private boolean isNoOption() {
//        PackageManager packageManager = getapplicationContext().getPackageManager();
//        //打开允许获取应用使用情况的界面
//        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//        return list.size() > 0;
//    }
//
//    /**
//     * 监测允许查看应用使用情况是否打开 * @return
//     */
//    private boolean isNoSwitch() {
//        long ts = System.currentTimeMillis();
//        UsageStatsManager usageStatsManager = (UsageStatsManager) getApplicationContext().getSystemService(USAGE_STATS_SERVICE);
//        List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, 0, ts);
//        if (queryUsageStats == null || queryUsageStats.isEmpty()) {
//            return false;
//        }
//        return true;
//    }

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



