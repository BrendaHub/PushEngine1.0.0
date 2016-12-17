package com.binggou.sms.mission.core.send.factory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import com.binggou.sms.mission.core.send.SendMessage;
import com.huawei.insa2.util.Args;
import com.huawei.insa2.util.Cfg;
/**
 * <p>
 * Title: 发送工厂
 * </p>
 * <p>
 * Description: 根据参数的不同，返回不同的发送类实例，会去读取SMProxy.xml文件
 * </p>
 * @see //静态工厂模式,同时也是单例模式
 * @author chenhj(brenda)
 * @version 1.0
 */
public class SenderFactory 
{
	private static SendMessage sender = null;
	synchronized public static SendMessage getSender(int channelId)throws Exception
	{
		if(sender == null)
		{
			Args args = null;
			try {
				System.out.println(">>> channelId = " + channelId);
				args = new Cfg("bin/SMProxy.xml", false).getArgs("Channel" + channelId);
			} catch (IOException e) {
				e.printStackTrace();
				
			}
			String className = args.get("channel-class","");
			Class clz = null;
			try {
				clz = Class.forName(className);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				Class[] icp = new Class[1];
				icp[0] = String.class;		
				SenderFactory.sender = (SendMessage) clz.getMethod("getInstance", icp).invoke(null, "" + channelId);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		return sender;
	}
}