package com.puyixiaowo.fbook.exception;

/**
 * @author Moses
 * @date 2017-09-03
 */
public class BaseControllerException extends RuntimeException {
    public BaseControllerException() {
    }

    public BaseControllerException(String message) {
        super(message);
    }
}
