package com.hitwh.reggie.common;

/**
 * 自定义的业务异常
 */
public class CustomException extends RuntimeException{
    public CustomException(String message){
        super(message);
    }
}
