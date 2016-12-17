package com.binggou.mission.common;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 面向将来发送平台的任务扩展，针对不同的处理任务，如短信发送任务和短信发送任务。 任务队列接口与其它接口一起，提供统一的接口规范，利于平台的任务扩展。 任务队列接口主要是定义访问平台任务队列的规范。
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */


public interface Queueble
{
    /**
     * 得到当前任务队列中的任务数量
     * 
     * @return 任务数量
     */
    public int getCount();

    /**
     * 清空任务队列
     */
    public void clearTasks();

    /**
     * 向任务队列提交一个新任务，增加到列表末尾；该方法是线程安全的。
     * 
     * @param task 任务句柄
     * @return 操作成功返回true,操作失败返回false
     */
    public boolean putTask(TaskAccessible task);
    
    /**
     * 向任务队列提交一个新任务，增加到列表末尾；该方法是线程安全的。
     * 
     * @param task 任务句柄
     * @return 操作成功返回true,操作失败返回false
     */
    public boolean putTaskHead(TaskAccessible task);

    /**
     * 从任务队列顶部取得一个新任务；该方法是线程安全的。
     * 
     * @return 取的的任务句柄，如果失败返回NULL
     */
    public TaskAccessible getTask();
    
    /**
     *  查看任务队列顶部任务，但不删除；该方法是线程安全的。
     * 
     * @return 取的的任务句柄，如果失败返回NULL
     */
    public TaskAccessible peekTask();

    /**
     * 从任务队列顶部取得一个新任务；该方法是线程安全的。
     * 
     * @param taskId 指定的任务ID
     * @return 取的的任务句柄，如果失败返回NULL
     */
    public TaskAccessible getTask(String taskId);
}