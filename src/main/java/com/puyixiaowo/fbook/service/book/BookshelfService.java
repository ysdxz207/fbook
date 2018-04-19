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

    public static BookshelfBean getBookshelfById(Long id) {
        BookshelfBean bookshelfBean = new BookshelfBean();
        bookshelfBean.setId(id);
        return DBUtils.selectOne("select * from bookshelf where id=:id", bookshelfBean);
    }

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

    public static BookshelfBean getUserShelf(Long userId) {
        BookshelfBean bookshelfBean = new BookshelfBean();
        bookshelfBean.setUserId(userId);
        return DBUtils.selectOne("select * from bookshelf where user_id = :userId", bookshelfBean);
    }

    public static boolean isBookOnShelf(UserBean userBean,
                                        BookBean bookBean) {
        if (bookBean == null
                || bookBean.getaId() == null) {
            return false;
        }

        BookshelfBean bookshelfBean = getUserShelf(userBean.getId());
        if (bookshelfBean == null
                || bookshelfBean.getBookIds() == null) {
            return false;
        }

        Long bookId = bookBean.getId();


        if (bookId == null) {
            BookBean bookBeanDB = BookService.selectBookBeanByAId(bookBean.getaId());
            if (bookBeanDB == null) {
                return false;
            }
            bookId = bookBeanDB.getId();
        }
        return bookshelfBean.getBookIds().indexOf("" + bookId) != -1;
    }

    public static boolean addOrDelBookFromBookshelf(UserBean userBean, Long bookId) {
        BookshelfBean bookshelfBean = getUserShelf(userBean.getId());
        if (bookshelfBean == null) {
            //创建书架并添加书籍
            bookshelfBean = new BookshelfBean();
            bookshelfBean.setCreateTime(System.currentTimeMillis());
            bookshelfBean.setUserId(userBean.getId());
        }
        String [] bookIds = new String[0];
        boolean isOnBookshelf = false;
        if (StringUtils.isNotBlank(bookshelfBean.getBookIds())) {
            bookIds = bookshelfBean.getBookIds().split(",");
            isOnBookshelf = bookshelfBean.getBookIds().indexOf("" + bookId) != -1;
        }

        List<String> bookIdList = new ArrayList(Arrays.asList(bookIds));

        Iterator it = bookIdList.iterator();
        if (isOnBookshelf) {
            while (it.hasNext()) {
                if (it.next().equals(bookId + "")) {
                    it.remove();
                    //删除读书配置
                    BookReadService.deleteByBookId(bookId);
                }
            }
        } else {
            bookIdList.add(bookId + "");
        }

        bookshelfBean.setBookIds(StringUtils.join(bookIdList.toArray(), ","));

        DBUtils.insertOrUpdate(bookshelfBean, false);

        return !isOnBookshelf;
    }
}
