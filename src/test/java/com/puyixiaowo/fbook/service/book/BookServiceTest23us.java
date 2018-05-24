package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.BookChapterBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import com.puyixiaowo.fbook.utils.pickrules.impl.DefaultPickRulesTemplateImpl;
import com.puyixiaowo.fbook.utils.pickrules.impl.Two3usPickRulesTemplateImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class BookServiceTest23us {

    @Test
    public void testSearchByPick() throws Exception {

        String source = Two3usPickRulesTemplateImpl.class.getName();
        PickRulesUtils.updatePickRulesTemplate(source);
        PageBean pageBean = new PageBean();
        BookService.searchByPick("道君", pageBean, source);
        System.out.println(JSON.toJSONString(pageBean));
    }

    @Test
    public void testGetBookDetailByPick() {
        BookBean bookBean = new BookBean();
        bookBean.setBookIdThird("20845");
        String source = Two3usPickRulesTemplateImpl.class.getName();
        bookBean = BookService.getBookDetailByPick(bookBean, source);
        System.out.println(JSON.toJSONString(bookBean));
    }

    @Test
    public void testGetBookChapterListByPick() throws Exception {
        DBUtils.initDB("jdbc.properties");
        Long bookId = 43823644234433L;
        String source = Two3usPickRulesTemplateImpl.class.getName();
        List<BookChapterBean> list = BookChapterService.getChapterListByPick(bookId, source);
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void testGetBookChapterDetailByPick() throws Exception {
        String link = "https://m.w23us.com/book/20845/0.html";
        String source = Two3usPickRulesTemplateImpl.class.getName();
        BookChapterBean bookChapterBean = BookChapterService.getBookContentByPick(link, source);
        System.out.println(JSON.toJSONString(bookChapterBean));
    }

    @Test
    public void test() throws IOException {
        System.out.println(HtmlUtils.getPage("https://m.w23us.com/", "UTF-8", "PHONE").body());
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme