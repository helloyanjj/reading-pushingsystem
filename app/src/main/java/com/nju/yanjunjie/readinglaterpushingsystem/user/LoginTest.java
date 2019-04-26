//package com.nju.yanjunjie.readinglaterpushingsystem.user;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.EditText;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.nju.yanjunjie.readinglaterpushingsystem.R;
//
//public class LoginTest extends Activity {                 //登录界面活动
//
//    public int pwdresetFlag=0;
//    private EditText loginTel;                        //登录手机号
//    private EditText loginIdentifyCode;               //登录验证码
//    private Button getIdentifyCode;                   //获取验证码按钮
//    private Button loginButtonWithIdC;                //登录按钮
//    private Button loginWithPwd;                      //跳转密码登录
//    private Button register;                          //注册
//    private TimeCount identifyCodeTime;
//
//
//    private EditText loginPwd;
//    private EditText findPwd;
//
//    private Button mCancleButton;                     //注销按钮
//    private CheckBox mRememberCheck;
//
//    private SharedPreferences login_sp;
//    private String userNameValue,passwordValue;
//
//    private View loginView;                           //登录
//    private View loginSuccessView;
//    private TextView loginSuccessShow;
//    private TextView mChangepwdText;
//    private UserDataManager mUserDataManager;         //用户数据管理类
//
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.login);
//
//        loginTel = (EditText) findViewById(R.id.login_tel_account);
//        loginIdentifyCode = (EditText) findViewById(R.id.login_verification_code);
//        getIdentifyCode = (Button) findViewById(R.id.get_verification_code);
//        loginButtonWithIdC = (Button) findViewById(R.id.login_with_verification_code);
//        loginWithPwd = findViewById(R.id.login_with_password);
//        register = findViewById(R.id.Register);
//
//
//        mCancleButton = (Button) findViewById(R.id.login_btn_cancle);
//        loginView=findViewById(R.id.login_view);
//        loginSuccessView=findViewById(R.id.login_success_view);
//        loginSuccessShow=(TextView) findViewById(R.id.login_success_show);
//
//        mChangepwdText = (TextView) findViewById(R.id.login_text_change_pwd);
//
//        mRememberCheck = (CheckBox) findViewById(R.id.Login_Remember);
//
//        login_sp = getSharedPreferences("userInfo", 0);
//        String name=login_sp.getString("USER_NAME", "");
//        String pwd =login_sp.getString("PASSWORD", "");
//        boolean choseRemember =login_sp.getBoolean("mRememberCheck", false);
//        boolean choseAutoLogin =login_sp.getBoolean("mAutologinCheck", false);
//        //如果上次选了记住密码，那进入登录页面也自动勾选记住密码，并填上用户名和密码
//        if(choseRemember){
//            mAccount.setText(name);
//            mPwd.setText(pwd);
//            mRememberCheck.setChecked(true);
//        }
//
//        getIdentifyCode.setOnClickListener(mListener);
//
//        mRegisterButton.setOnClickListener(mListener);                      //采用OnClickListener方法设置不同按钮按下之后的监听事件
//        mLoginButton.setOnClickListener(mListener);
//        mCancleButton.setOnClickListener(mListener);
//        mChangepwdText.setOnClickListener(mListener);
//
//        ImageView image = (ImageView) findViewById(R.id.logo);             //使用ImageView显示logo
//        image.setImageResource(R.drawable.logo);
//
//        if (mUserDataManager == null) {
//            mUserDataManager = new UserDataManager(this);
//            mUserDataManager.openDataBase();                              //建立本地数据库
//        }
//    }
//
//    OnClickListener mListener = new OnClickListener() {                  //不同按钮按下的监听事件选择
//        public void onClick(View v) {
//            switch (v.getId()) {
//                case R.id.get_verification_code:
//                    identifyCodeTime.start();
//                    break;
//                case R.id.login_btn_register:                            //登录界面的注册按钮
//                    Intent intent_Login_to_Register = new Intent(LoginTest.this,Register.class) ;    //切换Login Activity至User Activity
//                    startActivity(intent_Login_to_Register);
//                    finish();
//                    break;
//                case R.id.login_btn_login:                              //登录界面的登录按钮
//                    login();
//                    break;
//                case R.id.login_btn_cancle:                             //登录界面的注销按钮
//                    cancel();
//                    break;
//                case R.id.login_text_change_pwd:                             //登录界面的注销按钮
//                    Intent intent_Login_to_reset = new Intent(LoginTest.this,Resetpwd.class) ;    //切换Login Activity至User Activity
//                    startActivity(intent_Login_to_reset);
//                    finish();
//                    break;
//            }
//        }
//    };
//
//    class TimeCount extends CountDownTimer {
//
//        public TimeCount(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//            getIdentifyCode.setBackgroundColor(Color.parseColor("#B6B6D8"));
//            getIdentifyCode.setClickable(false);
//            getIdentifyCode.setText("("+millisUntilFinished / 1000 +") 秒后可重新发送");
//        }
//
//        @Override
//        public void onFinish() {
//            getIdentifyCode.setText("重新获取验证码");
//            getIdentifyCode.setClickable(true);
//            getIdentifyCode.setBackgroundColor(Color.parseColor("#4EB84A"));
//        }
//    }
//
//
//    public void login(View view) {                                              //登录按钮监听事件
//        if (isUserNameAndPwdValid()) {
//            String userName = loginTel.getText().toString().trim();    //获取当前输入的手机和密码信息
//            String userPwd = getIdentifyCode.getText().toString().trim();
//            SharedPreferences.Editor editor =login_sp.edit();
//            int result=mUserDataManager.findUserByNameAndPwd(userName, userPwd);
//            if(result==1){                                             //返回1说明用户名和密码均正确
//                //保存用户名和密码
//                editor.putString("USER_NAME", userName);
//                editor.putString("PASSWORD", userPwd);
//
//                //是否记住密码
//                if(mRememberCheck.isChecked()){
//                    editor.putBoolean("mRememberCheck", true);
//                }else{
//                    editor.putBoolean("mRememberCheck", false);
//                }
//                editor.commit();
//
//                Intent intent = new Intent(LoginTest.this,User.class) ;    //切换Login Activity至User Activity
//                startActivity(intent);
//                finish();
//                Toast.makeText(this, getString(R.string.login_success),Toast.LENGTH_SHORT).show();//登录成功提示
//            }else if(result==0){
//                Toast.makeText(this, getString(R.string.login_fail),Toast.LENGTH_SHORT).show();  //登录失败提示
//            }
//        }
//    }
//
//    public void cancel() {           //注销
//        if (isUserNameAndPwdValid()) {
//            String userName = mAccount.getText().toString().trim();    //获取当前输入的用户名和密码信息
//            String userPwd = mPwd.getText().toString().trim();
//            int result=mUserDataManager.findUserByNameAndPwd(userName, userPwd);
//            if(result==1){                                             //返回1说明用户名和密码均正确
//                Toast.makeText(this, getString(R.string.cancel_success),Toast.LENGTH_SHORT).show();
////                <span style="font-family: Arial;">//注销成功提示</span>
//                mPwd.setText("");
//                mAccount.setText("");
//                mUserDataManager.deleteUserDatabyname(userName);
//            }else if(result==0){
//                Toast.makeText(this, getString(R.string.cancel_fail),Toast.LENGTH_SHORT).show();  //注销失败提示
//            }
//        }
//
//    }
//
//    public boolean isUserNameAndPwdValid() {
//        if (mAccount.getText().toString().trim().equals("")) {
//            Toast.makeText(this, getString(R.string.account_empty),
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (mPwd.getText().toString().trim().equals("")) {
//            Toast.makeText(this, getString(R.string.pwd_empty),
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        }
//        return true;
//    }
//
//    @Override
//    protected void onResume() {
//        if (mUserDataManager == null) {
//            mUserDataManager = new UserDataManager(this);
//            mUserDataManager.openDataBase();
//        }
//        super.onResume();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//    }
//
//    @Override
//    protected void onPause() {
//        if (mUserDataManager != null) {
//            mUserDataManager.closeDataBase();
//            mUserDataManager = null;
//        }
//        super.onPause();
//    }
//}