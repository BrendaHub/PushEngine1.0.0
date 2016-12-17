package com.binggou.mission;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import com.binggou.sms.mission.core.about.util.ConnectionFactory;
import com.binggou.sms.mission.core.about.util.SmsplatGlobalVariable;
import org.apache.log4j.Logger;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 任务中心是整个发送任务平台的调度和交流中心。任务中心初始化各个子模块， 调度它们处理任务，用任务列表协调各个模块的交互。
 * </p>
 * @author chenhj(brenda)
 * @version 1.0  update by zhengya 2009-12-16
 */

public class MissionCenter
{
	/**与socketServer相互连接时所需要的变量*/
	private int channel_id = 0;
    private Socket socket;
    private String serverSocketPort;
    private String host;
    private BufferedReader in;
    private PrintWriter out;
    private InetAddress ias ;
    private boolean isException = false;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss.S");
    private static Date startTime = new Date();
    private static long moCount=0;//上行短信的条数
    private static long statusSuccessCount=0;//状态报告的成功条数
    private static long statusFailCount=0;//状态报告的失败条数
    private static long sendSuccessCount=0;//发送的成功条数
    private static long sendFailCount=0;//发送的失败条数   
    private static long lastSendCount=0;//发送上一次的发送总数
    private long reconnect_interval =0;//连接中断时重连间隔时间(单位：毫秒)
    private long heartbeat_interval =0;//心跳信息发送间隔时间(单位：毫秒)
    private boolean doFailback = true;//是否进行故障恢复
    private static Logger logger = Logger.getLogger(MissionCenter.class.getName());//日志类
    private MissionConfig config = null;//平台配置对象句柄
//    private MissionLog log = null;//日志对象句柄
    private MissionQueue processingQueue = null;//正在系统中中处理的任务队列
    private MissionQueue extractQueue = null;//待处理任务队列句柄
    private MissionQueue callbackQueue = null;//待处理回调任务队列句柄
    private LinkedList<MissionThread> extractorThreads = null;//任务榨取线程列表句柄
    private LinkedList<MissionThread> callbackThreads = null;//任务回调线程列表句柄
    private LinkedList<MissionThread> popperThreads = null;//任务处理线程列表句柄
    protected HashMap<String, String> paramsMap = null;//对象参数信息mission-center.xml
    protected HashMap<String, String> configsMap = null;//对象参数信息
    private int waitTime = 10;//wait的时间主要用于MissionQueue.java中
    /**
     * 构造函数
     */
    public MissionCenter()
    {
        //构造成员对象
        config = new MissionConfig();
        System.out.println("config = " + config);
        paramsMap = config.getObjectParams("mission-center");
        System.out.println("paramMap = " + paramsMap);
        configsMap = config.getConfigParams("HJHZConnect");
        System.out.println("config map = " + configsMap);
        host = configsMap.get("host");
        channel_id = Integer.parseInt(configsMap.get("channel-id"));
        serverSocketPort = configsMap.get("port");
        reconnect_interval =Long.parseLong(configsMap.get("reconnect-interval"));
        heartbeat_interval =Long.parseLong(configsMap.get("heartbeat-interval"));
        waitTime = Integer.parseInt(paramsMap.get("waitTime"));
    	/**建立InetAddress，为与SocketServer端建立连接做准备**/
		try {
				ias = InetAddress.getByName(host);
			} catch (UnknownHostException e) {
				e.printStackTrace();
			}
    }

