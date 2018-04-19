package com.puyixiaowo.fbook.utils.captcha;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.puyixiaowo.fbook.utils.ResourceUtils;

/**
 * 
 * @author Moses
 * @date 2017-12-05
 * 
 */
public class CaptchaProducer extends DefaultKaptcha {

    private static final String PATH_CAPTCHA_BOOK = "conf/captcha_book.properties";
    private Config config;

    public CaptchaProducer() {

        String url = PATH_CAPTCHA_BOOK;
        this.config = new Config(ResourceUtils.load(url));
    }

    @Override
    public Config getConfig() {
        return this.config;
    }
}
