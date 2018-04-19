var book = {};

(function (book) {

    book.bind = function () {
        $('.btn-back').on('click', function (e) {
            sloading();
            var urls = window.localStorage['last_link'];
            var url = '/book/index';
            if (urls) {
                var arr = JSON.parse(urls);
                url = arr.pop();
                window.localStorage['last_link'] = JSON.stringify(arr);
            }
            location.href = url;
        });

        $('.btn-go-source').on('click', function (e) {
            var aid = $(this).data('aid');
            var url = '/book/source?aId=' + aid;
            var urls = JSON.parse(window.localStorage['last_link']);
            urls.push(window.location.href);
            window.localStorage['last_link'] = JSON.stringify(urls);
            location.href = url;
        });

    };

    book.init = function () {
        book.bind();
    };

    book.init();
})(book);

