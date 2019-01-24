package com.puyixiaowo.fbook.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.fbook.constants.BookConstants;
import com.puyixiaowo.fbook.utils.RedisUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import win.hupubao.common.email.Email;
import win.hupubao.common.http.Page;
import win.hupubao.common.utils.LoggerUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.jsoup.Connection.Method.GET;

public class CollectGoodBookScheduler {

    private static final int PAGE_SIZE = 50;
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String [] TO = {"ysdxz207@qq.com"};
    private static final String KEY = "FBOOK_EXCLUDE_BOOKS";
    private static final String [] CATEGORIES = {
            "玄幻",
            "玄幻奇幻",
            "武侠仙侠",
            "仙侠"
    };
    private static final Email.Config config = new Email.Config();
    private static final Email.SendTo sendTo = new Email.SendTo();
    private static Email email;

    private static int total;
    private static int pageCount;

    static {
        config.setHost("smtp.sina.com");
        config.setUsername("ysdxz207@sina.com");
        config.setAuthcode("***");
        config.setPort(465);
        email = new Email(config);

        sendTo.setSenderName("好书搜集");
        sendTo.setTo(TO);
    }

    @SuppressWarnings("unchecked")
    public static void collect() {
        LoggerUtils.info("开始搜集好书");
        List<String> excludeBooks = RedisUtils.getDefault(KEY, List.class, new ArrayList());
        List<String> goodBooks = new ArrayList<>();
        int page = 1;

        for (String category :
                CATEGORIES) {


            goodBooks = getGoodsBookList(page, category, goodBooks, excludeBooks);
            LoggerUtils.info("搜集[{}]结果：{}", category, JSON.toJSONString(goodBooks));
        }

        if (goodBooks.size() == 0) {
            return;
        }

        sendTo.setTitle(DateFormatUtils.format(new Date(), DATE_FORMAT) + " 好书搜集");
        StringBuilder sb = new StringBuilder();
        goodBooks.stream().map((b) -> b + "</br>").forEach(sb::append);
        sendTo.setContent(sb.toString());
        try {
            LoggerUtils.info("搜集好书发送邮件：" + sendTo.getContent());
            email.send(sendTo);
            excludeBooks.addAll(goodBooks);
            RedisUtils.set(KEY, JSON.toJSONString(excludeBooks));
        } catch (Exception e) {
            LoggerUtils.error("搜集好书发送邮件异常", e);
        }

    }

    private static List<String> getGoodsBookList(int page,
                                         String category,
                                         List<String> goodBooks,
                                         List<String> excludeBooks) {
        int start = (page - 1) * PAGE_SIZE;
        JSONObject json = getBooksJson(category, start);
        if (page == 1) {
            total = json.getIntValue("total");
            pageCount = total / PAGE_SIZE + 1;
        }

        if (page >= pageCount || start > 1000) {
            return goodBooks;
        }

        JSONArray books = json.getJSONArray("books");
        for (Object o :
                books) {
            JSONObject book = (JSONObject) o;
            Double retentionRatio = book.getDouble("retentionRatio");
            if (retentionRatio == null) {
                continue;
            }
            String title = book.getString("title");
            if (retentionRatio.compareTo(70d) > 0) {
                if (excludeBooks.contains(title)) {
                    continue;
                }

                goodBooks.add(title);
            }
        }

        try {
            Thread.sleep(5 * 1000L);
        } catch (InterruptedException ignored) {
        }
        return getGoodsBookList(page + 1, category, goodBooks, excludeBooks);
    }

    private static JSONObject getBooksJson(String major,
                                           int start) {
        Page.Response response = Page.create()
                        .loggerOff()
                        .connectionTimeout(5000)
                        .readTimeout(5000)
                        .retryTimes(3).request(BookConstants.URL_BY_CATEGORIES + "?type=reputation&major={major}&start={start}&limit={limit}"
                        .replace("{major}", major)
                        .replace("{start}", start + "")
                        .replace("{limit}", PAGE_SIZE + ""),
                null, GET);
        return (JSONObject) response.toJson(false);
    }

}