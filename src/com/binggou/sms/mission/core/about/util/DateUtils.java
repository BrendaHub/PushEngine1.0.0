package com.binggou.sms.mission.core.about.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Administrator
 * 
 * 日期工具
 * @author chenhj(brenda)
 * @version 1.0
 */
public class DateUtils
{
    /**
     * 字符串形式日期格式
     */
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");

    /**
     * 字符串形式时间格式
     */
    private static SimpleDateFormat datetimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    /**
     * 字符串形式日期格式
     */
    private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
   
    /**
     * 字符串形式时间格式
     */
    private static SimpleDateFormat smstimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 得到当前字符串形式日期 格式：yyyy年mm月dd日
     * 
     * @return String 当前字符串形式日期
     */
    public static String getCurrDate()
    {
        return dateFormat.format(new Date());
    }
    
    /**
     * 得到当前字符串形式日期 格式：yyyy-MM-dd
     * 
     * @return String 当前字符串形式日期
     */
    public static String getCurrDate2()
    {
        return dateFormat2.format(new Date());
    }
    
    /**
     * 得到当前字符串形式时间 格式：yyyy年mm月dd日 hh:MM:ss
     * 
     * @return String 当前字符串形式时间
     */
    public static String getCurrTime()
    {
        return datetimeFormat.format(new Date());
    }
    
    /**
     * 得到当前字符串形式时间 格式：yyyy年mm月dd日 hh:MM:ss
     * 
     * @return String 当前字符串形式时间
     */
    public static String getSMSCurrTime()
    {
        return smstimeFormat.format(new Date());
    }
    
    /**
     * 得到当前字符串形式时间 格式：yyyy年mm月dd日 hh:MM:ss
     * 
     * @return String 当前时间-30分钟字符串形式时间
     */
    public static String getSMSTime()
    {
    	Date curDate = new Date();
    	
    	//得到当前时间减去30分钟后的时间
    	long smsTime = curDate.getTime()-30*60*1000;
    	
    	return  datetimeFormat.format(new Date(smsTime));
    }
	
	/**
	 * 返回字符串表示的日期
	 * @param dateStr 日期字符串
	 * @return 日期
	 */
	public static Date parseToDate(String dateStr) {
		return parseToDate(dateStr,null);
	}
	
    /**
     * 日期
     * @param dateStr 日期字符串
     * @param format 日期格式
     * @return 日期
     */
	public static Date parseToDate(String dateStr, String format) {
		if("".equals(dateStr)||dateStr==null)
			return null;
		try {
			if (format != null) {
				return (new SimpleDateFormat(format)).parse(dateStr);
			} else {
				return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
						.parse(dateStr);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 返回指定格式的日期所对应的字符串格式
	 * @param date 指定的日期
	 * @param format 日期格式
	 * @return  字符串
	 */
	public static String parseToString(Date date, String format) {
		if(date==null){
			return "";
		}
		if (format != null) {
			return (new SimpleDateFormat(format)).format(date);
		} else {
			return (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"))
					.format(date);
		}
	}
	
}
