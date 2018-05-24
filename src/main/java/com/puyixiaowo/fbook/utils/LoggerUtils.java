package com.puyixiaowo.fbook.utils;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Moses
 * @date 2018-05-24 15:01:20
 * 日志工具
 * 此工具旨在解决日志记录异常时遇到空指针异常记录值为空问题
 */

public class LoggerUtils {

    /**
     * 获取日志记录对象，若有调用方则日志打印变量[%c]为调用类
     * 否则为LoggerUtils.class
     * @return
     */
    private static Logger getLogger() {
        String clazzCallerName = new Exception().getStackTrace()[1].getClassName();
        Class clazzCaller = null;
        try {
            clazzCaller = Class.forName(clazzCallerName);
        } catch (ClassNotFoundException e) {
        }


        if (clazzCaller == null) {
            return LoggerFactory.getLogger(LoggerUtils.class);
        }

        return LoggerFactory.getLogger(clazzCaller);
    }

    public static String getMessage(String message,
                                         Throwable t) {
        String msgException = t.getMessage() == null ? JSON.toJSONString(t)
                : t.getMessage();
        return StringUtils.isNotEmpty(message) ? (message + msgException) : msgException;
    }

    public static void debug(Throwable t) {
        debug(null, t);
    }

    public static void debug(String message,
                            Throwable t) {
        getLogger().debug(getMessage(message, t));
    }

    public static void info(Throwable t) {
        info(null, t);
    }

    public static void info(String message,
                            Throwable t) {
        getLogger().info(getMessage(message, t));
    }

    public static void error(Throwable t) {
        error(null, t);
    }

    public static void error(String message,
                            Throwable t) {
        getLogger().error(getMessage(message, t));
    }

}
