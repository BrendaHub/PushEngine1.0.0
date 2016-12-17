package com.binggou.sms;

import java.util.Date;
import java.util.HashMap;

import com.binggou.mission.Popper;
import com.binggou.mission.Task;
import com.binggou.sms.mission.core.send.factory.SenderFactory;
//import org.apache.log4j.Logger;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 短信业务处理实现对象，从Popper类派生，实现具体的短信发送逻辑
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

public class SMSPopper extends Popper
{
    protected HashMap<String, String> paramsMap = null;//短信任务处理对象参数信息
    private int channel_id = 0;//用到的通道
//    private static Logger logger = Logger.getLogger(SMSPopper.class);
    /**
     * 构造函数
     */
    public SMSPopper()
    {    }
    
    /**
     * 短信发送任务处理对象初始化
     * @return 初始化是否成功
     */
    public boolean init()
    {
    	paramsMap = config.getConfigParams("HJHZConnect");
		if (paramsMap == null)
		{
//		  logger.info("严重错误，榨取线程在读取BGEngineConfig.xml出问题");
			System.out.println("严重错误，榨取线程在读取BGEngineConfig.xml出问题");
		  return false;
		}else{
			channel_id = new Integer(paramsMap.get("channel-id")).intValue();//采用通道号码
			return true;
		}
    }

    int mobileQuantities = 1;
    
    /**
     * 业务发送任务处理
     * @return 操作成功返回true,操作失败返回false
     */
    public boolean pop()
    {
    	long startTime = System.currentTimeMillis();
    	boolean result_flag = false;
    	
    	Task task = (Task) extractQueue.getTask();//extractQueue对象句柄是从PoperThread父类继承过来的
//    	logger.info("从榨取队列中获取即将要发送的task："+task);
		System.out.println("从榨取队列中获取即将要发送的task："+task);

    	if (task == null)
    	{
    		return true;
		}
 	    
       /**获得一个待处理任务,从榨取队列里面来获取*/
        try
        {
        	try{
        		//task.addAttrib("pushTime" ,new Timestamp(new Date().getTime()));//设置发送时间
        		task.addAttrib("pushTime" ,new Date().getTime());//设置发送时间
        	}catch(Exception e){
        		e.printStackTrace();
        	}
        task = SenderFactory.getSender(channel_id).sendMessage(task);//利用创建工厂方式，来创建发送的实现类,然后调用实现类的sendMessage方法来发送
      		
        }
        catch (Exception e)//得确保exception往外抛出来
        {
			e.printStackTrace();
//			logger.info(
			System.out.println("发送线程发送消息时出错: 错误内容为："+e.getMessage());
			task.addAttrib("SUBMIT", "exception");//设置提交状态
            result_flag = false;
        }
        finally{
    		 //task.addAttrib("reportTime", new Timestamp(new Date().getTime()));//设置确认状态的时间戳
    		 task.addAttrib("reportTime", new Date().getTime());//设置确认状态的时间戳
             callbackQueue.putTask(task);        	
        }
        //处理成功，返回成功
        long end = System.currentTimeMillis();
        System.out.println("end-start::::::::::::::::::"+(end-startTime));
        return result_flag;
    }

	public int getMobileQuantities() {
		return mobileQuantities;
	}
    
}