package com.nju.yanjunjie.readinglaterpushingsystem;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
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
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerAdapter recyclerAdapter;
    private FloatingActionButton addContent;
    private HttpUtil httpUtils = new HttpUtil();
    private Handler handler;
    private ShareContent shareContent = new ShareContent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initToolbar();
        initRecyclerView();
        initDrawerLayout ();
        initFloatingActionButton();
        initSwipeRefreshLayout();

        initView();

//        Intent intent = getIntent();
//        String action=intent.getAction();
//        String type = intent.getType();
//        final String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
//        String[] data = {"dsf","fds","546","fds","546","fds","546","fds","546","fds","546","fds",
//                "546","fds","546","fds","546",sharedText};
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                MainActivity.this, android.R.layout.simple_list_item_1, data
//        );
//        ListView listView = (ListView)findViewById(R.id.save_info);
//        listView.setAdapter(adapter);

        //test http
//        Button button = (Button)findViewById(R.id.testButton);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        shareContent.setAction("send");
//                        shareContent.setContent("dsfafsada");
//                        shareContent.setType("textd");
//                        final Response response = httpUtils.sendHttpRequest(shareContent, "http://172.19.121.8:6666/addContent");
//
//                        try {
//                            final String responseData = response.body().string();
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Toast.makeText(MainActivity.this, responseData,
//                                            Toast.LENGTH_LONG).show();
//                                    Log.d("MainActivity", responseData);
//                                }
//                            });
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }).start();
//
//
//            }
//        });
    }

    private void initView() {
        TextView mTxtWeb = (TextView) findViewById(R.id.testTextView);
        String htmlLinkText = "https://www.jianshu.com/p/4059e5c85434";
        mTxtWeb.setText(Html.fromHtml(htmlLinkText));
        mTxtWeb.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = mTxtWeb.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) mTxtWeb.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();
            for (final URLSpan url : urls) {
                CustomUrlSpan myURLSpan = new CustomUrlSpan(url.getURL());
                style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            mTxtWeb.setText(style);
        }
    }


    private void getShareContent() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                shareContent.setUserId("yanjunjie");
                Response response =
                        httpUtils.sendHttpRequest(shareContent, "http://172.19.121.8:6666/getContent");
                String shareContents = "";
                try{
                    shareContents = response.body().string();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Message message = new Message();
                message.obj = shareContents;
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();
    }

    private void initFloatingActionButton() {
        addContent = (FloatingActionButton)findViewById(R.id.add);
        addContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edit = new EditText(MainActivity.this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setView(edit);
                dialog.setTitle("添加分享内容");
                dialog.setCancelable(true);
                dialog.setPositiveButton("添加", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                                @Override
                            public void run() {
                                String content = edit.getText().toString();
                                shareContent.setUserId("yanjunjie");
                                shareContent.setAction("send");
                                shareContent.setContent(content);
                                shareContent.setType("textd");
                                final Response response =
                                        httpUtils.sendHttpRequest(shareContent, "http://172.19.121.8:6666/addContent");
                                try {
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

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();


                    }
                });

                dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
        });
    }

    private void initSwipeRefreshLayout() {
        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);
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

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle(getString(R.string.app_name));
        toolbar.setTitleTextColor(getResources().getColor(android.R.color.white));
    }

    private void initRecyclerView() {
        getShareContent();
        handler = new Handler(){
            public void handleMessage(Message message) {
                Gson gson = new Gson();
                List<ShareContent> list = gson.fromJson(message.obj.toString(), new TypeToken<List<ShareContent>>(){}
                .getType());
                RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                linearLayoutManager.setStackFromEnd(true);
                linearLayoutManager.setReverseLayout(true);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerAdapter = new RecyclerAdapter(list);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        };

    }

    private void initDrawerLayout () {
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView)findViewById(R.id.nav_view);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        navigationView.setCheckedItem(R.id.nav_mail);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
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

    private void handleImage(){
        ImageView imageView = null;
        Intent intent=getIntent();
        String action=intent.getAction();
        String type=intent.getType();
        if (action.equals(Intent.ACTION_SEND)&&type.equals("text/plain")) {
            Uri uri=intent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri != null) {
                Toast.makeText(this,uri.toString(), Toast.LENGTH_SHORT).show();
            }
        }
//        if(action.equals(Intent.ACTION_SEND)&&type.equals("image/*")){
//            Uri uri=intent.getParcelableExtra(Intent.EXTRA_STREAM);
//            //接收多张图片
//            //ArrayList<Uri> uris=intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
//            if(uri!=null ){
//                try {
//                    FileInputStream fileInputStream=new FileInputStream(uri.getPath());
//                    Bitmap bitmap= BitmapFactory.decodeStream(fileInputStream);
//                    imageView.setImageBitmap(bitmap);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }
}
