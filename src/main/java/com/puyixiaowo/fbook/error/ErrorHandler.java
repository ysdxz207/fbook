package com.puyixiaowo.fbook.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static spark.Spark.notFound;
/**
 *
 * @author Moses
 * @date 2018-03-14 14:54:58
 */

public class ErrorHandler {
    private static final Logger logger = LoggerFactory.getLogger(ErrorHandler.class);


    public static void init() {


    }

    /**
     * 处理错误信息
     */
    public static void handleSystemErrors() {

        handle404();
        handle500();
    }

    private static void handle404(){
        notFound((request, response) -> "404");
    }

    private static void handle500(){

//        exception(Exception.class, (exception, request, response) -> {
//
//
//        });
    }

}
