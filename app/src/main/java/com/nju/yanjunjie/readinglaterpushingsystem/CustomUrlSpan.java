package com.nju.yanjunjie.readinglaterpushingsystem;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;

public class CustomUrlSpan extends UnderlineSpan implements Parcelable {


    String url;

    public CustomUrlSpan(String url) {
        this.url = url;

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        if (ds != null) {
            ds.setUnderlineText(false);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.url);
    }

    protected CustomUrlSpan(Parcel in) {
        super(in);
        this.url = in.readString();
    }

    public static final Creator<CustomUrlSpan> CREATOR = new Parcelable.Creator<CustomUrlSpan>() {
        public CustomUrlSpan createFromParcel(Parcel source) {
            return new CustomUrlSpan(source);
        }

        public CustomUrlSpan[] newArray(int size) {
            return new CustomUrlSpan[size];
        }
    };
}
