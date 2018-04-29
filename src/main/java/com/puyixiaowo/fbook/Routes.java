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
            get("/", ((request, response) ->
                    BookController.bookShelf(request, response)));
            post("/", ((request, response) ->
                    BookController.bookShelf(request, response)));
            get("/captcha.jpg", ((request, response) ->
                    LoginController.captcha(request, response)));
            post("/login", ((request, response) ->
                    LoginController.login(request, response)));
            post("/logout", ((request, response) ->
                    LoginController.logout(request, response)));
            post("/register", ((request, response) ->
                    LoginController.register(request, response)));

            post("/detail", ((request, response) ->
                    BookController.bookDetail(request, response)));

            post("/chapter", ((request, response) ->
                    BookController.chapterContent(request, response)));

            post("/saveReadingSetting", ((request, response) ->
                    BookController.saveBookReadSetting(request, response)));

            post("/search", ((request, response) ->
                    BookController.search(request, response)));
            post("/chapterList", ((request, response) ->
                    BookController.chapterList(request, response)));

            post("/addOrDel", ((request, response) ->
                    BookController.addOrDelBook(request, response)));

            post("/source", ((request, response) ->
                    BookController.bookSource(request, response)));

            post("/source/change", ((request, response) ->
                    BookController.changeBookSource(request, response)));
        });
    }
}
