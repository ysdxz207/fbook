package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.fbook.bean.UserBean;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.BookInfo;
import com.puyixiaowo.fbook.bean.book.BookReadSettingBean;
import com.puyixiaowo.fbook.bean.book.BookSource;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.constants.BookConstants;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.enums.EnumChannel;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import com.puyixiaowo.fbook.utils.HttpUtils;
import com.puyixiaowo.fbook.utils.StringUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

/**
 * @author Moses
 * @date 2017-12-19
 */
public class BookService {
    private static final Logger logger = LoggerFactory.getLogger(BookService.class);

    public static String getSelectSql(BookBean bookBean,
                                      PageBean pageBean) {

        StringBuilder sbSql = new StringBuilder("select t.* from book t where 1 = 1 ");

        buildSqlParams(sbSql, bookBean);
        sbSql.append("order by t.id desc ");
        sbSql.append("limit ");
        sbSql.append(pageBean.getRowBounds().getOffset());
        sbSql.append(", ");
        sbSql.append(pageBean.getRowBounds().getLimit());
        return sbSql.toString();
    }

    public static PageBean selectBookPageBean(BookBean bookBean, PageBean pageBean) {
        return DBUtils.selectPageBean(getSelectSql(bookBean, pageBean), bookBean, pageBean);
    }

    public static void buildSqlParams(StringBuilder sbSql,
                                      BookBean bookBean) {
        if (bookBean.getName() != null) {
            sbSql.append("and t.name like :name ");
            bookBean.setName("%" + bookBean.getName() + "%");
        }

        if (bookBean.getBookIdThird() != null) {
            sbSql.append("and t.book_id_third = :bookIdThird ");
        }
    }

    public static List<BookBean> getUserBookList(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        List<BookBean> bookBeanList = DBUtils.selectList(BookBean.class,
                "select * from book where id in (" +
                        "  select book_id from bookshelf bs" +
                        "  where bs.user_id= :userId" +
                        ")", params);
        return bookBeanList;
    }

    public static BookBean selectBookBeanById(Long bookId) {
        BookBean bookBean = new BookBean();
        bookBean.setId(bookId);
        return DBUtils.selectOne("select * from book where id=:id", bookBean);
    }

    public static PageBean requestSearchBook(UserBean userBean,
                                             String keywords,
                                             PageBean pageBean) {
        BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userBean.getId());

        EnumChannel channel = EnumChannel.getEnum(bookReadSettingBean.getChannel());

        switch (channel) {
            case boy:
                pageBean = searchBoy(keywords, pageBean);
                break;

            case girl:
                pageBean = searchGirl(keywords, pageBean);
                break;

            default:
                return pageBean;
        }

