package com.binggou.mission.common;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 面向将来发送平台的任务扩展，针对不通的处理任务，如短信发送任务和短信发送任务。 任务回调处理接口抽象于这些任务，规范将来系统兼容的处理任务，利于平台的任务扩展。 任务回调处理接口主要是定义任务回调处理对象的操作规范。
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

public interface Callbackable
{
	/**
	 * 设置系统配置对象
	 * 
	 * @param config
	 *            配置对象句柄
	 */
	public void setConfig(ConfigAccessible config);

	/**
	 * 设置系统日志对象
	 * 
	 * @param log
	 *            日志对象句柄
	 */
	public void setLog(Logable log);

	/**
	 * 设置正在处理任务存放队列
	 * 
	 * @param queue
	 *            队列句柄
	 */
	public void setProcessingQueue(Queueble queue);

	/**
	 * 回调任务存放队列句柄
	 * 
	 * @param queue
	 *            队列句柄
	 */
	public void setCallbackQueue(Queueble queue);

	/**
	 * 任务回调处理对象初始化
	 * 
	 * @return 初始化是否成功
	 */
	public boolean init();

	/**
	 * 任务回调处理
	 * 
	 * @return 操作成功返回true,操作失败返回false
	 */
	public boolean callback();
}