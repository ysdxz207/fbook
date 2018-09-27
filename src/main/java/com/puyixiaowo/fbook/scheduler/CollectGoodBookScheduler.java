package com.puyixiaowo.fbook.scheduler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.fbook.constants.BookConstants;
import com.puyixiaowo.fbook.utils.LoggerUtils;
import com.puyixiaowo.fbook.utils.Page;
import com.puyixiaowo.fbook.utils.RedisUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import win.hupubao.common.email.Email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.jsoup.Connection.Method.GET;

public class CollectGoodBookScheduler {

    private static final int PAGE_SIZE = 1000;
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

    static {
        config.setHost("smtp.sina.com");
        config.setUsername("ysdxz207@sina.com");
        config.setAuthcode("//hupubao207");
        config.setPort(465);
        email = new Email(config);

        sendTo.setSenderName("好书搜集");
        sendTo.setTo(TO);
    }

    @SuppressWarnings("unchecked")
    public static void collect() {
        List<String> excludeBooks = RedisUtils.getDefault(KEY, List.class, new ArrayList());
        List<String> goodBooks = new ArrayList<>();
        int page = 1;

        for (String category :
                CATEGORIES) {


            goodBooks = getGoodsBookList(page, category, goodBooks, excludeBooks);
        }

        if (goodBooks.size() == 0) {
            return;
        }

        sendTo.setTitle(DateFormatUtils.format(new Date(), DATE_FORMAT) + " 好书搜集");
        StringBuilder sb = new StringBuilder();
        goodBooks.stream().map((b) -> b + "</br>").forEach(sb::append);
        sendTo.setContent(sb.toString());
        try {
            System.out.println("send" + sendTo.getContent());
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
        System.out.println(page + "=" + JSON.toJSONString(goodBooks));
        JSONObject json = getBooksJson(category, (page - 1) * PAGE_SIZE);
        int count = json.getIntValue("total");

        int pageCount = count / PAGE_SIZE + 1;
        System.out.println("页数：" + pageCount);
        if (page >= pageCount) {
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

        return getGoodsBookList(page + 1, category, goodBooks, excludeBooks);
    }

    private static JSONObject getBooksJson(String major,
                                           int start) {
        Page.Response response = Page.create().read(BookConstants.URL_BY_CATEGORIES + "?type=reputation&major={major}&start={start}&limit={limit}"
                        .replace("{major}", major)
                        .replace("{start}", start + "")
                        .replace("{limit}", PAGE_SIZE + ""),
                null, GET);
        return response.bodyToJSONObject();
    }

}