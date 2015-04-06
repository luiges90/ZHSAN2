package com.zhsan.common.exception;

/**
 * Created by Peter on 7/3/2015.
 */
public class FileWriteException extends RuntimeException {

    public FileWriteException(String msg) {
        super(msg);
    }

    public FileWriteException(String file, Exception cause) {
        super("Failed to save: " + file, cause);
    }

}
