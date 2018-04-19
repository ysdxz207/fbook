package com.puyixiaowo.fbook.controller;

import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.mustache.MustacheTemplateEngine;

/**
 *
 * @author Moses.wei
 * @date 2018-03-13 23:03:10
 *
 */
public class ErrorController extends BaseController {

    public static Object error404(Request request, Response response) {
        return new MustacheTemplateEngine()
                .render(new ModelAndView(null, "error/404.html"));
    }

    public static Object error500(Request request, Response response) {
        return new MustacheTemplateEngine()
                .render(new ModelAndView(null, "error/500.html"));
    }
}
