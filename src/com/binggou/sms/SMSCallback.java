package com.binggou.sms;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import com.binggou.sms.mission.core.about.util.SmsplatGlobalVariable;


import com.binggou.mission.Callback;
import com.binggou.mission.Task;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 短信回调任务处理实现对象，从Callback类派生，
 * </p>
 * 
 * @author chenhj(brenda)
 * @version 1.0
 */

public class SMSCallback extends Callback
{
	public static String module = SMSCallback.class.getName();
	
	private NumberFormat nf = new DecimalFormat("000000");
	private boolean resend_times_flag = false;//发送失败短信的重新提交次数 是否启用,默认不启用
	private int resend_times_q=3;//发送失败短信的重新提交次数
	/**
	 * 数据库操作代理对象
	 */
	protected SQLBridge sqlBridge = null;

	/**
	 * 短信回调处理兑现参数
	 */
	protected HashMap paramsMap = null;

	/**
	 * 回调者操作的特服号
	 */
	protected String specialServiceCode = null;

	/**
	 * 日志格式
	 */
	protected SimpleDateFormat simpleDateFmt = null;
	
	/**
	 * 更新的短信条数
	 */
	private int updateRows = 0;

	/**
	 * 构造函数
	 */
	public SMSCallback()
	{
		// 对象实例化
		sqlBridge = new SQLBridge();
		simpleDateFmt = new SimpleDateFormat("yyyyMMdd");
	}

	private String preparedSql = null;
	/**
	 * 短信发送任务回调对象初始化
	 * 
	 * @return 初始化是否成功
	 */
	public boolean init()
	{
		return true;
	}

	/**
	 * 任务回调处理
	 * 
	 * @return 操作成功返回true,操作失败返回false
	 */
	public boolean callback()
	{
		boolean result = true;
		// 获取新的待处理回调任务
		Task task = (Task) callbackQueue.getTask();
		if (task == null)
			return result;

        //记录日志
    	//Debug.logVerbose("准备回调处理短信发送任务 " + task.getTaskId(), module);
		try
		{
			updateRows = -1;
			// 建立数据库连接
			sqlBridge.openConnect();
			
			int index = 1;
			if("exception".equals((String)task.getAttribValue("SUBMIT")))//通道异常, 需要重新发送
			{
				String taskIDS = "";
				if(task.getTaskId()!=null){
					taskIDS = (String)task.getTaskId();
				}
				String preparedSql = "UPDATE t_push_task SET status = ? , status_info = ?, push_time = ?, report_time= ?  WHERE id in ( "+taskIDS+" )";//批量处理
				sqlBridge.prepareExecuteUpdate(preparedSql);
				sqlBridge.setInt(1, SmsplatGlobalVariable.NOT_SEND_STATE);//设置为未发送
				sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG"));
				sqlBridge.setLong(3, (Long)task.getAttribValue("pushTime"));//设置推送的时间戳 
				sqlBridge.setLong(4, new Date().getTime());//设置回调的时间戳 	
				updateRows = sqlBridge.executePreparedUpdate();
			}else if("sendfail_xinge".equals((String)task.getAttribValue("SUBMIT"))){
            	String preparedSql = "UPDATE t_push_task SET status = ? , status_info = ?, push_time = ?, report_time= ?  WHERE id in ( "+task.getTaskId()+" )";//批量处理
				sqlBridge.prepareExecuteUpdate(preparedSql);
				sqlBridge.setInt(1, SmsplatGlobalVariable.FAIL_SEND_STATE);//设置为发送失败
				sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG"));//标识错误原因
				sqlBridge.setLong(3, (Long)task.getAttribValue("pushTime"));//设置推送的时间戳 
				sqlBridge.setLong(4, new Date().getTime());//设置回调的时间戳 	
				updateRows = sqlBridge.executePreparedUpdate();
            }else if("xinge_success".equals((String)task.getAttribValue("SUBMIT"))){
            	String preparedSql = "UPDATE t_push_task SET status = ? , status_info = ?, push_time = ?, report_time= ?, push_back_ID=?  WHERE id in ( "+task.getTaskId()+" )";//批量处理
				sqlBridge.prepareExecuteUpdate(preparedSql);
				sqlBridge.setInt(1, SmsplatGlobalVariable.SEND_STATE);//设置为发送成功
				sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG"));//标识错误原因
				sqlBridge.setLong(3, (Long)task.getAttribValue("pushTime"));//设置推送的时间戳
				sqlBridge.setLong(4, new Date().getTime());//设置回调的时间戳 
				sqlBridge.setInt(5, (Integer)task.getAttribValue("pushId"));//设置推送Id
				updateRows = sqlBridge.executePreparedUpdate();
				String preparedSql2 = "UPDATE t_message SET push_id = ? ,push_time = ? WHERE id in ( "+task.getAttribValue("mesgId")+" )";
				sqlBridge.prepareExecuteUpdate(preparedSql2);
				sqlBridge.setInt(1, (Integer)task.getAttribValue("pushId"));//设置推送Id
				sqlBridge.setLong(2, (Long)task.getAttribValue("pushTime"));//设置推送时间
				updateRows = sqlBridge.executePreparedUpdate();
            }
		}catch (Exception e){
			//Debug.logError(e, module);
			e.printStackTrace();
			result = false;
		}finally{
			//此处处理和后面插入分短信似乎存在冲突
			if(-1 != updateRows ){				
				// 从正在处理任务队列中删除已完成任务
				processingQueue.getTask(task.getTaskId());
			}else{
				//出问题
				callbackQueue.putTask(task);
			}
			//清空结果集
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
		return result;
		}
	}
	
