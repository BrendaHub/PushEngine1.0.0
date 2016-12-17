package com.binggou.sms.mission.core.send.impl.xinge;

/**
 * 信鸽推送引擎<br/>
 * http://27.151.112.179:8008/SDFirewall/pushiphonemsg.php
 * ?phone=18612345678&msg=hello+world%E4%BD%A0%E5%A5%BD <br/>
 * @author chenhj<br/>
 * create on : 20120323<br/>
 * 
	1、成功时返回：
		success
	2、失败时返回：
		fail:未找到与18901666030相关的令牌
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Date;
import java.util.Map;

//import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.binggou.mission.Task;
import com.binggou.sms.SQLBridge;
import com.binggou.sms.mission.core.about.util.MD5;
import com.binggou.sms.mission.core.send.SendMessage;
import com.hjhz.xg.agent.MessagePushAgent;
import com.hjhz.xg.util.Tools;
import com.hjhz.xg.vo.JMessage;
import com.hjhz.xg.vo.JMessageIOS;
import com.hjhz.xg.work.MessagePush;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Cfg;
import com.tencent.xinge.Message;
import com.tencent.xinge.XingeApp;

public class HttpSMSCommon implements SendMessage {
	public static String module	= HttpSMSCommon.class.getName();
	private static HttpSMSCommon instance=null;
//	private static Logger logger = Logger.getLogger(HttpSMSCommon.class);//写日志
	private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	private static String url ="";//url
	private static int isSendSMS = 0;//是否需要短信重发
	private static MD5 md5= new MD5();
	private static MessagePushAgent mpa = new MessagePushAgent();
	private static MessagePush mp = new MessagePush();
	protected SQLBridge sqlBridge = null;
	private int updateRows = 0;
	
	private HttpSMSCommon(){}
	
	synchronized public static HttpSMSCommon getInstance(String config) {
		if(instance==null){
				if(config == null){
					System.exit(1);
				}
				Args args = null;
				try{
					args = new Cfg("bin/SMProxy.xml", false).getArgs("Channel" + config);
				}catch(IOException e){
					e.printStackTrace();
				}			
			instance = new HttpSMSCommon();
		}
		return instance;
	}
	
	/**
	 * 初始化信鸽Message方法
	 * 
	 */
	private JMessage getJMessage(String title,String message,int pushType,Map<String, Object> extParam){
		JMessage jMessage = new JMessage();
		jMessage.setTitle(title);
		jMessage.setContent(message);
		jMessage.setCustom(extParam);
		jMessage.setType(pushType==0?Message.TYPE_NOTIFICATION:Message.TYPE_MESSAGE);
		return jMessage;
	}
	
	private JMessageIOS getJMessageIOS(String title,String message,int pushType,Map<String, Object> extParam,int badgeNum){
	    JMessageIOS jMessageIOS = new JMessageIOS();
		jMessageIOS.setAlert(message);
		jMessageIOS.setCategory("陶牛帮");
		jMessageIOS.setBadge(badgeNum);
		jMessageIOS.setCustom(extParam);
		jMessageIOS.setSound("beep.wav");
		jMessageIOS.setExpireTime(10*60*60);
		return jMessageIOS;
	}
	
	
	/**
	 * 发送方法
	 * @param task
	 * @return
	 */
	public Task sendMessage(Task task){
		JSONObject json=new JSONObject();
		JSONObject jsonResult=new JSONObject();
		JSONObject resultAnd = null;
		JSONObject resultIOS = null;
		JSONObject actionParam = Tools.createActionParam("TYPE_ACTIVITY", "", 0, "", 0, 0, "");
		List<String> deviceList=new ArrayList<String>();//token的存放list
		String title = (String)task.getAttribValue("title");//推送信息标题
		String message = (String)task.getAttribValue("content");//推送信息内容
		String extParamMsg = (String)task.getAttribValue("extParam");//隐藏域内容
		int pushType = Integer.parseInt((String) task.getAttribValue("pushType"));//推送方式
		int badgeNum = Integer.parseInt((String) task.getAttribValue("badgeNum"));//IOS角标值
		String platType = ((String)task.getAttribValue("platType")).equals("0")?"ios":"android";//推送平台
		String pushKind = (String)task.getAttribValue("pushKind");//推送种类
		//JSONObject extParam = new JSONObject(extParamMsg);//隐藏域存储
		String tokens = ((String)task.getMobileTo()==null?"":(String)task.getMobileTo());//token串
		tokens = tokens.trim();
		tokens=tokens.replaceAll(";", ",");
		String[] token=tokens.split(",");
		if(token.length>0){
			for(int i=0;i<token.length;i++){
				deviceList.add(token[i]);
			}
		}
		ObjectMapper mapper = new ObjectMapper();  
		Map<String,Object> extParam = new HashMap<String,Object>();
//		try {
//			extParam = mapper.readValue(extParamMsg, Map.class);
//			System.out.println(extParam);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
             
		try{
			if(message == null || "".equals(message)){
				task.addAttrib("reportTime", new Date().getTime());//设置确认状态的时间戳
				task.addAttrib("SUBMIT", "sendfail_xinge");//设置发送失败
			}else{
				String jsonPush1="";
				int msgLength = 0;
				int titleLen = 0 ; 
				try {
					msgLength = message.getBytes("iso8859-1").length;
					titleLen = title.getBytes("iso8859-1").length;
					task.addAttrib("msgLength", msgLength);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				int maxLehgth=49;
				if(null!=platType && !"".equals(platType) && platType.equals("ios")){
					
				}else{
					maxLehgth=200;
					maxLehgth -= titleLen;
				}
				if(msgLength>(maxLehgth)){//说明消息内容的长度大于最大值（maxLehgth）减去扩展参数的长度
					message=message.substring(0, (maxLehgth-2))+"...";//因为要增加"..."所以消息内容多截取两位(多增加了三个字符)
				}
				long start = System.currentTimeMillis();
				Object JSONObject;
				if(token.length>0&&!token[0].equals("all")){//根据token数组长度判断推送类型为单推还是批量退，多个为批量退，单个为单推或推所有
					if(platType.equals("android")){
						System.out.println("android 开始创建pushid===========================");
						System.out.println("message = " + message);
						System.out.println("title" + title);
						System.out.println("extParam = " + extParam);
						System.out.println("pushType = " + pushType);
						json = mp.createMultipush(getJMessage(title, message, pushType,extParam));
						System.out.println("json reslult = "+ json);
						System.out.println("android 结束创建pushid===========================");
						//JSONObject json2 = (JSONObject)json.get("result");
						if(null!=json&&(Integer)json.get("ret_code")==0){
							
							JSONObject json2 = (JSONObject)json.get("result");//(1)
							System.out.println("android 开始开始发送===========================");
							System.out.println("pushID =" + (String)json2.get("push_id"));
							System.out.println("android deviceList =" + deviceList);
						    jsonResult=mp.pushDeviceListMultipleAndroid(Integer.parseInt((String)json2.get("push_id")), deviceList);
						    Thread.sleep(3000);//推送延时，信鸽后台对本接口的调用频率有限制，两次调用之间的时间间隔不能小于9秒。
							System.out.println("推送成功==="+message+"   jsonResult="+jsonResult);
							System.out.println("android 结束发送===========================");
							task.addAttrib("pushId", Integer.parseInt((String)json2.get("push_id")));
							json = jsonResult;
						}
					}else{
						System.out.println("IOS 开始创建pushid===========================");
						System.out.println("message = " + message);
						System.out.println("extParam = " + extParam);
						System.out.println("pushType = " + pushType);
						System.out.println("badgeNum" + badgeNum);
						System.out.println("title" + title);
						System.out.println("XingeApp.IOSENV_PROD" + XingeApp.IOSENV_PROD);
						json = mp.createMultipush(getJMessageIOS(title, message, pushType, extParam, badgeNum), XingeApp.IOSENV_PROD);
						System.out.println("ios json reslult = "+ json);
						//JSONObject json2 = (JSONObject)json.get("result");
						if(null!=json&&(Integer)json.get("ret_code")==0){
							
							JSONObject json2 = (JSONObject)json.get("result");//(2)
							System.out.println("IOS 开始开始发送===========================");
							System.out.println("pushID =" + (String)json2.get("push_id"));
							System.out.println("IOS deviceList =" + deviceList);
						    jsonResult=mp.pushDeviceListMultipleIOS(Integer.parseInt((String)json2.get("push_id")), deviceList);
						    Thread.sleep(3000);//推送延时，信鸽后台对本接口的调用频率有限制，两次调用之间的时间间隔不能小于9秒。
							System.out.println("推送成功==="+message+"   jsonResult="+jsonResult);
							task.addAttrib("pushId", Integer.parseInt((String)json2.get("push_id")));
							json = jsonResult;
						}
					}
				}else if(token[0].equals("all")){
						System.out.println("================android进入了推送all通道==============================");
						System.out.println("title =" + title);
						System.out.println("message =" + message);
						System.out.println("pushType =" + pushType);
						System.out.println("extParam =" + extParam);
						resultAnd = mp.pushAllDevicesAndroid(0, getJMessage(title, message, pushType,extParam));
						System.out.println("结束： " + resultAnd);
						Thread.sleep(3500);//推送延时，信鸽后台对本接口的调用频率有限制，两次调用之间的时间间隔不能小于3秒。

						System.out.println("================IOS进入了推送all通道==============================");
						System.out.println("title =" + title);
						System.out.println("message =" + message);
						System.out.println("pushType =" + pushType);
						System.out.println("extParam =" + extParam);
						System.out.println("badgeNum =" + badgeNum);
						System.out.println(" XingeApp.IOSENV_PROD =" +  XingeApp.IOSENV_PROD);
						resultIOS = mp.pushAllDevicesIOS(0, getJMessageIOS(title, message, pushType, extParam, badgeNum), XingeApp.IOSENV_PROD);
						//task.addAttrib("pushId", Integer.parseInt((String)resultAnd.get("push_id")));
						System.out.println("结束： " + resultIOS);
						int and = resultAnd.getInt("ret_code");
						int ios = resultIOS.getInt("ret_code");
						//json = new JSONObject("{ret_code:0}");
						if(and==0&&ios==0){
							json = new JSONObject("{ret_code:0}");
						}else{
							if(and!=0){
								json = new JSONObject("{err_msg:and_err"+resultAnd.getString("err_msg")+",ret_code:0}");
							}else{
								json = new JSONObject("{err_msg:ios_err"+resultIOS.getString("err_msg")+",ret_code:0}");
							}
						}
						task.addAttrib("pushId", 1);
//					}	
				}
				long end = System.currentTimeMillis();
//				logger.info(module + "INFO -> "+getCurDateTime()+", >>> 推送此条信息耗时 ..."+ (end - start) +" 秒。");
				System.out.println(module + "INFO -> "+getCurDateTime()+", >>> 推送此条信息耗时 ..."+ (end - start) +" 秒。");
			}
			if(null!=json && !json.isNull("ret_code")){
				Long ret_code=json.getLong("ret_code");
				if(ret_code==0){//信息推送成功
					
					task.addAttrib("SUBMIT", "xinge_success");
					task.addAttrib("ERR_MSG", "消息推送成功");
					try{
						task.addAttrib("pushTime", new Date().getTime());//设置确认状态的时间戳	
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{//信息推送失败
					String err_msg=json.getString("err_msg");
					task.addAttrib("ERR_MSG", err_msg);
					try{
						task.addAttrib("pushTime", new Date().getTime());//设置确认状态的时间戳	
					}catch(Exception e){
						e.printStackTrace();
					}
					//task.addAttrib("resend_times_flag", new Boolean(false));
					task.addAttrib("SUBMIT", "sendfail_xinge");
				}
			}else{//没有应答   
				task.addAttrib("ERR_MSG", "消息推送出现异常情况");
				try{
					task.addAttrib("pushTime", new Date().getTime());//设置确认状态的时间戳	
				}catch(Exception e){
					e.printStackTrace();
				}
				//task.addAttrib("resend_times_flag", new Boolean(false));
				task.addAttrib("SUBMIT", "sendfail_xinge");
			}
		}catch(Exception pe){
				pe.printStackTrace();
				/****(3)****/
				String err_msg=json.getString("err_msg");
				task.addAttrib("ERR_MSG", err_msg);
				/********/
				task.addAttrib("pushTime", new Date().getTime());//设置确认状态的时间戳
				task.addAttrib("SUBMIT", "sendfail_xinge");//设置发送失败		
			}finally{
				try {
					Thread.sleep(1000);//接口的访问间隔是0.1秒，最快访问速度是每秒十次。
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return task;
		}
	
	
	/**
	 * 发送方法
	 * @param task
	 * @return
	 */
	public Task sendMessage_old(Task task){
		JSONObject json=new JSONObject();
		JSONObject jsonResult=new JSONObject();
		JSONObject resultAnd = null;
		JSONObject resultIOS = null;
		JSONObject actionParam = Tools.createActionParam("TYPE_ACTIVITY", "", 0, "", 0, 0, "");
		List<String> deviceList=new ArrayList<String>();//token的存放list
		String title = (String)task.getAttribValue("title");//推送信息标题
		String message = (String)task.getAttribValue("content");//推送信息内容
		String extParamMsg = (String)task.getAttribValue("extParam");//隐藏域内容
		int pushType = Integer.parseInt((String) task.getAttribValue("pushType"));//推送方式
		int badgeNum = Integer.parseInt((String) task.getAttribValue("badgeNum"));//IOS角标值
		String platType = ((String)task.getAttribValue("platType")).equals("0")?"ios":"android";//推送平台
		String pushKind = (String)task.getAttribValue("pushKind");//推送种类
		//JSONObject extParam = new JSONObject(extParamMsg);//隐藏域存储
		String tokens = ((String)task.getMobileTo()==null?"":(String)task.getMobileTo());//token串
		tokens = tokens.trim();
		tokens=tokens.replaceAll(";", ",");
		String[] token=tokens.split(",");
		if(token.length>0){
			for(int i=0;i<token.length;i++){
				deviceList.add(token[i]);
			}
		}
		ObjectMapper mapper = new ObjectMapper();  
		Map<String,Object> extParam = new HashMap<String,Object>();
		try {
			extParam = mapper.readValue(extParamMsg, Map.class);
			System.out.println(extParam);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 
             
		try{
			if(message == null || "".equals(message)){
				task.addAttrib("reportTime", new Date().getTime());//设置确认状态的时间戳
				task.addAttrib("SUBMIT", "sendfail_xinge");//设置发送失败
			}else{
				String jsonPush1="";
				int msgLength = 0;
				int titleLen = 0 ; 
				try {
					msgLength = message.getBytes("iso8859-1").length;
					titleLen = title.getBytes("iso8859-1").length;
					task.addAttrib("msgLength", msgLength);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				int maxLehgth=49;
				if(null!=platType && !"".equals(platType) && platType.equals("ios")){
					
				}else{
					maxLehgth=200;
					maxLehgth -= titleLen;
				}
				if(msgLength>(maxLehgth)){//说明消息内容的长度大于最大值（maxLehgth）减去扩展参数的长度
					message=message.substring(0, (maxLehgth-2))+"...";//因为要增加"..."所以消息内容多截取两位(多增加了三个字符)
				}
				long start = System.currentTimeMillis();
				Object JSONObject;
				if(token.length>0&&!token[0].equals("all")){//根据token数组长度判断推送类型为单推还是批量退，多个为批量退，单个为单推或推所有
					if(platType.equals("android")){
						System.out.println("title:"+title+"message:"+message+"=="+"extParam:"+extParam);
						json = mp.createMultipush(getJMessage(title, message, pushType,extParam));
						System.out.println("json reslult = "+ json);
						//JSONObject json2 = (JSONObject)json.get("result");
						if(null!=json&&(Integer)json.get("ret_code")==0){
							
							JSONObject json2 = (JSONObject)json.get("result");//(1)
							
						    jsonResult=mp.pushDeviceListMultipleAndroid(Integer.parseInt((String)json2.get("push_id")), deviceList);
							System.out.println("推送成功==="+message+"   jsonResult="+jsonResult);
							task.addAttrib("pushId", Integer.parseInt((String)json2.get("push_id")));
							json = jsonResult;
						}
					}else{
						System.out.println("title:"+title+"   "+"message:"+message+"   "+"extParam:"+extParam);
						json = mp.createMultipush(getJMessageIOS(title, message, pushType, extParam, badgeNum), XingeApp.IOSENV_PROD);
						System.out.println("ios json reslult = "+ json);
						//JSONObject json2 = (JSONObject)json.get("result");
						if(null!=json&&(Integer)json.get("ret_code")==0){
							
							JSONObject json2 = (JSONObject)json.get("result");//(2)
							
						    jsonResult=mp.pushDeviceListMultipleIOS(Integer.parseInt((String)json2.get("push_id")), deviceList);
							System.out.println("推送成功==="+message+"   jsonResult="+jsonResult);
							task.addAttrib("pushId", Integer.parseInt((String)json2.get("push_id")));
							json = jsonResult;
						}
					}
				}else if(token[0].equals("all")){
						resultAnd = mp.pushAllDevicesAndroid(0, getJMessage(title, message, pushType,extParam));
						Thread.sleep(3500);//推送延时，信鸽后台对本接口的调用频率有限制，两次调用之间的时间间隔不能小于3秒。
						resultIOS = mp.pushAllDevicesIOS(0, getJMessageIOS(title, message, pushType, extParam, badgeNum), XingeApp.IOSENV_PROD);
						//task.addAttrib("pushId", Integer.parseInt((String)resultAnd.get("push_id")));
						int and = resultAnd.getInt("ret_code");
						int ios = resultIOS.getInt("ret_code");
						//json = new JSONObject("{ret_code:0}");
						if(and==0&&ios==0){
							json = new JSONObject("{ret_code:0}");
						}else{
							if(and!=0){
								json = new JSONObject("{err_msg:and_err"+resultAnd.getString("err_msg")+",ret_code:0}");
							}else{
								json = new JSONObject("{err_msg:ios_err"+resultIOS.getString("err_msg")+",ret_code:0}");
							}
						}
						task.addAttrib("pushId", 1);
//					}	
				}
				long end = System.currentTimeMillis();
//				logger.info(module + "INFO -> "+getCurDateTime()+", >>> 推送此条信息耗时 ..."+ (end - start) +" 秒。");
				System.out.println(module + "INFO -> "+getCurDateTime()+", >>> 推送此条信息耗时 ..."+ (end - start) +" 秒。");
			}
			if(null!=json && !json.isNull("ret_code")){
				Long ret_code=json.getLong("ret_code");
				if(ret_code==0){//信息推送成功
					
					task.addAttrib("SUBMIT", "xinge_success");
					task.addAttrib("ERR_MSG", "消息推送成功");
					try{
						task.addAttrib("pushTime", new Date().getTime());//设置确认状态的时间戳	
					}catch(Exception e){
						e.printStackTrace();
					}
				}else{//信息推送失败
					String err_msg=json.getString("err_msg");
					task.addAttrib("ERR_MSG", err_msg);
					try{
						task.addAttrib("pushTime", new Date().getTime());//设置确认状态的时间戳	
					}catch(Exception e){
						e.printStackTrace();
					}
					//task.addAttrib("resend_times_flag", new Boolean(false));
					task.addAttrib("SUBMIT", "sendfail_xinge");
				}
			}else{//没有应答   
				task.addAttrib("ERR_MSG", "消息推送出现异常情况");
				try{
					task.addAttrib("pushTime", new Date().getTime());//设置确认状态的时间戳	
				}catch(Exception e){
					e.printStackTrace();
				}
				//task.addAttrib("resend_times_flag", new Boolean(false));
				task.addAttrib("SUBMIT", "sendfail_xinge");
			}
		}catch(Exception pe){
				pe.printStackTrace();
				/****(3)****/
				String err_msg=json.getString("err_msg");
				task.addAttrib("ERR_MSG", err_msg);
				/********/
				task.addAttrib("pushTime", new Date().getTime());//设置确认状态的时间戳
				task.addAttrib("SUBMIT", "sendfail_xinge");//设置发送失败		
			}finally{
				try {
					Thread.sleep(1000);//接口的访问间隔是0.1秒，最快访问速度是每秒十次。
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			return task;
		}
	
	/**
     * 获得当前日期和时间
     *
     * @return String 当前日期和时间，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String getCurDateTime()
    {
        SimpleDateFormat nowDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return nowDate.format(new Date());

    }
    
	/**
	 * 获取短信平台WS接口的发送对象
	 */
//	public HarmonyServiceSoapPortType getWSSMS(){
//		HarmonyServiceSoapPortType testStub = null;
//		HarmonyServiceSoapLocator client = new HarmonyServiceSoapLocator();
//		try {
//			testStub = client.getHarmonyServiceSoapHttpPort();
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		}
//		return testStub;
//	}

}