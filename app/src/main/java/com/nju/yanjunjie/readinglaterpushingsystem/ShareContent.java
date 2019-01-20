package com.nju.yanjunjie.readinglaterpushingsystem;

import org.litepal.crud.DataSupport;


public class ShareContent extends DataSupport {
    private long id;
    private String userId;
    private String action;
    private String type;
    private String content;
    private String haveRead;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHaveRead() {
        return haveRead;
    }

    public void setHaveRead(String haveRead) {
        this.haveRead = haveRead;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "ShareContent{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", action='" + action + '\'' +
                ", type='" + type + '\'' +
                ", content='" + content + '\'' +
                ", haveRead=" + haveRead +
                '}';
    }
}
