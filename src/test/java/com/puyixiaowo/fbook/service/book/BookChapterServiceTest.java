package com.puyixiaowo.fbook.service.book;

import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import org.junit.Test;

public class BookChapterServiceTest {


    @Test
    public void testGetGirlChapterList() throws Exception {

        BookBean bookBean = new BookBean();
        bookBean.setId(439816480461160448L);
        bookBean.setaId("12946");


        PickRulesBean pickRulesBean = new PickRulesBean();
        pickRulesBean.setChapterListLink("@Override\n" +
                "    public String getChapterListLink(BookBean bookBean) {\n" +
                "        return \"http://www.lwxsw.cc/book/\" + bookBean.getaId() + \"/\";\n" +
                "    }");

        pickRulesBean.setChapterListItems("@Override\n" +
                "    public Elements getChapterListItems(Document document) {\n" +
                "        return null;\n" +
                "    }");

        pickRulesBean.setChapterListTitle("@Override\n" +
                "    public String getChapterListTitle(Element element) {\n" +
                "        return \"\";\n" +
                "    }");

        pickRulesBean.setChapterListDetailLink("@Override\n" +
                "    public String getChapterListDetailLink(Element element) {\n" +
                "        return \"\";\n" +
                "    }");
        PickRulesUtils.updatePickRulesTemplate(pickRulesBean);
        System.out.println(BookChapterService.getGirlChapterList(bookBean.getId()));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme