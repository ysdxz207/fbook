package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.fbook.bean.UserBean;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.BookChapterBean;
import com.puyixiaowo.fbook.bean.book.BookReadBean;
import com.puyixiaowo.fbook.bean.book.BookReadSettingBean;
import com.puyixiaowo.fbook.constants.BookConstants;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.enums.EnumSort;
import com.puyixiaowo.fbook.utils.*;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author Moses
 * @date 2017-12-19
 */
public class BookChapterService {

    private static final Logger logger = LoggerFactory.getLogger(BookChapterService.class);

    public static List<BookChapterBean> requestBookChapters(Long userId,
                                                            Long bookId,
                                                            String bookIdThird,
                                                            boolean keepSort) {

        List<BookChapterBean> list = new ArrayList<>();

        BookReadBean bookReadBean = BookReadService.getUserBookRead(userId, bookId);

        BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userId);
        bookReadBean = bookReadBean == null ? new BookReadBean() : bookReadBean;

        String source = bookReadBean.getSource();

        if (StringUtils.isBlank(source)) {
            source = BookService.getDefaultSource(userId, bookIdThird).get_id();
            //可能为第一次读书或配置被删除
            bookReadBean.setSource(source);
            bookReadBean.setBookId(bookId);
            bookReadBean.setUserId(userId);
        }

        //根据频道获取章节信息

        if (bookReadSettingBean.getUseApi()) {
            list = getChapterListByApi(bookId, source);
        } else {
            list = getChapterListByPick(bookId);
        }

        if (bookReadBean.getId() == null) {
            //
            BookChapterBean firstChapter = list.get(0);
            bookReadBean.setLastReadingChapter(firstChapter.getTitle());
            DBUtils.insertOrUpdate(bookReadBean, false);
        }
        if (!keepSort
                && bookReadSettingBean.getSort() != null
                && bookReadSettingBean.getSort() == EnumSort.SORT_REVERSE.sort) {
            Collections.reverse(list);
        }

