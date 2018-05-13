package com.puyixiaowo.fbook.enums;

public enum Encoding {
    GBK("GBK"),
    UTF8("UTF-8"),
    GB2312("GB2312");

    Encoding(String encoding) {
        this.encoding = encoding;
    }
    public String encoding;
}
