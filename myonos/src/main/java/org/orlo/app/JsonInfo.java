package org.orlo.app;

import java.io.Serializable;
import java.util.Arrays;

public class JsonInfo implements Serializable {
    private  String[] specifier = new String[5];
    private  String[] stats = new  String[8];

    public JsonInfo() {
    }

    public JsonInfo(String[] specifier, String[] status) {
        this.specifier = specifier;
        this.stats = status;
    }

    public  void setSpecifier(String[] specifier) {
        this.specifier = specifier;
    }

    public  void setStats(String[] status) {
        this.stats = status;
    }

    public  String[] getSpecifier() {
        return specifier;
    }

    public  String[] getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return "JsonInfo{" +
                "specifier=" + Arrays.toString(specifier) +
                ", stats=" + Arrays.toString(stats) +
                '}';
    }
}
