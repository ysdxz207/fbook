package com.puyixiaowo.fbook.exception;

/**
 * @author feihong
 * @date 2017-08-09
 */
public class DBObjectExistsException extends RuntimeException {

    public DBObjectExistsException(String message) {
        super(message);
    }
}
