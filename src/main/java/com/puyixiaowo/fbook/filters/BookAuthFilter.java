package com.puyixiaowo.fbook.filters;

import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.controller.LoginController;
import com.puyixiaowo.fbook.enums.EnumsRedisKey;
import com.puyixiaowo.fbook.utils.RedisUtils;

import java.util.List;

import static spark.Spark.before;
import static spark.Spark.halt;

/**
 *
 * @author Moses
 * @date 2017-12-19
 * 书用户权限控制过滤器
 */
public class BookAuthFilter {



    public static void init() {
        //书
        before("/*", (request, response) -> {
            String uri = request.uri();
            if (!isIgnorePath(uri)
                    && (request.session().attribute(Constants.SESSION_USER_KEY) == null)) {

                LoginController.rememberMeLogin(Constants.COOKIE_LOGIN_KEY_BOOK,
                        request, response);
                halt();
            }
        });
    }


    private static boolean isIgnorePath(String uri) {

        List<String> ignores = RedisUtils.get(EnumsRedisKey.REDIS_KEY_IGNORE_CONF_BOOK.key,
                List.class);

        for (String path : ignores) {
            if (removeFirstSeparator(path).equals(removeFirstSeparator(uri))) {
                return true;
            }
        }
        return false;
    }

    private static String removeFirstSeparator(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        return path;
    }

}
