package com.zhsan.common.exception;

/**
 * Created by Peter on 7/3/2015.
 */
public class FileReadException extends RuntimeException {

    public FileReadException(String file, Exception cause) {
        super("Failed to load: " + file, cause);
    }

}
