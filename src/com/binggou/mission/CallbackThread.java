package com.binggou.mission;

import java.util.HashMap;

import com.binggou.mission.common.Callbackable;
//import org.apache.log4j.Logger;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 任务回调处理线程对象提供一个运行环境，调用实现了Callbackable接口的任务回调处理对象， 完成具体的回调任务处理
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

public class CallbackThread extends MissionThread
{
//	private static Logger logger = Logger.getLogger(CallbackThread.class);
    Callbackable caller = null;//回调任务处理对象句柄,具体实现是哪个实现类的话，请查看mission-config.xml中的CallbackClassName

    /**
     * 构造函数
     */
    public CallbackThread()
    {
    }

    /**
     * 回调处理线程初始化
     */
    public void init()
    {
        //实例化具体的回调处理对象
        HashMap<String, String> paramsMap = config.getObjectParams("callback-thread");
        String strCallbackClassName = paramsMap.get("CallbackClassName");
        try
        {
            Class cls = Class.forName(strCallbackClassName);
            caller = (Callbackable) cls.newInstance();
        }
        catch (ClassNotFoundException e)
        {
        }
        catch (InstantiationException e)
        {
        }
        catch (IllegalAccessException e)
        {
        }
        caller.setConfig(config);
        caller.setLog(log);
        caller.setProcessingQueue(processingQueue);
        caller.setCallbackQueue(callbackQueue);
        caller.init();
    }

    /**
     * 线程运行
     */
    public void run()
    {
        init();
        //判断请求退出标示，循环处理任务
        while (true)
        {
        	if(!isRequiredQuit || extractQueue.getCount() > 0 || callbackQueue.getCount() > 0){
		            try {
						caller.callback();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
		            //当前线程休眠指定时间间隔
		            try
		            {
		                Thread.sleep(processInterval);
		            }
		            catch (Exception e)
		            {
		            	e.printStackTrace();
		            }
        	}else{
	           	 try{
//	        		 logger.info("回调处理线程退出!");
                     System.out.println("回调处理线程退出!");
                     sleep(1500);
	        	 }catch(Exception e){
	        		 e.printStackTrace();
	        	 }
        	}
        }
       // log.runtime("回调处理线程退出！");
    }
}