        return list;
    }

    public static List<BookChapterBean> getChapterListByApi(Long bookId,
                                                            String source) {

        List<BookChapterBean> list = new ArrayList<>();
        String url = BookConstants.URL_CHAPTERS + source + "?view=chapters";

        JSONObject jsonObject = null;
        try {
            Connection.Response response = HtmlUtils.getPage(url, "UTF-8");
            jsonObject = JSON.parseObject(response.body());
        } catch (Exception e) {
        }
        if (jsonObject == null) {
            logger.error("[book]api返回章节json为null,bookId=" + bookId);
            return list;
        }

        if (jsonObject.size() == 0) {
            return list;
        }

        JSONArray chapters = jsonObject.getJSONArray("chapters");
        if (chapters == null) {
            logger.error("[book]未从api返回的数据中获取到章节列表:" + jsonObject);
            return list;
        }


        try {
            int chapterNum = 1;
            for (Object obj :
                    chapters) {
                JSONObject json = (JSONObject) obj;
                BookChapterBean bookChapterBean = new BookChapterBean();
                bookChapterBean.setBookId(bookId);
                bookChapterBean.setTitle(json.getString("title"));
                bookChapterBean.setLink(URLEncoder.encode(json.getString("link"), Constants.ENCODING));
                bookChapterBean.setSource(source);
                bookChapterBean.setChapterNum(chapterNum);
                list.add(bookChapterBean);

                chapterNum ++;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     *
     * @param bookId
     * @return
     */
    public static List<BookChapterBean> getChapterListByPick(Long bookId) {

        List<BookChapterBean> list = new ArrayList<>();

        try {

            BookBean bookBean = BookService.selectBookBeanById(bookId);

            if (bookBean == null) {
                throw new RuntimeException("书不存在");
            }

            String url = PickRulesUtils.pickRulesTemplate.getChapterListLink(bookBean);
            Connection.Response response = HtmlUtils.getPage(url,
                    PickRulesUtils.pickRulesTemplate.getBookEncoding());

            if (response == null) {
                return list;
            }
            Document document = response.parse();

            Elements elements = PickRulesUtils.pickRulesTemplate.getChapterListItems(document);

            int chapterNum = 1;
            for (Element element : elements) {

                String title = PickRulesUtils.pickRulesTemplate.getChapterListTitle(element);
                String link = PickRulesUtils.pickRulesTemplate.getChapterListDetailLink(element);

                BookChapterBean bookChapterBean = new BookChapterBean();
                bookChapterBean.setBookId(bookId);
                bookChapterBean.setTitle(title);
                bookChapterBean.setLink(link);
                bookChapterBean.setChapterNum(chapterNum);

                list.add(bookChapterBean);
                chapterNum ++;
            }

        } catch (Exception e) {

            logger.info("[获取章节目录失败][pick]:" + e.getMessage() == null ? JSON.toJSONString(e) : e.getMessage());
            throw new RuntimeException("获取章节目录失败");
        }

        return list;
    }

        public static List<BookChapterBean> getChapterHasReadList(List<BookChapterBean> list, BookReadBean bookReadBean) {

        for (int i = 0; i < list.size(); i++) {
            BookChapterBean bookChapterBean = list.get(i);
            if (getChapterNum(list, bookChapterBean.getTitle()) <= bookReadBean.getLastReadingChapterNum()) {
                bookChapterBean.setHasRead(true);
            }
        }
        return list;
    }


    public static BookChapterBean requestBookContent(UserBean userBean,
                                                     String link) {

        if (StringUtils.isBlank(link)) {
            return null;
        }

        BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userBean.getId());

        if (bookReadSettingBean.getUseApi()) {
            return getBookContentByApi(link);
        } else{
            return getBookContentByPick(link);
        }

    }

    public static BookChapterBean getBookContentByApi(String link) {

        String linkDecode = "";

        try {

            linkDecode = URLDecoder.decode(link, Constants.ENCODING);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (linkDecode.equals(link)) {
            try {
                link = URLEncoder.encode(link, Constants.ENCODING);
            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            }
        }

        BookChapterBean bookChapterBean = new BookChapterBean();
        String url = BookConstants.URL_CHAPTER_CONTENT + link;
        JSONObject json;
        try {
            json = JSON.parseObject(HttpUtils.httpGet(url, null));

        } catch (Exception e) {
            return null;
        }
        if (json == null) {
            logger.error("[book]api返回章节内容json为null");
            return null;
        }
        boolean ok = json.getBoolean("ok") == null ? false : json.getBoolean("ok");
        if (ok) {
            JSONObject chapter = json.getJSONObject("chapter");

            String title = chapter.getString("title");
            String content = chapter.getString("body");
            String cpContent = chapter.getString("cpContent");

            if (StringUtils.isNotBlank(cpContent)) {
                content = cpContent;
            }
            bookChapterBean.setTitle(title);
            bookChapterBean.setContent(content != null ? content.replace("<a href=\"", "") : content);
            bookChapterBean.setLink(link);
            return bookChapterBean;
        }
        return null;
    }

    public static BookChapterBean getBookContentByPick(String link) {
        BookChapterBean bookChapterBean = new BookChapterBean();

        try {
            Connection.Response response = HtmlUtils.getPage(link,
                    PickRulesUtils.pickRulesTemplate.getBookEncoding());

            if (response == null) {
                return bookChapterBean;
            }
            Document document = response.parse();

            String title = PickRulesUtils.pickRulesTemplate.getChapterDetailTitle(document);
            String content = PickRulesUtils.pickRulesTemplate.getChapterDetailContent(document);

            bookChapterBean.setTitle(title);
            bookChapterBean.setContent(content);
            bookChapterBean.setLink(link);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bookChapterBean;
    }


    /**
     * 判断是否同一章
     *
     * @param lastReadingChapter
     * @param title
     * @return
     */
    @Deprecated
    public static boolean isSameChapterTitle(String lastReadingChapter, String title) {

        if (StringUtils.isBlank(lastReadingChapter)
                && StringUtils.isBlank(title)) {
            return true;
        }

        if (StringUtils.isBlank(lastReadingChapter)
                && StringUtils.isNotBlank(title)) {
            return false;
        }

        if (StringUtils.isNotBlank(lastReadingChapter)
                && StringUtils.isBlank(title)) {
            return false;
        }


        String[] arr1 = lastReadingChapter.split("章");
        String[] arr2 = title.split("章");
        String title1 = lastReadingChapter;
        String title2 = title;
        if (arr1.length == 2
                && arr2.length == 2) {
            title1 = arr1[1];
            title2 = arr2[1];
        }
        if (StringUtils.getSimilarityRatio(title1.replaceAll("[\\pP\\p{Punct}]", ""), title2.replaceAll("[\\pP\\p{Punct}]", "")) > 0.9) {
            return true;
        }
        return false;
    }


    public static BookChapterBean getNextChapter(int page,
                                                 Long userId,
                                                 Long bookId,
                                                 String bookIdThird,
                                                 BookReadBean bookReadBean) {

        List<BookChapterBean> bookChapterBeanList = requestBookChapters(userId, bookId, bookIdThird, true);

        int index = bookReadBean.getLastReadingChapterNum() - 1 + page;
        if (index < 0
                || index >= bookChapterBeanList.size()) {
            return null;
        }
        return bookChapterBeanList.get(index);
    }

    /**
     * 可能发生分篇，每篇有n章情况
     * @param chapterTitle
     * @return
     */
    @Deprecated
    public static int getChapterNum(String chapterTitle) {
        if (StringUtils.isBlank(chapterTitle)) {
            return 0;
        }

        Pattern pattern = compile("第(\\d+)|([零一二三四五六七八九十百千]+)章");
        Matcher matcher1 = pattern.matcher(chapterTitle);
        if (matcher1.find()) {
            chapterTitle = matcher1.group(1);
            chapterTitle = chapterTitle == null ? matcher1.group(2) : chapterTitle;
        }

        return NumberUtils.hasNumber(chapterTitle) ?
                StringUtils.parseInteger(chapterTitle) :
                NumberUtils.convertToNumber(chapterTitle);
    }

    public static int getChapterNum(List<BookChapterBean> chapterBeanList, String chapterTitle) {

        if (StringUtils.isBlank(chapterTitle)) {
            return 1;
        }
        for (int i = 0; i < chapterBeanList.size(); i++) {
            BookChapterBean bookChapterBean = chapterBeanList.get(i);
            if (chapterTitle.equals(bookChapterBean.getTitle())) {
                return i + 1;
            }
        }
        return 1;
    }

    public static BookChapterBean getChapter(UserBean userBean,
                                             List<BookChapterBean> bookChapterBeanList,
                                             int chapterNum) {

        for (BookChapterBean bookChapterBean:
             bookChapterBeanList) {
            int num = BookChapterService.getChapterNum(bookChapterBeanList, bookChapterBean.getTitle());
            if (num == chapterNum) {
                BookChapterBean bookChapterBeanContent = BookChapterService.requestBookContent(userBean,
                        bookChapterBean.getLink());
                if (bookChapterBeanContent == null) {
                    //接口没返回内容，可能源有问题
                    return null;
                }
                bookChapterBean.setContent(bookChapterBeanContent.getContent());
                return bookChapterBean;
            }
        }
        return null;
    }



    public static void main(String[] args) throws Exception {


        System.out.println(JSON.toJSONString(getBookContentByPick("http://www.lwxsw.cc/book/7153/4096497_2.html")));
    }

}
