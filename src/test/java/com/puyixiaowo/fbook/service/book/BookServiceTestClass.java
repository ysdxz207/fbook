package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import com.puyixiaowo.fbook.utils.pickrules.impl.DefaultPickRulesTemplateImpl;
import com.puyixiaowo.fbook.utils.pickrules.impl.Two3usPickRulesTemplateImpl;
import org.junit.Test;

import java.io.IOException;

public class BookServiceTestClass {

    @Test
    public void testSearchByPick() throws Exception {

        PickRulesUtils.getPickRulesTemplate(Two3usPickRulesTemplateImpl.class);
        PageBean pageBean = new PageBean();
        String source = Two3usPickRulesTemplateImpl.class.getName();
        BookService.searchByPick("地球上线", pageBean, source);
        System.out.println(JSON.toJSONString(pageBean));
    }

    @Test
    public void testGetBookDetailByPick() {
        BookBean bookBean = new BookBean();
        bookBean.setBookIdThird("12946");
        String source = DefaultPickRulesTemplateImpl.class.getName();
        bookBean = BookService.getBookDetailByPick(bookBean, source);
        System.out.println(JSON.toJSONString(bookBean));
    }

    @Test
    public void test() throws IOException {
        System.out.println(HtmlUtils.getPage("https://m.23us.com.cn/", "UTF-8").body());
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme