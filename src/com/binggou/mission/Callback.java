package com.binggou.mission;

import com.binggou.mission.common.Callbackable;
import com.binggou.mission.common.ConfigAccessible;
import com.binggou.mission.common.Logable;
import com.binggou.mission.common.Queueble;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 实现了任务回调处理接口的基对象，对公共的方法提供了封装，具体回调业务处理对象 从其继承，简化代码
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */


public abstract class Callback implements Callbackable
{
    /**
     * 保存平台配置对象句柄
     */
    protected ConfigAccessible config = null;

    /**
     * 保存日志对象句柄
     */
    protected Logable log = null;

    /**
     * 保存正在处理任务队列句柄
     */
    protected Queueble processingQueue = null;

    /**
     * 保存待处理回调任务队列句柄
     */
    protected Queueble callbackQueue = null;

    /**
     * 构造函数
     */
    public Callback()
    {
    }

    /**
     * 设置系统配置对象
     * 
     * @param config 配置对象句柄
     */
    public void setConfig(ConfigAccessible config)
    {
        this.config = config;
    }

    /**
     * 设置系统日志对象
     * 
     * @param log 日志对象句柄
     */
    public void setLog(Logable log)
    {
        this.log = log;
    }

    /**
     * 设置正在处理任务存放队列
     * 
     * @param queue 队列句柄
     */
    public void setProcessingQueue(Queueble queue)
    {
        this.processingQueue = queue;
    }

    /**
     * 回调任务存放队列句柄
     * 
     * @param queue 队列句柄
     */
    public void setCallbackQueue(Queueble queue)
    {
        this.callbackQueue = queue;
    }
}