    /**
     * 任务处理平台的入口函数
     * 
     * @param args 参数
     */
    public static void main(String[] args)
    {
    	System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println("开始创建数据库连接池");
        ConnectionFactory.createDataSource();
        System.out.println("数据库连接池创建结束");
        MissionCenter missionCenter = new MissionCenter();
        String db = missionCenter.configsMap.get("db-type");
        if(db == null){
            //日志记录
            logger.error("文件BGEngineConfig.xml配置出错，未配置数据库类型. 系统运行结束");
            System.exit(0);
        }
		
		try {
            int _channel_id = Integer.parseInt(missionCenter.configsMap.get("channel-id"));
            System.out.println("当前通道号： " + _channel_id );
            missionCenter.channel_id = _channel_id;
            SmsplatGlobalVariable.CHANNEL_ID = _channel_id;
			SmsplatGlobalVariable.PACK_QUANTITIES = Integer.parseInt(missionCenter.configsMap.get("pack-quantities"));
			try {
		            missionCenter.init();//任务中心对象初始化自己，并且调度各个子模块进行初始化
		            missionCenter.beginMission();//任务中心对象调度开始任务处理，任务中心对象调度各个相关对象开始任务处理
		            // SendMessage sender = SenderFactory.getSender(missionCenter.channel_id);//初始化通道,主要为了启动获取上行短信和状态报告的线程
			} catch (Exception e) {
				e.printStackTrace();
                logger.error("引擎主程序，初始化失败！");
			}
		} catch (Exception e1) {
//			logger.info("未配置该引擎对应的短信发送通道......");
            logger.error("未配置该引擎对应的通道编号，请检查BGEngineConfig.xml配置文件......");
            System.exit(0);
		}		
    }

//	   /**计算状态报告的成功条数**/
//	   synchronized public static void setStatusSuccessCount(int i){
//	   	statusSuccessCount +=i;
//	   }
//
//	   /**计算状态报告的失败条数**/
//	   synchronized public static void setStatusFailCount(int i){
//	   	statusFailCount +=i;
//	   }
//
//	   /**计算短信的成功条数**/
//	   synchronized public static void setSendSuccessCount(int i){
//	   	sendSuccessCount +=i;
//	   }
//
//	   /**计算短信的失败条数**/
//	   synchronized public static void setSendFailCount(int i){
//	   	sendFailCount +=i;
//	   }
	   
	   /**
	    * 调度各个工作线程退出当前任务处理
	    */
	   public boolean endMission(boolean flag)
	   {
	   	boolean result = false;
	       /**调度榨取线程请求退出*/
	       Iterator<MissionThread> iterator = extractorThreads.iterator();
	       while (iterator.hasNext())
	       {
	           ExtractorThread extractor = (ExtractorThread) iterator.next();
	           extractor.requireQuit(flag);
	       }
	       /**检测回调处理线程是否退出 */
	       iterator = callbackThreads.iterator();
	       while (iterator.hasNext())
	       {
	           CallbackThread callback = (CallbackThread) iterator.next();
	           callback.requireQuit(flag);
	       }
	       /**检测回调处理线程是否退出*/
	       iterator = popperThreads.iterator();
	       while (iterator.hasNext())
	       {
	           PopperThread popper = (PopperThread) iterator.next();
	           popper.requireQuit(flag);
	       }
	       result = true;
	       return result;
	   }  

