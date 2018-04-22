package com.puyixiaowo.fbook.bean.error;

public enum SystemError implements Error{

    PARAMS_ERROR("参数错误");


    public String msg;

    SystemError(String msg) {
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
