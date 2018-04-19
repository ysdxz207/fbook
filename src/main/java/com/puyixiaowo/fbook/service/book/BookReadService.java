package com.puyixiaowo.fbook.service.book;

import com.puyixiaowo.fbook.bean.book.BookReadBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.bean.sys.ResponseBean;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.StringUtils;

public class BookReadService {

    public static String getSelectSql(BookReadBean bookReadBean,
                                      PageBean pageBean) {

        StringBuilder sbSql = new StringBuilder("select t.* from book_read t where 1 = 1 ");

        buildSqlParams(sbSql, bookReadBean);
        sbSql.append("order by t.id desc ");
        sbSql.append("limit ");
        sbSql.append(pageBean.getRowBounds().getOffset());
        sbSql.append(", ");
        sbSql.append(pageBean.getRowBounds().getLimit());
        return sbSql.toString();
    }
    public static PageBean selectBookPageBean(BookReadBean bookReadBean, PageBean pageBean){
        return DBUtils.selectPageBean(getSelectSql(bookReadBean, pageBean), bookReadBean, pageBean);
    }
    public static void buildSqlParams(StringBuilder sbSql,
                                      BookReadBean bookReadBean) {
        if (bookReadBean.getUserId() != null) {
            sbSql.append("and t.userId = :userId ");
            bookReadBean.setUserId(bookReadBean.getUserId());
        }
    }

    public static BookReadBean getUserBookRead(Long userId,
                                               Long bookId) {
        BookReadBean bookReadBean = new BookReadBean();
        bookReadBean.setUserId(userId);
        bookReadBean.setBookId(bookId);
        bookReadBean = DBUtils.selectOne("select * from book_read where " +
                "user_id = :userId and book_id = :bookId", bookReadBean);

        if (bookReadBean == null) {
            bookReadBean = new BookReadBean();
            bookReadBean.setUserId(userId);
            bookReadBean.setBookId(bookId);
            bookReadBean.setLastReadingChapterNum(1);
        }
        return bookReadBean;
    }


    public static void deleteByBookId(Long bookId) {

        BookReadBean bookReadBean = new BookReadBean();
        bookReadBean.setBookId(bookId);
        DBUtils.executeSql("delete from book_read where book_id=:bookId", bookReadBean);
    }

    public static ResponseBean saveBookRead(BookReadBean bookReadBean) {
        ResponseBean responseBean = new ResponseBean();

        //读书配置不存在
        if (bookReadBean == null) {
            return responseBean.errorMessage("读书配置不可为空");
        }
        if (StringUtils.isBlank(bookReadBean.getBookId())) {
            return responseBean.errorMessage("{读书配置]书ID不可为空");
        }
        if (StringUtils.isBlank(bookReadBean.getUserId())) {
            return responseBean.errorMessage("{读书配置]用户ID不可为空");
        }
        if (StringUtils.isBlank(bookReadBean.getLastReadingChapter())) {
            return responseBean.errorMessage("{读书配置]最后读章不可为空");
        }
        if (StringUtils.isBlank(bookReadBean.getSource())) {
            return responseBean.errorMessage("{读书配置]书来源不可为空");
        }
        //读取读书配置
        BookReadBean bookReadBeanDB = BookReadService
                .getUserBookRead(bookReadBean
                        .getUserId(), bookReadBean.getBookId());



        //更新读书配置
        if (bookReadBeanDB != null) {
            bookReadBean.setId(bookReadBeanDB.getId());
        }

        bookReadBean.setBookId(bookReadBean.getBookId());
        DBUtils.insertOrUpdate(bookReadBean, false);

        return responseBean;
    }
}
