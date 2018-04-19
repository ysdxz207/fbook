var bookDetail = {
};

(function (bookDetail) {

    bookDetail.bind = function () {

        $('.btn-add-book').on('click', function () {

            bookDetail.addOrDelBook(this);
        });
        $('.btn-del-book').on('click', function () {
            bookDetail.addOrDelBook(this);
        });
        $('.btn-read-book').on('click', function () {
            //
            var arr = JSON.parse(window.localStorage['last_link']);
            arr.push(window.location.href);
            window.localStorage['last_link'] = JSON.stringify(arr);
            var url = "/book/chapter?bookId=" + bookDetail.bookId;
            location.href = url;
        });
    };

    bookDetail.disabledBtn = function (btn) {
        $(btn).removeClass('btn-add-book')
            .removeClass('btn-del-book').addClass('btn-book-detail-disabled');
    }

    bookDetail.addOrDelBook = function (btn) {
        bookDetail.disabledBtn(btn)
        $(btn).text('处理中..');
        $.ajax({
            url: "/book/addOrDel",
            data: {aId: bookDetail.aId},
            method: "POST",
            dataType: "json",
            success: function (result) {
                if (result.statusCode == 200) {
                    if (result.data) {
                        $('.btn-book-detail-add-del').removeClass('btn-add-book')
                            .addClass('btn-del-book')
                            .removeClass('btn-book-detail-disabled').text('- 不追了');
                    } else {
                        $('.btn-book-detail-add-del').removeClass('btn-del-book')
                            .addClass('btn-add-book')
                            .removeClass('btn-book-detail-disabled').text('+ 追书');
                        //移出书架后删除书籍相关配置
                        window.localStorage.removeItem(bookDetail.bookId);
                        window.localStorage.removeItem('fblog_tools_book_sort');
                    }
                } else {
                    alert("服务器内部错误：" + result.message);
                }
            }
        });
    };

    bookDetail.init = function () {
        bookDetail.bookId = $('#hidden_book_detail_book_id').val();
        bookDetail.aId = $('#hidden_book_detail_book_aid').val();
        bookDetail.bind();
    };

    bookDetail.init();
})(bookDetail);

