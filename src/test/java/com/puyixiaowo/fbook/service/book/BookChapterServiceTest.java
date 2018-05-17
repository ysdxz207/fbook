package com.puyixiaowo.fbook.service.book;

import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import org.junit.Test;

public class BookChapterServiceTest {


    @Test
    public void testGetGirlChapterList() throws Exception {

        BookBean bookBean = new BookBean();
        bookBean.setaId("123123");

        String templateString = "@Override\n" +
                "    public String getChapterListLink(BookBean bookBean) {\n" +
                "        return \"http://www.lwxsw.cc/book/\" + bookBean.getaId();\n" +
                "    }";

        PickRulesBean pickRulesBean = new PickRulesBean();
        pickRulesBean.setChapterListLink(templateString);
        PickRulesUtils.updatePickRulesTemplate(pickRulesBean);
        System.out.println(Constants.pickRulesTemplate.getChapterListLink(bookBean));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme