package com.binggou.sms.mission.core.receive;


import com.binggou.sms.mission.core.about.data.dto.Message;
import com.binggou.sms.mission.core.about.util.SmsplatGlobalVariable;

/**
 * 新内核接收程序，与web耦合
 * @author chenhj(brenda)
 * @version 0.1
 */
public class ReceiveMessageBridge {
	/**
	 * 接收上行消息
	 * @param message 消息
	 */
	public static void receiveMessage(Message message) {
////		SmsDao dao = new SmsDao();
////		dao.receiveMessage(message);
//
//		//System.out.println(" ==============处理结果上行短信=============");
//
//		String userName = "HJHZMORECEIVER";//指定登录用户名
//		String password = "82259680";//指定登录密码
//		String mobile = message.getFromNum();
//		String sp = message.getServiceID();
//		String msg = message.getMSG_CONTEXT();
//		String channel = message.getChannelId();
//
//		HarmonyServiceSoapLocator lo = new HarmonyServiceSoapLocator();
//		HarmonyServiceSoapPortType stub;
//		try {
//			stub = lo.getHarmonyServiceSoapHttpPort();
//			String result = stub.receiveMessage(userName, password, sp, mobile, msg, channel);
//			SmsplatGlobalVariable.RECEIVE_QUANTITY++;
//			//System.out.println(" 处理结果 = " + result);
//		} catch (ServiceException e) {
//			e.printStackTrace();
//		} catch (RemoteException e) {
//			e.printStackTrace();
//		}
	}
	
	public static void main(String[] args){
		Message a = new Message();
		a.setFromNum("13581644435");
		a.setMSG_CONTEXT("上行测试");
		a.setServiceID("106575580128");
		
		receiveMessage(a);
	}
	
}
