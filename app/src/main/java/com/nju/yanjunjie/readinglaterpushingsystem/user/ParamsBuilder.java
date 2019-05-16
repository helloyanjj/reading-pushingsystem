package com.nju.yanjunjie.readinglaterpushingsystem.user;

import java.util.HashMap;

public class ParamsBuilder {

    private HashMap<String, String> params;

    public ParamsBuilder() {

    }

    public ParamsBuilder addParam(String key, String value) {
        if (params == null) {
            params = new HashMap<>();
        }
        params.put(key, value);
        return this;
    }

    public HashMap<String, String> getParams() {
        return params;
    }
}