        return pageBean;
    }

    public static BookBean selectBookBeanByBookIdThird(String bookIdThird) {
        BookBean bookBean = new BookBean();
        bookBean.setBookIdThird(bookIdThird);
        return DBUtils.selectOne("select * from book where book_id_third=:bookIdThird", bookBean);
    }

    public static Date getBookDate(String dateStr) {
        try {
            return DateUtils
                    .parseDate(dateStr,
                            Locale.CHINA,
                            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static BookBean requestBookDetail(UserBean userBean,
                                             BookBean bookBean) {

        BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userBean.getId());

        EnumChannel channel = EnumChannel.getEnum(bookReadSettingBean.getChannel());

        switch (channel) {
            case boy:
                return getBookDetailBoy(bookBean);

            case girl:
                return getBookDetailGirl(bookBean);

            default:
                return null;
        }


    }


    private static BookBean getBookDetailBoy(BookBean bookBean) {
        if (bookBean == null
                || bookBean.getBookIdThird() == null) {
            return null;
        }


        String url = BookConstants.URL_BOOK + bookBean.getBookIdThird();

        JSONObject json = JSON.parseObject(HttpUtils.httpGet(url, null));

        Boolean ok = json.getBoolean("ok");


        if (ok != null && !ok) {
            ok = false;
        } else {
            ok = true;
        }

        if (json == null || !ok) {
            return null;
        }

        String description = json.getString("longIntro");
        String cover = json.getString("cover");
        if (StringUtils.isNotBlank(cover)) {
            try {
                cover = URLDecoder.decode(cover, Constants.ENCODING);
                cover = cover.replace("/agent/", "");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        String rating = json.getString("rating");
        String retentionRatio = json.getString("retentionRatio");//读着留存率
        String updated = json.getString("updated");
        String lastChapter = json.getString("lastChapter");
        String category = json.getString("cat");


        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookId(bookBean.getId());
        bookInfo.setDescription(description);
        bookInfo.setRating(rating);
        bookInfo.setRetentionRatio(retentionRatio);
        bookInfo.setUpdated(getUpdateDateString(updated));
        bookInfo.setCategory(category);
        bookInfo.setLastUpdateChapter(lastChapter);

        bookBean.setName(json.getString("title"));
        bookBean.setAuthor(json.getString("author"));
        bookBean.setFaceUrl(cover);
        bookBean.setBookInfo(bookInfo);
        return bookBean;
    }

    public static BookBean getBookDetailGirl(BookBean bookBean) {

        try {
            String url = PickRulesUtils.pickRulesTemplate.getBookDetailLink(bookBean);
            Connection.Response response = HtmlUtils.getPage(url,
                    PickRulesUtils.pickRulesTemplate.getBookEncoding());

            if (response == null) {
                logger.info("[girl获取书籍信息失败]response为空");
                throw new RuntimeException("[girl获取书籍信息失败]response为空");
            }
            Document document = response.parse();

            String title = PickRulesUtils.pickRulesTemplate.getBookDetailTitle(document);
            String author = PickRulesUtils.pickRulesTemplate.getBookDetailAuthor(document);
            String category = PickRulesUtils.pickRulesTemplate.getBookDetailCategory(document);
            String description = PickRulesUtils.pickRulesTemplate.getBookDetailDescription(document);
            String faceUrl = PickRulesUtils.pickRulesTemplate.getBookDetailFaceUrl(document);
            String updateDate = PickRulesUtils.pickRulesTemplate.getBookDetailUpdateDate(document);
            String updateChapter = PickRulesUtils.pickRulesTemplate.getBookDetailUpdateChapter(document);


            BookInfo bookInfo = new BookInfo();
            bookInfo.setBookId(bookBean.getId());
            bookInfo.setDescription(description);
            bookInfo.setUpdated(updateDate);
            bookInfo.setCategory(category);
            bookInfo.setLastUpdateChapter(updateChapter);

            bookBean.setName(title);
            bookBean.setAuthor(author);
            bookBean.setFaceUrl(faceUrl);
            bookBean.setBookInfo(bookInfo);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookBean;
    }


    private static String getUpdateDateString(String updated) {
        updated = DateFormatUtils
                .format(getBookDate(updated),
                        "yyyy-MM-dd HH:mm:ss");
        return StringUtils.isBlank(updated) ? "" : updated;
    }

    public static List<BookSource> getBookSource(String bookIdThird) {
        List<BookSource> list = new ArrayList<>();

        String url = BookConstants.URL_BOOK_SOURCE + bookIdThird;
        JSONArray json = JSON.parseArray(HttpUtils.httpGet(url, null));

        if (json == null) {
            return list;
        }


        for (Object obj : json) {
            JSONObject jsonObj = (JSONObject) obj;
            BookSource bookSource = jsonObj.toJavaObject(BookSource.class);
            bookSource.setUpdated(getUpdateDateString(bookSource.getUpdated()));

            if (!"zhuishuvip".equals(bookSource.getSource())) {
                list.add(bookSource);
            }
        }

        return list;
    }

    public static BookSource getDefaultSource(Long userId,
                                              String bookIdThird) {
        BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userId);

        //根据频道获取章节信息
        EnumChannel channel = EnumChannel.getEnum(bookReadSettingBean.getChannel());

        switch (channel) {
            case boy:
                return getBookSourceBoy(bookIdThird);

            case girl:
                return getBookSourceGirl();

            default:
        }
        return getBookSourceBoy(bookIdThird);
    }

    private static BookSource getBookSourceGirl() {

        BookSource bookSource = new BookSource();
        return bookSource;
    }

    public static BookSource getBookSourceBoy(String bookIdThird) {
        List<BookSource> bookSourceList = getBookSource(bookIdThird);

        if (bookSourceList.size() == 1) {
            return bookSourceList.get(0);
        }

        return Collections.max(bookSourceList);
    }

    public static BookBean getBookByBookIdThird(String bookIdThird) {

        Map<String, Object> params = new HashMap<>();
        params.put("bookIdThird", bookIdThird);
        return DBUtils.selectOne(BookBean.class, "select * from book where book_id_third=:bookIdThird", params);
    }

    public static PageBean searchBoy(String keywords, PageBean pageBean) {
        List<BookBean> bookBeanList = new ArrayList<>();

        Map<String, String> params = new HashMap<>();
        params.put("query", keywords);
        params.put("start", pageBean.getRowBounds().getOffset() + "");
        params.put("limit", pageBean.getRowBounds().getLimit() + "");
        JSONObject json = JSONObject.parseObject(HttpUtils.httpGet(BookConstants.URL_SEARCH, params));

        if (json == null) {
            pageBean.errorMessage("未从接口获取到结果");
            return pageBean;
        }

        JSONArray books = json.getJSONArray("books");

        for (Object obj : books) {
            JSONObject jsonBook = (JSONObject) obj;
            BookBean bookBean = new BookBean();
            BookInfo bookinfo = new BookInfo();

            String faceUrl = jsonBook.getString("cover");

            if (StringUtils.isNotBlank(faceUrl)) {
                try {
                    faceUrl = URLDecoder.decode(faceUrl
                            .replace("/agent/", ""), Constants.ENCODING);

                    if (faceUrl.lastIndexOf("/") == faceUrl.length() - 1) {
                        faceUrl = faceUrl.substring(0, faceUrl.length() - 1);
                    }

                    if (faceUrl.startsWith("/")) {
                        faceUrl = BookConstants.HOST_API + faceUrl;
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            bookBean.setBookIdThird(jsonBook.getString("_id"));
            bookBean.setAuthor(jsonBook.getString("author"));
            bookBean.setName(jsonBook.getString("title"));
            bookBean.setCreateTime(System.currentTimeMillis());
            bookBean.setFaceUrl(faceUrl);
            bookBean.setOnShelf(false);


            bookinfo.setLastUpdateChapter(jsonBook.getString("lastChapter"));
            bookinfo.setCategory(jsonBook.getString("cat"));
            bookinfo.setDescription(jsonBook.getString("shortIntro"));
            bookinfo.setRetentionRatio(jsonBook.getString("retentionRatio"));
            bookinfo.setBookId(bookBean.getId());
            bookBean.setBookInfo(bookinfo);

            bookBeanList.add(bookBean);
        }
        pageBean.setList(bookBeanList);

        return pageBean;
    }

    public static PageBean searchGirl(String keywords,
                                       PageBean pageBean) {
        List<BookBean> bookBeanList = new ArrayList<>();

        try {
            String url = PickRulesUtils.pickRulesTemplate.getSearchLink(keywords);
            Connection.Response response = HtmlUtils.getPage(url,
                    PickRulesUtils.pickRulesTemplate.getSearchEncoding());

            if (response == null) {
                throw new RuntimeException("搜索接口未响应");
            }
            Document document = response.parse();



            Elements elements = PickRulesUtils.pickRulesTemplate.getSearchItems(document);
            for (Element e :
                    elements) {
                BookBean bookBean = new BookBean();
                BookInfo bookInfo = new BookInfo();

                String bookIdThird = PickRulesUtils.pickRulesTemplate.getSearchItemBookIdThird(e);
                String title = PickRulesUtils.pickRulesTemplate.getSearchItemTitle(e);
                String img = PickRulesUtils.pickRulesTemplate.getSearchItemFaceUrl(e);
                String author = PickRulesUtils.pickRulesTemplate.getSearchItemAuthor(e);
                String category = PickRulesUtils.pickRulesTemplate.getSearchItemCategory(e);
                String updated = PickRulesUtils.pickRulesTemplate.getSearchItemUpdateDate(e);
                String newChapter = PickRulesUtils.pickRulesTemplate.getSearchItemUpdateChapter(e);

                bookBean.setName(title);
                bookBean.setAuthor(author);
                bookBean.setFaceUrl(img);
                bookBean.setBookIdThird(bookIdThird);

                bookInfo.setLastUpdateChapter(newChapter);
                bookInfo.setCategory(category);
                bookInfo.setUpdated(updated);
                bookBean.setBookInfo(bookInfo);

                bookBeanList.add(bookBean);
            }
        } catch (Exception e) {
            logger.info("[获取搜索列表失败][girl]:" + e.getMessage() == null ? JSON.toJSONString(e) : e.getMessage());
            throw new RuntimeException("获取搜索列表失败");
        }

        pageBean.setList(bookBeanList);

        return pageBean;
    }
}
