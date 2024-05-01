package com.hitwh.reggie.common;

/**
 * 基于threadLocal封装工具类，用于保存当前登录用户的id
 */
public class BaseContext {
    private static ThreadLocal<Long> threadLocal =new ThreadLocal<>();

    public static void setCurrentid(Long id){
        threadLocal.set(id);
    }
    public static Long getCurrentid(){
        return threadLocal.get();
    }
}
