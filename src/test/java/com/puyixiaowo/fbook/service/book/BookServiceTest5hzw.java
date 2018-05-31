package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.BookChapterBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import com.puyixiaowo.fbook.utils.pickrules.impl.DefaultPickRulesTemplateImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class BookServiceTest5hzw {

    @Test
    public void testSearchByPick() throws Exception {

        String source = DefaultPickRulesTemplateImpl.class.getName();
        PickRulesUtils.updatePickRulesTemplate(source);
        PageBean pageBean = new PageBean();
        BookService.searchByPick("地球上线", pageBean, source);
        System.out.println(JSON.toJSONString(pageBean));
    }

    @Test
    public void testGetBookDetailByPick() {
        BookBean bookBean = new BookBean();
        bookBean.setBookIdThird("2_2390");
        String source = DefaultPickRulesTemplateImpl.class.getName();
        bookBean = BookService.getBookDetailByPick(bookBean, source);
        System.out.println(JSON.toJSONString(bookBean));
    }

    @Test
    public void testGetBookChapterListByPick() throws Exception {
        DBUtils.initDB("jdbc.properties");
        Long bookId = 448993300779630592L;
        String source = DefaultPickRulesTemplateImpl.class.getName();
        List<BookChapterBean> list = BookChapterService.getChapterListByPick(bookId, source);
        System.out.println(JSON.toJSONString(list));
    }

    @Test
    public void testGetBookChapterDetailByPick() throws Exception {
        String link = "http://www.5hzw.com/2_2390/7946313.html";
        String source = DefaultPickRulesTemplateImpl.class.getName();
        BookChapterBean bookChapterBean = BookChapterService.getBookContentByPick(link, source);
        System.out.println(JSON.toJSONString(bookChapterBean));
    }

    @Test
    public void test() throws IOException {
        System.out.println(HtmlUtils.getPage("http://www.mxguan.com/book/840/8360558.html").body());
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme