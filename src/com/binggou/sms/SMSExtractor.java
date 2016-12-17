package com.binggou.sms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.binggou.mission.Extractor;
import com.binggou.mission.Task;
import com.binggou.sms.mission.core.about.util.SmsplatGlobalVariable;
import com.binggou.sms.mission.core.sqlfactory.SqlFactory;
import org.apache.log4j.Logger;

/**
 * <p>
 * Title: 发送任务处理平台
 * </p>
 * <p>
 * Description: 短信业务榨取实现对象，从Extractor类派生，实现具体的短信发送任务榨取逻辑
 * </p>
 * @author chenhj(brenda)
 * @version 1.0  update by zhengya 2009-12-15
 */

public class SMSExtractor extends Extractor {
	private SQLBridge sqlBridge = null;//数据库操作代理对象
	private HashMap<String, String> paramsMap = null;//短信榨取对象参数信息
	private int extract_quantities = 1000;//队列总容量，默认是1000
	private int extractQueueCapability = 200;//每次榨取队列容量设置
	private int maximumExtractQuantities = 200;//当短信榨取线程任务总容量满了后，可以再榨取设定优先级的最大数量
	private int extractQuantitiesLevel = 1;//当短信榨取线程任务总容量满了后，设定的优先级的级别
	private boolean maximun_flag = false;//如果执行最高优先级的榨取的话，那么就把值置为true
	private int db_type =4; //数据库类型4-ORACLE, 5-SQLSERVER, 6-MYSQL默认是4
	private int resend_times = 3;//发送失败短信的重新提交次数
	private int channel_id = 0;//用到的通道
	private static Logger logger = Logger.getLogger(SMSExtractor.class.getName());
	/**
	 * 构造函数
	 */
	public SMSExtractor() {
		// 对象实例化
		sqlBridge = new SQLBridge();
	}
	/**
	 * 短信发送任务榨取对象初始化
	 * @return 初始化是否成功
	 */
	public boolean init() {
		paramsMap = config.getObjectParams("smsextractor");
		if (paramsMap == null)
			{
//			  logger.error("严重错误，榨取线程在读取misson-config.xml出问题");
				System.out.println("严重错误，榨取线程在读取misson-config.xml出问题");
			  return false;
			}else{
				extractQueueCapability = new Integer(paramsMap.get("ExtractQueueCapability")).intValue();// 得到榨取队列容量设置
				extract_quantities = new Integer(paramsMap.get("ExtractQuantities")).intValue();//读取榨取线程的总容量
				maximumExtractQuantities =new Integer(paramsMap.get("MaximumExtractQuantities")).intValue();//当短信榨取线程任务总容量满了后，可以再榨取设定优先级的最大数量
				extractQuantitiesLevel =new Integer(paramsMap.get("ExtractQuantitiesLevel")).intValue();//当短信榨取线程任务总容量满了后，设定的优先级的级别
			}
		paramsMap = config.getConfigParams("HJHZConnect");
		if (paramsMap == null)
		{
//		  logger.error("严重错误，榨取线程在读取BGEngineConfig.xml出问题");
			System.out.println("严重错误，榨取线程在读取BGEngineConfig.xml出问题");
		  return false;
		}else{
			db_type = new Integer(paramsMap.get("db-type")).intValue();//数据库类型4-ORACLE, 5-SQLSERVER, 6-MYSQL默认是4
			resend_times = new Integer(paramsMap.get("resend-times")).intValue();//发送失败短信的重新提交次数
			channel_id = new Integer(paramsMap.get("channel-id")).intValue();//采用通道号码
		}
		return true;
	}

