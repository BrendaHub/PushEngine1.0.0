package com.binggou.sms.mission.core.about.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.binggou.sms.SQLBridge;

/**
 * <p>
 * Title: 短信平台通道相关的工具类
 * </p>
 * <p>
 * Description: 短信回调任务处理实现对象，从Callback类派生，
 * </p>
 * 
 * @author chenhj(brenda)
 * @version 0.1
 */
public class ChannelHelpler {
	 private static SQLBridge sqlBridge = new SQLBridge();
	/**
	 * 根据手机号码判断运营商类型
	 * @param mobile 手机号码
	 * @return 号码段类型
	 */
	private static int getComType(String mobile)
	{
		String[] mobiles = mobile.replaceAll(",", ";").split(";");//用分号拆分所有手机
		
		int type = SmsplatGlobalVariable.MIX_NUMBERS;//运营商类型
		int tmpType = SmsplatGlobalVariable.MIX_NUMBERS;//临时的运营商类型

		for(int i=0;i<mobiles.length;i++)
		{
			String tmp = mobiles[i].trim();//得到号码, 去空
			
			int preThreeNumber = 0;
			if(tmp.startsWith("1")){
				preThreeNumber = Integer.parseInt( tmp.substring(0, 3) );//该号码的前三个数字	
			}
			else if(tmp.startsWith("861")){

				preThreeNumber = Integer.parseInt( tmp.substring(0, 5) );//该号码的前三个数字	
			}
			
			if(i == 0)//第一个号码
			{
				if( SmsplatGlobalVariable.UNION_TYPES.contains(""+preThreeNumber)){
					type = SmsplatGlobalVariable.UNION_CHANNEL_TYPE;//联通号段类型
					tmpType = SmsplatGlobalVariable.UNION_CHANNEL_TYPE;//联通号段类型
				}
				else if(SmsplatGlobalVariable.MOBILE_TYPES.contains(""+preThreeNumber)){
					type = SmsplatGlobalVariable.MOBILE_CHANNEL_TYPE;//移动号段类型
					tmpType = SmsplatGlobalVariable.MOBILE_CHANNEL_TYPE;//移动号段类型
				}
				else {
					type = SmsplatGlobalVariable.MIX_NUMBERS;//SP号段类型(因为联通发往移动的信息可以但收费高, 而移动发往联通的类型则不可以)
					tmpType = SmsplatGlobalVariable.MIX_NUMBERS;//SP号段类型(因为联通发往移动的信息可以但收费高, 而移动发往联通的类型则不可以)
				}
				//根据号码段, 判断运营商类型
//				switch(preThreeNumber)
//				{
//					case 130:
//					case 131:
//					case 132:
//					case 133:
//					case 153:
//					case 156:
//						type = SmsplatGlobalVariable.UNION_CHANNEL_TYPE;//联通号段类型
//						tmpType = SmsplatGlobalVariable.UNION_CHANNEL_TYPE;//联通号段类型
//						break;
//					case 134:
//					case 135:
//					case 136:
//					case 137:
//					case 138:
//					case 139:
//					case 150:
//					case 157:
//					case 158:
//					case 159:
//						type = SmsplatGlobalVariable.MOBILE_CHANNEL_TYPE;//移动号段类型
//						tmpType = SmsplatGlobalVariable.MOBILE_CHANNEL_TYPE;//移动号段类型
//						break;
//					default:
//						type = SmsplatGlobalVariable.MIX_NUMBERS;//SP号段类型(因为联通发往移动的信息可以但收费高, 而移动发往联通的类型则不可以)
//						tmpType = SmsplatGlobalVariable.MIX_NUMBERS;//SP号段类型(因为联通发往移动的信息可以但收费高, 而移动发往联通的类型则不可以)
//					break;
//				}
			}
			else//其他号码
			{
				if( SmsplatGlobalVariable.UNION_TYPES.contains(""+preThreeNumber)){
					tmpType = SmsplatGlobalVariable.UNION_CHANNEL_TYPE;//联通号段类型
				}
				else if(SmsplatGlobalVariable.MOBILE_TYPES.contains(""+preThreeNumber)){
					tmpType = SmsplatGlobalVariable.MOBILE_CHANNEL_TYPE;//移动号段类型
				}
				else {
					tmpType = SmsplatGlobalVariable.MIX_NUMBERS;//SP号段类型(因为联通发往移动的信息可以但收费高, 而移动发往联通的类型则不可以)
				}
				//根据号码段, 判断运营商类型
//				switch(preThreeNumber)
//				{
//					case 130:
//					case 131:
//					case 132:
//					case 133:
//					case 153:
//					case 156:
//						tmpType = SmsplatGlobalVariable.UNION_CHANNEL_TYPE;//联通号段类型
//						break;
//					case 134:
//					case 135:
//					case 136:
//					case 137:
//					case 138:
//					case 139:
//					case 150:
//					case 157:
//					case 158:
//					case 159:
//						tmpType = SmsplatGlobalVariable.MOBILE_CHANNEL_TYPE;//移动号段类型
//						break;
//					default:
//						tmpType = SmsplatGlobalVariable.MIX_NUMBERS;//SP号段类型(因为联通发往移动的信息可以但收费高, 而移动发往联通的类型则不可以)
//						break;
//				}

				//判断类型是否有变化
				if( type == tmpType )//无变化
				{					
				}
				else
				{
					type = SmsplatGlobalVariable.MIX_NUMBERS;//混合号段类型(需另行处理)
					return type;
				}
			}
			
		}

		//返回运营商类型
		return type;
	}


