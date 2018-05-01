package com.puyixiaowo.fbook.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.puyixiaowo.fbook.Routes;
import com.puyixiaowo.fbook.bean.sys.AppConfigBean;
import com.puyixiaowo.fbook.error.ErrorHandler;
import com.puyixiaowo.fbook.utils.AppUtils;
import com.puyixiaowo.fbook.utils.ConfigUtils;
import com.puyixiaowo.generator.utils.CustomIdSerializer;

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
    }
}
