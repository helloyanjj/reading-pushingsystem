package com.nju.yanjunjie.readinglaterpushingsystem.user;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mob.MobSDK;
import com.nju.yanjunjie.readinglaterpushingsystem.R;
import com.nju.yanjunjie.readinglaterpushingsystem.data.HttpUtil;
import com.nju.yanjunjie.readinglaterpushingsystem.data.ReturnInfo;
import com.nju.yanjunjie.readinglaterpushingsystem.readlater.MainActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Response;

public class Login extends Activity {                 //登录界面活动

    public int pwdresetFlag = 0;
    private EditText loginTel;                        //登录手机号
    private EditText loginIdentifyCode;               //登录验证码
    private Button getIdentifyCode;                   //获取验证码按钮
    private Button loginButton;                       //登录按钮
    private Button loginWithPwd;                      //跳转密码登录
    private Button registerButton;                    //注册
    private SharedPreferences sharedPreferences;

    private TimeCount identifyCodeTime = new TimeCount(3000, 1000);
//    private UserDataManager mUserDataManager;         //用户数据管理类


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        MobSDK.init(Login.this, "2af96f10204f0", "336ca229cff5753c60749728f10343c0");
        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);

        loginTel = (EditText) findViewById(R.id.login_tel);
        loginIdentifyCode = (EditText) findViewById(R.id.login_verification_code);
        getIdentifyCode = (Button) findViewById(R.id.get_verification_code);
        loginButton = (Button) findViewById(R.id.login_btn);
        loginWithPwd = findViewById(R.id.login_with_password);
        registerButton = findViewById(R.id.register_btn);

        loginButton.setOnClickListener(mListener);
        getIdentifyCode.setOnClickListener(mListener);
        loginWithPwd.setOnClickListener(mListener);
        registerButton.setOnClickListener(mListener);

        ImageView image = (ImageView) findViewById(R.id.logo);             //使用ImageView显示logo
        image.setImageResource(R.drawable.logo);
    }

    OnClickListener mListener = new OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.get_verification_code:
                    if (isTelValid()) {
                        SMSSDK.getVerificationCode("86", loginTel.getText().toString().trim());
                        identifyCodeTime.start();
                    }
                    // 在尝试读取通信录时以弹窗提示用户（可选功能）
                    SMSSDK.setAskPermisionOnReadContact(true);
                    break;
                case R.id.login_btn:
                    login();
                    break;
                case R.id.login_with_password:
                    Intent intent_login_with_password = new Intent(Login.this, LoginWithPassword.class);
                    startActivity(intent_login_with_password);
                    break;
                case R.id.register_btn:
                    Intent intent_register = new Intent(Login.this, Register.class);
                    startActivity(intent_register);
                    break;
            }
        }
    };

    class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getIdentifyCode.setBackgroundColor(Color.parseColor("#B6B6D8"));
            getIdentifyCode.setClickable(false);
            getIdentifyCode.setText("(" + millisUntilFinished / 1000 + ") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            getIdentifyCode.setText("重新获取验证码");
            getIdentifyCode.setClickable(true);
            getIdentifyCode.setBackgroundColor(Color.parseColor("#B6B6D8"));
        }
    }

    EventHandler eventHandler = new EventHandler() {
        public void afterEvent(int event, int result, Object data) {
            // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
            Message msg = new Message();
            msg.arg1 = event;
            msg.arg2 = result;
            msg.obj = data;
            new Handler(Looper.getMainLooper(), new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    int event = msg.arg1;
                    int result = msg.arg2;
                    Object data = msg.obj;
                    if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // TODO 处理成功得到验证码的结果
                            // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                            Toast.makeText(Login.this,"验证码发送成功",Toast.LENGTH_SHORT).show();
                        } else {
                            // TODO 处理错误的结果
                            ((Throwable) data).printStackTrace();
                            Toast.makeText(Login.this,"验证码发送失败",Toast.LENGTH_SHORT).show();
                        }
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // TODO 处理验证码验证通过的结果
                            Intent mainActivity = new Intent(Login.this, MainActivity.class);
                            startActivity(mainActivity);
                            finish();
                        } else {
                            Toast.makeText(Login.this,"验证码错误",Toast.LENGTH_SHORT).show();
                            ((Throwable) data).printStackTrace();
                        }
                    }
                    // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
                    return false;
                }
            }).sendMessage(msg);
        }
    };

    public void login() {                                              //登录按钮监听事件
        if (isTelAndCodeValid()) {
            String tel = loginTel.getText().toString().trim();    //获取当前输入的手机和密码信息
            User user = new User();
            user.setUserId(tel);
            HttpUtil.sendOkHttpRequest(user, ReturnInfo.address + ":2221/validateUser", new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    if (responseData.equals("false")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String fail = "您的手机号未注册";
                                Toast.makeText(Login.this, fail,
                                        Toast.LENGTH_LONG).show();
                                Log.d("Login", fail);
                            }
                        });
                    } else if (responseData.equals("true")) {
                        // 提交验证码，其中的code表示验证码，如“1357”
                        SMSSDK.submitVerificationCode("86", loginTel.getText().toString(), loginIdentifyCode.getText().toString());


                    }

                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String fail = "网络连接失败";
                            Toast.makeText(Login.this, fail,
                                    Toast.LENGTH_LONG).show();
                            Log.d("MainActivity", fail);
                        }
                    });
                }

            });
        }
    }

    public boolean isTelValid() {
        if (loginTel.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.tel_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isTelAndCodeValid() {
        if (loginTel.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.tel_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (loginIdentifyCode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.identifyCode_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
//        if (mUserDataManager == null) {
//            mUserDataManager = new UserDataManager(this);
//            mUserDataManager.openDataBase();
//        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }

    @Override
    protected void onPause() {
//        if (mUserDataManager != null) {
//            mUserDataManager.closeDataBase();
//            mUserDataManager = null;
//        }
        super.onPause();
    }

//    //初始化数据，获得应用的token并且保存
//    public void initData() {
//        //判断有没有旧的token，AndroidFileUtil这个工具类在下面的代码中
//        String myToken = AndroidFileUtil.readFileByLines(getCacheDir().getAbsolutePath() + "/" + DataConfig.TOKEN_FILE_NAME);
//        if (!TextUtils.isEmpty(myToken)) {
//            Log.d("WelcomeActivity","Token: "+myToken);
//        } else {
//            APIConfig.getDataIntoView(new Runnable() {
//                @Override
//                public void run() {
//                    String member = "member";
//                    Map<String, String> map = new HashMap<>();
//                    map.put("grantType", member);
//                    map.put("token","");
//                    map.put("appId","");
//                    map.put("appSecret","");
//                //对传入的数据进行加密
//                    String paramJson = EncryptUtil.encrypt(map);
//                //下面的是获取token的服务器地址，项目中应该根据具体的请求地址
//                    String url = "http://47.96.175.241/shop/api/token/refresh.do";
//                    String rs = HttpUtil.GetDataFromNetByPost(url,
//                            new ParamsBuilder().addParam("paramJson", paramJson).getParams());
//                //对数据进行解密到我们的一个保存token的类中（UserToken类）
//                    final UserToken result = EncryptUtil.decrypt(rs, UserToken.class);
//                    if (result != null && result.getResult() == APIConfig.CODE_SUCCESS) {
//                    //保存我们获取的token在文件中，方便下次获取，这个工具也在下面
//                        APIUtil.saveToken(result.getData());
//                    } else {
//                  //下面的是自己写的一个工具类，也可以用Toast弹窗消息
//                        ToastUtil.toastByCode(result);
//                    }
//                }
//            });
//        }
//    }


}