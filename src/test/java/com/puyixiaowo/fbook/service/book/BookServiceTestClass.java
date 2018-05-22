package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import com.puyixiaowo.fbook.utils.pickrules.impl.Two3usPickRulesTemplateImpl;
import org.junit.Test;

import java.io.IOException;

public class BookServiceTestClass {

    @Test
    public void testSearchByPick() throws Exception {

        PickRulesUtils.updatePickRulesTemplate(Two3usPickRulesTemplateImpl.class);
        PageBean pageBean = new PageBean();
        BookService.searchByPick("地球上线", pageBean);
        System.out.println(JSON.toJSONString(pageBean));
    }

    @Test
    public void testGetBookDetailByPick() {
        PickRulesBean pickRulesBean = new PickRulesBean();
        BookBean bookBean = new BookBean();
        bookBean.setBookIdThird("12946");


        pickRulesBean.setBookDetailLink("@Override\n" +
                "    public String getBookDetailLink(BookBean bookBean) {\n" +
                "        return \"http://www.lwxsw.cc/book/\" + bookBean.getBookIdThird() + \"/\";\n" +
                "    }");


        pickRulesBean.setBookDetailTitle("@Override\n" +
                "    public String getBookDetailTitle(Document document) {\n" +
                "        return document.select(\".bookTitle\").text();\n" +
                "    }");

        pickRulesBean.setBookDetailAuthor("@Override\n" +
                "    public String getBookDetailAuthor(Document document) {\n" +
                "        return document.select(\".booktag\").get(0).getAllElements().eachText().get(1);\n" +
                "    }");

        pickRulesBean.setBookDetailCategory("@Override\n" +
                "    public String getBookDetailCategory(Document document) {\n" +
                "        return document.select(\".booktag\").get(0).getAllElements().eachText().get(2);\n" +
                "    }");

        pickRulesBean.setBookDetailDescription("@Override\n" +
                "    public String getBookDetailDescription(Document document) {\n" +
                "        return document.select(\"#bookIntro\").text();\n" +
                "    }");

        pickRulesBean.setBookDetailFaceUrl("@Override\n" +
                "    public String getBookDetailFaceUrl(Document document) {\n" +
                "        return document.select(\"#bookIntro img\").attr(\"src\");\n" +
                "    }");

        pickRulesBean.setBookDetailUpdateDate("@Override\n" +
                "    public String getBookDetailUpdateDate(Document document) {\n" +
                "        return document.select(\"p.visible-xs\").text().split(\"：\")[1];\n" +
                "    }");

        pickRulesBean.setBookDetailUpdateChapter("@Override\n" +
                "    public String getBookDetailUpdateChapter(Document document) {\n" +
                "        return document.select(\"p\").get(1).select(\"a\").text();\n" +
                "    }");


        PickRulesUtils.updatePickRulesTemplate(pickRulesBean);

        bookBean = BookService.getBookDetailByPick(bookBean);
        System.out.println(JSON.toJSONString(bookBean));
    }

    @Test
    public void test() throws IOException {
        System.out.println(HtmlUtils.getPage("https://m.23us.com.cn/", "UTF-8").body());
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme