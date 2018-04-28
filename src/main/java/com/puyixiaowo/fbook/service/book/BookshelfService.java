package com.puyixiaowo.fbook.service.book;

import com.puyixiaowo.fbook.bean.UserBean;
import com.puyixiaowo.fbook.bean.book.BookBean;
import com.puyixiaowo.fbook.bean.book.BookshelfBean;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Moses
 * @date 2017-12-19
 */
public class BookshelfService {

    public static String getSelectSql(BookshelfBean bookshelfBean,
                                                      PageBean pageBean) {

        StringBuilder sbSql = new StringBuilder("select * from bookshelf t where 1 = 1 ");

        buildSqlParams(sbSql, bookshelfBean);
        sbSql.append("order by t.id asc ");
        sbSql.append("limit ");
        sbSql.append(pageBean.getRowBounds().getOffset());
        sbSql.append(", ");
        sbSql.append(pageBean.getRowBounds().getLimit());
        return sbSql.toString();
    }
    public static PageBean selectBookshelfPageBean(BookshelfBean bookshelfBean, PageBean pageBean){
        return DBUtils.selectPageBean(getSelectSql(bookshelfBean, pageBean), bookshelfBean, pageBean);
    }
    public static void buildSqlParams(StringBuilder sbSql,
                                               BookshelfBean bookshelfBean) {
    }

    public static boolean isBookOnShelf(UserBean userBean,
                                        Long bookId) {
        if (userBean == null
                || bookId == null) {
            return false;
        }


        BookshelfBean bookShelfBean = new BookshelfBean();
        bookShelfBean.setUserId(userBean.getId());
        bookShelfBean.setBookId(bookId);
        int count = DBUtils.count("select * from bookshelf where user_id=:userId and book_id=:bookId", bookShelfBean);
        return count != 0;
    }

    public static boolean addOrDelBookFromBookshelf(UserBean userBean,
                                                    String aId) {
        boolean isOnBookShelf;
        //首先根据aId获取bookId
        BookBean bookBean = BookService.getBookByAId(aId);

        if (bookBean == null) {
            isOnBookShelf = false;
        } else {
            isOnBookShelf = isBookOnShelf(userBean, bookBean.getId());
        }


        BookshelfBean bookshelfBean = new BookshelfBean();
        bookshelfBean.setUserId(userBean.getId());
        bookshelfBean.setBookId(bookBean.getId());

        if (isOnBookShelf) {
            //从书架上移除
            DBUtils.executeSql("delete from bookshelf where user_id=:userId and book_id=:bookId", bookshelfBean);
            //删除读书配置
            BookReadService.deleteByBookId(bookBean.getId());

        } else {
            //创建书架并添加书籍
            bookshelfBean.setCreateTime(System.currentTimeMillis());
            DBUtils.insertOrUpdate(bookshelfBean, false);
        }


        return !isOnBookShelf;
    }
}
