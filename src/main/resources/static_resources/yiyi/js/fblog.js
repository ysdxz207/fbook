var fblog = {
    BASE_PATH: $('#base_path').val(),
    $tagTop: $('.yiyi-tag-top'),
    $articleListContainer: $('#fblog_article_list_container'),
    $containerWidgetTags: $('#container_widget_tags'),
    $containerWidgetCategories: $('#container_widget_categories'),
    colors: ["#23D160",
            "#FF3860",
            "#72D0EB",
            "#FFDD57",
            "#4A4A4A",
            "#DC6BDB",
            "#FFB11B",
            "#13CCAB",
            "#FF634B"]
};

(function (fblog) {

    fblog.loadTopTags = function () {
        $.getJSON(fblog.BASE_PATH + "/tag/top?num=20", function (data) {
            if (!data) {
                return;
            }
            fblog.$containerWidgetTags.empty();

           $.each(data, function (i, tag) {
               var delay = 300 + 50 * i;
               var $tag = fblog.makeTag(tag, delay)
               fblog.$containerWidgetTags.append($tag);
               $tag.fadeIn(700);
           });
        })
    };

    /**
     * 获取随机颜色
     * @returns {*}
     */
    fblog.getRandomColor = function () {
        var randomIndex = Math.floor(Math.random() * fblog.colors.length);
        var color = fblog.colors[randomIndex];
        return color;
    };

    fblog.bind = function () {

        //搜索
        $(document).on('click', '#btn_fblog_search,#btn_fblog_search_nav', function () {
            if (fblog.checkSearch()) {

                $('form[role=search]').submit();
            }
        });
    };

    fblog.loadCategories = function (pageCurrent) {
        $.getJSON(fblog.BASE_PATH + "/category/list", {
            pageCurrent: pageCurrent,
            pageSize: 6
        }, function (data) {
            if (data.list.length > 0) {
                fblog.$containerWidgetCategories.find('.more').remove();
                $.each(data.list, function (i, category) {
                    var delay = 300 + i *50;
                    var $category = fblog.makeCategory(category, delay);
                    fblog.$containerWidgetCategories.append($category);
                });
                //加载更多
                if (data.pageCurrent < data.pageTotal) {
                    var $category = $('<a href="javascript:fblog.loadCategories(' + (pageCurrent + 1) + ')" class="list-group-item text-center more">加载更多...</a>');
                    fblog.$containerWidgetCategories.append($category);
                }
            }
        });
    };

    fblog.loadArticleTags = function () {
        var $articleDetailTagsContent = $('#article_detail_tags_content');
        var articleId = $('#hidden_article_detail_article_id').val();
        if ($articleDetailTagsContent) {
            $.getJSON(fblog.BASE_PATH + "/article/tags?articleId=" + articleId, function (data) {
                if (data.statusCode != 200) {
                    return;
                }
                $.each(data.data, function (i, tag) {
                    var $tag = fblog.makeTag(tag)
                    $articleDetailTagsContent.append($tag);
                    $tag.fadeIn();
                });
            });
        }
    };

    /**
     * 组装tag标签
     * @param tag
     * @returns {*|jQuery}
     */
    fblog.makeTag = function (tag, delay) {
        var $tag = $('<span class="label label-default aos-item" ' +
            'data-aos="flip-left" data-aos-once="true" ' +
            'data-aos-offset="0" data-aos-delay="' + delay + '" ' +
            'style="display: inline-block;border-radius: 1em;' +
            'margin-left: 6px;margin-bottom: 4px;' +
            'font-size: 100%;font-weight: 100;' +
            'line-height: inherit;"><a></a></span>');
        var color = fblog.getRandomColor();
        var href = fblog.BASE_PATH + '/?tags=' + tag.name;
        $tag.css('background-color', color);
        $tag.find('a').attr('href', href).text(tag.name);
        return $tag;
    };

    /**
     * 组装分类标签
     * @param tag
     * @returns {*|jQuery}
     */
    fblog.makeCategory = function (category, delay) {
        var $category = $('<a class="list-group-item aos-item" data-aos="fade-left" data-aos-once="true" data-aos-offset="0" data-aos-delay="' + delay + '"></a>');
        var href = fblog.BASE_PATH + '/?category=' + category.name;
        $category.attr('href', href);
        $category.text(category.name);
        return $category;
    };

    fblog.init = function () {
        AOS.init({
            easing: 'ease-in-out-sine'
        });
        fblog.bind();
        fblog.loadTopTags();
        fblog.loadCategories(1);

        //detail
        fblog.loadArticleTags();
    };

    fblog.checkSearch = function () {
        var search = $('#input_fblog_search').val(),
            searchNav = $('#input_fblog_search_nav').val();
        if (!search && !searchNav) {
            return false;
        }
        return true;
    }

    fblog.init();
})(fblog);

