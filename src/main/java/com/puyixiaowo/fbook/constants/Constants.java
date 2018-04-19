package com.puyixiaowo.fbook.constants;

public class Constants {

    public static final String ENCODING = "UTF-8";
    /*
     * 后台用户session key
     */
    public static final String SESSION_USER_KEY = "fbook_session_user_key";
    public static final int DEFAULT_PAGE_SIZE = 10;
    /*
     * 验证码session key
     */
    public static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY_FBOOK";

    /**
     * book登录cookie key
     */
    public static final String COOKIE_LOGIN_KEY_BOOK = "COOKIE_LOGIN_KEY_BOOK";
    /*
     * 成功状态码
     */
    public static final int RESPONSE_STATUS_CODE_SUCCESS = 200;
    /*
     * 错误状态码
     */
    public static final int RESPONSE_STATUS_CODE_ERROR = 300;
    /*
     * 成功描述
     */
    public static final String RESPONSE_SUCCESS_MESSAGE = "操作成功";


    /*
     * 密码des密钥,值由ConfigUtils初始化从admin_auth.yaml中取
     */
    public static String PASS_DES_KEY = "20151106";
}
