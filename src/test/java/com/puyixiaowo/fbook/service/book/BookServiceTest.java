package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import com.puyixiaowo.fbook.utils.pickrules.impl.LwxswPickRulesTemplateImpl;
import org.junit.Test;

import java.io.IOException;

public class BookServiceTest {

    @Test
    public void testSearchByPick() throws Exception {

        String source = LwxswPickRulesTemplateImpl.class.getName();
        PageBean pageBean = new PageBean();
        BookService.searchByPick("道君", pageBean, source);
        System.out.println(JSON.toJSONString(pageBean));
    }

    @Test
    public void testGetBookDetailByPick() {
        BookBean bookBean = new BookBean();
        bookBean.setBookIdThird("12946");
        String source = LwxswPickRulesTemplateImpl.class.getName();
        bookBean = BookService.getBookDetailByPick(bookBean, source);
        System.out.println(JSON.toJSONString(bookBean));
    }

    @Test
    public void test() throws IOException {
        System.out.println(HtmlUtils.getPage("https://m.23us.com.cn/", "UTF-8").body());
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme