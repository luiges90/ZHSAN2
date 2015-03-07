package com.zhsan.common.exception;

/**
 * Created by Peter on 7/3/2015.
 */
public class XmlException extends RuntimeException {

    public XmlException(String file, Exception cause) {
        super("Failed to load: " + file, cause);
    }

}
