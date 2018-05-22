package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import com.puyixiaowo.fbook.utils.pickrules.impl.DefaultPickRulesTemplateImpl;
import org.junit.Test;

public class BookChapterServiceTest {


    @Test
    public void testGetByPickChapterList() throws Exception {

        BookBean bookBean = new BookBean();
        bookBean.setId(443514092943048154L);
        String source = DefaultPickRulesTemplateImpl.class.getName();
        System.out.println(JSON.toJSONString(BookChapterService.getChapterListByPick(bookBean.getId(), source)));
    }

    @Test
    public void testGetBookContentByPick() {
        String link = "http://www.lwxsw.cc/book/12946/7554155.html";
        String source = DefaultPickRulesTemplateImpl.class.getName();

        System.out.println(JSON.toJSONString(BookChapterService.getBookContentByPick(link, source)));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme