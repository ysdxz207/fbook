var bookContent = {
    bookId: undefined,
    lastReadingChapter: '',
    lastReadingChapterLink: ''
};

(function (bookContent) {

    /**
     * 触摸滚动
     */
    bookContent.tapScroll = function () {

        $('.book-chapter-content').on('click', function (e) {

            var pageMethod = window.localStorage['fblog_tools_book_pageMethod']
                ? window.localStorage['fblog_tools_book_pageMethod'] : 1;

            var delay = 10;
            var lineHeight = 28;
            var isTop = $(document).scrollTop() < 10;
            var isBottom = ($(document).height() -
                ($(window).height() + $(document).scrollTop())) == 0;

            var tapX = e.clientX;
            var tapY = e.clientY;
            var tap = pageMethod == "1" ? tapY : tapX;

            var width = screen.width;
            var height = screen.height;
            var widthOrHeight = pageMethod == "1" ? height : width;

            //点击屏幕中央唤起菜单
            if (tap < (widthOrHeight / 3 * 2) && tap > (widthOrHeight / 3 * 1)) {
                bookMenu.toggle();
                return;
            }

            if (bookMenu.isShow()) {
                return;
            }

            if ((tap > (widthOrHeight / 3 * 2))
                && !isBottom) {
                //向下滚动
                $('html,body')
                    .animate({scrollTop: $(document).scrollTop() + height - lineHeight}, delay);
                return;
            }

            if (tap < (widthOrHeight / 3 * 1)
                && !isTop) {
                //向上滚动
                $('html,body')
                    .animate({scrollTop: $(document).scrollTop() - height + lineHeight}, delay);
                return;
            }

        });
    };


    bookContent.saveReadSetting = function () {
        var bookReadSetting = {};
        bookReadSetting.sort = window.localStorage['fblog_tools_book_sort']
            ? window.localStorage['fblog_tools_book_sort'] : 0;

        bookReadSetting.fontSize = bookContent.fontSize;
        bookReadSetting.bgColor = bookContent.bgColor;
        bookReadSetting.fontSize = bookContent.fontSize;
        bookReadSetting.lineHeight = bookContent.lineHeight;
        bookReadSetting.pageMethod = window.localStorage['fblog_tools_book_pageMethod']
            ? window.localStorage['fblog_tools_book_pageMethod'] : 1;

        //保存到后端
        $.ajax({
            url: "/book/saveReadingSetting",
            data: bookReadSetting,
            method: "POST",
            dataType: "json",
            success: function (result) {
                if (result.statusCode == 200) {

                } else {
                    salert(result.message);
                }
            }
        });
    };

    bookContent.loadChapters = function () {
        //章节列表
        $.ajax({
            url: "/book/chapters",
            data: {bookId: bookContent.bookId},
            method: "POST",
            dataType: "json",
            success: function (result) {
                if (result.statusCode == 200) {
                    result.data.forEach(function (chapter, number) {
                        var hasReadClass = (chapter.hasRead || chapter.title == bookContent.lastReadingChapter) ? 'has-read' : '';

                        var url = "/book/chapter?bookId=" + bookContent.bookId + "&chapterName=" + chapter.title;
                        var li = $('<li class="' + hasReadClass + '"><a href="' + url + '" class="loading">'+ chapter.title + '</a></li>');

                        $('#book_chapters_ul').append(li);
                    });

                } else {
                    salert(result.message);
                }
            }
        });

    };

    bookContent.preloadNextPage = function () {
        $('.btn-book-content-next-chapter').preload();
    };
    bookContent.init = function () {

        bookContent.bookId = $('#hidden_book_content_book_id').val();
        bookContent.lastReadingChapter = $('#hidden_book_content_reading_chapter').val();
        bookContent.lastReadingChapterLink = $('#hidden_book_content_reading_chapter_link').val();


        // bookContent.loadChapters();
        bookContent.tapScroll();

        // bookContent.saveReadSetting();

        // bookContent.preloadNextPage();
    };

    bookContent.init();
})(bookContent);

