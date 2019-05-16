package com.nju.yanjunjie.readinglaterpushingsystem.user;

import android.content.Intent;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nju.yanjunjie.readinglaterpushingsystem.R;
import com.nju.yanjunjie.readinglaterpushingsystem.data.HttpUtil;
import com.nju.yanjunjie.readinglaterpushingsystem.data.ReturnInfo;

import java.io.IOException;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import okhttp3.Call;
import okhttp3.Response;

public class Register extends AppCompatActivity {
    private EditText registerTel;
    private EditText identifyCode;
    private EditText registerPassword;
    private Button getIdentifyCode;
    private Button registerButton;
    private User user = new User();

    private TimeCount identifyCodeTime = new TimeCount(3000, 1000);

    //    private UserDataManager mUserDataManager;         //用户数据管理类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        // 注册一个事件回调，用于处理SMSSDK接口请求的结果
        SMSSDK.registerEventHandler(eventHandler);

        registerTel = findViewById(R.id.register_tel);
        registerPassword = findViewById(R.id.register_password);
        identifyCode = findViewById(R.id.enter_verification_code);
        getIdentifyCode = (Button) findViewById(R.id.get_verification_code);
        registerButton = (Button) findViewById(R.id.register_btn);

        getIdentifyCode.setOnClickListener(m_register_Listener);
        registerButton.setOnClickListener(m_register_Listener);

//        if (mUserDataManager == null) {
//            mUserDataManager = new UserDataManager(this);
//            mUserDataManager.openDataBase();                              //建立本地数据库
//        }
    }

    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.get_verification_code:                       //确认按钮的监听事件
                    if (isTelValid()) {
                        SMSSDK.getVerificationCode("86", registerTel.getText().toString().trim());
                        identifyCodeTime.start();
                    }
                    break;
                case R.id.register_btn:                     //取消按钮的监听事件,由注册界面返回登录界面
                    register_check();
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

    public void register_check() {                                //确认按钮的监听事件
        if (isTelAndCodeAndPwdValid()) {
            String userId = registerTel.getText().toString().trim();
            String userPwd = registerPassword.getText().toString().trim();
            user.setUserId(userId);
            user.setPassword(userPwd);

            HttpUtil.sendOkHttpRequest(user, ReturnInfo.address + ":2221/validateUser", new okhttp3.Callback() {
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseData = response.body().string();
                    if (responseData.equals("true")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(Register.this, "您的手机号已经注册",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else if (responseData.equals("false")) {
                        // 提交验证码，其中的code表示验证码，如“1357”
                        SMSSDK.submitVerificationCode("86", registerTel.getText().toString(), identifyCode.getText().toString());
                    }

                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String fail = "连接失败";
                            Toast.makeText(Register.this, fail,
                                    Toast.LENGTH_LONG).show();
                            Log.d("MainActivity", fail);
                        }
                    });
                }
            });
        }
    }

    public boolean isTelValid() {
        if (registerTel.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.tel_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    public boolean isTelAndCodeAndPwdValid() {
        if (registerTel.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.tel_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (identifyCode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.identifyCode_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (registerPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
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
                            Toast.makeText(Register.this, "验证码发送成功", Toast.LENGTH_SHORT).show();
                        } else {
                            // TODO 处理错误的结果
                            ((Throwable) data).printStackTrace();
                            Toast.makeText(Register.this, "验证码发送失败", Toast.LENGTH_SHORT).show();
                        }
                    } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            // TODO 处理验证码验证通过的结果
                            HttpUtil.sendOkHttpRequest(user, ReturnInfo.address + ":2221/registerUser", new okhttp3.Callback() {
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    String responseData = response.body().string();
                                    if(responseData.equals("true")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String success = "注册成功";
                                                Toast.makeText(Register.this, success,
                                                        Toast.LENGTH_LONG).show();
                                                Log.d("Register", success);
                                            }
                                        });
                                    }else if (responseData.equals("false")) {
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String fail = "注册失败";
                                                Toast.makeText(Register.this, fail,
                                                        Toast.LENGTH_LONG).show();
                                                Log.d("Register", fail);
                                            }
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call call, IOException e) {
                                    e.printStackTrace();
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            String fail = "网络连接失败";
                                            Toast.makeText(Register.this, fail,
                                                    Toast.LENGTH_LONG).show();
                                            Log.d("MainActivity", fail);
                                        }
                                    });
                                }
                            });
                            Intent loginActivity = new Intent(Register.this, Login.class);
                            startActivity(loginActivity);
                        } else {
                            // TODO 处理错误的结果
                            ((Throwable) data).printStackTrace();
                            Toast.makeText(Register.this, "验证码错误", Toast.LENGTH_SHORT).show();
                        }
                    }
                    // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
                    return false;
                }
            }).sendMessage(msg);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(eventHandler);
    }
}
