package com.binggou.sms.mission.core.send.impl.hjhttptest;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


import com.binggou.mission.Task;
import com.binggou.sms.mission.core.about.util.MessageUtil;
import com.binggou.sms.mission.core.send.SendMessage;
//import org.apache.log4j.Logger;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Cfg;

/**
 * 中信控股通道
 * 为中信证券短信平台项目所写
 * 该通道使用数据库接口，并且需要通道程序定时到数据库同步发送状态
 */
public class HJ_THHPSMS  implements SendMessage {
//	private static Logger logger = Logger.getLogger(HJ_THHPSMS.class); //日志类
	private static HJ_THHPSMS instance = null;
	private static String httpurl = "http://www.sjzqzx.com/MessagePlat/smsSendServlet.htm";
	private static String userName = "syssender";
	private static String PWD = "hjhz20100910";
	private String sp_no 		= null;//扩展号
	   
	//中信控股通道的ct_out的sp_no字段为varchar(15)
	private static int MAX_SP_NO_LENGTH = 15;
	
	private String branch_no	= null;//分支编码
	
	private String channelId = null; //数据源
	
	public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");//19970113
	public static SimpleDateFormat timeFormat = new SimpleDateFormat("HHmmss");//122133
	private static long recordTime = System.currentTimeMillis();
	private static int quantity = 0;
	private boolean isRunning = false;
	private Thread moThread;
	String extendNumber = "00";//扩展号
	int queryMoNumber = 10;//一次取得上行短信的数量
	String moOrgid;
    private static int moQueryInterval;//查询上行短信的间隔

	synchronized public static HJ_THHPSMS getInstance(String config) 
	{
		if(instance == null)
		{
			instance = new HJ_THHPSMS(config);
		}
		return instance;
	}
	
	private HJ_THHPSMS(String channelId)
	{
		Args args = null;
		try {
			args = new Cfg("SMProxy.xml", false).getArgs("Channel" + channelId);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//初始化参数
		this.channelId = channelId;
		this.userName = args.get("login-name", "");
		this.PWD = args.get("login-pass", "");
		
		this.sp_no = args.get("sp-no", "");
		this.branch_no = args.get("branch-no", "");
		
		this.isRunning = true;
		//上行程序
		this.moOrgid = args.get("mo-org-id", "");
		String moQueryIntervalS = (String) args.get("mo-query-interval","");
		if(moQueryIntervalS != null && moQueryIntervalS.length() > 0)
		{
			this.moQueryInterval = Integer.parseInt(moQueryIntervalS) * 1000;
		}
		
		this.extendNumber = args.get("extend-number", "");
		String queryMoNumber_ = args.get("query-mo-number", "");
		if(queryMoNumber_ != null && !"".equals(queryMoNumber_))
		{
			this.queryMoNumber = Integer.parseInt(queryMoNumber_);
		}
	}

	
	public Task sendMessage(Task task)
	{
		//将短信调用和佳自已通道ＨＴＴＰ发短信.
		String taskid = task.getTaskId();
		String mobile_to = (String) task.getAttribValue("MOBILE_TO");
		String msg = (String) task.getAttribValue("SEND_MSG");
		String singallge = (String) task.getAttribValue("SIGNATURE");
		System.out.println("singallge====> "+ singallge);
		msg += singallge;
		System.out.println("msg====> "+ msg);
		String user_id = (String)task.getAttribValue("USER_ID");
		
		//特服号   
		String serviceNumber = this.sp_no;
		//扩展号
		String extNumber = (String) task.getAttribValue("SP_SERV_NO");
		if(extNumber != null && extNumber.length() > 0)
		{
			serviceNumber += extNumber;
		}
		
		//检查扩展号的长度
		if(serviceNumber.length() > MAX_SP_NO_LENGTH)
		{
			serviceNumber = serviceNumber.substring(0, MAX_SP_NO_LENGTH);
		}		
		//System.out.println(" sp_no = " + serviceNumber);
		
		task.addAttrib("SUBMIT", "success");
		mobile_to = mobile_to.replace(",", "");
		mobile_to = mobile_to.replace(";", "");
		
		String user_id2 = user_id;
		//将user_id前面的0去掉
		while(user_id2.startsWith("0"))
		{   
			user_id2 = user_id2.substring(1);
		}
		//判断号码属于的号段
		int com_type = MessageUtil.getMobileCom(mobile_to);
		
		Date now = new Date();
		
		try
		{
			int index = 1;
			
			//拼参数：http://localhost:8888/MessagePlat/smsSendServlet.htm?
			//command=sendSMS&username=XXXXX&pwd=XXXXX&mobiles=13552666934&content=%B6%CC%D0%C5%B2%E2%CA%D4%A1%A3&rstype=xml
			StringBuffer urlParam = new StringBuffer(httpurl);
			urlParam.append("?command=sendSMS&");
			urlParam.append("username=");
			urlParam.append(userName);
			urlParam.append("&pwd=");
			urlParam.append(PWD);
			urlParam.append("&mobiles=");
			urlParam.append(mobile_to);
			urlParam.append("&content=");
			String tmpMsg = java.net.URLEncoder.encode(msg,"GBK");
			urlParam.append(tmpMsg);
			urlParam.append("&pfex=1");
			String submitRepMsg = DXHTHttpConnection.sendMessage(urlParam.toString());
			System.out.println("send -- > "+ submitRepMsg );
			
			long tmpSS = System.currentTimeMillis();
			Random random = new Random();
			int tmpint = random.nextInt(1000);
//			logger.info("success**********");
			String seq = String.valueOf(tmpSS+tmpint);
			task.addAttrib("SEQUENCE", seq);  
			task.addAttrib("SUBMIT", "success");
		
			long subtime = System.currentTimeMillis();
			if((subtime - recordTime)<= 60*1000 )
			{
				//发送量++
				quantity ++;
			}
			else
			{
				//SmsplatGlobalVariable.SEND_RATIO = (((float)quantity)/60);
				quantity = 1;//重置quantity
				recordTime = System.currentTimeMillis();//重置即时开始时间
			}
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			task.addAttrib("SUBMIT", "exception");
//			logger.error(e.getMessage());
		}
		finally
		{
			

		}
		
		return task;
	}
}
