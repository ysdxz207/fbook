package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.fbook.bean.UserBean;
import com.puyixiaowo.fbook.bean.book.*;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.constants.BookConstants;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import com.puyixiaowo.fbook.utils.HttpUtils;
import com.puyixiaowo.fbook.utils.StringUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import com.puyixiaowo.generator.Run;
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

        if (bookReadSettingBean.getUseApi()) {
            pageBean = searchByApi(keywords, pageBean);
        } else {
            pageBean = searchByPick(keywords, pageBean, bookReadSettingBean.getSearchSource());
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
        BookBean bookBeanDB = BookService.selectBookBeanByBookIdThird(bookBean.getBookIdThird());

        if (bookBeanDB != null) {
            bookBean.setId(bookBeanDB.getId());
            bookBean.setUseApi(bookBeanDB.getUseApi());
        }
        BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userBean.getId());


        if (bookBeanDB != null
                && !bookReadSettingBean.getUseApi()
                .equals(bookBean.getUseApi())) {
            String msg = bookBean.getUseApi() ? "[接口书籍]请将设置中使用接口源打开" : "[非接口书籍]请将设置中使用接口源关闭";
            throw new RuntimeException(msg);
        }

        if (bookReadSettingBean.getUseApi()) {
            return getBookDetailByApi(bookBean);
        } else {
            BookReadBean bookReadBean = BookReadService.getUserBookRead(userBean.getId(), bookBean.getId());
            //第一次因为没读过书，从配置中读取source
            String source = bookReadBean.getSource() == null ? bookReadSettingBean.getSearchSource() : bookReadBean.getSource();
            return getBookDetailByPick(bookBean, source);
        }

    }


    private static BookBean getBookDetailByApi(BookBean bookBean) {
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
        bookBean.setUseApi(true);
        return bookBean;
    }

    public static BookBean getBookDetailByPick(BookBean bookBean,
                                               String source) {

        try {
            String url = PickRulesUtils.getPickRulesTemplate(source).getBookDetailLink(bookBean);
            Connection.Response response = HtmlUtils.getPage(url,
                    PickRulesUtils.getPickRulesTemplate(source).getBookEncoding());

            if (response == null) {
                logger.info("[pick获取书籍信息失败]response为空");
                throw new RuntimeException("[pick获取书籍信息失败]response为空");
            }
            Document document = response.parse();

            String title = PickRulesUtils.getPickRulesTemplate(source).getBookDetailTitle(document);
            String author = PickRulesUtils.getPickRulesTemplate(source).getBookDetailAuthor(document);
            String category = PickRulesUtils.getPickRulesTemplate(source).getBookDetailCategory(document);
            String description = PickRulesUtils.getPickRulesTemplate(source).getBookDetailDescription(document);
            String faceUrl = PickRulesUtils.getPickRulesTemplate(source).getBookDetailFaceUrl(document);
            String updateDate = PickRulesUtils.getPickRulesTemplate(source).getBookDetailUpdateDate(document);
            String updateChapter = PickRulesUtils.getPickRulesTemplate(source).getBookDetailUpdateChapter(document);


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
            bookBean.setUseApi(false);

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

        if (bookReadSettingBean.getUseApi()) {
            return getBookSourceByApi(bookIdThird);
        } else {
            return getBookSourceByPick();
        }
    }

    private static BookSource getBookSourceByPick() {

        BookSource bookSource = new BookSource();
        return bookSource;
    }

    public static BookSource getBookSourceByApi(String bookIdThird) {
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

    public static PageBean searchByApi(String keywords, PageBean pageBean) {
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

    public static PageBean searchByPick(String keywords,
                                        PageBean pageBean,
                                        String source) {
        List<BookBean> bookBeanList = new ArrayList<>();

        try {
            String url = PickRulesUtils.getPickRulesTemplate(source).getSearchLink(keywords);
            JSONObject params = PickRulesUtils.getPickRulesTemplate(source).getSearchParams(keywords);
            String method = PickRulesUtils.getPickRulesTemplate(source).getSearchMethod();
            Connection.Response response;

            switch (method) {
                default:
                case "GET":
                    response = HtmlUtils.getPage(url,
                            PickRulesUtils.getPickRulesTemplate(source).getSearchEncoding());
                    break;
                case "POST":
                    response = HtmlUtils.postPage(url, params, PickRulesUtils.getPickRulesTemplate(source).getSearchEncoding());
                    break;
            }

            if (response == null) {
                throw new RuntimeException("搜索接口未响应");
            }
            Document document = response.parse();


            Elements elements = PickRulesUtils.getPickRulesTemplate(source).getSearchItems(document);
            for (Element e :
                    elements) {
                BookBean bookBean = new BookBean();
                BookInfo bookInfo = new BookInfo();

                String bookIdThird = PickRulesUtils.getPickRulesTemplate(source).getSearchItemBookIdThird(e);
                String title = PickRulesUtils.getPickRulesTemplate(source).getSearchItemTitle(e);
                String img = PickRulesUtils.getPickRulesTemplate(source).getSearchItemFaceUrl(e);
                String author = PickRulesUtils.getPickRulesTemplate(source).getSearchItemAuthor(e);
                String category = PickRulesUtils.getPickRulesTemplate(source).getSearchItemCategory(e);
                String updated = PickRulesUtils.getPickRulesTemplate(source).getSearchItemUpdateDate(e);
                String newChapter = PickRulesUtils.getPickRulesTemplate(source).getSearchItemUpdateChapter(e);

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
            logger.info("[获取搜索列表失败][pick]:" + e.getMessage() == null ? JSON.toJSONString(e) : e.getMessage());
            throw new RuntimeException("获取搜索列表失败");
        }

        pageBean.setList(bookBeanList);

        return pageBean;
    }
}
