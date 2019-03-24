package com.nju.yanjunjie.readinglaterpushingsystem.freetime;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.ArraySet;
import android.util.Log;

import com.nju.yanjunjie.readinglaterpushingsystem.TrackingInfo;
import com.nju.yanjunjie.readinglaterpushingsystem.data.AppStatus;
import com.nju.yanjunjie.readinglaterpushingsystem.data.HttpUtil;
import com.nju.yanjunjie.readinglaterpushingsystem.data.ReturnInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Response;

import static com.nju.yanjunjie.readinglaterpushingsystem.data.ReturnInfo.fail;
import static com.nju.yanjunjie.readinglaterpushingsystem.data.ReturnInfo.success;
import static com.nju.yanjunjie.readinglaterpushingsystem.TrackingInfo.stampToDate;

public class TrackInfoService extends Service {
    public TrackInfoService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        AppStatus appStatus = new AppStatus();

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
        Set<AppStatus> appStatusSet = new ArraySet<>();
        Calendar calendar = Calendar.getInstance();
        long endTime = calendar.getTimeInMillis();
        while (day <= 6) {
            calendar.add(Calendar.DAY_OF_WEEK, -1);
            long beginTime = calendar.getTimeInMillis();

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

                        appStatus.setPackageName(initName);
                        appStatus.setFirstTimeStamp(initTime);

                        firstTime = initTime;
                        n = 2;
                        continue;
                    } else {
                        String packageName = event.getPackageName();
                        long timeStamp = event.getTimeStamp();
                        if (packageName == initName) {
                            time = time + timeStamp - initTime;
                            initTime = timeStamp;
                            appStatus.setTotalTimeInForeground(time/1000/60);
                            eventInfo = packageName + "---" + stampToDate(firstTime) + "---" + time / 1000 / 60;
                        } else {
                            if (time / 1000 / 60 >= 5) {
                                eventFor.add(eventInfo);
//                                appStatusesEvent.add(appStatus);
                                appStatusSet.add(appStatus);

                            }

                            initName = packageName;
                            initTime = timeStamp;
                            firstTime = initTime;
                            appStatus = new AppStatus();
                            appStatus.setPackageName(packageName);
                            appStatus.setFirstTimeStamp(timeStamp);
                            time = 0;
                        }
                    }
//                String s = packageName + "时间:" + stampToDate(timeStamp) + " ---" + type;
//                eventFor.add(s);
                }


            }

        }

        List<AppStatus> appStatusesEvent = new ArrayList<>(appStatusSet);

        HttpUtil.sendOkHttpRequest(appStatusesEvent, ReturnInfo.address + ":2222/addAppStats", new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Log.d("save appstats", success);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.d("save appstats", fail);
            }

        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
