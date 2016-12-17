package com.binggou.sms.mission.core.about.util;

public class MessageUtil {
	public static int getMobileCom(String mobile_to)
	{
		//按通道－号段统计数量				
		String preThreeNum = mobile_to.substring(0,3);
		int mobile_com = 0;
		if(SmsplatGlobalVariable.MOBILE_TYPES.contains(preThreeNum)){
			mobile_com = 1;//移动号段
		}
		else if(SmsplatGlobalVariable.UNION_TYPES.contains(preThreeNum)){
			mobile_com = 2;//联通号段
		}
		else if(SmsplatGlobalVariable.DX_TYPES.contains(preThreeNum)){
			mobile_com = 3;//电信号段
		}
		//号码以0开始也是电信的通道
		else if(mobile_to.startsWith("0"))
		{
			mobile_com = 3;//电信号段
		}
		return mobile_com;
	}
}
