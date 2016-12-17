package com.binggou.sms.mission.core.about.util;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 短信平台全局变量
 * @author chenhj(brenda)
 * @version 0.1
 */
public final class SmsplatGlobalVariable 
{	
	/**
	 * 榨取队列的当前容量
	 */
	public static int CURRENT_EXTRACT_QUANTITIES = 0;
	
	/**
	 * 榨取队列的总容量
	 */
	public static int EXTRACT_QUANTITIES = 1000;
	
	/**
	 * 短信提交失败后, 平台重新提交该短信的最大次数
	 */
	public static final int RESEND_TIMES = 3; 
	
	/**
	 * 待发短信状态码值 0
	 */
	public static final int NOT_SEND_STATE = 0;//未推消息
		
	/**
	 * 已提交短信状态码值 1
	 */
	public static final int SENT_STATE = 1;//已推送
	
	/**
	 * 正在发送短信状态码值 2
	 */
	public static final int SENDING_STATE = 2;//正在推送
	
	/**
	 * 重发短信状态码值 3
	 */
	public static final int RE_SEND_STATE = 3;
	
	/**
	 * 删除短信状态码值 4
	 */
	public static final int DELETE_SEND_STATE = 4;
	
	/**
	 * 发送成功短信状态码值 5
	 */
	public static final int SEND_STATE = 5;//推送成功
	
	/**
	 * 发送失败短信状态码值 6
	 */
	public static final int FAIL_SEND_STATE = 6;//推送失败
	
	/**
	 * 黑名单短信状态码值 7
	 */
	public static final int BLACH_SEND_STATE = 7;
	
	/**
	 * 二次重发失败短信状态码值 8
	 */
	public static final int RE_FAIL_SEND_STATE = 8;
	
	/**
	 * 短信网关连接失败的短信状态码值 9
	 */
	public static final int GW_REFUSED_SEND_STATE = 9;
	
	
	/**
	 * 移动支持的号段类型 1
	 */
	public static final int MOBILE_CHANNEL_TYPE = 1;
	
	/**
	 * 联通支持的号段类型 2
	 */
	public static final int UNION_CHANNEL_TYPE = 2;
	
	
	/**
	 *  同时存在移动和联通的手机号码的标识 3
	 */
	public static final int MIX_NUMBERS = 3; 
	
	/**
	 * 移动号段所有类型
	 */
	public static List<String> MOBILE_TYPES = new ArrayList<String>();
	
	/**
	 * 联通号段所有类型
	 */
	public static List<String> UNION_TYPES = new ArrayList<String>();
	
	/**
	 * 电信号段所有类型
	 */
	public static List<String> DX_TYPES = new ArrayList<String>();
	
	public static String MO_USERNAME;
	public static String MO_USERPASSWORD;
	
	/**
	 * 当前系统使用的数据库类型
	 */
	public static int currentDB = 5;

	/**
	 * FOXPRO_DB 数据库
	 */	
	public static final int FOXPRO_DB = 1;	
	
	/**
	 * GREATWALL_DB 数据库
	 */
	public static final int GREATWALL_DB = 2;
	
	/** 
	 * MYSQL_DB 数据库
	 */	
	public static final int MYSQL_DB = 3;
	
	/**
	 * ORACLE_DB 数据库
	 */	
	public static final int ORACLE_DB = 4;
	
	/**
	 * SQLSERVER_DB 数据库
	 */
	public static final int SQLSERVER_DB = 5;

	/**
	 * SYBASE_DB 数据库
	 */
	public static final int SYBASE_DB = 6;

	/**
	 * 签名
	 */
	public static String signature = "";
	
	/**
	 * 配置信息
	 */
	public static Map<String, String> configsMap = new HashMap<String, String>();

	/**
	 * 远程连接对象
	 */
	public static Socket remoteConnector = null;

	/**
	 * 远程连接对象
	 */
	public static PrintWriter outputWriter = null;

	/**
	 * 远程连接对象
	 */
	public static BufferedReader inputReader = null;
	
//	/**
//	 * 日志对象
//	 */
//	public static MissionLog missionLog = null;
//
//
//	/**
//	 * 日志对象
//	 */
//	public static MissionLog remoteMissionLog = null;
	
	
	/**
	 * 移动直连通道id 1
	 */
	public static final int MOBILE_DIRECT_CHANNEL_ID = 1;

	/**
	 * 联通直连通道id 4
	 */
	public static final int UNION_DIRECT_CHANNEL_ID = 4;
	
	/**
	 * HJHZ通道id 8
	 */
	public static final int HJHZ_CHANNEL_ID = 8;
	
	/**
	 * 江苏电信账户2 通道id 12
	 */
	public static final int JSDX_ACCT2_CHANNEL_ID = 12;
	
	/**
	 * 江苏电信账户1 通道id 13
	 */
	public static final int JSDX_ACCT1_CHANNEL_ID = 13;

	
	public static long INIT_CLIENT2_TIME = System.currentTimeMillis()-10*999;

	/**
	 * 连接状态 0-正常， 1－不正常
	 */
	public static int CLIENT2_STATUS = 1;
	

	
	

	
	public static long INIT_CLIENT1_TIME = System.currentTimeMillis()-10*999;
	
