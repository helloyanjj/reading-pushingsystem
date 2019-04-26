package com.nju.yanjunjie.readinglaterpushingsystem.user;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nju.yanjunjie.readinglaterpushingsystem.R;

public class Login extends Activity {                 //登录界面活动

    public int pwdresetFlag=0;
    private EditText loginTel;                        //登录手机号
    private EditText loginIdentifyCode;               //登录验证码
    private Button getIdentifyCode;                   //获取验证码按钮
    private Button loginButton;                       //登录按钮
    private Button loginWithPwd;                      //跳转密码登录
    private Button registerButton;                    //注册

    private TimeCount identifyCodeTime;
    private UserDataManager mUserDataManager;         //用户数据管理类

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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
                    identifyCodeTime.start();
                    break;
                case R.id.login_with_password:
                    Intent intent_login_with_password = new Intent(Login.this,LoginWithPassword.class);
                    startActivity(intent_login_with_password);
//                    finish();
                    break;
                case R.id.register_btn:
                    Intent intent_register = new Intent(Login.this,Register.class);
                    startActivity(intent_register);
//                    finish();
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
            getIdentifyCode.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
        }

        @Override
        public void onFinish() {
            getIdentifyCode.setText("重新获取验证码");
            getIdentifyCode.setClickable(true);
            getIdentifyCode.setBackgroundColor(Color.parseColor("#4EB84A"));
        }
    }


    public void login(View view) {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            String userId = loginTel.getText().toString().trim();    //获取当前输入的手机和密码信息
            String userPwd = getIdentifyCode.getText().toString().trim();
//            SharedPreferences.Editor editor =login_sp.edit();
            int result=mUserDataManager.findUserByNameAndPwd(userId, userPwd);
            if(result==1){                                             //返回1说明用户名和密码均正确
                //保存用户名和密码
//                editor.putString("USER_NAME", userName);
//                editor.putString("PASSWORD", userPwd);
//                editor.commit();

                Intent intent = new Intent(Login.this,User.class) ;    //切换Login Activity至User Activity
                startActivity(intent);
                finish();
                Toast.makeText(this, getString(R.string.login_success),Toast.LENGTH_SHORT).show();//登录成功提示
            }else if(result==0){
                Toast.makeText(this, getString(R.string.login_fail),Toast.LENGTH_SHORT).show();  //登录失败提示
            }
        }
    }

    public boolean isUserNameAndPwdValid() {
        if (loginTel.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.tel_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (getIdentifyCode.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.identifyCode_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if (mUserDataManager != null) {
            mUserDataManager.closeDataBase();
            mUserDataManager = null;
        }
        super.onPause();
    }
}