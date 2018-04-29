package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.BookInfo;
import com.puyixiaowo.fbook.bean.book.BookSource;
import com.puyixiaowo.fbook.bean.book.BookshelfBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.constants.BookConstants;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.HttpUtils;
import com.puyixiaowo.fbook.utils.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.*;

/**
 * @author Moses
 * @date 2017-12-19
 */
public class BookService {

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

        if (bookBean.getaId() != null) {
            sbSql.append("and t.a_id = :aId ");
        }
    }

    public static List<BookBean> getUserBookList(Long userId) {
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);

        List<BookBean> bookBeanList = DBUtils.selectList(BookBean.class,
                "select b.* from bookshelf bs left join book b on b.id = bs.book_id and bs.user_id=:userId", params);
        return bookBeanList;
    }

    public static BookBean selectBookBeanById(Long bookId) {
        BookBean bookBean = new BookBean();
        bookBean.setId(bookId);
        return DBUtils.selectOne("select * from book where id=:id", bookBean);
    }

    public static PageBean requestSearchBook(String name, PageBean pageBean) {


        Map<String, String> params = new HashMap<>();
        params.put("query", name);
        params.put("start", pageBean.getRowBounds().getOffset() + "");
        params.put("limit", pageBean.getRowBounds().getLimit() + "");
        JSONObject json = JSONObject.parseObject(HttpUtils.httpGet(BookConstants.URL_SEARCH, params));

        if (json == null) {
            pageBean.errorMessage("未从接口获取到结果");
            return pageBean;
        }

        JSONArray books = json.getJSONArray("books");
        List<BookBean> bookBeanList = new ArrayList<>();

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
            bookBean.setaId(jsonBook.getString("_id"));
            bookBean.setAuthor(jsonBook.getString("author"));
            bookBean.setName(jsonBook.getString("title"));
            bookBean.setCreateTime(System.currentTimeMillis());
            bookBean.setLastUpdateChapter(jsonBook.getString("lastChapter"));
            bookBean.setFaceUrl(faceUrl);
            bookBean.setOnShelf(false);


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

    public static BookBean selectBookBeanByAId(String aId) {
        BookBean bookBean = new BookBean();
        bookBean.setaId(aId);
        return DBUtils.selectOne("select * from book where a_id=:aId", bookBean);
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


    public static BookBean requestBookDetail(BookBean bookBean) {

        if (bookBean == null
                || bookBean.getaId() == null) {
            return null;
        }


        String url = BookConstants.URL_BOOK + bookBean.getaId();

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

        bookBean.setName(json.getString("title"));
        bookBean.setAuthor(json.getString("author"));
        bookBean.setFaceUrl(cover);
        bookBean.setLastUpdateChapter(lastChapter);
        bookBean.setBookInfo(bookInfo);
        return bookBean;
    }

    private static String getUpdateDateString(String updated) {
            updated = DateFormatUtils
                    .format(getBookDate(updated),
                            "yyyy-MM-dd HH:mm:ss");
        return StringUtils.isBlank(updated) ? "" : updated;
    }

    public static List<BookSource> getBookSource(String aId) {
        List<BookSource> list = new ArrayList<>();

        String url = BookConstants.URL_BOOK_SOURCE + aId;
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

    public static BookSource getDefaultSource(String aId) {
        List<BookSource> bookSourceList = getBookSource(aId);

        if (bookSourceList.size() == 1) {
            return bookSourceList.get(0);
        }

        return Collections.max(bookSourceList);
    }

    public static BookBean getBookByAId(String aId) {

        Map<String, Object> params = new HashMap<>();
        params.put("aId", aId);
        return DBUtils.selectOne(BookBean.class, "select * from book where a_id=:aId", params);
    }
}
