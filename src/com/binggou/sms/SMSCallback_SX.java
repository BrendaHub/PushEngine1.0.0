package com.binggou.sms;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import com.binggou.mission.MissionCenter;
import com.binggou.mission.Task;
import com.binggou.sms.mission.core.about.util.SmsplatGlobalVariable;


import com.eaio.uuid.UUID;
import com.binggou.mission.Callback;

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

public class SMSCallback_SX extends Callback
{
	public static String module = SMSCallback_SX.class.getName();
	
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
	public SMSCallback_SX()
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
				String preparedSql = "UPDATE SEND SET SEND_STATUS = ?, SEND_TIME= ?, COM_RTN_TIME = ? , SEQUENCE = ?, RETURN_MSG_FLAG = ?  WHERE SEND_ID in ("+taskIDS+")";//批量处理
				sqlBridge.prepareExecuteUpdate(preparedSql);
				
				String[] sequences_id =  ((String)task.getAttribValue("SEQUENCE")).split(",");
			    System.out.println("task.getTaskId()**************"+(String)task.getAttribValue("SEND_IDS")+"  SEQUENCE******"+(String)task.getAttribValue("SEQUENCE"));

			    sqlBridge.setInt(1, SmsplatGlobalVariable.SENT_STATE);//标识为已提交
				sqlBridge.setTimestamp(2, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
				sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 
				sqlBridge.setString(4,  "");
				sqlBridge.setString(5, "");//下行短信提交到神奇mas后返回来的下行标识

				updateRows = sqlBridge.executePreparedUpdate();
				System.out.println("成功更新发送成功记录条数*****************************"+updateRows);
//				MissionCenter.setSendSuccessCount(tasks_id.length);//发送成功数量
				System.out.println("通道正常,短信id是: " + task.getTaskId());
			}else if("sendException".equals((String)task.getAttribValue("SUBMIT"))){//发送太快，设置重发。
				System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@   "+ (String)task.getAttribValue("SUBMIT"));
				index =1;
				//更新状态和sequence
				if(1== SmsplatGlobalVariable.PACK_QUANTITIES )
				{
					preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID = ?";
				}
				else
				{
					preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID in (" + (String)task.getAttribValue("SEND_IDS") + ")";
				}
				sqlBridge.prepareExecuteUpdate(preparedSql);
				sqlBridge.setInt(index++, SmsplatGlobalVariable.FAIL_SEND_STATE);//设置重新发送状态
				sqlBridge.setString(index++, (String)task.getAttribValue("errorInfo"));//标识错误原因
				sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
				sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 		
				if(1== SmsplatGlobalVariable.PACK_QUANTITIES )
				{
					sqlBridge.setString(index++, task.getTaskId());//设置短信id
				}
				updateRows = sqlBridge.executePreparedUpdate();
				//Debug.logVerbose("发送太快，设置重发, 短信id是: " + task.getTaskId()  + " ,时间在: " + new Date(), module);
			}
			else if("exception".equals((String)task.getAttribValue("SUBMIT")))//通道异常, 需要重新发送
			{
				String taskIDS = "";
				if(task.getAttribValue("SEND_IDS")!=null){
					taskIDS = (String)task.getAttribValue("SEND_IDS");
				}else{
					taskIDS = (String)task.getTaskId();
				}
				resend_times_flag = (Boolean)task.getAttribValue("resend_times_flag");
				String preparedSql = "UPDATE SEND SET SEND_STATUS = ?, PRE_SEND_TIME = PRE_SEND_TIME +　( 5 / (1 * 24 * 60 * 60)) WHERE SEND_ID in ( "+taskIDS+" )";//批量处理

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
				System.out.println("设置重发记录条数*****************************"+updateRows);
				
				/**重发三次失败时，把状态改为6*/
				if(resend_times_flag==true){
					//sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = 6 WHERE SEND_ID = ? and RESEND_TIMES >= ? ");//单挑处理
					sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = 6, ERR_MSG = ?  WHERE SEND_ID in ( "+taskIDS+" ) and RESEND_TIMES >= " + resend_times_q);//批量处理
					sqlBridge.setString(1, (String)task.getAttribValue("ERR_MSG")==null?"":(String)task.getAttribValue("ERR_MSG"));//设置短信id
					//sqlBridge.setString(2, taskIDS);//设置短信id
					sqlBridge.executePreparedUpdate();	
					System.out.println("重发"+resend_times_q+"次后仍失败更新失败的记录条数*****************************"+updateRows);
				}
			}
			else if("overspeed".equals((String)task.getAttribValue("SUBMIT")))//超速，暂不再提交
			{
				index =1;
				//更新状态和sequence
				if(1 == SmsplatGlobalVariable.PACK_QUANTITIES )
				{
					preparedSql = "UPDATE SEND SET SEND_STATUS = ? , report = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID = ?";
				}
				else
				{
					preparedSql = "UPDATE SEND SET SEND_STATUS = ? , report = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID in (" + (String)task.getAttribValue("SEND_IDS") + ")";
				}
				sqlBridge.prepareExecuteUpdate(preparedSql);
				sqlBridge.setInt(index++, SmsplatGlobalVariable.SENT_STATE);//设置为已提交
				sqlBridge.setString(index++, (String)task.getAttribValue("ERR_MSG"));//标识错误原因
				sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
				sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 		
				if(1== SmsplatGlobalVariable.PACK_QUANTITIES )
				{
					sqlBridge.setString(index++, task.getTaskId());//设置短信id
				}
				
				updateRows = sqlBridge.executePreparedUpdate();
				//Debug.logVerbose("发送短信时网关连接异常。短信id是: " + task.getTaskId()  + " ,时间在: " + new Date(), module);
			}
			else if("cmppoverspeed".equals((String)task.getAttribValue("SUBMIT")))//超速，暂不再提交
			{
				index =1;
				//更新状态和sequence
				preparedSql = "UPDATE SEND SET SEND_STATUS = ? WHERE SEND_ID in (" + (String)task.getAttribValue("SEND_IDS") + ")";

				sqlBridge.prepareExecuteUpdate(preparedSql);
				sqlBridge.setInt(index++, SmsplatGlobalVariable.NOT_SEND_STATE);//设置为已提交
				
				updateRows = sqlBridge.executePreparedUpdate();
				//Debug.logVerbose("发送短信时超速，更新状态为待发。短信id是: " + task.getTaskId()  + " ,时间在: " + new Date(), module);
			}
			else if("longsmsover".equals((String)task.getAttribValue("SUBMIT")))//超速，暂不再提交
			{
				index = 1;
				
				//更新状态和sequenc
				preparedSql = "UPDATE SEND SET SEND_STATUS = ?, longsms_splited = ?, SEND_TIME= ?, COM_RTN_TIME = ? WHERE SEND_ID in (" + (String)task.getAttribValue("SEND_IDS") + ")";

				sqlBridge.prepareExecuteUpdate(preparedSql);
				sqlBridge.setInt(index++, SmsplatGlobalVariable.SENT_STATE);//标识为已提交
				sqlBridge.setInt(index++, 1);//标识为已提交
				sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
				sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳

				updateRows = sqlBridge.executePreparedUpdate();

				//Debug.logVerbose("更新短信提交状态和sequence, 短信id是: " + task.getTaskId()  + ", sequence: " + (String)task.getAttribValue("SEQUENCE") + " ,提交时间为: " + (Timestamp)task.getAttribValue("COM_RTN_TIME"), module);
			}
			else if("noResp".equals((String)task.getAttribValue("SUBMIT")))
			{
                if(SmsplatGlobalVariable.PACK_QUANTITIES==1){
                    preparedSql = "UPDATE SEND SET SEND_STATUS = ?, SEND_TIME= ?, err_msg2 = ?  WHERE SEND_ID = ?";
                }
                else{
                    preparedSql = "UPDATE SEND SET SEND_STATUS = ?, SEND_TIME= ?, err_msg2 = ?  WHERE SEND_ID in (" + (String)task.getAttribValue("SEND_IDS") + ")";
                }
                sqlBridge.prepareExecuteUpdate(preparedSql);
                sqlBridge.setInt(index++, -9);
                sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("SEND_TIME"));
                sqlBridge.setString(index++, (String)task.getAttribValue("ERR_MSG"));
                if(SmsplatGlobalVariable.PACK_QUANTITIES==1)
                    sqlBridge.setString(index++, task.getTaskId());
                updateRows = sqlBridge.executePreparedUpdate();
            }else if("sendfail".equals((String)task.getAttribValue("SUBMIT"))){
            	String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG = ?, SEND_TIME= ?, COM_RTN_TIME = ?  WHERE SEND_ID in ( "+task.getTaskId()+" )";//批量处理
				sqlBridge.prepareExecuteUpdate(preparedSql);
				sqlBridge.setInt(1, SmsplatGlobalVariable.FAIL_SEND_STATE);//设置为已提交
				sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG"));//标识错误原因
				sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
				sqlBridge.setTimestamp(4, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 	
				updateRows = sqlBridge.executePreparedUpdate();
				System.out.println("提交失败异常处理:短信id: " + task.getTaskId());
            }else if("sendfail_iphone".equals((String)task.getAttribValue("SUBMIT"))){
            	String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG2 = ?, SEND_TIME= ?, SEND_TIME2= ?, CHANNEL_ID = ?  WHERE SEND_ID in ( "+task.getTaskId()+" )";//批量处理
				sqlBridge.prepareExecuteUpdate(preparedSql);
				//是否需要重发 
				String resend_config = (String)task.getAttribValue("RESEND_CONFIG"); 
				System.out.println(">>>>>>>>>>>>>>>>>>>>>>>> " + resend_config);
				int resend_config_num = -1; 
				try{
					resend_config_num = Integer.parseInt(resend_config);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				switch(resend_config_num){
					case -1://异常情不进行重发,有可能是号码，或内容有问题
						sqlBridge.setInt(1, SmsplatGlobalVariable.FAIL_SEND_STATE);//设置为发送失败
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 不重发");//标识错误原因
						sqlBridge.setString(5, "60");//设置本短信原本走的通道号，进行短信通道重发
						break;
					case 0://不需要重发
						sqlBridge.setInt(1, SmsplatGlobalVariable.FAIL_SEND_STATE);//设置为发送失败
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 不重发");//标识错误原因
						sqlBridge.setString(5, "60");//设置本短信原本走的通道号，进行短信通道重发
						break;
					case 1: //失败需要重发
						sqlBridge.setInt(1, SmsplatGlobalVariable.NOT_SEND_STATE);//设置为重发
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 重发");//标识错误原因
						sqlBridge.setString(5, (String)task.getAttribValue("OLD_SEND_CHANNEL_ID"));//设置本短信原本走的通道号，进行短信通道重发
						break;
					case 2:
						sqlBridge.setInt(1, SmsplatGlobalVariable.NOT_SEND_STATE);//设置为重发
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 重发");//标识错误原因
						sqlBridge.setString(5, (String)task.getAttribValue("OLD_SEND_CHANNEL_ID"));//设置本短信原本走的通道号，进行短信通道重发
						break;
					default:
						sqlBridge.setInt(1, SmsplatGlobalVariable.FAIL_SEND_STATE);//设置为发送失败
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 不重发");//标识错误原因
						sqlBridge.setString(5, "60");//设置本短信原本走的通道号，进行短信通道重发
						break;
				}
				
				sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
				sqlBridge.setTimestamp(4, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 	
				
				
				updateRows = sqlBridge.executePreparedUpdate();
            }else if("iphone_success".equals((String)task.getAttribValue("SUBMIT"))){
            	
            	String preparedSql = "UPDATE SEND SET SEND_STATUS = ? , ERR_MSG2 = ?, SEND_TIME= ?, SEND_TIME2= ?, CHANNEL_ID = ?  WHERE SEND_ID in ( "+task.getTaskId()+" )";//批量处理
				sqlBridge.prepareExecuteUpdate(preparedSql);
				
				//是否需要重发
				String resend_config = (String)task.getAttribValue("RESEND_CONFIG"); 
				System.out.println(">>>>>>>>>>>>>>>>>>ssssssssssss>>>>>> " + resend_config);
				int resend_config_num = -1;
				try{
					resend_config_num = Integer.parseInt(resend_config);
				}catch(Exception ex){
					ex.printStackTrace();
				}
				switch(resend_config_num){
					case -1://异常情不进行重发,有可能是号码，或内容有问题
						sqlBridge.setInt(1, SmsplatGlobalVariable.SENT_STATE);//设置为发送失败
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 不重发");//标识错误原因
						sqlBridge.setString(5, "60");//设置本短信原本走的通道号，进行短信通道重发
						break;
					case 0://不需要重发
						sqlBridge.setInt(1, SmsplatGlobalVariable.SENT_STATE);//设置为发送失败
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 不重发");//标识错误原因
						sqlBridge.setString(5, "60");//设置本短信原本走的通道号，进行短信通道重发
						break;
					case 1: //失败需要重发
						sqlBridge.setInt(1, SmsplatGlobalVariable.SENT_STATE);//推送成功了，不再重发
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 不重发");//标识错误原因
						sqlBridge.setString(5, "60");//设置本短信原本走的通道号，进行短信通道重发
						break;
					case 2:
						sqlBridge.setInt(1, SmsplatGlobalVariable.NOT_SEND_STATE);//成了也需要重发
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 重发");//标识错误原因
						sqlBridge.setString(5, (String)task.getAttribValue("OLD_SEND_CHANNEL_ID"));//设置本短信原本走的通道号，进行短信通道重发
						break;
					default:
						sqlBridge.setInt(1, SmsplatGlobalVariable.SENT_STATE);//设置为发送失败
						sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG")+", 不重发");//标识错误原因
						sqlBridge.setString(5, "60");//设置本短信原本走的通道号，进行短信通道重发
						break;
				}
				
//				sqlBridge.setInt(1, SmsplatGlobalVariable.NOT_SEND_STATE);//设置为末发送
//				sqlBridge.setString(2, (String)task.getAttribValue("ERR_MSG"));//标识错误原因
				sqlBridge.setTimestamp(3, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
				sqlBridge.setTimestamp(4, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 	
				updateRows = sqlBridge.executePreparedUpdate();
            }
			else if("insetException".equals((String)task.getAttribValue("SUBMIT")))//插入错误
			{
				updateRows = insertIntoSend(task);
			}
			else 
			{
				if("insetException".equals((String)task.getAttribValue("isSplitException")))
				{
					//记录拆分短信时异常，需要重新记录
					updateRows = insertIntoSend(task);
				}
				else
				{
					//提交失败的处理
					//TODO 记录失败次数, 并更新到数据库
					//如果不是三次提交失败，记录提交次数加1, 并重置状态为待发送状态。是三次则标记发送失败
					int resend_times = 0;
					try 
					{
						List subTasks = (List)task.getAttribValue("subTasks");
						if(subTasks != null && subTasks.size() > 0)
						{
							for(int ti = 0; ti < subTasks.size() ; ti++)
							{
								Task task_temp = (Task)subTasks.get(ti);
								String tmp_taskID = (String)task_temp.getTaskId();
								if(tmp_taskID != null )
								{
									if(tmp_taskID.equals(task.getTaskId()))
									{
										resend_times = Integer.parseInt( (String)task_temp.getAttribValue("RESEND_TIMES") );
										break;
									}
									else
									{
										continue;
									}
								}
							}
						}
					}
					catch (RuntimeException e1)
					{
						e1.printStackTrace();
					}
					int interval = 0;
					try
					{
						interval = Integer.parseInt( SmsplatGlobalVariable.configsMap.get("resend-interval-"+(1+resend_times)) )*1000 ;
					}
					catch (Exception e)
					{
						interval = 120*1000;
					}
					
					if(resend_times == 0)//第一次提交失败
					{		
						index =1;
						preparedSql = "UPDATE SEND SET SEND_STATUS = ?, PRE_SEND_TIME = ?, SEND_TIME= ?, COM_RTN_TIME = ?, ERR_MSG=?, RESEND_TIMES = ? WHERE SEND_ID in (" + (String)task.getAttribValue("SEND_IDS") + ")";
						
						sqlBridge.prepareExecuteUpdate(preparedSql);
						sqlBridge.setInt(index++, SmsplatGlobalVariable.NOT_SEND_STATE);//标识为待发送状态
						sqlBridge.setTimestamp(index++, new Timestamp( System.currentTimeMillis() + interval));//设置下次提交的间隔时间	
						sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳
						sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳
						sqlBridge.setString(index++, (String)task.getAttribValue("errorInfo"));//标识错误原因
						sqlBridge.setInt(index++,resend_times+ 1 );//记录提交次数(原次数加1)
						//Debug.logInfo("第 " + (resend_times + 1) + " 次提交短信失败。短信id: " + task.getTaskId()  + " 。时间在: " + new Date(), module);
					}
					else if(resend_times == 1 )//第二次提交失败
					{
						index =1;
						preparedSql = "UPDATE SEND SET SEND_STATUS = ?, PRE_SEND_TIME = ?, SEND_TIME = ?, COM_RTN_TIME = ?, ERR_MSG=?, RESEND_TIMES = ? WHERE SEND_ID in (" + (String)task.getAttribValue("SEND_IDS") + ")";
						
						sqlBridge.prepareExecuteUpdate(preparedSql);
						sqlBridge.setInt(index++, SmsplatGlobalVariable.NOT_SEND_STATE);//标识为待发送状态
						sqlBridge.setTimestamp(index++, new Timestamp( System.currentTimeMillis() + interval));//设置下次提交的间隔时间
						sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
						sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 	
						sqlBridge.setString(index++, (String)task.getAttribValue("errorInfo"));//标识为错误原因
						sqlBridge.setInt(index++, resend_times + 1 );//记录提交次数(原次数加1)

						//Debug.logInfo("第 " + (resend_times + 1) + " 次提交短信失败。短信id: " + task.getTaskId()  + " 。时间在: " + new Date(), module);
					}
					else if(resend_times == 2)//第三次提交失败
					{
						index =1;
						preparedSql = "UPDATE SEND SET SEND_STATUS = ?, SEND_TIME = ?, COM_RTN_TIME = ?, RESEND_TIMES = ?,ERR_MSG = ? WHERE SEND_ID in (" + (String)task.getAttribValue("SEND_IDS") + ")";

						sqlBridge.prepareExecuteUpdate(preparedSql);
						sqlBridge.setInt(index++, SmsplatGlobalVariable.FAIL_SEND_STATE);//标识为待发送状态
						sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("SEND_TIME"));//设置发送的时间戳 					
						sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳 	
						sqlBridge.setInt(index++, resend_times + 1 );//记录提交次数(原次数加1)
						//sqlBridge.setString(index++, SmsplatGlobalVariable.RESEND_TIMES + "次提交该短信失败");//设置提交的时间戳
						sqlBridge.setString(index++, task.getAttribValue("errorInfo")!=null?((String)task.getAttribValue("errorInfo")):"");//错误信息
						
						//Debug.logInfo("第 " + (resend_times+ 1) + " 次提交短信失败。短信id: " + task.getTaskId()  + " 。时间在: " + new Date(), module);
					}
					else//最大次数提交失败
					{
						index=1;
						//记录提交时间和发送失败原因
						preparedSql = "UPDATE SEND SET SEND_STATUS = ?, SEND_TIME = ?, COM_RTN_TIME = ?, ERR_MSG = ? WHERE SEND_ID in (" + (String)task.getAttribValue("SEND_IDS") + ")";

						sqlBridge.prepareExecuteUpdate(preparedSql);
						sqlBridge.setInt(index++, SmsplatGlobalVariable.FAIL_SEND_STATE);//标识为发送失败
						sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("SEND_TIME"));//设置最后一次提交该短信的时间
						sqlBridge.setTimestamp(index++, (Timestamp)task.getAttribValue("COM_RTN_TIME"));//设置提交的时间戳
						//sqlBridge.setString(index++, SmsplatGlobalVariable.RESEND_TIMES + "次提交该短信失败");//设置提交的时间戳
						sqlBridge.setString(index++, task.getAttribValue("errorInfo")!=null?((String)task.getAttribValue("errorInfo")):"");//错误信息
						
						//Debug.logInfo("第 " + (resend_times + 1) + " 次提交短信失败。短信id: " + task.getTaskId()  + " 。时间在: " + new Date(), module);
					}

					updateRows = sqlBridge.executePreparedUpdate();//执行更新
				
					//记录日志
					//Debug.logInfo("短信提交失败, 短信id是: " + task.getTaskId()  + ", 记录时间: " + new Date() + " , 提交次数 " + task.getAttribValue("RESEND_TIMES") , module);
				}
			}
			//Debug.logInfo("回调处理短信发送任务！ID = " + task.getTaskId(), module);
		}
		catch (Exception e)
		{
			//Debug.logError(e, module);
			result = false;
		}
		finally
		{
			//此处处理和后面插入分短信似乎存在冲突
			
			if(-1 != updateRows )
			{				
				// 从正在处理任务队列中删除已完成任务
				processingQueue.getTask(task.getTaskId());
			}
			else
			{
				//出问题
				callbackQueue.putTask(task);
			}
			//清空结果集
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}

		
		/*
		 * 记录长短信的被拆分的短信
		 */
		List subList = (List)task.getAttribValue("sendSubList");
		
		if(null == subList)
		{
			
		}
		else
		{
			updateRows = -1;
			try
			{
				//插入分短信的发送记录
				for(int i = 0; i < subList.size(); i++)
				{
					Task subTask = (Task)subList.get(i);
	
					updateRows = insertIntoSend(subTask);
					
					if(updateRows > 0)
					{
					}
					else
					{
						//插入失败，则将本任务放入回调队列，重新插入，
						//插入状态是什么呢？原来的状态
						subTask.addAttrib("isSplitException", "insetException");
						callbackQueue.putTask(subTask);
					}
				}
			}
			catch (Exception e)
			{
				//Debug.logError(e, module);
				result = false;
			}
		}

		return result;
	}
	
	/**
	 * 向数据库中插入拆分后的分短信
	 * @param task 一个分短信，他有一个subList，表示对应的手机号
	 * @return
	 */
	private int insertIntoSend(Task task){
		int updateRows = 0;
		
		SQLBridge sqlBridge2 = null;
		try
		{
			String sendMsg = (String)task.getAttribValue("SEND_MSG");
			if(null == sendMsg || "".equals(sendMsg))
			{
				sendMsg = " ";
			}
			sqlBridge2 = new SQLBridge();
			// 建立数据库连接
			sqlBridge2.openConnect();
			
			List subTasks = (List)task.getAttribValue("subTasks");
			for(int i=0;i<subTasks.size();i++)
			{
				Task subTask = (Task)subTasks.get(i);
	
				String mobile_com = (String) subTask.getAttribValue("mobile_com");
				if(mobile_com == null || "".equals(mobile_com))
				{
					mobile_com = "1";
				}
				
				int index = 1;
				//拆分短信提交失败
				if("subTaskFail".equals((String)task.getAttribValue("SUBMIT")))
				{
					//写入数据库
					preparedSql = "insert into send(send_id, mobile_to, send_msg, sp_serv_no, prior_id, send_status, channel_id, mobile_com, signature, " +
							" msg_type, is_longsms, is_original_sms, originalsms_sendid, cuurent_pageno, originalsms_totalpages, longsms_tag, user_id, CONSUME_SEND_ID , USER_ORGANIZATION , ERR_MSG ) " +
							" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
					sqlBridge2.prepareExecuteUpdate(preparedSql);
					sqlBridge2.setString(index++, new UUID().toString());//设置id
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("MOBILE_TO"));//设置号码
					sqlBridge2.setString(index++, sendMsg);//设置短信内容
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("SP_SERV_NO"));//设置扩展码
					sqlBridge2.setString(index++, "3");//设置优先级，最高
					sqlBridge2.setInt(index++, SmsplatGlobalVariable.NOT_SEND_STATE);//设置为待发状态
					sqlBridge2.setInt(index++, SmsplatGlobalVariable.EXTRACT_CHANNEL_ID);//设置通道号
					sqlBridge2.setInt(index++, Integer.parseInt(mobile_com));//设置号段
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("SIGNATURE"));//设置签名
					sqlBridge2.setString(index++, (String)task.getAttribValue("MSG_TYPE"));
					sqlBridge2.setInt(index++, 1);//设置为长短信
					sqlBridge2.setInt(index++, 1);//设置为非原始短信
					sqlBridge2.setString(index++, subTask.getTaskId());//设置原始短信的send_id
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("CUURENT_PAGENO")));//设置当前页码
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("ORIGINALSMS_TOTALPAGES")));//设置总页数
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("BYTE4")));//设置标记符
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("USER_ID"));//设置标记符
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("CONSUME_SEND_ID"));//设置客户端send_id
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("USER_ORGANIZATION"));//设置机构编号
					sqlBridge2.setString(index++, subTask.getAttribValue("errorInfo")!=null?((String)subTask.getAttribValue("errorInfo")):"");//设置错误信息
				}
				//拆分短信发送成功
				else if("subTaskSuccess".equals((String)task.getAttribValue("SUBMIT")))
				{
					//取得每个发送任务的网关短信编号
					String seq = (String)task.getAttribValue("SEQUENCE");
					if(seq != null)
					{
						BigInteger a = new BigInteger(seq);
						
						BigInteger b = a.add(new BigInteger(String.valueOf(i)));
						
						seq = b.toString();
					}
					
					
					
					//写入数据库
					preparedSql = "insert into send(send_id, mobile_to, send_msg, sp_serv_no, send_status, channel_id, mobile_com, signature, send_time, com_rtn_time, sequence, " +
							" msg_type, is_longsms, is_original_sms, originalsms_sendid, cuurent_pageno, originalsms_totalpages, longsms_tag, user_id, CONSUME_SEND_ID , USER_ORGANIZATION ) " +
							" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? )";
	
					sqlBridge2.prepareExecuteUpdate(preparedSql);
					sqlBridge2.setString(index++, new UUID().toString());//设置id
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("MOBILE_TO"));//设置号码
					sqlBridge2.setString(index++, sendMsg);//设置短信内容
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("SP_SERV_NO"));//设置扩展码
					sqlBridge2.setInt(index++, SmsplatGlobalVariable.SENT_STATE);//设置为待发状态
					sqlBridge2.setInt(index++, SmsplatGlobalVariable.EXTRACT_CHANNEL_ID);//设置通道号
					sqlBridge2.setInt(index++, Integer.parseInt(mobile_com));//设置号段
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("SIGNATURE"));//设置签名
					sqlBridge2.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));//提交时间
					sqlBridge2.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));//网关确认时间
					sqlBridge2.setString(index++, seq);//网关序列号
					sqlBridge2.setString(index++, (String)task.getAttribValue("MSG_TYPE"));
					sqlBridge2.setInt(index++, 1);//设置为长短信
					sqlBridge2.setInt(index++, 1);//设置为非原始短信
					sqlBridge2.setString(index++, subTask.getTaskId());//设置原始短信的send_id
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("CUURENT_PAGENO")));//设置当前页码
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("ORIGINALSMS_TOTALPAGES")));//设置总页数
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("BYTE4")));//设置标记符
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("USER_ID"));//设置标记符
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("CONSUME_SEND_ID"));//设置客户端send_id
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("USER_ORGANIZATION"));//设置机构编号
				}
				//拆分短信发送异常
				else if("subTaskException".equals((String)task.getAttribValue("SUBMIT")))
				{
					//写入数据库
					preparedSql = "insert into send(send_id, mobile_to, send_msg, sp_serv_no, prior_id, send_status, channel_id, mobile_com, signature, " +
							" msg_type, is_longsms, is_original_sms, originalsms_sendid, cuurent_pageno, originalsms_totalpages, longsms_tag, user_id, CONSUME_SEND_ID, USER_ORGANIZATION, ERR_MSG ) " +
							" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
					sqlBridge2.prepareExecuteUpdate(preparedSql);
					sqlBridge2.setString(index++, new UUID().toString());//设置id
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("MOBILE_TO"));//设置号码
					sqlBridge2.setString(index++, sendMsg);//设置短信内容
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("SP_SERV_NO"));//设置扩展码
					sqlBridge2.setString(index++, "3");//设置优先级，最高
					sqlBridge2.setInt(index++, SmsplatGlobalVariable.NOT_SEND_STATE);//设置为待发状态
					sqlBridge2.setInt(index++, SmsplatGlobalVariable.EXTRACT_CHANNEL_ID);//设置通道号
					sqlBridge2.setInt(index++, Integer.parseInt(mobile_com));//设置号段
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("SIGNATURE"));//设置签名
					sqlBridge2.setString(index++, (String)task.getAttribValue("MSG_TYPE"));
					sqlBridge2.setInt(index++, 1);//设置为长短信
					sqlBridge2.setInt(index++, 1);//设置为非原始短信
					sqlBridge2.setString(index++, subTask.getTaskId());//设置原始短信的send_id
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("CUURENT_PAGENO")));//设置当前页码
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("ORIGINALSMS_TOTALPAGES")));//设置总页数
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("BYTE4")));//设置标记符
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("USER_ID"));//设置标记符
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("CONSUME_SEND_ID"));//设置客户端send_id
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("USER_ORGANIZATION"));//设置机构编号
					sqlBridge2.setString(index++, subTask.getAttribValue("errorInfo")!=null?((String)subTask.getAttribValue("errorInfo")):"");//设置错误信息
				}
				//拆分短信超速
				else if("cmmppsuboverspeed".equals((String)task.getAttribValue("SUBMIT")))
				{
					//写入数据库
					preparedSql = "insert into send(send_id, mobile_to, send_msg, sp_serv_no, prior_id, send_status, channel_id, mobile_com, signature, " +
							" msg_type, is_longsms, is_original_sms, originalsms_sendid, cuurent_pageno, originalsms_totalpages, longsms_tag, user_id, CONSUME_SEND_ID , USER_ORGANIZATION ) " +
							" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
					sqlBridge2.prepareExecuteUpdate(preparedSql);
					sqlBridge2.setString(index++, new UUID().toString());//设置id
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("MOBILE_TO"));//设置号码
					sqlBridge2.setString(index++, sendMsg);//设置短信内容
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("SP_SERV_NO"));//设置扩展码
					sqlBridge2.setString(index++, "3");//设置优先级，最高
					sqlBridge2.setInt(index++, SmsplatGlobalVariable.NOT_SEND_STATE);//设置为待发状态
					sqlBridge2.setInt(index++, SmsplatGlobalVariable.EXTRACT_CHANNEL_ID);//设置通道号
					sqlBridge2.setInt(index++, Integer.parseInt(mobile_com));//设置号段
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("SIGNATURE"));//设置签名
					sqlBridge2.setString(index++, (String)task.getAttribValue("MSG_TYPE"));
					sqlBridge2.setInt(index++, 1);//设置为长短信
					sqlBridge2.setInt(index++, 1);//设置为非原始短信
					sqlBridge2.setString(index++, subTask.getTaskId());//设置原始短信的send_id
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("CUURENT_PAGENO")));//设置当前页码
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("ORIGINALSMS_TOTALPAGES")));//设置总页数
					sqlBridge2.setInt(index++, Integer.parseInt((String)task.getAttribValue("BYTE4")));//设置标记符
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("USER_ID"));//设置标记符
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("CONSUME_SEND_ID"));//设置客户端send_id
					sqlBridge2.setString(index++, (String)subTask.getAttribValue("USER_ORGANIZATION"));//设置机构编号
				}
			
				updateRows += sqlBridge2.executePreparedUpdate();
			}
		}
		catch (Exception e)
		{
			//Debug.logError(e, module);
		}
		finally
		{
			//清空结果集
			sqlBridge2.clearResult();
			sqlBridge2.closeConnect();
		}
		
		return updateRows;
	}
}