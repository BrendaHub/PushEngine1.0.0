package com.binggou.mission.common;

import java.util.Iterator;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 面向将来发送平台的任务扩展，针对不通的处理任务，如短信发送任务和短信发送任务。 任务接口抽象于这些任务，规范将来系统兼容的处理任务，利于平台的任务扩展。
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */


public interface TaskAccessible
{

    /**
     * 设置任务ID
     * 
     * @param taskId 任务ID
     */
    public void setTaskId(String taskId);

    /**
     * 得到任务ID
     * 
     * @return 任务ID
     */
    public String getTaskId();

    /**
     * 增加属性
     * 
     * @param attribName 属性名称
     * @param attribValue 属性值
     * @return 操作成功返回true,操作失败返回false
     */
    public boolean addAttrib(String attribName, Object attribValue);

    /**
     * 删除指定的属性
     * 
     * @param attribName 属性名称
     * @return 操作成功返回true,操作失败返回false
     */
    public boolean removeAttrib(String attribName);

    /**
     * 得到指定任务属性值。
     * 
     * @param attribName 属性名称
     * @return 属性值
     */
    public Object getAttribValue(String attribName);

    /**
     * 得到任务对象属性名称的遍历器
     * 
     * @return 属性名称的遍历器
     */
    public Iterator<String> iteratorAttribsName();
}