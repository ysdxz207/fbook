var bookMenu = {
    contentY: 0
};

(function (bookMenu) {

    /**
     */
    bookMenu.isShow = function () {
        return bookMenu.menuObj.css('display') != 'none';
    };

    bookMenu.toggle = function () {
        bookMenu.menuObj.toggle();
        $('.book-menu-footer-font-setup').hide();
        $('.book-menu-footer').show();
    };

    bookMenu.bind = function () {

        $('.btn-back-content').on('click', function (e) {
            //滚动回内容记录位置,因为章节列表菜单和章节内容在统一页面，所以直接是html,body
            $('html,body').scrollTop(bookMenu.contentY);

            $('#book_menu_chapters').hide();
            $('.book-content-main').show();
        });

        $('#btn_book_menu_font_chapters').on('click', function () {
            //记录一下当前内容滚动位置，当从章节列表返回时再滚动到这个位置
            bookMenu.contentY = $(document).scrollTop();
            //进入章节列表时，把章节列表滚动到顶
            $('html,body').scrollTop(0);

            $('.book-content-main').hide();
            $('#book_menu').hide();
            $('#book_menu_chapters').show();
        });



        $('#btn_reverse_book_chapters').on('click', function() {
            var sort = parseInt(window.localStorage['fblog_tools_book_sort']) ? 0 : 1;
            console.log(sort);
            var ul = $('#book_chapters_ul');
            var lis = ul.find('li').get().reverse();
            ul.empty().append(lis);
            window.localStorage['fblog_tools_book_sort'] = sort;
            //保存配置
            bookContent.saveReadSetting();
        });

        //字体设置按钮
        $('#btn_book_menu_font_setup').on('click', function () {
            $('.book-menu-footer').hide();
            $('.book-menu-footer-font-setup').show();
        });

        $('.btn-font-big').on('click', function () {
            var chapterContent = $('.book-chapter-content');
            var fontSize = chapterContent.css('font-size');
            fontSize = parseInt(fontSize);
            fontSize += 3;
            if (fontSize > 29) {
                salert('已经是最大字体了');
                return;
            }
            chapterContent.css('font-size', fontSize + 'px');
            bookContent.fontSize = fontSize;
            //保存配置
            bookContent.saveReadSetting();
        });

        $('.btn-font-small').on('click', function () {
            var chapterContent = $('.book-chapter-content');
            var fontSize = chapterContent.css('font-size');
            fontSize = parseInt(fontSize);
            fontSize -= 3;
            if (fontSize < 9) {
                salert('已经是最小字体了');
                return;
            }
            chapterContent.css('font-size', fontSize + 'px');
            bookContent.fontSize = fontSize;
            //保存配置
            bookContent.saveReadSetting();
        });

        $('.btn-line-height-small').on('click', function () {
            var chapterContent = $('.book-chapter-content');
            var lineHeight = chapterContent.css('line-height');
            var fontSize = chapterContent.css('font-size');

            lineHeight = parseInt(lineHeight);
            lineHeight -= 1;
            if (lineHeight < parseInt(fontSize)) {
                salert('已经是最小行距了');
                return;
            }
            chapterContent.css('line-height', lineHeight + 'px');
            bookContent.lineHeight = lineHeight;
            //保存配置
            bookContent.saveReadSetting();
        });

        $('.btn-line-height-big').on('click', function () {
            var chapterContent = $('.book-chapter-content');
            var lineHeight = chapterContent.css('line-height');
            var fontSize = chapterContent.css('font-size');
            lineHeight = parseInt(lineHeight);
            lineHeight += 1;
            if (lineHeight > parseInt(fontSize) * 2) {
                salert('已经是最大行距了');
                return;
            }
            chapterContent.css('line-height', lineHeight + 'px');
            bookContent.lineHeight = lineHeight;
            //保存配置
            bookContent.saveReadSetting();
        });

        $('.btn-color-group button').on('click', function () {
            var btn = $(this);
            var color = btn.css('background-color');
            $('.book-content-main').css('background-color', color);

            bookContent.bgColor = color;
            //保存配置
            bookContent.saveReadSetting();
        });

        //翻页方式
        $("#check_page_method").bootstrapSwitch({
            labelText: '翻页方式',
            onText: '上下',
            offText: '左右',
            onColor: 'success',
            offColor: 'warning',
            size: 'mini',
            labelWidth: '80',
            state: $("#check_page_method").val() == 1 ? true : false,
            onSwitchChange: function (event, state) {
                var pageMethod = state ? '1' : '0';
                window.localStorage['fblog_tools_book_pageMethod'] = pageMethod;
                //保存配置
                bookContent.saveReadSetting();
            }
        });

    };

    bookMenu.loada = function () {
        console.log();
    };

    /**
     * 预加载章节列表
     */
    bookMenu.preLoadChapters = function() {
        tools.createFloatPage(data);
    };

    bookMenu.init = function () {
        bookMenu.menuObj = $('#book_menu');
        bookMenu.bind();
    };

    bookMenu.init();
})(bookMenu);

