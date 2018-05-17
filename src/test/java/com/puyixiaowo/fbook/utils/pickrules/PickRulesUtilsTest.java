package com.puyixiaowo.fbook.utils.pickrules;

import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.enums.EnumSourceGirl;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class PickRulesUtilsTest {

    @Test
    public void testUpdatePickRulesTemplate() throws Exception {


        PickRulesBean pickRulesBean = new PickRulesBean();

        pickRulesBean.setBookDetailTitle("@Override\n" +
                "    public String getBookDetailTitle(Document document) {\n" +
                "        return document.select(\".bookTitle\").text();\n" +
                "    }");
        PickRulesUtils.updatePickRulesTemplate(pickRulesBean);

        String url = "http://www.lwxsw.cc/book/12946";
        Connection.Response response = HtmlUtils.getPage(url, EnumSourceGirl.LWXSW.encoding);

        Document document = response.parse();

        System.out.println(Constants.pickRulesTemplate.getBookDetailTitle(document));

    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme