package com.nju.yanjunjie.readinglaterpushingsystem.readlater;

import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.nju.yanjunjie.readinglaterpushingsystem.R;
import com.nju.yanjunjie.readinglaterpushingsystem.data.ReturnInfo;
import com.nju.yanjunjie.readinglaterpushingsystem.data.HttpUtil;
import com.nju.yanjunjie.readinglaterpushingsystem.data.MyApplication;
import com.nju.yanjunjie.readinglaterpushingsystem.data.ShareContent;
import com.suke.widget.SwitchButton;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public class RecyclerItemViewHolder extends RecyclerView.ViewHolder {
    public final TextView mItemTextView;
    private final View shareContentView;
    private SwitchButton isRead;
    private Handler handler;

    public RecyclerItemViewHolder(final View parent) {
        super(parent);
        mItemTextView = (TextView) parent.findViewById(R.id.itemTextView);
        isRead = (SwitchButton) parent.findViewById(R.id.isRead);
        shareContentView = parent;
    }

    public void setItemText(CharSequence text) {
        mItemTextView.setText(text);
    }

    public void isReadOnClick(final ShareContent shareContent) {
        String haveRead = shareContent.getHaveRead();
        boolean read;
        if (haveRead.equals("true")) {
            read = true;
        } else read = false;
        isRead.setChecked(read);
        isRead.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {
                changeReadStatus(isChecked, shareContent);
            }
        });
    }

    public void changeReadStatus(final boolean changeStatus, final ShareContent shareContent) {
        final String readStatus;
        if (changeStatus == true) {
            readStatus = "true";
        } else readStatus = "false";
        shareContent.setHaveRead(readStatus);
        HttpUtil.sendOkHttpRequest(shareContent, ReturnInfo.address+":2221/changeReadStatus", new okhttp3.Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
                Message message = new Message();
                message.obj = responseData;
                message.what = 2;
                handler.sendMessage(message);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

        });

        handler = new Handler() {
            public void handleMessage(Message message) {
                if (message.what == 2) {
                    Toast.makeText(MyApplication.getContext(), "更改状态", Toast.LENGTH_SHORT).show();
                }
            }
        };

    }

}