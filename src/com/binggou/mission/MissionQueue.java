package com.binggou.mission;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import com.binggou.mission.common.Queueble;
import com.binggou.mission.common.TaskAccessible;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 任务队列对象为平台存放处理任务，考虑平台对任务的访问是多线程的， 所以任务队列对象提供线程安全的访问模式
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

public class MissionQueue implements Queueble
{
    protected LinkedList<TaskAccessible> taskList = null;//任务存放列表
    private int waitTime = 10;//wait的时间
    
    /**
     * 构造函数
     */
    public MissionQueue(int wTime)
    {
        taskList = new LinkedList<TaskAccessible>();
        waitTime = wTime;
    }

    /**
     * 得到当前任务队列中的任务数量
     * 
     * @return 任务数量
     */
    public int getCount()
    {
        return taskList.size();
    }

    /**
     * 清空当前队列中所有任务
     */
    public synchronized void clearTasks()
    {
        while (!taskList.isEmpty())
            taskList.removeFirst();
    }

    /**
     * 向任务队列提交一个新任务，增加到列表末尾；该方法是线程安全的
     * 
     * @param task 任务句柄
     * @return 操作成功返回true,操作失败返回false
     */
    public synchronized boolean putTask(TaskAccessible task)
    {
        if (!(task instanceof TaskAccessible))
            return false;
        //判断Task对象是否重复
        for (Iterator<TaskAccessible> iterator = taskList.iterator(); iterator.hasNext();)
        {
            if (iterator.next().equals(task))
            {
//            	System.out.println("\n榨取到已存在任务");
            	iterator.remove();//如果已经存在了，就删掉
                return false;
            }
        }
//        System.out.println("\nput task to queue: " + task.getTaskId() + " @ " + new java.util.Date());
        taskList.addLast(task);
        //提交任务成功后，通知其它等待队列的线程
        notifyAll();
        return true;
    }
    
    /**
     * 向任务队列提交一个新任务，增加到列表开头；该方法是线程安全的
     * 
     * @param task 任务句柄
     * @return 操作成功返回true,操作失败返回false
     */
    public synchronized boolean putTaskHead(TaskAccessible task)
    {
        if (!(task instanceof TaskAccessible))
            return false;
        //判断Task对象是否重复
        for (Iterator<TaskAccessible> iterator = taskList.iterator(); iterator.hasNext();)
        {
            if (iterator.next().equals(task))
            {
//            	System.out.println("\n榨取到已存在任务");
                return false;
            }
        }
//        System.out.println("\nput task to queue: " + task.getTaskId() + " @ " + new java.util.Date());
        taskList.addFirst(task);
        //提交任务成功后，通知其它等待队列的线程
        notifyAll();
        return true;
    }
    
    /**
     * 从任务队列顶部取得一个新任务;该方法是线程安全的
     * 
     * @return 取得的任务句柄
     */
    public synchronized TaskAccessible getTask()
    {
        if (taskList.size() == 0)
        {
            //任务队列为空，等待新任务
            try
            {
                wait(this.waitTime); //最长等待5秒
            }
            catch (InterruptedException e)
            {
            }
        }
        //如果还没有待处理任务，直接返回null
        if (taskList.size() == 0)
            return null;
        return taskList.removeFirst();
    }
    
    /**
     * 查看任务队列顶部任务，但不删除；该方法是线程安全的。
     * 
     * @return 取得的任务句柄
     */
    public synchronized TaskAccessible peekTask()
    {
        if (taskList.size() == 0)
        {
            //任务队列为空，等待新任务
            try
            {
                wait(5000); //最长等待5秒
            }
            catch (InterruptedException e)
            {
            }
        }
        //如果还没有待处理任务，直接返回null
        if (taskList.size() == 0)
            return null;
        return taskList.peek();
    }

    /**
     * 从任务队列顶部取得一个新任务；该方法是线程安全的。
     * 
     * @param taskId 指定的任务ID
     * @return 取的的任务句柄，如果失败返回NULL
     */
    public synchronized TaskAccessible getTask(String taskId)
    {
        //判断参数有效性
        if (taskId == null)
            return null;
        TaskAccessible task = null;
        //遍历整个任务队列，查看是否有指定ID的任务，如果有返回，并从任务队列中删除
        ListIterator<TaskAccessible> iterator = taskList.listIterator();
        while (iterator.hasNext())
        {
            task = iterator.next();
            if (task.getTaskId().equals(taskId))
            {
                iterator.remove();
                break;
            }
        }
        return task;
    }
}