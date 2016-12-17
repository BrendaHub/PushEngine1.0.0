package com.binggou.sms.mission.core.sqlfactory;

import com.binggou.sms.mission.core.about.util.SmsplatGlobalVariable;
import com.binggou.sms.mission.core.sql.oracle9.Oracle9SQL;

import java.util.Date;

public class SqlFactory {
	public static String getExtractSql(int i, int channel_id,int resend_times,int num,long nowTime){
		
		if(i==4){//oracle
			return Oracle9SQL.getExtractSqlNoPrior(channel_id,resend_times,num);//ORACLE 数据库
		}else if(i==5){
			   //SQLSERVER数据库，代码未填写
		}else if(i==6){
			if(SmsplatGlobalVariable.CHANNEL_ID == 80) {//榨取push任务
				return Oracle9SQL.getExtractMySqlNoPrior_push(channel_id, resend_times, num, nowTime);// MySQL的push 任务
			}else if(SmsplatGlobalVariable.CHANNEL_ID == 90){//榨取短信任务
				return Oracle9SQL.getExtractMySqlNoPrior(channel_id, resend_times, num, new Date()); //MySQL 的短信 任务
			}
		}
		return "";
	}
}
