package com.coder520.mamabike.common.exception;

/**
 * Created by JackWangon[www.coder520.com] 2017/7/31.
 */
public class MaMaBikeException extends Exception{

    /**
     *
     */
    private static final long serialVersionUID = -7370331410579650067L;

    public MaMaBikeException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return 500;
    }
}
