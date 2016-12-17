package com.binggou.mission;

import java.util.HashMap;

//import org.apache.log4j.Logger;


import com.binggou.mission.common.Popable;

/**
 * <p>Title: 发送任务处理平台</p>
 * <p>Description:
 * 任务处理线程对象提供一个运行环境，调用实现了Popable接口的任务处理对象，
 * 完成具体的任务处理。
 * </p>
 * @author chenhj(brenda)
 * @version 1.0
 */

public class PopperThread extends MissionThread
{
//	private static Logger logger = Logger.getLogger(PopperThread.class);
    Popable popper = null;//发送对象句柄,具体实现是哪个实现类的话，请查看mission-config.xml中的PopperClassName
    private long startTime =0;
    private long endTime =0;
   /**
    * 构造函数
    */
   public PopperThread()
   { }

   /**
    * 任务榨取线程初始化
    */
   public void init()
   {
      //实例化具体的任务处理对象
      HashMap<String, String> paramsMap = config.getObjectParams("popper-thread");
      String strPopperClassName = paramsMap.get("PopperClassName");
      try
      {
         Class cls = Class.forName(strPopperClassName);
         popper = (Popable)cls.newInstance();
      }
      catch (ClassNotFoundException e)
      {
      	e.printStackTrace();
//      	logger.error(e.getMessage());
      }
      catch (InstantiationException e)
      {
      	e.printStackTrace();
//      	logger.error(e.getMessage());
      }
      catch (IllegalAccessException e)
      {
      	e.printStackTrace();
//      	logger.error(e.getMessage());
      }
      popper.setConfig(config);
      popper.setLog(log);
      popper.setExtractQueue(extractQueue);
      popper.setCallbackQueue(callbackQueue);
      popper.init();
   }

   /**
    * 线程运行
    */
   public void run()
   {
      init();
      //判断请求退出标示，循环处理任务
      while(true)
      {
//    	  logger.info("extractQueue.getCount()*******"+extractQueue.getCount());
          System.out.println("extractQueue.getCount()*******"+extractQueue.getCount());
          if(!isRequiredQuit || extractQueue.getCount()>0){
	         /**休眠机制要改善，用标准时间减去此次发送用时计算本次休眠时间*/
	    	 int mq = 1;
	         try {
//	        	logger.info("发送一个任务时需要花费时间00000*******");
                 System.out.println("发送一个任务时需要花费时间00000*******");
                 startTime = System.currentTimeMillis();
				popper.pop();
				endTime = System.currentTimeMillis(); 
				mq = popper.getMobileQuantities();
//				logger.info("发送一个任务时需要花费时间11111*******"+mq);
                 System.out.println("发送一个任务时需要花费时间11111*******"+mq);
             } catch (Exception e1) {
				e1.printStackTrace();
//				logger.error(e1.getMessage());
			}
	         //当前线程休眠指定间隔时间
	         try{
//				     logger.info("发送一个任务时需要花费时间*******"+mq * processInterval+"*****************"+(endTime-startTime));
                     System.out.println("发送一个任务时需要花费时间*******"+mq * processInterval+"*****************"+(endTime-startTime));
                     if(mq * processInterval>(endTime-startTime)){
	                     Thread.sleep(mq * processInterval-(endTime-startTime));
	                 }else{
	                     Thread.sleep(5);
	                 }
	         }catch(Exception e){
	        	 e.printStackTrace();
//	        	 logger.error(e.getMessage());
	         }
         }else{
        	 try{
//        		 logger.info("处理线程正处于暂停阶段!");
                 System.out.println("处理线程正处于暂停阶段!");
                 sleep(1500);
        	 }catch(Exception e){
        		 e.printStackTrace();
//        		 logger.error(e.getMessage());
        	 }
         }
      }
     // log.runtime("处理线程退出!");
   }
}