	/**
	 * 榨取短信任务
	 * @return 操作成功返回true,操作失败返回false
	 */
	public boolean extract() {
		logger.debug(":榨取线程准备开始... ");

		boolean result_flag = true;
		List al = null;//list存放着task列表
		
		/**榨取线程队列+回调线程队列=队列总容量,如果超过队列总容量的话，那么就停止榨取队列*/
		int currentTaskNum = extractQueue.getCount() + callbackQueue.getCount();
		logger.info("榨取线程队列+回调线程队列 的容量===="+currentTaskNum);
		if(currentTaskNum >= (extract_quantities+maximumExtractQuantities))
		{
			logger.info("总队列榨取队列容量满.");
			return result_flag;
		}
		
		/**未超过队列总容量时，进行的榨取处理*/
		  try {
	            long start1 = System.currentTimeMillis();
				  logger.debug("开始创建连接....");
				  sqlBridge.openConnect();// 建立数据库连接
			    /**按数据库优先级来处理，在BGEngineConfig.xml文件中prior_type进行配置,默认是-1，即-1按数据库表中优先级*/
				if(currentTaskNum < extract_quantities){
					/**控制普通榨取发送的总量不能超过设定的总量*/
					if((extract_quantities-currentTaskNum)>extractQueueCapability){
						al = extractByPriorForCitics(extractQueueCapability);// 榨取发送的短信任务
					}else{
						al = extractByPriorForCitics((extract_quantities-currentTaskNum));//榨取发送短信任务
					}
				// logger.info("从表中榨取出来的数据===="+al);
				maximun_flag =false;
				}
				long end1 = System.currentTimeMillis();
				// logger.error("榨取花费的时间***000***********************"+(end1-start1));
				if ( al == null) {
					return false;//标识没有task往外发送
				}
				
				/**根据存储过程返回的榨取结果集，生成新任务提交*/
				long start2 = System.currentTimeMillis();
				Task task = null;
				
				for (int i = 0; i< al.size();i++){
					task = (Task)al.get(i);	//生成新任务	
					boolean b = processingQueue.putTask(task);
					if (!b) {//提交新任务到等待队列里
						//System.out.println("task.getTaskId()***********************"+task.getTaskId());
						continue;
					}
					
					String tasks = task.getTaskId();
					//将榨取队列里的状态设置为正在发
					String table_name = "";
					if(SmsplatGlobalVariable.CHANNEL_ID == 80){//push 推送
						table_name = "bg_task_push";
					}else if(SmsplatGlobalVariable.CHANNEL_ID == 90){// sms 发送
						table_name = "bg_task_sms";
					}
					StringBuffer prepareUpdate= new StringBuffer(" update "+table_name+" set status = 2 where id in ( ");
					if(1 == SmsplatGlobalVariable.PACK_QUANTITIES){
						prepareUpdate.append("'");
						prepareUpdate.append(tasks);
						prepareUpdate.append("'");
					}else{
						prepareUpdate.append(tasks);
					}
					prepareUpdate.append(" ) ");
					int	updateRows =sqlBridge.executeUpdate(prepareUpdate.toString());//在表里把状态置为1(正在推)
					prepareUpdate = null;
					tasks = null;
					if(-1 == updateRows ){
						processingQueue.getTask(task.getTaskId());//如果表里数据修改失败的话，那么把新任务从等待队列移除
					}
					else {
						/**提交成功的处理*/
						if(maximun_flag){//执行了最高优先级的榨取后，往队列的头部添加
							extractQueue.putTaskHead(task);// 提交新任务到榨取队列尾部
							logger.info("榨取到短信同时放到短信队列头部 ID = "+task.getTaskId());//写运行日志
						}else{//正常的榨取处理 
							boolean b1 = extractQueue.putTask(task);// 提交新任务到榨取队列尾部
							System.out.println(b1);
						}
					}
				}
			}catch(Exception e){
				e.printStackTrace();
				logger.error(e.getMessage());
			}
			finally{
				sqlBridge.clearResult();//清空数据
				sqlBridge.closeConnect();//关闭数据库连接，c3p0会置为闲置，不会真实的关闭连接
			}

		return result_flag;
	}
	
	/**
	 * 一般执行榨取短信方法
	 * @return 返回来list，里面放置task的列表
	 */
	private List extractByPriorForCitics(int num) {		
		String procCall = SqlFactory.getExtractSql(db_type, channel_id, resend_times, num, new Date().getTime());// 榨取任务的sql语句
//		logger.info("执行榨取的sql语句===="+procCall);
		System.out.println("执行榨取的sql语句===="+procCall);
		if (!sqlBridge.executeQuery(procCall))// 数据库连接查询失败
		{
			// 调用存储过程,榨取任务失败
//			logger.error("查询数据库短信任务失败!");
//			System.out.println("查询数据库短信任务失败!");
			logger.debug("查询数据库短信任务失败!");
			return null;
		} 
		else 
		{
			//根据查询出来的数据开始组装处理任务队列， 在这里需要判断一下引擎的功能， 90 ，push; 80, sms
			if(SmsplatGlobalVariable.CHANNEL_ID == 90 ){
				return resultSetToList_SMS();
			}else if(SmsplatGlobalVariable.CHANNEL_ID == 80){
				return resultSetToList();
			}else{
				return null;
			}

		}
	}

