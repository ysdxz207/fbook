package com.puyixiaowo.fbook.exception;

/**
 * @author Moses
 * @date 2017-08-30
 */
public class NoPermissionsException extends RuntimeException {
    public NoPermissionsException() {
        super();
    }

    public NoPermissionsException(String message) {
        super(message);
    }
}
