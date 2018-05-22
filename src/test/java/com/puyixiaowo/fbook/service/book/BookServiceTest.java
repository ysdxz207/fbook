package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import org.junit.Test;

import java.io.IOException;

public class BookServiceTest {

    @Test
    public void testSearchByPick() throws Exception {

        PickRulesBean pickRulesBean = new PickRulesBean();

        pickRulesBean.setSearchEncoding("@Override\n" +
                "    public String getSearchEncoding() {\n" +
                "        return \"UTF-8\";\n" +
                "    }");


        pickRulesBean.setSearchLink("@Override\n" +
                "    public String getSearchLink(String keywords) {\n" +
                "        return \"http://zhannei.baidu.com/cse/search?p=0&area=1&s=6939410700241642371&q=\" + keywords;\n" +
                "    }");
        pickRulesBean.setSearchItems("@Override\n" +
                "    public Elements getSearchItems(Document document) {\n" +
                "        return document.select(\".result-item\");\n" +
                "    }");

        pickRulesBean.setSearchItemTitle("@Override\n" +
                "    public String getSearchItemTitle(Element element) {\n" +
                "        return element.select(\".result-item-title a\").attr(\"title\");\n" +
                "    }");

        pickRulesBean.setSearchItemBookIdThird("@Override\n" +
                "    public String getSearchItemBookIdThird(Element element) {\n" +
                "        Matcher matcherBookIdThird = Pattern.compile(\"http\\\\:\\\\/\\\\/.*\\\\/.*\\\\/(.*)\\\\/\").matcher(element.select(\".result-item-title a\").attr(\"href\"));\n" +
                "        String bookIdThird = matcherBookIdThird.find() ? matcherBookIdThird.group(1) : \"\";\n" +
                "        return bookIdThird;\n" +
                "    }");

        pickRulesBean.setSearchItemFaceUrl("@Override\n" +
                "    public String getSearchItemFaceUrl(Element element) {\n" +
                "        return element.select(\"img\").attr(\"src\");\n" +
                "    }");

        pickRulesBean.setSearchItemAuthor("@Override\n" +
                "    public String getSearchItemAuthor(Element element) {\n" +
                "        List<String> listInfos = element.select(\".result-game-item-info-tag\").eachText();\n" +
                "        String author = listInfos.get(0).split(\"：\")[1];\n" +
                "\n" +
                "        return author;\n" +
                "    }");

        pickRulesBean.setSearchItemCategory("@Override\n" +
                "    public String getSearchItemCategory(Element element) {\n" +
                "        List<String> listInfos = element.select(\".result-game-item-info-tag\").eachText();\n" +
                "        String author = listInfos.get(1).split(\"：\")[1];\n" +
                "\n" +
                "        return author;\n" +
                "    }");

        pickRulesBean.setSearchItemUpdateDate("@Override\n" +
                "    public String getSearchItemUpdateDate(Element element) {\n" +
                "        List<String> listInfos = element.select(\".result-game-item-info-tag\").eachText();\n" +
                "        String author = listInfos.get(2).split(\"：\")[1];\n" +
                "\n" +
                "        return author;\n" +
                "    }");

        pickRulesBean.setSearchItemUpdateChapter("@Override\n" +
                "    public String getSearchItemUpdateChapter(Element element) {\n" +
                "        List<String> listInfos = element.select(\".result-game-item-info-tag\").eachText();\n" +
                "        String author = listInfos.get(3).split(\"：\")[1];\n" +
                "\n" +
                "        return author;\n" +
                "    }");

        PickRulesUtils.getPickRulesTemplate(pickRulesBean);
        PageBean pageBean = new PageBean();
        BookService.searchByPick("道君", pageBean);
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


        PickRulesUtils.getPickRulesTemplate(pickRulesBean);

        bookBean = BookService.getBookDetailByPick(bookBean);
        System.out.println(JSON.toJSONString(bookBean));
    }

    @Test
    public void test() throws IOException {
        System.out.println(HtmlUtils.getPage("https://m.23us.com.cn/", "UTF-8").body());
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme