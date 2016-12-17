package com.binggou.sms;

import java.sql.Timestamp;
import java.util.HashMap;


import com.binggou.mission.MissionCenter;
import com.binggou.mission.Task;
import com.binggou.sms.mission.core.about.util.SmsplatGlobalVariable;
//import org.apache.log4j.Logger;

import com.binggou.mission.Callback;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 短信回调任务处理实现对象，从Callback类派生，
 * </p>
 * @author chenhj(brenda)
 * @version 1.0 update by zhengya 2009-12-16
 */

public class SMSCallback_bak extends Callback
{
	protected SQLBridge sqlBridge = null;//数据库操作代理对象
//	private static Logger logger = Logger.getLogger(SMSExtractor.class);
	protected HashMap <String,String>paramsMap = null;//短信回调处理兑现参数
	protected String specialServiceCode = null;//回调者操作的特服号
	private int resend_times_q=3;//发送失败短信的重新提交次数
    private boolean resend_times_flag = false;//发送失败短信的重新提交次数 是否启用,默认不启用
	/**
	 * 构造函数
	 */
	public SMSCallback_bak()
	{
		// 对象实例化
		sqlBridge = new SQLBridge();

	}

	/**
	 * 短信发送任务回调对象初始化
	 * @return 初始化是否成功
	 */
	public boolean init()
	{
		paramsMap = config.getConfigParams("HJHZConnect");
		if (paramsMap == null)
		{
//		  logger.debug("严重错误，榨取线程在读取BGEngineConfig.xml出问题");
			System.out.println("严重错误，榨取线程在读取BGEngineConfig.xml出问题");
		  return false;
		}else{
			resend_times_q = Integer.parseInt(paramsMap.get("resend-times"));//发送失败短信的重新提交次数
			resend_times_flag = Boolean.parseBoolean(paramsMap.get("resend-times-flag"));
		}
		return true;
	}

