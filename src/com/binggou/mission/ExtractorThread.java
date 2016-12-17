package com.binggou.mission;

import java.util.HashMap;


import com.binggou.mission.common.Extractable;
//import org.apache.log4j.Logger;
//import org.apache.log4j.BasicConfigurator;
//import org.apache.log4j.Level;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 任务榨取线程对象提供一个运行环境，调用实现了Extractable接口的任务榨取对象， 完成具体的榨取任务。
 * </p>
 * @author chenhj(brenda)
 * @version 1.0   update by zhengya 2009-12-15
 */

public class ExtractorThread extends MissionThread
{
//	private static Logger logger = Logger.getLogger(ExtractorThread.class);
    Extractable extractor = null;//任务榨取对象句柄,具体实现是哪个实现类的话，请查看mission-config.xml中的ExtractorClassName

    /**
     * 构造函数
     */
    public ExtractorThread()
    {
    }

    /**
     * 任务榨取线程初始化
     */
    public void init()
    {
        //实例化具体的任务榨取对象
        HashMap<String, String> paramsMap = config.getObjectParams("extractor-thread");
        String strExtractorClassName = paramsMap.get("ExtractorClassName");
        try
        {
            Class cls = Class.forName(strExtractorClassName);
            extractor = (Extractable) cls.newInstance();
        }
        catch (ClassNotFoundException e)
        {
        	e.printStackTrace();
//        	logger.debug(e.getMessage());
        }
        catch (InstantiationException e)
        {
        	e.printStackTrace();
//        	logger.debug(e.getMessage());
        }
        catch (IllegalAccessException e)
        {
        	e.printStackTrace();
//        	logger.debug(e.getMessage());
        }
        extractor.setConfig(config);
        extractor.setLog(log);
        extractor.setProcessingQueue(processingQueue);
        extractor.setExtractQueue(extractQueue);
        extractor.setCallbackQueue(callbackQueue);
        extractor.init();
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
        	if(!isRequiredQuit){
	            try
	            {
	                extractor.extract();
	                //当前线程休眠指定时间间隔
	                Thread.sleep(processInterval);
	            }
	            catch (Exception e)
	            {
	            	e.printStackTrace();
//	            	logger.debug(e.getMessage());
	            }
        	}else{
	           	 try{
//	            	logger.debug("榨取处理线程退出!");
                     System.out.println("榨取处理线程退出!");
                     sleep(1500);
	        	 }catch(Exception e){
	        		 e.printStackTrace();
//	        		 logger.debug(e.getMessage());
	        	 }
        	}
        }
    }
}