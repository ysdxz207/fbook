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
import com.puyixiaowo.fbook.utils.StringUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesTemplate;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import com.puyixiaowo.fbook.utils.pickrules.impl.LwxswPickRulesTemplateImpl;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
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

        Document document = HtmlUtils.getPage(url);
        JSONObject json = JSON.parseObject(document.text());

        if (json == null) {
            logger.error("[" + bookBean.getName() + "][内容接口返回为空]bookId=" + bookBean.getId());
            return null;
        }
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
            PickRulesTemplate pickRulesTemplate = PickRulesUtils.getPickRulesTemplate(source);

            String url = pickRulesTemplate.getBookDetailLink(bookBean);
            Document document = HtmlUtils.getPage(url);

            if (document == null) {
                logger.info("[pick获取书籍信息失败]response为空");
                throw new RuntimeException("[pick获取书籍信息失败]response为空");
            }

            String title = pickRulesTemplate.getBookDetailTitle(document);
            String author = pickRulesTemplate.getBookDetailAuthor(document);
            String category = pickRulesTemplate.getBookDetailCategory(document);
            String description = pickRulesTemplate.getBookDetailDescription(document);
            String faceUrl = pickRulesTemplate.getBookDetailFaceUrl(document);
            String updateDate = pickRulesTemplate.getBookDetailUpdateDate(document);
            String updateChapter = pickRulesTemplate.getBookDetailUpdateChapter(document);


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

    public static List<BookSource> getBookSourceByPick(Long userId) {
        List<BookSource> list = new ArrayList<>();
        BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userId);

        Class [] classes = Constants.BOOK_SOURCE_PICK;

        for (Class clazz : classes) {
            BookSource bookSource = new BookSource();
            PickRulesTemplate template = PickRulesUtils.getPickRulesTemplate(clazz);

            bookSource.setName(template.getName());
            bookSource.setSource(clazz.getName());
            bookSource.setCurrentSource(bookSource.getSource().equalsIgnoreCase(bookReadSettingBean.getSearchSource()));
            list.add(bookSource);
        }

        return list;
    }

    public static List<BookSource> getBookSourceByApi(Long userId,
                                                       String bookIdThird) {
        List<BookSource> list = new ArrayList<>();

        String url = BookConstants.URL_BOOK_SOURCE + bookIdThird;
        Document document = HtmlUtils.getPage(url);
        JSONArray json = JSON.parseArray(document.text());
        if (json == null) {
            return list;
        }


        //查询bookRead获取当前书源
        BookBean bookBean = BookService.selectBookBeanByBookIdThird(bookIdThird);
        BookReadBean bookReadBean = BookReadService.getUserBookRead(userId, bookBean.getId());

        for (Object obj : json) {
            JSONObject jsonObj = (JSONObject) obj;
            BookSource bookSource = jsonObj.toJavaObject(BookSource.class);

            if (!"zhuishuvip".equals(bookSource.getSource())) {
                bookSource.setUpdated(getUpdateDateString(bookSource.getUpdated()));
                if (bookReadBean != null
                        && bookSource.getSource().equals(bookReadBean.getSource())) {
                    bookSource.setCurrentSource(true);
                }
                list.add(bookSource);
            }
        }
        return list;
    }

    public static BookSource getDefaultSource(Long userId,
                                              String bookIdThird) {
        BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userId);

        if (bookReadSettingBean.getUseApi()) {
            return getDefaultBookSourceByApi(userId, bookIdThird);
        } else {
            return getDefaultBookSourceByPick();
        }
    }

    private static BookSource getDefaultBookSourceByPick() {

        BookSource bookSource = new BookSource();
        bookSource.setName(new LwxswPickRulesTemplateImpl().getName());
        bookSource.setSource(LwxswPickRulesTemplateImpl.class.getName());
        return bookSource;
    }

    public static BookSource getDefaultBookSourceByApi(Long userId,
                                                       String bookIdThird) {
        List<BookSource> bookSourceList = getBookSourceByApi(userId, bookIdThird);

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

        JSONObject params = new JSONObject();
        params.put("query", keywords);
        params.put("start", pageBean.getRowBounds().getOffset() + "");
        params.put("limit", pageBean.getRowBounds().getLimit() + "");
        JSONObject json = null;
        try {
            Document document = HtmlUtils.getPage(BookConstants.URL_SEARCH, params, "PC");
            json = JSON.parseObject(document.text());
        } catch (Exception e) {
        }
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
        PickRulesTemplate pickRulesTemplate = PickRulesUtils.getPickRulesTemplate(source);
        try {
            String url = pickRulesTemplate.getSearchLink(keywords);
            JSONObject params = pickRulesTemplate.getSearchParams(keywords);
            String method = pickRulesTemplate.getSearchMethod();
            Document document;

            switch (method) {
                default:
                case "GET":
                    document = HtmlUtils.getPage(url,
                            params,
                            pickRulesTemplate.getSearchDevice());
                    break;
                case "POST":
                    document = HtmlUtils.postPage(url, params);
                    break;
            }

            if (document == null) {
                throw new RuntimeException("搜索接口未响应");
            }

            Elements elements = pickRulesTemplate.getSearchItems(document);
            for (Element e :
                    elements) {
                BookBean bookBean = new BookBean();
                BookInfo bookInfo = new BookInfo();

                String bookIdThird = pickRulesTemplate.getSearchItemBookIdThird(e);
                String title = pickRulesTemplate.getSearchItemTitle(e);
                String img = pickRulesTemplate.getSearchItemFaceUrl(e);
                String author = pickRulesTemplate.getSearchItemAuthor(e);
                String category = pickRulesTemplate.getSearchItemCategory(e);
                String updated = pickRulesTemplate.getSearchItemUpdateDate(e);
                String newChapter = pickRulesTemplate.getSearchItemUpdateChapter(e);

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
            logger.info("[获取搜索列表失败][pick]:" + (e.getMessage() == null ? JSON.toJSONString(e) : e.getMessage()));
            throw new RuntimeException("获取搜索列表失败");
        }

        pageBean.setList(bookBeanList);

        return pageBean;
    }
}
