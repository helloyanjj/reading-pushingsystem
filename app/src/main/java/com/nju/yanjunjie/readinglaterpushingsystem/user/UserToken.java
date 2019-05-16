package com.nju.yanjunjie.readinglaterpushingsystem.user;

/**
 * 打开应用就获取到token
 * Created by acer-pc on 2018/7/26.
 */

public class UserToken extends BaseResult {
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}