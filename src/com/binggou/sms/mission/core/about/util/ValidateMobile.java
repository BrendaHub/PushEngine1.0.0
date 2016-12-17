package com.binggou.sms.mission.core.about.util;

import java.util.List;

import com.binggou.sms.mission.core.about.data.dto.ValidateMobileDto;

/**
 * 判断目标号码是否是合理号码，并返回标准格式
 * @author DongBiao
 * 2008-03-08
 * version 1.0
 */
public class ValidateMobile {
	/**
	 * 目标号码的开头部分
	 */
	private static String preNum = "";
		
	private static int rtNum = 0;
	
	private static ValidateMobileDto valMobile = new ValidateMobileDto();

	private static List unions = SmsplatGlobalVariable.UNION_TYPES;

	private static List mobiles = SmsplatGlobalVariable.MOBILE_TYPES;
	
	/**
	 * 判断目标号码的合法性，并返回该号码的标准格式
	 * @param mobile 目标号码
	 * @return 目标号码是合法号码时，返回目标号码的标准格式；否则返回null
	 */
	public static ValidateMobileDto getValidateMobile(String mobile){		
		valMobile.reset();
		rtNum = 0;
		
		if(null==mobile){ //当目标号码是null时，返回null
			valMobile.setErrorDesc("下行号码不存在");
			return valMobile;
		}
		else {
			String[] submobiles = mobile.replaceAll(",",";").split(";");
			for(int i=0;i<submobiles.length;i++){
				mobile = submobiles[i].trim();
				
				if(mobile.length() == 0)
				{
					continue;
				}
				try{
					Long.parseLong(mobile);//不是数字
				}catch(NumberFormatException e){
					valMobile.setErrorDesc("下行号码不正确:");
					valMobile.setErrorNumber( mobile );
					return valMobile;
				}

				if(!mobile.startsWith("0"))
				{
					if(mobile.length()==11){//11位的号码
						preNum = "" + Integer.parseInt(mobile.substring(0, 3));
					}
					else if(mobile.length()==13){//13位的号码
						preNum = "" + Integer.parseInt(mobile.substring(0, 5));
					}
					else {//非法号码
						valMobile.setErrorDesc("下行号码非法,号码位数不对:");
						valMobile.setErrorNumber( mobile );
						return valMobile;
					}
					
					if(unions.contains(preNum) || mobiles.contains(preNum)){//联通号码						
						rtNum ++;
					}
					else {
						valMobile.setErrorDesc("下行号码为非法号段:");
						valMobile.setErrorNumber( mobile );
						return valMobile;
					}
				}
				else if(mobile.startsWith("0"))
				{
					rtNum ++;
				}
			}
			if(rtNum == 0){
				valMobile.setErrorDesc("下行号码中没有有效号码");
				return valMobile;
			}
			valMobile.setRtNum(true);
			return valMobile;
		}
	}

	public static boolean getValidateContent(String content){
		if(null == content){
			return false;
		}
		
		if("".equals(content.trim())){
			return false;
		}
		
		return true;
	}
	
	public static void main(String[] args){
		System.out.println("abcdefghijk   --> " + getValidateMobile("abcdefghijk").getErrorNumber());
		System.out.println("1234567890    --> " + getValidateMobile("1234567890").getErrorNumber());
		System.out.println("12345678901   --> " + getValidateMobile("12345678901").getErrorNumber());
		System.out.println("123456789012  --> " + getValidateMobile("123456789012").getErrorNumber());
		System.out.println("13345678901   --> " + getValidateMobile("13345678901").getErrorNumber());
		System.out.println("13445678901   --> " + getValidateMobile("13445678901").getErrorNumber());
		System.out.println("14445678901   --> " + getValidateMobile("14445678901").getErrorNumber());
		System.out.println("86123456789   --> " + getValidateMobile("86123456789").getErrorNumber());
		System.out.println("8612345678901 --> " + getValidateMobile("8612345678901").getErrorNumber());
		System.out.println("86abc45678901 --> " + getValidateMobile("86abc45678901").getErrorNumber());
		System.out.println("8613545678901 --> " + getValidateMobile("8613545678901").getErrorNumber());
		System.out.println("8613545678901,8613545678901;8613545678901 --> " + getValidateMobile("8613545678901,8613545678901;8613545678901").getErrorDesc());
		System.out.println("8613545678901,,8613545678901 --> " + getValidateMobile("8613545678901,,8613545678901").getErrorDesc());
		System.out.println("8613545678901,,,861354567890 --> " + getValidateMobile("8613545678901,,,861354567890").getErrorDesc());
		System.out.println(";;;; --> " + getValidateMobile(";;;;").getErrorDesc());
	}
	
}
