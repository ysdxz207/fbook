package com.puyixiaowo.fbook.utils.pickrules;

import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.enums.EnumSourceGirl;
import com.puyixiaowo.fbook.service.book.BookChapterServiceTest;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class PickRulesUtilsTest {

    @Test
    public void testUpdatePickRulesTemplate() throws Exception {

        BookChapterServiceTest bookChapterServiceTest = new BookChapterServiceTest();
        String url = bookChapterServiceTest.testGetGirlChapterList();

        PickRulesBean pickRulesBean = new PickRulesBean();

        pickRulesBean.setBookDetailTitle("@Override\n" +
                "    public String getBookDetailTitle(Document document) {\n" +
                "        return document.select(\".bookTitle\").text();\n" +
                "    }");
        pickRulesBean.setBookDetailAuthor("@Override\n" +
                "    public String getBookDetailAuthor(Document document) {\n" +
                "        return document.select(\".bookTitle\").text();\n" +
                "    }");
        PickRulesUtils.updatePickRulesTemplate(pickRulesBean);

        Connection.Response response = HtmlUtils.getPage(url, EnumSourceGirl.LWXSW.encoding);

        Document document = response.parse();

        System.out.println(PickRulesUtils.pickRulesTemplate.getBookDetailTitle(document));
        System.out.println(PickRulesUtils.pickRulesTemplate.getBookDetailAuthor(document));

    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme