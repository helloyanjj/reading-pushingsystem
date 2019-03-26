package com.nju.yanjunjie.readinglaterpushingsystem.freetime;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.nju.yanjunjie.readinglaterpushingsystem.ForegroundService;
import com.nju.yanjunjie.readinglaterpushingsystem.R;
import com.nju.yanjunjie.readinglaterpushingsystem.data.MyApplication;
import com.suke.widget.SwitchButton;

public class FreeTimeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private SwitchButton isAutoPushON;
    private SwitchButton isRealTimePushON;
    private SwitchButton isCustomPushON;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_time);

//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
        initToolbar();

        isAutoPushON = (SwitchButton)findViewById(R.id.isAutoPushON);
        isRealTimePushON = (SwitchButton)findViewById(R.id.isRealTimePushON);
        isCustomPushON = (SwitchButton)findViewById(R.id.isCustomPushON);

        isAutoPushON.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                setIsAutoPushON(isChecked);
            }
        });

        isRealTimePushON.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                setIsRealTimePushON(isChecked);
            }
        });

        isCustomPushON.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                setIsCustomPushON(isChecked);
            }
        });



    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    public void setIsAutoPushON(Boolean isAutoPushON) {
        if (isAutoPushON) {
            Intent startIntent = new Intent(MyApplication.getContext(), TrackInfoService.class);
            startService(startIntent);
            Toast.makeText(FreeTimeActivity.this, "开启自动推送", Toast.LENGTH_SHORT).show();
        } else {
            Intent stopIntent = new Intent(MyApplication.getContext(), TrackInfoService.class);
            stopService(stopIntent);
            Toast.makeText(FreeTimeActivity.this, "取消自动推送", Toast.LENGTH_SHORT).show();
        }
    }

    public void setIsRealTimePushON(Boolean isRealTimePushON) {
        if (isRealTimePushON) {
            Intent startIntent = new Intent(MyApplication.getContext(), ForegroundService.class);
            startService(startIntent);
            Toast.makeText(FreeTimeActivity.this, "开启实时推送", Toast.LENGTH_SHORT).show();

        } else {
            Intent startIntent = new Intent(MyApplication.getContext(), ForegroundService.class);
            stopService(startIntent);
            Toast.makeText(FreeTimeActivity.this, "关闭实时推送", Toast.LENGTH_SHORT).show();

        }
    }

    public void setIsCustomPushON(Boolean isCustomPushON) {
        if (isCustomPushON) {
            Intent intent = new Intent(FreeTimeActivity.this, CustomTimeActivity.class);
            startActivity(intent);

        } else {

        }
    }
}
