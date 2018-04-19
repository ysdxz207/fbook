package com.puyixiaowo.fbook.service.book;

import com.puyixiaowo.fbook.bean.book.BookReadSettingBean;
import com.puyixiaowo.fbook.utils.DBUtils;

public class BookReadSettingService {

    public static BookReadSettingBean getUserReadSetting(Long userId) {
        BookReadSettingBean bookReadSettingBean = new BookReadSettingBean();
        bookReadSettingBean.setUserId(userId);
        bookReadSettingBean = DBUtils.selectOne("select * from book_read_setting where " +
                "user_id = :userId", bookReadSettingBean);

        if (bookReadSettingBean == null) {
            bookReadSettingBean = new BookReadSettingBean();
            bookReadSettingBean.setUserId(userId);
            DBUtils.insertOrUpdate(bookReadSettingBean, false);
        }
        return bookReadSettingBean;
    }
}
