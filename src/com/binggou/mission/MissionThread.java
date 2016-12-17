package com.binggou.mission;

import com.binggou.mission.common.ConfigAccessible;
import com.binggou.mission.common.Queueble;
import com.binggou.mission.common.Logable;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description:
 * </p>
 * 操作线程基类，提供平台操作线程公共操作，其它线程对象从其派生，简化代码
 * <p>
 * @author chenhj(brenda)
 * @version 1.0
 */
public abstract class MissionThread extends Thread
{
    /**
     * 标示是否被请求退出
     */
    protected boolean isRequiredQuit = false;

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
     * 保存待处理任务队列句柄
     */
    protected Queueble extractQueue = null;

    /**
     * 保存待处理回调任务队列句柄
     */
    protected Queueble callbackQueue = null;

    /**
     * 线程处理间隔
     */
    protected int processInterval = 20;

    /**
     * 构造函数
     */
    public MissionThread()
    {
    }

    /**
     * 设置系统配置对象
     * @param config 配置对象句柄
     */
    public void setConfig(ConfigAccessible config)
    {
        this.config = config;
    }

    /**
     * 设置系统日志对象
     * @param log 日志对象句柄
     */
    public void setLog(Logable log)
    {
        this.log = log;
    }

    /**
     * 设置正在处理任务存放队列
     * @param queue 队列句柄
     */
    public void setProcessingQueue(Queueble queue)
    {
        this.processingQueue = queue;
    }

    /**
     * 设置榨取任务存放队列
     * @param queue 队列句柄
     */
    public void setExtractQueue(Queueble queue)
    {
        this.extractQueue = queue;
    }

    /**
     * 回调任务存放队列句柄
     * @param queue 队列句柄
     */
    public void setCallbackQueue(Queueble queue)
    {
        this.callbackQueue = queue;
    }

    /**
     * 请求退出,修改线程循环处理标示
     */
    public void requireQuit(boolean flag)
    {
        this.isRequiredQuit = flag;
    }

    /**
     * 设置线程处理间隔
     * @param interval 间隔时间
     */
    public void setProcessInterval(int interval)
    {
        this.processInterval = interval;
    }
}