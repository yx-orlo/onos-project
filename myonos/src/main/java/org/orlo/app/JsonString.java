package org.orlo.app;

import java.io.Serializable;
import java.util.HashMap;


public class JsonString implements Serializable {
    private  HashMap<String, String> jsonStringMap = new HashMap<String, String>();
    public boolean flag;

    public JsonString() {
    }

    public  void setJsonStringByKey(String key, String value) {
        this.jsonStringMap.put(key, value);
    }

    public  String getJsonStringByKey(String key) {
        return this.jsonStringMap.get(key);
    }
}
