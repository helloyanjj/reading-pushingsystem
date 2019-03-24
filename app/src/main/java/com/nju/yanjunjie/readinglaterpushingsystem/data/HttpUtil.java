package com.nju.yanjunjie.readinglaterpushingsystem.data;

import com.google.gson.Gson;
import com.nju.yanjunjie.readinglaterpushingsystem.data.AppStatus;
import com.nju.yanjunjie.readinglaterpushingsystem.data.ShareContent;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {

    public static Response sendHttpRequest(ShareContent shareContent, String address) {
        Response response = null;
        OkHttpClient client = new OkHttpClient();
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String content = new Gson().toJson(shareContent);
        RequestBody requestBody = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;

    }

    public static void sendOkHttpRequest(ShareContent shareContent, String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String content = new Gson().toJson(shareContent);
        RequestBody requestBody = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);

    }

    public static void sendOkHttpRequest(AppStatus appStatus, String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String content = new Gson().toJson(appStatus);
        RequestBody requestBody = RequestBody.create(JSON, content);
        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);

    }

    public static void sendOkHttpRequest(List<AppStatus> appStatus, String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        System.out.println(appStatus);
        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String content = new Gson().toJson(appStatus);
        RequestBody requestBody = RequestBody.create(JSON, content);

        Request request = new Request.Builder()
                .url(address)
                .post(requestBody)
                .build();
        client.newCall(request).enqueue(callback);

    }


}
