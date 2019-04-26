package com.nju.yanjunjie.readinglaterpushingsystem.user;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.nju.yanjunjie.readinglaterpushingsystem.R;

public class Register extends AppCompatActivity {
    private EditText registerTel;
    private EditText identifyCode;
    private EditText registerPassword;
    private Button getIdentifyCode;
    private Button registerButton;

    private UserDataManager mUserDataManager;         //用户数据管理类
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        registerTel = findViewById(R.id.register_tel);
        registerPassword = findViewById(R.id.register_password);
        identifyCode = findViewById(R.id.enter_verification_code);
        getIdentifyCode = (Button) findViewById(R.id.get_verification_code);
        registerButton = (Button) findViewById(R.id.register_btn);

        getIdentifyCode.setOnClickListener(m_register_Listener);
        registerButton.setOnClickListener(m_register_Listener);

        if (mUserDataManager == null) {
            mUserDataManager = new UserDataManager(this);
            mUserDataManager.openDataBase();                              //建立本地数据库
        }

    }
    View.OnClickListener m_register_Listener = new View.OnClickListener() {    //不同按钮按下的监听事件选择
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.get_verification_code:                       //确认按钮的监听事件
                    register_check();
                    break;
                case R.id.register_btn:                     //取消按钮的监听事件,由注册界面返回登录界面
                    Intent intent_Register_to_Login = new Intent(Register.this,Login.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                    break;
            }
        }
    };
    public void register_check() {                                //确认按钮的监听事件
        if (isUserNameAndPwdValid()) {
            String userName = registerTel.getText().toString().trim();
            String userPwd = registerPassword.getText().toString().trim();
//            String userPwdCheck = mPwdCheck.getText().toString().trim();
            //检查用户是否存在
            int count=mUserDataManager.findUserByName(userName);
            //用户已经存在时返回，给出提示文字
            if(count>0){
                Toast.makeText(this, getString(R.string.name_already_exist, userName),Toast.LENGTH_SHORT).show();
                return ;
            }
//            if(userPwd.equals(userPwdCheck)==false){     //两次密码输入不一样
            if(false){     //两次密码输入不一样
                Toast.makeText(this, getString(R.string.pwd_not_the_same),Toast.LENGTH_SHORT).show();
                return ;
            } else {
                UserData mUser = new UserData(userName, userPwd);
                mUserDataManager.openDataBase();
                long flag = mUserDataManager.insertUserData(mUser); //新建用户信息
                if (flag == -1) {
                    Toast.makeText(this, getString(R.string.register_fail),Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(this, getString(R.string.register_success),Toast.LENGTH_SHORT).show();
                    Intent intent_Register_to_Login = new Intent(Register.this,Login.class) ;    //切换User Activity至Login Activity
                    startActivity(intent_Register_to_Login);
                    finish();
                }
            }
        }
    }
    public boolean isUserNameAndPwdValid() {
//        if (mAccount.getText().toString().trim().equals("")) {
//            Toast.makeText(this, getString(R.string.account_empty),
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        } else if (mPwd.getText().toString().trim().equals("")) {
//            Toast.makeText(this, getString(R.string.pwd_empty),
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        }else if(mPwdCheck.getText().toString().trim().equals("")) {
//            Toast.makeText(this, getString(R.string.pwd_check_empty),
//                    Toast.LENGTH_SHORT).show();
//            return false;
//        }
        return true;
    }
}
