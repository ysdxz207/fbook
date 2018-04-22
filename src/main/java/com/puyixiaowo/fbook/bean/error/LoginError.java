package com.puyixiaowo.fbook.bean.error;

public enum LoginError implements Error{

    LOGIN_NO_USERNAME("请输入用户名"),
    LOGIN_NO_PASSWORD("请输入密码"),
    LOGIN_NO_CAPTCHA("请输入验证码"),
    LOGIN_WRONG_CAPTCHA("验证码错误"),
    LOGIN_WRONG_PASSWORD("用户名或密码不正确");


    public String msg;

    LoginError(String msg) {
        this.msg = msg;
    }

    @Override
    public String getCode() {
        return this.name();
    }

    @Override
    public String getMsg() {
        return this.msg;
    }
}
