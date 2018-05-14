package com.puyixiaowo.fbook.enums;
/**
 *
 * @author Moses.wei
 * @date 2018-05-13 14:05:38
 * 女频源
 */
public enum EnumSourceGirl {
    GEGE("格格党",
            "http://www.ggdown.com/",
            "10855655014424708676",
            "http://zhannei.baidu.com/cse/search?p=0&area=1&s={s}&q={q}",
            Encoding.GBK),
    LWXSW("乐文小说网",
            "http://www.lwxsw.cc/",
            "6939410700241642371",
            "http://zhannei.baidu.com/cse/search?p=0&area=1&s={s}&q={q}",
            Encoding.GBK);

    EnumSourceGirl(String name,
                   String link,
                   String sourceId,
                   String searchLink,
                   Encoding encoding) {
        this.name = name;
        this.link = link;
        this.sourceId = sourceId;
        this.searchLink = searchLink;
        this.encoding = encoding;
    }

    public String name;
    public String link;
    public String sourceId;
    public String searchLink;
    public Encoding encoding;
}