	/**
	 * 根据手机号码选择最佳通道
	 * @param mobile 手机号码
	 * @return 最佳通道
	 */
	public static int getBestChannelWithMobile(String mobile)
	{
		int comType = getComType(mobile);//运营商类型
		int bestChannel = SmsplatGlobalVariable.MIX_NUMBERS;//混合号段类型
		
		//根据号段类型取得最佳通道
		if( comType == SmsplatGlobalVariable.MOBILE_CHANNEL_TYPE )//移动运营商
		{
			bestChannel = SmsplatGlobalVariable.MOBILE_CHANNEL_TYPE;//移动类型号段的最佳通道
		}
		else if( comType == SmsplatGlobalVariable.UNION_CHANNEL_TYPE )//联通运营商
		{
			bestChannel = SmsplatGlobalVariable.UNION_CHANNEL_TYPE;//联通类型号段的最佳通道
		}
		
		return bestChannel;//返回最佳的通道
	}
	
	public static Map<String, List<String>> getComList(String mobile)
	{
		Map<String, List<String>> mobileMap = new HashMap<String, List<String>>();
		List<String> mobileList = new ArrayList<String>();
		List<String> unionList = new ArrayList<String>();
		if(null == mobile)
			return mobileMap;
		
		String[] mobiles = mobile.replaceAll(",", ";").split(";");//用分号拆分所有手机
		String tmp = "";

		for(int i=0;i<mobiles.length;i++)
		{
			tmp = mobiles[i].trim();//得到号码, 去空
//			int preThreeNumber = Integer.parseInt( tmp.substring(0, 3) );//该号码的前三个数字			
//			//根据号码段, 判断运营商类型
//			switch(preThreeNumber)
//			{
//				case 134:
//				case 135:
//				case 136:
//				case 137:
//				case 138:
//				case 139:
//				case 150:
//				case 157:
//				case 158:
//				case 159:
//					mobileList.add(tmp);
//					break;
//				default :
//					unionList.add(tmp);
//					break;
//			}			
			int preThreeNumber = 0;
			if(tmp.startsWith("1")){
				preThreeNumber = Integer.parseInt( tmp.substring(0, 3) );//该号码的前三个数字	
			}
			else if(tmp.startsWith("861")){

				preThreeNumber = Integer.parseInt( tmp.substring(0, 5) );//该号码的前三个数字	
			}
			
			if( SmsplatGlobalVariable.UNION_TYPES.contains(""+preThreeNumber)){
				unionList.add(tmp);
			}
			else if(SmsplatGlobalVariable.MOBILE_TYPES.contains(""+preThreeNumber)){
				mobileList.add(tmp);
			}
		}
		
		mobileMap.put("mobile", mobileList);
		mobileMap.put("union", unionList);
		
		//返回运营商类型
		return mobileMap;
	}
	
	
	/**
	 * 根据码值和运营商名称查找错误原因
	 * @param code 错误码
	 * @param comName 运营商名称
	 * @return 错误原因
	 */
	public static String getErrorCode(String code, String comName)
	{
		String desc = "";
		sqlBridge.openConnect();
		try {
			sqlBridge.executeQuery("select DESCRIPTION from ERROR_CODE where COM_NAME = '" + comName  + "' and ERROR_CODE = '" + code + "'");
			sqlBridge.executePreparedUpdate();			
			while(sqlBridge.next()){
				desc = sqlBridge.getFieldString("DESCRIPTION");
			}
		} catch (Exception e) {
		}
		finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
		return desc;
	}
	
}
