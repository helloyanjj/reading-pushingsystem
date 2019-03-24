package com.nju.yanjunjie.readinglaterpushingsystem.readlater;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nju.yanjunjie.readinglaterpushingsystem.gtpush.MessagePushService;
import com.nju.yanjunjie.readinglaterpushingsystem.R;
import com.nju.yanjunjie.readinglaterpushingsystem.data.ReturnInfo;
import com.nju.yanjunjie.readinglaterpushingsystem.freetime.TrackInfoService;
import com.nju.yanjunjie.readinglaterpushingsystem.data.HttpUtil;
import com.nju.yanjunjie.readinglaterpushingsystem.data.MyApplication;
import com.nju.yanjunjie.readinglaterpushingsystem.gtpush.DemoIntentService;
import com.nju.yanjunjie.readinglaterpushingsystem.data.ShareContent;

import com.igexin.sdk.PushManager;

import okhttp3.Call;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerAdapter recyclerAdapter;
    private FloatingActionButton addContent;
    private ShareContent shareContent = new ShareContent();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // com.getui.demo.DemoPushService 为第三方自定义推送服务
        PushManager.getInstance().initialize(this.getApplicationContext(),
                MessagePushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(),
                DemoIntentService.class);

        initToolbar();
        initRecyclerView();
        initFloatingActionButton();
        initSwipeRefreshLayout();
        appShareLink();
        initDrawerLayout();

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.backup:
                Toast.makeText(this, "backup", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delete:
                Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
                break;
            case R.id.settings:
                Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }

        return true;
    }

    private void initRecyclerView() {
        new ShowShareContents().execute();
    }

    class ShowShareContents extends AsyncTask<Void, String, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            shareContent.setUserId("yanjunjie");
            HttpUtil.sendOkHttpRequest(shareContent, ReturnInfo.address + ":2221/getContent", new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    System.out.println(responseData);
                    publishProgress(responseData);
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String fail = "读取失败";
                            Toast.makeText(MainActivity.this, fail,
                                    Toast.LENGTH_LONG).show();
                            Log.d("MainActivity", fail);
                        }
                    });
                }

            });
            return true;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Gson gson = new Gson();
            List<ShareContent> list = gson.fromJson(values[0], new TypeToken<List<ShareContent>>() {
            }.getType());
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyApplication.getContext());
            linearLayoutManager.setStackFromEnd(true);
            linearLayoutManager.setReverseLayout(true);
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerAdapter = new RecyclerAdapter(list);
            recyclerView.setAdapter(recyclerAdapter);

        }

        @Override
        protected void onPostExecute(Boolean o) {
            super.onPostExecute(o);
        }
    }

    private void initFloatingActionButton() {
        addContent = (FloatingActionButton) findViewById(R.id.add);
        addContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edit = new EditText(MainActivity.this);
                addShareContent(edit);
            }
        });
    }

    private void appShareLink() {
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        String sharedContent = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (action.equals(Intent.ACTION_SEND) && type.equals("text/plain")) {
            final EditText edit = new EditText(MainActivity.this);
            edit.setText(sharedContent);
            addShareContent(edit);
        }
    }

    private void addShareContent(final EditText edit) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setView(edit);
        dialog.setTitle("添加分享内容");
        dialog.setCancelable(true);
        dialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String content = edit.getText().toString();
                shareContent.setUserId("yanjunjie");
                shareContent.setAction("send");
                shareContent.setContent(content);
                shareContent.setType("textd");
                HttpUtil.sendOkHttpRequest(shareContent, ReturnInfo.address + ":2221/addContent", new okhttp3.Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        final String responseData = response.body().string();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String showInfo;
                                if (responseData.equals(ReturnInfo.success)) {
                                    showInfo = "添加内容成功！";
                                    refresh();
                                } else showInfo = "添加内容失败！";

                                Toast.makeText(MainActivity.this, showInfo,
                                        Toast.LENGTH_LONG).show();

                                Log.d("MainActivity", showInfo);
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String fail = "添加失败";
                                Toast.makeText(MainActivity.this, fail,
                                        Toast.LENGTH_LONG).show();
                                Log.d("MainActivity", fail);
                            }
                        });

                    }
                });
            }
        });

        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.show();
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });
    }

    private void refresh() {
        initRecyclerView();
        new Thread(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navigationView.setCheckedItem(R.id.nav_mail);//默认选项
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getTitle().equals("自动推送")) {
                    Intent startIntent = new Intent(MyApplication.getContext(), TrackInfoService.class);
                    startService(startIntent);
                    Toast.makeText(MainActivity.this, "开启自动推送", Toast.LENGTH_SHORT).show();
                } else if (item.getTitle().equals("自定义时间")) {
                    Intent stopIntent = new Intent(MyApplication.getContext(), TrackInfoService.class);
                    stopService(stopIntent);
                    Toast.makeText(MainActivity.this, "取消自动推送", Toast.LENGTH_SHORT).show();
                }

                mDrawerLayout.closeDrawers();
                return true;
}
        });
    }

}
