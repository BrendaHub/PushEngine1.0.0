package com.binggou.sms.mission.core.sqlfactory;

import com.binggou.sms.mission.core.sql.oracle9.Oracle9SQL;

public class SqlFactory {
	public static String getExtractSql(int i, int channel_id,int resend_times,int num,long nowTime){
		
		if(i==4){//oracle
			return Oracle9SQL.getExtractSqlNoPrior(channel_id,resend_times,num);//ORACLE 数据库
		}else if(i==5){
			   //SQLSERVER数据库，代码未填写
		}else if(i==6){
			return Oracle9SQL.getExtractMySqlNoPrior(channel_id, resend_times, num, nowTime);//MYSQL 数据库
		}
		return "";
	}
}