	/**
	 * 任务回调处理
	 * @return 操作成功返回true,操作失败返回false
	 */
	public boolean callback()
	{
		int updateRows = -1;
		boolean result = true;
		Task task = (Task) callbackQueue.getTask();// 获取新的待处理回调任务
		if (task == null){
			return result;
		}
		/**对回调队列中的task进行处理*/
		//long start1 = System.currentTimeMillis();
		try
		{
			//建立数据库连接
			sqlBridge.openConnect();
			if("success".equals((String)task.getAttribValue("SUBMIT"))){//提交成功的处理	
					String[] tasks_id = new String[]{};
					String taskIDS = "";
					if(task.getAttribValue("SEND_IDS")!=null){
						tasks_id = ((String)task.getAttribValue("SEND_IDS")).replaceAll("'","").split(",");
						taskIDS = (String)task.getAttribValue("SEND_IDS");
					}else{
						tasks_id = task.getTaskId().replaceAll("'","").split(",");
						taskIDS = (String)task.getTaskId();
					}
					//更新状态和sequence
					String preparedSql = "UPDATE SEND SET SEND_STATUS = ?, SEND_TIME= ?, COM_RTN_TIME = ? , SEQUENCE = ?,RETURN_MSG_FLAG = ?  WHERE SEND_ID in ("+taskIDS+")";//批量处理
					sqlBridge.prepareExecuteUpdate(preparedSql);
					
					String[] sequences_id =  ((String)task.getAttribValue("SEQUENCE")).split(",");
				    System.out.println("task.getTaskId()**************"+(String)task.getAttribValue("SEND_IDS")+"  SEQUENCE******"+(String)task.getAttribValue("SEQUENCE"));

				    sqlBridge.setInt(1, SmsplatGlobalVariable.SENT_STATE);//标识为已提交
					sqlBridge.setTimestamp(2, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
					sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 
					sqlBridge.setString(4,  "");
					sqlBridge.setString(5, "");//下行短信提交到神奇mas后返回来的下行标识

					updateRows = sqlBridge.executePreparedUpdate();
//					logger.info("成功更新发送成功记录条数*****************************"+updateRows);
				System.out.println("成功更新发送成功记录条数*****************************"+updateRows);
//					MissionCenter.setSendSuccessCount(tasks_id.length);//发送成功数量
//					logger.info("通道正常,短信id是: " + task.getTaskId());
					System.out.println("通道正常,短信id是: " + task.getTaskId());
				}else if("exception".equals((String)task.getAttribValue("SUBMIT")))//通道异常, 需要重新发送
				{
					String taskIDS = "";
					if(task.getAttribValue("SEND_IDS")!=null){
						taskIDS = (String)task.getAttribValue("SEND_IDS");
					}else{
						taskIDS = (String)task.getTaskId();
					}
					resend_times_flag = (Boolean)task.getAttribValue("resend_times_flag");
					String preparedSql = "UPDATE SEND SET SEND_STATUS = ?, PRE_SEND_TIME = PRE_SEND_TIME +　( 5 / (1 * 24 * 60 * 60)) WHERE SEND_ID in ( "+taskIDS+" )";//批量处理

//					logger.info("resend_times_flag*****************************"+resend_times_flag);
					System.out.println("resend_times_flag*****************************"+resend_times_flag);
					if(resend_times_flag==true){
						preparedSql = "UPDATE SEND SET SEND_STATUS = ?, PRE_SEND_TIME = PRE_SEND_TIME +　( 5 / (1 * 24 * 60 * 60)), RESEND_TIMES = RESEND_TIMES+1 WHERE SEND_ID in ( "+taskIDS+" )";
					}
					System.out.println(">>>>>>>>>>>>>>(String)task.getTaskId()>>>>>>>>>>>>>>>>>> "+ (String)task.getTaskId());
					System.out.println(">>>>(String)task.getAttribValue(SEND_IDS)>>>>>> "+ (String)task.getAttribValue("SEND_IDS"));
					
					sqlBridge.prepareExecuteUpdate(preparedSql);
					System.out.println(">>>>>>>>>>>>>>preparedSql>>>>>>>>>>>>>>>>>> "+ preparedSql);
					sqlBridge.setInt(1, SmsplatGlobalVariable.NOT_SEND_STATE);
					//sqlBridge.setTimestamp(2, new Timestamp( System.currentTimeMillis() + 10000));//设置下次提交的间隔时间+ 10秒	
					//sqlBridge.setString(2, taskIDS);//设置短信id
					System.out.println(">>>>>>>>>>>>>>>>>>>SmsplatGlobalVariable.NOT_SEND_STATE>>>>>>>>>>>>>  "+ SmsplatGlobalVariable.NOT_SEND_STATE);
					System.out.println(">>>>>>>>>>>>>>>>>>>new Timestamp( System.currentTimeMillis() + 10000)>>>>>>>>>>>>>  "+ new Timestamp( System.currentTimeMillis() + 10000));
					System.out.println(">>>>>>>>>>>>>>>>>>>taskIDS>>>>>>>>>>>>>  "+ taskIDS);
					updateRows = sqlBridge.executePreparedUpdate();
//					logger.info("设置重发记录条数*****************************"+updateRows);
					System.out.println("设置重发记录条数*****************************"+updateRows);
					
					/**重发三次失败时，把状态改为6*/
					if(resend_times_flag==true){
						//sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = 6 WHERE SEND_ID = ? and RESEND_TIMES >= ? ");//单挑处理
						sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = 6, ERR_MSG = ?  WHERE SEND_ID in ( "+taskIDS+" ) and RESEND_TIMES >= " + resend_times_q);//批量处理
						sqlBridge.setString(1, (String)task.getAttribValue("ERR_MSG")==null?"":(String)task.getAttribValue("ERR_MSG"));//设置短信id
						//sqlBridge.setString(2, taskIDS);//设置短信id
						sqlBridge.executePreparedUpdate();	
//						logger.info("重发"+resend_times_q+"次后仍失败更新失败的记录条数*****************************"+updateRows);
					}
				}else if("overSendDeadline".equals((String)task.getAttribValue("SUBMIT"))){//超过短信发送截止时间
					    //String preparedSql = "update SEND t set t.SEND_STATUS = 9 , err_msg = ? WHERE SEND_ID = ? ";//单挑处理
					    String preparedSql = "update SEND t set t.SEND_STATUS = 9 , err_msg = ? WHERE SEND_ID in ( ? ) ";//批量处理
						sqlBridge.prepareExecuteUpdate(preparedSql);
						sqlBridge.setString(1, "超过发送截止时间");//标识不能发送的原因
						sqlBridge.setString(2, task.getTaskId());//标识为已提交				
						updateRows = sqlBridge.executePreparedUpdate();
//						logger.info("超过发送截止时间异常处理。短信id是: " + task.getTaskId());
						System.out.println("超过发送截止时间异常处理。短信id是: " + task.getTaskId());
				}else if("overspeed".equals((String)task.getAttribValue("SUBMIT")))//超速，暂不再提交
				{
					//String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID = ?";//单挑处理
					String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID in ( ? )";//批量处理
					sqlBridge.prepareExecuteUpdate(preparedSql);
					sqlBridge.setInt(1, 1);//设置为已提交
					sqlBridge.setString(2, (String)task.getAttribValue("errorInfo"));//标识错误原因
					sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
					sqlBridge.setTimestamp(4, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 		
					sqlBridge.setString(5, task.getTaskId());//设置短信id
					updateRows = sqlBridge.executePreparedUpdate();
//					logger.info("超速，暂不再提交异常处理。短信id是: " + task.getTaskId());
				}else if("lawlessContent".equals((String)task.getAttribValue("SUBMIT")))//非法内容
				{
					//String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID = ?";//单挑处理
					String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID in ( ? )";//批量处理
					sqlBridge.prepareExecuteUpdate(preparedSql);
					sqlBridge.setInt(1, 6);//设置为发送失败
					sqlBridge.setString(2, "短信中含网关规定的非法字符。( "+(String)task.getAttribValue("errorInfo") + " )");//标识错误原因
					sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
					sqlBridge.setTimestamp(4, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 		
					sqlBridge.setString(5, task.getTaskId());//设置短信id
					updateRows = sqlBridge.executePreparedUpdate();
//					MissionCenter.setSendFailCount(1);//发送短信失败数
//					logger.info("发送短信非法内容。短信id是: " + task.getTaskId());
				}else if("lawlessTime".equals((String)task.getAttribValue("SUBMIT")))//非法发送时间
				{
					//String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID = ?";//单挑处理
					String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID in ( ? )";//批量处理
					sqlBridge.prepareExecuteUpdate(preparedSql);
					sqlBridge.setInt(1, 6);//设置为已提交
					sqlBridge.setString(2,  "非法发送时间。( "+(String)task.getAttribValue("errorInfo") + " )");//标识错误原因
					sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
					sqlBridge.setTimestamp(4, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 		
					sqlBridge.setString(5, task.getTaskId());//设置短信id
					updateRows = sqlBridge.executePreparedUpdate();
//					MissionCenter.setSendFailCount(1);//发送短信失败数
//					logger.info("非法发送时间异常处理。短信id是: " + task.getTaskId());
				}else if("NoChannel".equals((String)task.getAttribValue("SUBMIT")))//没有相对应通道, 需要重新发送
				{
					//String preparedSql = "UPDATE SEND SET SEND_STATUS = ? WHERE SEND_ID = ?";//单挑处理
					String preparedSql = "UPDATE SEND SET SEND_STATUS = ? WHERE SEND_ID in ( ? )";//批量处理
					sqlBridge.prepareExecuteUpdate(preparedSql);
					sqlBridge.setInt(1, 6);//标识为未提交
					sqlBridge.setString(2, task.getTaskId());//设置短信id
					updateRows = sqlBridge.executePreparedUpdate();
//					MissionCenter.setSendFailCount(1);//发送短信失败数
//					logger.info("没有相对应通道, 需要重新发送异常处理:短信id: " + task.getTaskId());
				}else if("xphtfail".equals((String)task.getAttribValue("SUBMIT")))//信普恒通提交失败
				{
					
					//String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID = ?";//单挑处理
					String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID in ( ? )";//批量处理
					sqlBridge.prepareExecuteUpdate(preparedSql);
					sqlBridge.setInt(1, 6);//设置为已提交
					sqlBridge.setString(2, (String)task.getAttribValue("errorInfo"));//标识错误原因
					sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
					sqlBridge.setTimestamp(4, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 		
					sqlBridge.setString(5, task.getTaskId());//设置短信id
					updateRows = sqlBridge.executePreparedUpdate();
//					logger.info("信普恒通提交失败异常处理:短信id: " + task.getTaskId());
				}else if("sendfail".equals((String)task.getAttribValue("SUBMIT"))){//发送失败
					String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID in ( "+task.getTaskId()+" )";//批量处理
					sqlBridge.prepareExecuteUpdate(preparedSql);
					sqlBridge.setInt(1, SmsplatGlobalVariable.FAIL_SEND_STATE);//设置为已提交
					sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG"));//标识错误原因
					sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
					sqlBridge.setTimestamp(4, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 	
					updateRows = sqlBridge.executePreparedUpdate();
//					logger.info("信普恒通提交失败异常处理:短信id: " + task.getTaskId());
				}
		}
		catch (Exception e)
		{
			e.printStackTrace();
//			logger.error(e.getMessage());
			result = false;
		}
		finally {
			if(-1 != updateRows ){
				// 从正在处理任务队列中删除已完成任务
				processingQueue.getTask(task.getTaskId());	
			}
			else {//出问题
				callbackQueue.putTask(task);
			}
			//清空结果集
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
			// long end1 = System.currentTimeMillis();
			// logger.error("回调函数每次调用花费的时间***"+task.getTaskId()+"*****"+task.getAttribValue("SUBMIT")+"**********"+updateRows+"*************"+(end1-start1));
		}
		return result;
	}
	
}