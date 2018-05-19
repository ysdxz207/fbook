package com.puyixiaowo.fbook.bean.error;

public enum UserError implements Error{

    NOT_MATCH_ERROR("不能操作其他帐号。"),
    NOT_EXISTS_ERROR("帐号不存在。");


    public String msg;

    UserError(String msg) {
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
