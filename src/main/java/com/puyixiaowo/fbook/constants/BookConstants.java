package com.puyixiaowo.fbook.constants;


/**
 * 
 * @author Moses
 * @date 2017-12-18
 * 
 */
public class BookConstants {
    public static final String HOST_API = "http://api.zhuishushenqi.com";

    /**
     * 书源信息
     * Id是模糊搜索获取到的_id
     * http://api.zhuishushenqi.com/toc?view=summary&book=57206c3539a913ad65d35c7b
     */
    public static final String URL_BOOK_SOURCE = HOST_API + "/toc?view=summary&book=";
    /**
     * 模糊搜索
     * http://api.zhuishushenqi.com/book/fuzzy-search?query=一念&start=0&limit=2
     */
    public static final String URL_SEARCH = HOST_API + "/book/fuzzy-search";

    /**
     * 书籍详情
     * http://api.zhuishushenqi.com/book/书籍id(_id)
     * http://api.zhuishushenqi.com/book/57206c3539a913ad65d35c7b
     */
    public static final String URL_BOOK = HOST_API + "/book/";

    /**
     * 章节列表
     * http://api.zhuishushenqi.com/toc/577b477dbd86a4bd3f8bf1b2?view=chapters
     */
    public static final String URL_CHAPTERS = HOST_API + "/toc/";

    /**
     * 根据链接获取章节内容，链接需encodeurl
     * http://chapter2.zhuishushenqi.com/chapter/章节link(从章节列表中获得)?k=2124b73d7e2e1945&t=1468223717
     */
    public static final String URL_CHAPTER_CONTENT = "http://chapter2.zhuishushenqi.com/chapter/";

    /**
     * 根据分类获取书籍列表
     * http://api.zhuishushenqi.com/book/by-categories?gender=male&type=hot&major=玄幻&minor=东方玄幻&start=0&limit=20
     */
    public static final String URL_BY_CATEGORIES = HOST_API + "/book/by-categories";
}
