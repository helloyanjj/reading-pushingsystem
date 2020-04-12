package com.nju.yanjunjie.readinglaterpushingsystem.freetime;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.support.v7.widget.Toolbar;

import com.nju.yanjunjie.readinglaterpushingsystem.R;

import java.util.Calendar;

import static com.mob.tools.utils.Strings.getString;

public class CustomTimeActivity extends AppCompatActivity {

    private Button addButton;
    private int houre;
    private int minute;
    private Toolbar toolbar;
    TextView endTimeTv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_time);

//        initToolbar();

        addButton = findViewById(R.id.addCustomTime);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endTimeTv = findViewById(R.id.tv_end_time);

//                new TimePickerDialog(this, AlertDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        houre = hourOfDay;
//                        CustomTimeActivity.this.minute = minute;
//                        if (CustomTimeActivity.this.minute < 10){
//                            endTimeTv.setText(houre+":"+"0"+CustomTimeActivity.this.minute);
//                        }else {
//                            endTimeTv.setText(houre+":"+CustomTimeActivity.this.minute);
//                        }
//
//                    }
//                },0,0,true).show();

//                Calendar calendar=Calendar.getInstance();
//                TimePickerDialog dialog=new TimePickerDialog(this,this,
//                        calendar.get(Calendar.HOUR_OF_DAY),
//                        calendar.get(Calendar.MINUTE),
//                        true);
//                dialog.show();



            }
        });


    }

    public void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }
}
