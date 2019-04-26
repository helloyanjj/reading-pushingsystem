package com.nju.yanjunjie.readinglaterpushingsystem.user;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.nju.yanjunjie.readinglaterpushingsystem.R;

public class LoginWithPassword extends Activity {                 //登录界面活动

    public int pwdresetFlag=0;
    private EditText loginTel;                        //登录手机号
    private EditText loginPassword;               //登录密码
    private EditText findPassword;
    private Button loginButton;                //登录按钮
    private Button loginWithIdentifyCode;                      //跳转验证码登录
    private Button registerButton;                          //注册


    private SharedPreferences login_sp;
    private String userNameValue,passwordValue;
    private UserDataManager mUserDataManager;         //用户数据管理类


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_password);

        loginTel = (EditText) findViewById(R.id.login_tel);
        loginPassword = (EditText) findViewById(R.id.login_password);
        findPassword = findViewById(R.id.find_password);
        loginButton = findViewById(R.id.login_btn);
        loginWithIdentifyCode = findViewById(R.id.login_with_verification_code);
        registerButton = findViewById(R.id.register_btn);

//        ImageView image = (ImageView) findViewById(R.id.logo);             //使用ImageView显示logo
//        image.setImageResource(R.drawable.logo);


        login_sp = getSharedPreferences("userInfo", 0);
        String name=login_sp.getString("USER_NAME", "");
        String pwd =login_sp.getString("PASSWORD", "");
        boolean choseRemember =login_sp.getBoolean("mRememberCheck", false);
        boolean choseAutoLogin =login_sp.getBoolean("mAutologinCheck", false);


        findPassword.setOnClickListener(mListener);
        loginButton.setOnClickListener(mListener);
        loginWithIdentifyCode.setOnClickListener(mListener);
        registerButton.setOnClickListener(mListener);

        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();                              //建立本地数据库
        }
    }

    OnClickListener mListener = new OnClickListener() {                  //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.find_password:
                    Toast.makeText(LoginWithPassword.this, "sd",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.login_btn:                            //登录界面的注册按钮

                    break;
                case R.id.login_with_verification_code:                              //登录界面的登录按钮
                    Intent intent_login_with_verification_code = new Intent(LoginWithPassword.this,Login.class) ;    //切换Login Activity至User Activity
                    startActivity(intent_login_with_verification_code);
//                    finish();
                    break;
                case R.id.register_btn:                             //登录界面的注销按钮
                    Intent intent_register = new Intent(LoginWithPassword.this,Register.class) ;    //切换Login Activity至User Activity
                    startActivity(intent_register);
//                    finish();
                    break;
            }
        }
    };

    public void login(View view) {                                              //登录按钮监听事件
        if (isUserNameAndPwdValid()) {
            String userName = loginTel.getText().toString().trim();    //获取当前输入的手机和密码信息
            String userPwd = loginPassword.getText().toString().trim();
            SharedPreferences.Editor editor =login_sp.edit();
            int result=mUserDataManager.findUserByNameAndPwd(userName, userPwd);
            if(result==1){                                             //返回1说明用户名和密码均正确
                //保存用户名和密码
                editor.putString("USER_NAME", userName);
                editor.putString("PASSWORD", userPwd);

//                //是否记住密码
//                if(mRememberCheck.isChecked()){
//                    editor.putBoolean("mRememberCheck", true);
//                }else{
//                    editor.putBoolean("mRememberCheck", false);
//                }
                editor.commit();

                Intent intent = new Intent(LoginWithPassword.this,User.class) ;    //切换Login Activity至User Activity
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
            Toast.makeText(this, getString(R.string.account_empty),
                    Toast.LENGTH_SHORT).show();
            return false;
        } else if (loginPassword.getText().toString().trim().equals("")) {
            Toast.makeText(this, getString(R.string.pwd_empty),
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