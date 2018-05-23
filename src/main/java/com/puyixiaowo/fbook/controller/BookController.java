package com.puyixiaowo.fbook.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.puyixiaowo.core.entity.RowBounds;
import com.puyixiaowo.fbook.bean.UserBean;
import com.puyixiaowo.fbook.bean.book.*;
import com.puyixiaowo.fbook.bean.error.ReadError;
import com.puyixiaowo.fbook.bean.error.SystemError;
import com.puyixiaowo.fbook.bean.sys.PageBean;
import com.puyixiaowo.fbook.bean.sys.ResponseBean;
import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.enums.EnumSort;
import com.puyixiaowo.fbook.service.book.*;
import com.puyixiaowo.fbook.utils.DBUtils;
import com.puyixiaowo.fbook.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.Response;

import java.util.Collections;
import java.util.List;

/**
 * @author Moses
 * @date 2017-12-18
 */
public class BookController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(BookController.class);


    public static ResponseBean bookShelf(Request request, Response response) {
        ResponseBean responseBean = new ResponseBean();
        JSONObject json = new JSONObject();

        try {
            UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);
            List<BookBean> list = BookService.getUserBookList(userBean.getId());
            BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userBean.getId());
            json.put("bookList", list);
            json.put("bookReadSetting", bookReadSettingBean);
            responseBean.setData(json);

        } catch (Exception e) {
            responseBean.error(e);
        }

        return responseBean;
    }

    public static ResponseBean bookDetail(Request request, Response response) {
        ResponseBean responseBean = new ResponseBean();
        BookBean bookBean = null;
        try {
            UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);

            bookBean = getParamsEntity(request, BookBean.class, true, false);



            //保存或更新书籍信息

            bookBean = BookService.requestBookDetail(userBean,
                    bookBean);
            DBUtils.insertOrUpdate(bookBean, false);

            //是否在书架里
            bookBean.setOnShelf(BookshelfService.isBookOnShelf(request.session().attribute(Constants.SESSION_USER_KEY),
                    bookBean.getId()));

            responseBean.setData(bookBean);
        } catch (Exception e) {
            logger.error("[书]获取章节列表异常：" + e.getMessage());
            responseBean.error(e);
        }
        return responseBean;
    }

    public static ResponseBean chapterContent(Request request, Response response) {
        ResponseBean responseBean = new ResponseBean();
        try {

            //必传,书籍详情页面看书只有bookId
            String bookIdStr = request.queryParams("bookId");
            //仅用于接口获取到章节名为.时显示
            String chapterName = request.queryParams("chapterName");
            //章节号
            Integer chapter = request.queryParams("chapter") != null ?
                    Integer.valueOf(request.queryParams("chapter")) : null;

            //是否预加载
            boolean isPreLoad = Boolean.valueOf(request.queryParamOrDefault("preLoad", "false"));

            if (StringUtils.isBlank(bookIdStr)) {
                return responseBean.errorMessage("bookId不可为空");
            }


            Long bookId = Long.valueOf(bookIdStr);
            JSONObject model = new JSONObject();

            UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);
            //读取读书配置
            BookReadBean bookReadBean = BookReadService.getUserBookRead(userBean.getId(), bookId);

            BookBean bookBean = BookService.selectBookBeanById(bookId);

            if (bookBean == null) {
                return responseBean.errorMessage("书不存在");
            }
            //章节列表
            List<BookChapterBean> chapterBeanList = BookChapterService
                    .requestBookChapters(userBean.getId(), bookId, bookBean.getBookIdThird(), true);

            if (StringUtils.isNotBlank(chapterName)) {
                chapter = BookChapterService.getChapterNum(chapterBeanList, chapterName);
            }

            if (chapter == null) {
                chapter = bookReadBean.getLastReadingChapterNum();
            }


            if (chapter == null) {
                return responseBean.errorMessage("章节号为空");
            }

            //最后一章
            if (chapter > chapterBeanList.size()) {
                return responseBean.error(ReadError.READ_LAST_CHAPTER_ERROR);
            }

            BookChapterBean bookChapterBean = BookChapterService.getChapter(userBean,
                    chapterBeanList,
                    chapter);
            if (bookChapterBean == null) {

                //提示切换书源
                return responseBean.error(ReadError.READ_SOURCE_ERROR);
            }

            //保存读书配置
            if (!isPreLoad) {
                bookReadBean.setLastReadingChapter(bookChapterBean.getTitle());
                bookReadBean.setLastReadingChapterNum(chapter);
                BookReadService.saveBookRead(bookReadBean);
            }


            bookChapterBean.setBookId(bookId);
            String content = bookChapterBean
                    .getContent().replaceAll("\n", "</p>\n<p>&nbsp;&nbsp;&nbsp;&nbsp;");
            bookChapterBean.setContent(content);


            //已读
            chapterBeanList = BookChapterService.getChapterHasReadList(chapterBeanList, bookReadBean);

            BookReadSettingBean bookReadSettingBean = BookReadSettingService.getUserReadSetting(userBean.getId());

            if (bookReadSettingBean.getSort() == EnumSort.SORT_REVERSE.sort) {
                Collections.reverse(chapterBeanList);
            }
            model.put("chapter", bookChapterBean);
            model.put("book", bookBean);
            model.put("bookRead", bookReadBean);
            model.put("bookReadSetting", bookReadSettingBean);
            model.put("bookChapters", chapterBeanList);
            responseBean.setData(model);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[书]获取章节内容异常：" + (e.getMessage() == null ? JSON.toJSONString(e.getMessage()) : e.getMessage()));
            responseBean.error(e);
        }

        return responseBean;
    }

    public static ResponseBean saveBookReadSetting(Request request,
                                             Response response) {
        ResponseBean responseBean = new ResponseBean();

        try {
            UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);
            BookReadSettingBean bookReadSettingBean = getParamsEntity(request, BookReadSettingBean.class, false, false);

            //读取读书配置
            BookReadSettingBean bookReadSettingBeanDB = BookReadSettingService.getUserReadSetting(userBean.getId());


            //读书配置不存在
            if (bookReadSettingBean == null) {
                responseBean.errorMessage("配置不存在");
                return responseBean;
            }
            //更新读书配置
            if (bookReadSettingBeanDB != null) {
                bookReadSettingBean.setId(bookReadSettingBeanDB.getId());
            }

            bookReadSettingBean.setUserId(userBean.getId());
            DBUtils.insertOrUpdate(bookReadSettingBean, false);

            //如果有读书记录则保存
            if (StringUtils.isNotBlank(bookReadSettingBean.getLastReadingChapter())
                    && bookReadSettingBean.getLastReadingChapterNum() != null
                    && bookReadSettingBean.getBookId() != null) {
                BookReadBean bookReadBean = BookReadService.getUserBookRead(userBean.getId(),
                        bookReadSettingBean.getBookId());
                bookReadBean.setLastReadingChapter(bookReadSettingBean.getLastReadingChapter());
                bookReadBean.setLastReadingChapterNum(bookReadSettingBean.getLastReadingChapterNum());
                DBUtils.insertOrUpdate(bookReadBean, false);
            }
        } catch (Exception e) {
            responseBean.error(e);
        }

        return responseBean;
    }

    public static PageBean search(Request request, Response response) {

        PageBean pageBean = getPageBean(request);
        try {
            UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);

            String keywords = request.queryParams("keywords");

            RowBounds rowBounds = pageBean.getRowBounds();
            rowBounds.setLimit(100);
            pageBean.setRowBounds(rowBounds);
            pageBean = BookService.requestSearchBook(userBean,
                    keywords, pageBean);
        } catch (Exception e) {
            pageBean.error(e);
        }
        return pageBean;
    }

    public static ResponseBean chapterList(Request request, Response response) {
        ResponseBean responseBean = new ResponseBean();

        try {
            String bookIdStr = request.queryParams("bookId");

            if (StringUtils.isBlank(bookIdStr)) {
                responseBean.errorMessage("bookId不可为空");
                return responseBean;
            }
            Long bookId = Long.valueOf(bookIdStr);

            BookBean bookBean = BookService.selectBookBeanById(bookId);
            UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);
            responseBean.setData(BookChapterService
                    .requestBookChapters(userBean.getId(), bookId, bookBean.getBookIdThird(), false));
        } catch (Exception e) {
            responseBean.error(e);
        }

        return responseBean;
    }

    public static ResponseBean addOrDelBook(Request request, Response response) {
        ResponseBean responseBean = new ResponseBean();

        try {
            UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);

            BookBean bookBean = getParamsEntity(request, BookBean.class, true, false);


            bookBean = BookService.requestBookDetail(userBean,
                    bookBean);

            if (bookBean == null) {
                return responseBean.errorMessage("书籍不存在");
            }

            //添加或更新书籍信息
            DBUtils.insertOrUpdate(bookBean, false);

            //增加到书架或删除书籍
            boolean isOnBookshelf = BookshelfService.addOrDelBookFromBookshelf(userBean, bookBean.getBookIdThird());

            responseBean.setData(isOnBookshelf);
        } catch (Exception e) {
            responseBean.error(e);
            return responseBean;
        }
        return responseBean;
    }

    public static ResponseBean bookSource(Request request, Response response) {

        ResponseBean responseBean = new ResponseBean();
        String bookIdThird = request.queryParams("bookIdThird");
        String title = request.queryParams("title");
        if (StringUtils.isBlank(bookIdThird)) {
            return responseBean.error(SystemError.PARAMS_ERROR);
        }

        UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);

        List<BookSource> list = BookService.getBookSource(userBean.getId(), bookIdThird);

        //查询bookRead获取当前书源
        BookBean bookBean = BookService.selectBookBeanByBookIdThird(bookIdThird);
        for (BookSource bookSource : list) {
            if (bookBean != null) {
                BookReadBean bookReadBean = BookReadService.getUserBookRead(userBean.getId(), bookBean.getId());
                if (bookReadBean != null
                        && bookSource.get_id().equals(bookReadBean.getSource())) {
                    bookSource.setCurrentSource(true);
                }
            }
        }
        JSONObject model = new JSONObject();
        model.put("list", list);

        model.put("bookIdThird", bookIdThird);
        model.put("bookId", bookBean.getId());
        model.put("title", title);

        responseBean.setData(model);
        return responseBean;
    }

    public static Object changeBookSource(Request request, Response response) {
        ResponseBean responseBean = new ResponseBean();
        String sourceId = request.queryParams("sourceId");
        String bookIdStr = request.queryParams("bookId");

        UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);

        try {
            Long bookId = Long.valueOf(bookIdStr);
            BookReadBean bookReadBean = BookReadService
                    .getUserBookRead(userBean.getId(), bookId);

            bookReadBean.setSource(sourceId);
            DBUtils.insertOrUpdate(bookReadBean, false);
        } catch (Exception e) {
            responseBean.error(e);
        }


        return responseBean.serialize();
    }

    public static Object saveBookRead(Request request,
                                             Response response) {
        ResponseBean responseBean = new ResponseBean();

        try {
            UserBean userBean = request.session().attribute(Constants.SESSION_USER_KEY);
            BookReadBean bookReadBean = getParamsEntity(request, BookReadBean.class, false, false);

            bookReadBean.setUserId(userBean.getId());

            if (bookReadBean.getId() == null) {
                BookReadBean bookReadBeanDB = BookReadService.getUserBookRead(userBean.getId(),
                        bookReadBean.getBookId());
                bookReadBean.setId(bookReadBeanDB.getId());
            }
            DBUtils.insertOrUpdate(bookReadBean, false);
        } catch (Exception e) {
            responseBean.error(e);
        }

        return responseBean.serialize();
    }
}