	//将结果集转换成集合，目的是用来实现批量处理
	private List<Task> resultSetToList_SMS(){

		List<Task> al = new ArrayList<Task>();
		Task task = null;
		while(sqlBridge.next()) //
		{
			task = new Task();
			String taskId = sqlBridge.getFieldString("id");//记录ID 主键
			task.setTaskId(taskId);		//设置id
			String mobile=sqlBridge.getFieldString("mobile");//手机号码
			task.setMobileTo(mobile);   //设置mobile
			task.addAttrib("mesgId", taskId);// 设置推送消息的id
			task.addAttrib("content", sqlBridge.getFieldString("content"));// 设置推送内容
			task.addAttrib("status", sqlBridge.getFieldString("status"));// 设置发送状态
			task.addAttrib("errInfo", sqlBridge.getFieldString("err_info"));// 设置推送状态描述
			al.add(task);
		}
		int a= SmsplatGlobalVariable.PACK_QUANTITIES;
		if(1== SmsplatGlobalVariable.PACK_QUANTITIES )
			return al;

//        //群发号码的组装
//        //1.用map.put(msg+downsms_id,List);用短信内容+设备类型来做主键，List是所有的相同内容和设备类型的集合
//        //2.用map来遍历取出各个主键，然后按照每次群发号码的个数来组建每个task，数据源从List里面取

		List<Task> al_qf = new ArrayList<Task>();
		if(al.size()>0){
			/**
			 * 执行第一步
			 * 用map.put(msg+downsms_id+SPLITFLAG,List);用短信内容+设备类型+是否拆分标识+短信重发策略 来做主键，List是所有的相同内容和设备类型的集合
			 */
			Map<String,List<Task>> map = new HashMap<String,List<Task>>();
			for(int j=0;j<al.size();j++){
				Task dto = (Task)al.get(j);
				String content = (String)dto.getAttribValue("content");
				//String token = (String)dto.getMobileTo();
				// String resendStrategy = (String)dto.getAttribValue("RESENDSTRATEGY");
				String key  = content;

				if(map.get(key)!=null){
					//已存在，只需要把已存在的List取出来，再加上此个号码就行
					List<Task> alsub =  (ArrayList<Task>)map.get(key);
					alsub.add(dto);
					map.put(key,alsub);
				}else{
					//不存在，直接添加就行
					List<Task> alsub = new ArrayList<Task>();
					alsub.add(dto);
					map.put(key,alsub);
				}
			}

			/**
			 * 执行第二步
			 * 用map来遍历取出各个主键，然后按照每次群发token的个数来组建每个task，数据源从List里面取
			 */
			Set keys = map.keySet();
			Iterator iter = keys.iterator();
			String currentMsg = "";
			while(iter.hasNext())
			{
				currentMsg = (String)iter.next();
				List<Task> list = (ArrayList<Task>)map.get(currentMsg);
				if(list.size()>0){
					StringBuffer tokens = new StringBuffer();
					StringBuffer sendids = new StringBuffer();
					//StringBuffer mobiles = new StringBuffer();
					/**
					 * 主要用来组装每个token和主键 以及手机号（用于下发短信）
					 * token格式如下:token1,token2...token10;token11,token12...
					 * 主键格式如下:'send_id1','send_id2',...;'send_id11','send_id12'...
					 * 手机号格式如下：mobile_to1,mobile_to2,...;mobile_to11,mobile_to12...
					 */
					for(int k=0;k<list.size();k++){
						Task tk = (Task)list.get(k);
						/**
						 * 每组有多少号码的组装
						 */
						if(k!=0&&(k%SmsplatGlobalVariable.PACK_QUANTITIES)==0){
							//刚好是配置的号码个数+1
							tokens.append(";");
							sendids.append(";");
						}
						tokens.append(tk.getMobileTo());
						tokens.append(",");

						sendids.append("'");
						sendids.append(tk.getTaskId());
						sendids.append("'");
						sendids.append(",");
					}
					Task tk1 = list.get(0);
					int length  = 0;
					if(tokens!=null)
						length = tokens.toString().split(";").length;
					if(length>0){
						String[] tokens_sub = tokens.toString().split(";");
						for(int i = 0;i<tokens_sub.length;i++){
							Task tk0 = new Task();
							String sendid = sendids.toString().split(";")[i];
							//tk0.addAttrib("ids",sendid.substring(0,sendid.length()-1));
							tk0.setTaskId(sendid.substring(0,sendid.length()-1));

							String token = tokens.toString().split(";")[i];
							//tk0.addAttrib("tokens",token.substring(0,token.length()-1));
							tk0.setMobileTo(token.substring(0,token.length()-1));
							tk0.addAttrib("mesgId", tk1.getAttribValue("mesgId"));// 设置推送消息的id
							tk0.addAttrib("content", tk1.getAttribValue("content"));// 设置推送内容
							tk0.addAttrib("status", tk1.getAttribValue("status"));// 设置发送状态
							tk0.addAttrib("errInfo", tk1.getAttribValue("errInfo"));// 设置推送状态描述
							al_qf.add(tk0);
						}
					}
				}
			}

		}
		return al_qf;
	}

