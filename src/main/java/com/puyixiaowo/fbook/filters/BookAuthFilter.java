package com.puyixiaowo.fbook.filters;

import com.puyixiaowo.fbook.constants.Constants;
import com.puyixiaowo.fbook.controller.LoginController;
import com.puyixiaowo.fbook.enums.EnumsRedisKey;
import com.puyixiaowo.fbook.utils.RedisUtils;
import com.puyixiaowo.fbook.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
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

    private static final Logger logger = LoggerFactory.getLogger(BookAuthFilter.class);

    public static void init() {
        //书
        before("/*", (request, response) -> {

//            String origin = request.headers("Origin");
//            if (StringUtils.isNotBlank(origin)) {
//                String originAllowed = Arrays.asList(Constants.ALLOWED_ORIGINS).contains(origin) ? origin : "";
//                logger.info("[" + origin + "][" + originAllowed + "]");
//                response.header("Access-Control-Allow-Origin", originAllowed);
//                response.header("Access-Control-Allow-Methods", "PUT,POST,GET,DELETE,OPTIONS");
//                response.header("Access-Control-Allow-Headers", "X-Requested-With,Content-Type");
//                response.header("Access-Control-Allow-Credentials", "true");
//
//            }


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
