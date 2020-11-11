package com.junipero.capturetheflag;

import java.util.Map;

public class LocalUserMap {
    private Map<String, String> map;

    public LocalUserMap() {}

    public LocalUserMap(Map<String, String> map) {
        this.map = map;
    }

    public Map<String, String> getMap() {
        return map;
    }

    public void setMap(Map<String, String> map) {
        this.map = map;
    }
}
