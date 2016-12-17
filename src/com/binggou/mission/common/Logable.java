package com.binggou.mission.common;

/**
 * <p>Title: 发送任务处理平台</p>
 * <p>Description:
 * 面向将来发送平台的任务扩展，针对不同的处理任务，如短信发送任务和短信发送任务。
 * 日志接口与其它接口一起，提供统一的接口规范，利于平台的任务扩展。
 * 日志接口主要是定义平台对象记录日志信息的规范。
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */


public interface Logable
{
   /**
    * 记录系统调试信息日志
    * @param obj 调试信息
    */
   public void debug(Object obj);

   /**
    * 记录系统运行信息日志
    * @param obj 运行信息
    */
   public void runtime(Object obj);

   /**
    * 记录系统错误信息日志
    * @param obj 错误信息
    */
   public void error(Object obj);  
   
   /**
    * 记录系统错误信息日志
    * @param obj 错误信息
    */
   public void error(String error, StackTraceElement[] elms);
}