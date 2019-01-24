package com.puyixiaowo.fbook.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.puyixiaowo.fbook.Routes;
import com.puyixiaowo.fbook.bean.sys.AppConfigBean;
import com.puyixiaowo.fbook.error.ErrorHandler;
import com.puyixiaowo.fbook.scheduler.CollectGoodBookScheduler;
import com.puyixiaowo.fbook.utils.AppUtils;
import com.puyixiaowo.fbook.utils.ConfigUtils;
import com.puyixiaowo.generator.utils.CustomIdSerializer;
import win.hupubao.common.scheduler.Scheduler;
import win.hupubao.common.utils.DateUtils;

import java.util.Date;

import static spark.Spark.port;

/**
 * @author Moses
 * @date 2017-08-01
 */
public class Main {

    /**
     * 支持启动设置端口：java -jar f-blog-1.0.jar -p 8010,
     * 默认启动端口8010
     *
     * @param args
     */
    public static void main(String[] args) {

        AppConfigBean config = AppUtils.getAppConfigBean(args);
        port(config.getPort());

        ConfigUtils.init();

        ErrorHandler.handleSystemErrors();
        Routes.init();

        //关闭循环引用
        JSON.DEFAULT_GENERATE_FEATURE |= SerializerFeature.DisableCircularReferenceDetect.getMask();
        //ID序列化为字符串类型
        SerializeConfig.getGlobalInstance().put(Long.class, new CustomIdSerializer());

        //搜集好书
        Long todayZeroMiliseconds = DateUtils.getZeroClockByDate(new Date()).getTime();
//        todayZeroMiliseconds += 20 * 60 * 60 * 1000L;
        todayZeroMiliseconds += 17 * 60 * 60 * 1000L + 17 * 60 * 1000;
        long delay = todayZeroMiliseconds - System.currentTimeMillis();
        Scheduler.runTimer("good-book-timer",
                delay, 24 * 60 * 60 * 1000L, CollectGoodBookScheduler::collect);
    }
}
