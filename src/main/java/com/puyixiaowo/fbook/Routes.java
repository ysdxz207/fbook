package com.puyixiaowo.fbook;

import com.puyixiaowo.fbook.controller.BookController;
import com.puyixiaowo.fbook.controller.LoginController;
import com.puyixiaowo.fbook.filters.BookAuthFilter;
import spark.Spark;

import static spark.Spark.*;

public class Routes {
    public static void init() {
        Spark.staticFileLocation("static_resources");
        BookAuthFilter.init();


        //book
        path("/", () -> {
            get("/captcha.jpg", ((request, response) ->
                    LoginController.captcha(request, response)));
            post("/login", ((request, response) ->
                    LoginController.login(request, response)));

            get("/", ((request, response) ->
                    BookController.userBooks(request, response)));

            get("/detail", ((request, response) ->
                    BookController.bookDetail(request, response)));

            get("/chapter", ((request, response) ->
                    BookController.chapterContent(request, response)));

            post("/saveReadingSetting", ((request, response) ->
                    BookController.saveBookReadSetting(request, response)));

            get("/searchPage", ((request, response) ->
                    BookController.searchPage(request, response)));

            get("/search", ((request, response) ->
                    BookController.search(request, response)));
            post("/chapters", ((request, response) ->
                    BookController.chapters(request, response)));

            post("/addOrDel", ((request, response) ->
                    BookController.addOrDelBook(request, response)));

            get("/source", ((request, response) ->
                    BookController.bookSource(request, response)));

            post("/source/change", ((request, response) ->
                    BookController.changeBookSource(request, response)));

        });
    }
}
