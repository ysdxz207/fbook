package com.puyixiaowo.fbook.service.book;

import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import org.junit.Test;

public class BookChapterServiceTest {


    @Test
    public String testGetGirlChapterList() throws Exception {

        BookBean bookBean = new BookBean();
        bookBean.setaId("12946");

        String templateString = "@Override\n" +
                "    public String getChapterListLink(BookBean bookBean) {\n" +
                "        return \"http://www.lwxsw.cc/book/\" + bookBean.getaId();\n" +
                "    }";

        PickRulesBean pickRulesBean = new PickRulesBean();
        pickRulesBean.setChapterListLink(templateString);
        PickRulesUtils.updatePickRulesTemplate(pickRulesBean);
        System.out.println(PickRulesUtils.pickRulesTemplate.getChapterListLink(bookBean));
        return PickRulesUtils.pickRulesTemplate.getChapterListLink(bookBean);
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme