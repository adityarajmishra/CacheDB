package com.cachedb.core;

import java.io.Serializable;

public class CacheEntry implements Serializable {
    private final String username;
    private final String userdata;
    private final long expirationTime;

    public CacheEntry(String username, String userdata, long ttl) {
        this.username = username;
        this.userdata = userdata;
        this.expirationTime = System.currentTimeMillis() + ttl * 1000;
    }

    public String getUsername() {
        return username;
    }

    public String getUserdata() {
        return userdata;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() > expirationTime;
    }

    public boolean isExpired(long now) {
        return now > expirationTime;
    }
}