package com.puyixiaowo.fbook.utils.pickrules.impl;

import com.puyixiaowo.fbook.utils.pickrules.PickRulesTemplate;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

public class DefaultPickRulesTemplateImpl implements PickRulesTemplate{

    @Override
    public String getBookDetailTitle(Document document) {
        return document.select(".bookTitle").text();
    }
}
