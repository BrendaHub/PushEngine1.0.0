package com.binggou.mission;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.binggou.mission.common.TaskAccessible;
import com.binggou.sms.mission.core.about.util.SmsplatGlobalVariable;
import com.eaio.uuid.UUID;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 实现了任务接口的基对象，对公共的方法提供了封装，具体任务对象 从其继承，简化代码。
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

public class Task implements TaskAccessible
{
    /**
     * 任务ID
     */
    private String taskId = null;

    /**
     * 存放任务属性名称及其对应值
     */
    private HashMap<String, Object> attribsMap = null;
    
    /**
     * 推送所使用的手机号(pushToken)
     */
    private String mobileTo = null;

    /**
     * 构造函数
     */
    public Task()
    {
        attribsMap = new HashMap<String, Object>(100);
    }

    /**
     * 设置任务ID
     * 
     * @param taskId 任务ID
     */
    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    /**
     * 得到任务ID
     * 
     * @return 任务ID
     */
    public String getTaskId()
    {
        return taskId;
    }
    
    

    public String getMobileTo() {
		return mobileTo;
	}

	public void setMobileTo(String mobileTo) {
		this.mobileTo = mobileTo;
	}

	/**
     * 增加属性
     * 
     * @param attribName 属性名称
     * @param attribValue 属性值
     * @return 操作成功返回true,操作失败返回false
     */
    public boolean addAttrib(String attribName, Object attribValue)
    {
        if (attribValue instanceof String)
            attribsMap.put(attribName.toLowerCase(), ((String) attribValue).trim());
        else
            attribsMap.put(attribName.toLowerCase(), attribValue);
        return true;
    }

    /**
     * 删除指定的属性
     * 
     * @param attribName 属性名称
     * @return 操作成功返回true,操作失败返回false
     */
    public boolean removeAttrib(String attribName)
    {
        attribsMap.remove(attribName.toLowerCase());
        return true;
    }

    /**
     * 得到指定任务对象属性对应的值。
     * 
     * @param attribName 域名称
     * @return 域对应的值
     */
    public Object getAttribValue(String attribName)
    {
        return attribsMap.get(attribName.toLowerCase());
    }

    /**
     * 得到任务对象属性名称的遍历器
     * 
     * @return 属性名称的遍历器
     */
    public Iterator<String> iteratorAttribsName()
    {
        return attribsMap.keySet().iterator();
    }

    /**
     * 判断对象是否相等
     * 
     * @param rhs 匹配对象
     * @return boolean 是否相等
     */
    public boolean equals(Object rhs)
    {
        if (rhs == null)
            return false;
        if (!(rhs instanceof Task))
            return false;
        Task that = (Task) rhs;
        if (this.getTaskId() != null && that.getTaskId() != null)
        {
            if (this.getTaskId().equals(that.getTaskId()))
            {
                return true;
            }
        }
        return false;
    }
    
    public Task copyTask(Task original){
    	Task task = new Task();
    	task.setTaskId(new UUID().toString());
        task.addAttrib("MOBILE_TO", (String)original.getAttribValue("MOBILE_TO"));
        task.addAttrib("SEND_IDS", (String)original.getAttribValue("SEND_IDS"));//将短信编号拷贝到子短信中
        task.addAttrib("RESEND_TIMES", "0");
        task.addAttrib("CHANNEL_ID", Integer.valueOf(SmsplatGlobalVariable.EXTRACT_CHANNEL_ID));
        task.addAttrib("SIGNATURE", (String)original.getAttribValue("SIGNATURE"));
        task.addAttrib("SP_SERV_NO", (String)original.getAttribValue("SP_SERV_NO"));
        task.addAttrib("PRIOR_ID", (String)original.getAttribValue("PRIOR_ID"));
        task.addAttrib("IS_LONGSMS", "1");
        task.addAttrib("IS_ORIGINAL_SMS", "1");
        task.addAttrib("ORIGINALSMS_SENDID", original.getTaskId());
        task.addAttrib("USER_ID", (String)original.getAttribValue("USER_ID"));
        task.addAttrib("CONSUME_SEND_ID", (String)original.getAttribValue("CONSUME_SEND_ID"));
        task.addAttrib("MSG_TYPE", (String)original.getAttribValue("MSG_TYPE"));
        task.addAttrib("MOBILE_COM", (String)original.getAttribValue("MOBILE_COM"));
        task.addAttrib("USER_ORGANIZATION", (String)original.getAttribValue("USER_ORGANIZATION"));
        //子任务
        task.addAttrib("subTasks", (List)original.getAttribValue("subTasks"));
    	return task;
    }
}