	/**
	 * 将结果集转换成集合
	 * @return
	 */
	private List<Task> resultSetToList()
	{
		List<Task> al = new ArrayList<Task>();
		Task task = null;
		while(sqlBridge.next()) //
		{
			task = new Task();
			String taskId = sqlBridge.getFieldString("id");//记录ID 主键
			task.setTaskId(taskId);		//设置id	
			String token=sqlBridge.getFieldString("clientId");//记录设备id
			task.setMobileTo(token);   //设置token
			task.addAttrib("mesgId", taskId);// 设置推送消息的id
			task.addAttrib("title", sqlBridge.getFieldString("title"));// 设置推送内容
			task.addAttrib("content", sqlBridge.getFieldString("content"));// 设置推送内容
			task.addAttrib("extParam", sqlBridge.getFieldString("ext_param"));// 设置隐藏域内容
			task.addAttrib("createTime", sqlBridge.getFieldString("create_time"));// 设置消息创建时间
			task.addAttrib("status", sqlBridge.getFieldString("status"));// 设置发送状态
			task.addAttrib("pushType", sqlBridge.getFieldString("push_type"));// 设置推送类型 推送方式 0：通知 / 1：透传
			task.addAttrib("pushOs", sqlBridge.getFieldString("push_os"));// 设置平台类型  推送平台 0：ios/ 1：android
			task.addAttrib("errInfo", sqlBridge.getFieldString("err_info"));// 设置推送状态描述
//			task.addAttrib("predictTime", sqlBridge.getFieldString("predict_time"));// 设置预推送时间
			task.addAttrib("badgeNum", sqlBridge.getFieldString("badge_num"));// 设置IOS角标
			al.add(task);
		}
		int a= SmsplatGlobalVariable.PACK_QUANTITIES;
        if(1== SmsplatGlobalVariable.PACK_QUANTITIES )
            return al;
        
//        //群发号码的组装
//        //1.用map.put(msg+downsms_id,List);用短信内容+设备类型来做主键，List是所有的相同内容和设备类型的集合
//        //2.用map来遍历取出各个主键，然后按照每次群发号码的个数来组建每个task，数据源从List里面取
  
        List<Task> al_qf = new ArrayList<Task>();
        if(al.size()>0){
            /**
             * 执行第一步
             * 用map.put(msg+downsms_id+SPLITFLAG,List);用短信内容+设备类型+是否拆分标识+短信重发策略 来做主键，List是所有的相同内容和设备类型的集合
             */
        	Map<String,List<Task>> map = new HashMap<String,List<Task>>();
        	for(int j=0;j<al.size();j++){
              Task dto = (Task)al.get(j);
              String title = (String)dto.getAttribValue("title");
              String content = (String)dto.getAttribValue("content");
              String platType = (String)dto.getAttribValue("pushOs");
              //String token = (String)dto.getMobileTo();
             // String resendStrategy = (String)dto.getAttribValue("RESENDSTRATEGY");
              String key  = title+content+platType;
             
              if(map.get(key)!=null){
            	  //已存在，只需要把已存在的List取出来，再加上此个号码就行
            	  List<Task> alsub = (ArrayList<Task>)map.get(key);
            	  alsub.add(dto);
            	  map.put(key,alsub);
              }else{
            	  //不存在，直接添加就行
            	  List<Task> alsub = new ArrayList<Task>();
            	  alsub.add(dto);
            	  map.put(key,alsub);
              }
        	}
        	
        	/**
        	 * 执行第二步
        	 * 用map来遍历取出各个主键，然后按照每次群发token的个数来组建每个task，数据源从List里面取
        	 */
	          Set keys = map.keySet();
	          Iterator iter = keys.iterator();
	          String currentMsg = "";
	          while(iter.hasNext())
	          {
	              currentMsg = (String)iter.next();
	              List<Task> list = (ArrayList<Task>)map.get(currentMsg);
	              if(list.size()>0){
	            	  StringBuffer tokens = new StringBuffer();
	            	  StringBuffer sendids = new StringBuffer();
	            	  //StringBuffer mobiles = new StringBuffer();
	            	  /**
	            	   * 主要用来组装每个token和主键 以及手机号（用于下发短信）
	            	   * token格式如下:token1,token2...token10;token11,token12...
	            	   * 主键格式如下:'send_id1','send_id2',...;'send_id11','send_id12'...
	            	   * 手机号格式如下：mobile_to1,mobile_to2,...;mobile_to11,mobile_to12...
	            	   */
	            	  for(int k=0;k<list.size();k++){
	            		  Task tk = (Task)list.get(k);
	            		  /**
	            		   * 每组有多少号码的组装
	            		   */
	            		  if(k!=0&&(k%SmsplatGlobalVariable.PACK_QUANTITIES)==0){
	            			  //刚好是配置的号码个数+1
	            			  tokens.append(";");
	            			  sendids.append(";");
	            		  }
	            		  	  tokens.append(tk.getMobileTo());
	            		      tokens.append(",");
	            			  
	            			  sendids.append("'");
	            			  sendids.append(tk.getTaskId());
	            			  sendids.append("'");	            			 
	            			  sendids.append(",");	
	            	  }
	            	  Task tk1 = list.get(0);
	            	  int length  = 0;
	            	  if(tokens!=null)
	            		  length = tokens.toString().split(";").length;
	            	  if(length>0){
	            		  String[] tokens_sub = tokens.toString().split(";");
	            		  for(int i = 0;i<tokens_sub.length;i++){
	            			    Task tk0 = new Task();
	            			    String sendid = sendids.toString().split(";")[i];
	            			    //tk0.addAttrib("ids",sendid.substring(0,sendid.length()-1));
	            			    tk0.setTaskId(sendid.substring(0,sendid.length()-1));
	            			    
	            			    String token = tokens.toString().split(";")[i];
	            			    //tk0.addAttrib("tokens",token.substring(0,token.length()-1));
	            			    tk0.setMobileTo(token.substring(0,token.length()-1));
	            			    tk0.addAttrib("mesgId", tk1.getAttribValue("mesgId"));// 设置推送消息的id
	            			    tk0.addAttrib("title", tk1.getAttribValue("title"));// 设置推送标题
	            			    tk0.addAttrib("content", tk1.getAttribValue("content"));// 设置推送内容
	            			    tk0.addAttrib("extParam", tk1.getAttribValue("extParam"));// 设置隐藏域内容
	            			    tk0.addAttrib("createTime", tk1.getAttribValue("createTime"));// 设置消息创建时间
	            			    tk0.addAttrib("status", tk1.getAttribValue("status"));// 设置发送状态
	            			    tk0.addAttrib("pushType", tk1.getAttribValue("pushType"));// 设置推送类型
	            			    tk0.addAttrib("pushOs", tk1.getAttribValue("pushOs"));// 设置平台类型
	            			    tk0.addAttrib("errInfo", tk1.getAttribValue("errInfo"));// 设置推送状态描述
//	            			    tk0.addAttrib("predictTime", tk1.getAttribValue("predict_time"));// 设置预推送时间
	            			    tk0.addAttrib("badgeNum", tk1.getAttribValue("badgeNum"));// 设置预推送时间
	            			    al_qf.add(tk0);
	            		  }
	            	  }
	              }
	          }
        	
        }
        return al_qf;
        
	}


}