    /**
     * 任务中心对象初始化自己，并且调度各个子模块进行初始化
     */
    public void init()
    {    	
        /**任务队列初始化*/
        processingQueue = new MissionQueue(waitTime);
        extractQueue = new MissionQueue(waitTime);
        callbackQueue = new MissionQueue(waitTime);        
    	
        /**调度榨取线程初始化*/
        int nThreadsCount = new Integer(paramsMap.get("ExtractorThreadsCount")).intValue();
        int processInterval = new Integer(paramsMap.get("ExtractorProcessInterval")).intValue();
        extractorThreads = new LinkedList<MissionThread>();
        for (int i = 0; i < nThreadsCount; i++)
        {
            ExtractorThread extractor = new ExtractorThread();
            extractor.setConfig(config);
//            extractor.setLog(log);
            extractor.setProcessingQueue(processingQueue);
            extractor.setExtractQueue(extractQueue);
            extractor.setCallbackQueue(callbackQueue);
            extractor.setProcessInterval(processInterval);
            extractorThreads.add(extractor);
            logger.info("调度榨取线程初始化完成....第" + i  + "线程");

        }
        /**调度回调处理线程初始化*/
        nThreadsCount = new Integer(paramsMap.get("CallbackThreadsCount")).intValue();
        processInterval = new Integer(paramsMap.get("CallbackProcessInterval")).intValue();
        callbackThreads = new LinkedList<MissionThread>();
        for (int i = 0; i < nThreadsCount; i++)
        {
            CallbackThread callback = new CallbackThread();
            callback.setConfig(config);
//            callback.setLog(log);
            callback.setProcessingQueue(processingQueue);
            callback.setExtractQueue(extractQueue);
            callback.setCallbackQueue(callbackQueue);
            callback.setProcessInterval(processInterval);
            callbackThreads.add(callback);
            logger.info("调度回调处理线程初始化始化完成....第" + i  + "线程");
        }
        /**调度处理线程初始化*/
        nThreadsCount = new Integer(paramsMap.get("PopperThreadsCount")).intValue();
        processInterval = new Integer(paramsMap.get("PopperProcessInterval")).intValue();
        popperThreads = new LinkedList<MissionThread>();
        for (int i = 0; i < nThreadsCount; i++)
        {
            PopperThread popper = new PopperThread();
            popper.setConfig(config);
//            popper.setLog(log);
            popper.setProcessingQueue(processingQueue);
            popper.setExtractQueue(extractQueue);
            popper.setCallbackQueue(callbackQueue);
            popper.setProcessInterval(processInterval);
            popperThreads.add(popper);
            logger.info("调度处理线程初始化完成....第" + i  + "线程");
        }       
        /**加载故障恢复的配置项*/
        String doFailbackString = paramsMap.get("DoFailback");
        if(!"true".equals(doFailbackString))
        {
        	this.doFailback = false;
        }
        logger.info("加载故障恢复的配置项======" + this.doFailback + "线程");
    }

    /**
     * 任务中心对象调度开始任务处理，任务中心对象调度各个相关对象开始任务处理。
     */
    public void beginMission()
    {
    	/**是否进行故障恢复，即把状态为2的短信变成状态0*/
    	if(this.doFailback)
    	{
    		//doFailback();
    	}
    	/**调度榨取线程开始工作*/
        Iterator<MissionThread> iterator = extractorThreads.iterator();
        while (iterator.hasNext())
        {
            ExtractorThread extractor = (ExtractorThread) iterator.next();
            extractor.start();
            logger.info("调度榨取线程开始工作");
        }
        /**调度处理线程开始工作*/
        iterator = popperThreads.iterator();
        while (iterator.hasNext())
        {
            PopperThread popper = (PopperThread) iterator.next();
            popper.start();
            //休眠一段时间后启动下一线程
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
            logger.info("调度处理线程开始工作");
        }
        /**调度回调处理线程开始工作*/
        iterator = callbackThreads.iterator();
        while (iterator.hasNext())
        {
            CallbackThread callback = (CallbackThread) iterator.next();
            callback.start();
            //休眠一段时间后启动下一线程
            try {
				Thread.sleep(100);
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
			logger.info("调度回调处理线程开始工作");
        }
        /**与SockeServer服务端建立连接*/
//       this.setSocketCliet();
//      Thread th = new Thread(MissionCenter.this);
//      th.start();
    }   

    /**
     * 短信引擎的时候进行故障恢复
     * @return
     */
    /*private boolean doFailback()
    {
    	logger.info("短信引擎的时候进行故障恢复 进来了..........................");
		try 
		{		
			SQLBridge sqlBridge = new SQLBridge();
			sqlBridge.openConnect();
			//将状态为2的短信置为0
			//String updateSql = "UPDATE SEND SET SEND_STATUS = 0 WHERE SEND_STATUS = 2 and channel_id="+this.channel_id;
			String updateSql = "update t_push_task SET status = 0 where status = 3";
			try
			{				
				int updateNum = sqlBridge.executeUpdate(updateSql);
				logger.info("短信引擎的时候进行故障恢复启用，修改的条数为:.........................."+updateNum);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				logger.info("短信引擎的时候进行故障异常=====");
				return false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
    	return true;
    }*/
}