package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.PickRulesBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import org.junit.Assert;
import org.junit.Test;

public class BookServiceTest {

    @Test
    public void testSearchGirl() throws Exception {

        PickRulesBean pickRulesBean = new PickRulesBean();
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

        pickRulesBean.setSearchItemAId("@Override\n" +
                "    public String getSearchItemAId(Element element) {\n" +
                "        Matcher matcherAid = Pattern.compile(\"http\\\\:\\\\/\\\\/.*\\\\/.*\\\\/(.*)\\\\/\").matcher(element.select(\".result-item-title a\").attr(\"href\"));\n" +
                "        String aid = matcherAid.find() ? matcherAid.group(1) : \"\";\n" +
                "        return aid;\n" +
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

        PickRulesUtils.updatePickRulesTemplate(pickRulesBean);
        PageBean pageBean = new PageBean();
        BookService.searchGirl("道君", pageBean);
        System.out.println(JSON.toJSONString(pageBean));
    }
}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme