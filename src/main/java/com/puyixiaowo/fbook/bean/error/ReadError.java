package com.puyixiaowo.fbook.bean.error;

public enum ReadError implements Error{

    READ_LAST_CHAPTER_ERROR("已经是最后一章啦！"),
    READ_SOURCE_ERROR("貌似书源有问题，再试试看。");


    public String msg;

    ReadError(String msg) {
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
