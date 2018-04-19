package com.puyixiaowo.fbook.enums;

public enum  EnumsRedisKey {
    REDIS_KEY_IGNORE_CONF_BOOK("REDIS_KEY_IGNORE_CONF_BOOK_", "book忽略路径配置");


    EnumsRedisKey(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public String key;
    public String description;

}