	/**
	 * 连接状态 0-正常， 1－不正常
	 */
	public static int CLIENT1_STATUS = 1;
	

	
	public static boolean isNowInitingJSDXClient1 = false;
	public static boolean isNowInitingJSDXClient2 = false;
	
	
	
	/**
	 * 引擎榨取短信的通道
	 */
	public static int EXTRACT_CHANNEL_ID = 12;

	/**
	 * 当前引擎的通道号
	 * 90 , 短信引擎
	 * 80 , push引擎
	 */
	public static int CHANNEL_ID = 90;

	/**
	 * 通道状态
	 * 0-好
	 * 1-一般
	 * 2-差
	 * 3-已关闭
	 * 999=没有记录
	 * TODO 通道做相应的修改
	 */
	public static int CHANNLE_SATATUS = 0;
	public static String CHANNLE_SATATUS_DESC = "";

	/**
	 * 发送速率
	 */
	public static float SEND_RATIO = 0f;

	/**
	 * 提交短信数量
	 */
	public static int SUBMIT_QUANTITY = 0;

	/**
	 * 发送成功短信数量
	 */
	public static int SUCCESS_QUANTITY = 0;

	/**
	 * 发送失败短信数量
	 */
	public static int FAIL_QUANTITY = 0;

	/**
	 * 上行短信数量
	 */
	public static int RECEIVE_QUANTITY = 0;

	
	/**
	 * 最高优先级
	 */
	public static final int  MAX_PRIOR = 3;
	
	/**
	 * 最高优先级待处理信息最大值
	 */
	public static final int  MAX_PRIOR_CAPABILITY = 500;
	
	/**
	 * 最高优先级待处理信息的当前数量
	 */
	public static int  MAX_CURRENT_CAPABILITY = 0;
		
	/**
	 * 普通优先级
	 */
	public static final int  NORMAL_PRIOR = 5;
	
	/**
	 * 普通优先级待处理信息最大值
	 */
	public static final int  NORMAL_PRIOR_CAPABILITY = 350;
	
	/**
	 * 普通优先级待处理信息的当前数量
	 */
	public static int  NORMAL_CURRENT_CAPABILITY = 0;
	
	/**
	 * 最低优先级
	 */
	public static final int  MIN_PRIOR = 8;
	
	/**
	 * 最低优先级待处理信息最大值
	 */
	public static final int  MIN_PRIOR_CAPABILITY = 1000;
	
	/**
	 * 最低优先级待处理信息的当前数量
	 */
	public static int  MIN_CURRENT_CAPABILITY = 0;
	
	/**
	 * 最后一次活动时间
	 */
	public static long LAST_ACTIVE_TIME = 0l;

	/**
	 * 打包发送的数量
	 */
	public static int PACK_QUANTITIES = 1;

	/**
	 * 最后一次提交时间发送
	 */
	public static long LAST_SUBMIT_TIME = 0;
	
	/**
	 * 是否正在更新sequence
	 */
	public static boolean IS_NOW_UPDATE_SEQUENCE = false;
		
	/**
	 * 客户登录状态。1-用户未登录, 2-登录成功, 3-用户名或密码不正确, 4-IP鉴权失败, 5-用户已登录
	 */
	public static int LOGIN_STATE = 1;
	
	/**
	 * 数据库状态 0正常，1不正常
	 */
	public static int SERVER_STATE = 0;
	
	/**
	 * 最后一次初始化连接的时间
	 */
	public static long LAST_CONNECT_TIME = 0l;
	
	/**
	 * 连接状态 0正常，1不正常，2正在初始化
	 */
	public static int LINK_STATE = 0;
	
	
	/**
	 * 是否发送引擎监控短信(0-不需要,1-需要)
	 */
	public static int MONITOR_REQUIRED = 0;
	
	/**
	 * 引擎监控短信号码
	 */
	public static String MONITOR_NUMBER = "";
    
	/**
	 * 引擎监控短信发送周期(单位:分钟)
	 */
	public static int MONITOR_CYCLE = 30;
    
    /**
     * 引擎监控短信监控起始时间(24小时制，如08:15:00,表示每天早晨八点十五分开始监控)
     */
	public static String MONITOR_START = null;
    
    /**
     * 引擎监控短信监控终止时间(24小时制，如17:30:00,表示每天下午五点三十分结束监控)
     */
	public static String MONITOR_END = null;
    
    /**
     * 周六日是否需要短信监控(0-不需要,1-需要)
     */
	public static int MONITOR_OFFDAY_REQUIRED = 0;
	
	/**
	 * 引擎是否需要启动
	 */
	public static boolean MONITOR_THREAD_NEED_WORD = true;
	
    /**
     * 待发短信是否有实效性要求(0-没有、是待发短信就发送；1-有时效性要求)
     */
	public static int EFFECTIVE_TIME_REQUIRED = 0;    
	
	/**
     * 待发短信的最长待发时间(单位:小时)
     */
	public static int ALIVE_TIME = 0;
	
	/**
	 * 发送通道类型
	 */
	public static String SEND_CHANNEL_TYPE = "";
	
	/**
	 * 通道榨取短信的优先级：-1按数据库表中优先级，0按引擎优先级
	 */
	public static int ENGINE_EXTRACTOR_PRIOR = -1;
	
}
