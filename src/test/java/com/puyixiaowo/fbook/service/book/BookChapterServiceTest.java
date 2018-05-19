package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import org.junit.Test;

public class BookChapterServiceTest {


    @Test
    public void testGetByPickChapterList() throws Exception {

        DBUtils.initDB("jdbc.properties");

        BookBean bookBean = new BookBean();
        bookBean.setId(443514092943048154L);


        PickRulesBean pickRulesBean = new PickRulesBean();
        pickRulesBean.setChapterListLink("@Override\n" +
                "    public String getChapterListLink(BookBean bookBean) {\n" +
                "        return \"http://www.lwxsw.cc/book/\" + bookBean.getBookIdThird() + \"/\";\n" +
                "    }");

        pickRulesBean.setChapterListItems("@Override\n" +
                "    public Elements getChapterListItems(Document document) {\n" +
                "        return document.select(\"#list-chapterAll .panel-chapterlist dd a\");\n" +
                "    }");

        pickRulesBean.setChapterListTitle("@Override\n" +
                "    public String getChapterListTitle(Element element) {\n" +
                "        return element.text();\n" +
                "    }");

        pickRulesBean.setChapterListDetailLink("@Override\n" +
                "    public String getChapterListDetailLink(Element element) {\n" +
                "        return element.baseUri() + element.attr(\"href\");\n" +
                "    }");
        PickRulesUtils.updatePickRulesTemplate(pickRulesBean);
        System.out.println(JSON.toJSONString(BookChapterService.getByPickChapterList(bookBean.getId())));
    }

    @Test
    public void testGetBookContentByPick() {
        String link = "http://www.lwxsw.cc/book/12946/7554155.html";
        PickRulesBean pickRulesBean = new PickRulesBean();

        pickRulesBean.setChapterDetailTitle("");
//        pickRulesBean.setChapterDetailContent("");



        System.out.println(JSON.toJSONString(BookChapterService.getBookContentByPick(link)));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme