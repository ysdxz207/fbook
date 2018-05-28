package com.puyixiaowo.fbook.service.book;

import com.alibaba.fastjson.JSON;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.BookChapterBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.HtmlUtils;
import com.puyixiaowo.fbook.utils.pickrules.PickRulesUtils;
import com.puyixiaowo.fbook.utils.pickrules.impl.LwxswPickRulesTemplateImpl;
import com.puyixiaowo.fbook.utils.pickrules.impl.Two3usPickRulesTemplateImpl;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class BookServiceTestLwxsw {

    @Test
    public void testSearchByPick() throws Exception {

        String source = LwxswPickRulesTemplateImpl.class.getName();
        PickRulesUtils.updatePickRulesTemplate(source);
        PageBean pageBean = new PageBean();
        BookService.searchByPick("地球上线", pageBean, source);
        System.out.println(JSON.toJSONString(pageBean));
    }

}

//Generated with love by TestMe :) Please report issues and submit feature requests at: http://weirddev.com/forum#!/testme