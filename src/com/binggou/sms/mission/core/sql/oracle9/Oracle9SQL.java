package com.binggou.sms.mission.core.sql.oracle9;

import com.binggou.mission.MissionCenter;
import org.apache.log4j.Logger;

public final class Oracle9SQL {


	private static Logger logger = Logger.getLogger(MissionCenter.class.getName());//日志类
	/**
	 * 拼凑成执行sql语句
	 * @param channel_id 通道号码
	 * @param resend_times 重复次数
	 * @param num 榨取条数
	 * @return String sql语句
	 */
	public static String getExtractSqlNoPrior(int channel_id,int resend_times,int num) {
		StringBuffer extractSqlNoPrior = new StringBuffer(" SELECT send_id,mobile_from,fee_mobile,downsms_id,fee,mobile_to,send_msg,channel_id,prior_id,send_status,resend_times,signature,sp_serv_no,user_id, USER_ORGANIZATION, MESSAGE_TYPE,RETURN_MSG_FLAG,WAPPUSH_URL, MOBILE_QUANTITIES,SEND_DEADLINE,JX_SMID,SPLITFLAG,RESENDSTRATEGY ");
		extractSqlNoPrior.append(" FROM ( ");
		extractSqlNoPrior.append("  select send_id,mobile_from,fee_mobile,downsms_id,fee,mobile_to,send_msg,channel_id,prior_id,send_status,resend_times,signature,sp_serv_no,user_id, USER_ORGANIZATION, MESSAGE_TYPE,RETURN_MSG_FLAG,WAPPUSH_URL, MOBILE_QUANTITIES,SEND_DEADLINE,JX_SMID,SPLITFLAG,RESENDSTRATEGY ");
		extractSqlNoPrior.append("  FROM SEND T ");
		extractSqlNoPrior.append("  WHERE ( PRE_SEND_TIME <= SYSDATE  or PRE_SEND_TIME IS NULL )");
		extractSqlNoPrior.append("  AND send_status = 0" );
		extractSqlNoPrior.append("  AND channel_id = ");
		extractSqlNoPrior.append(channel_id);
		extractSqlNoPrior.append("  AND RESEND_TIMES < ");
		extractSqlNoPrior.append(resend_times);
		extractSqlNoPrior.append("  ORDER BY PRIOR_ID, SYS_TIME");
		extractSqlNoPrior.append("  ) ");
		extractSqlNoPrior.append(" WHERE ROWNUM <= ");
		extractSqlNoPrior.append(num);
		logger.debug("榨取数据SQL  = " + extractSqlNoPrior.toString());
		return extractSqlNoPrior.toString();
	}
	
	/**
	 * 拼凑成执行sql语句
	 * @param channel_id 通道号码
	 * @param resend_times 重复次数
	 * @param num 榨取条数
	 * @return String sql语句
	 */
	public static String getExtractMySqlNoPrior(int channel_id,int resend_times,int num,long nowTime) {
		//select p.id,m.title,content,ext_param,p.create_time,m.mesg_len,token,plat_type,push_type,push_kind from t_push_task p,t_message m;
		StringBuffer extractSqlNoPrior = new StringBuffer(" select id, title, content, ext_param, create_time, status,err_info  clientId, clientId_type, push_type, push_os, badge_num ");
		extractSqlNoPrior.append(" from bg_push_task ");
		extractSqlNoPrior.append(" where status = 0 and prepush_time < ");
		extractSqlNoPrior.append(nowTime);
		extractSqlNoPrior.append(" ORDER BY priority DESC ");
		extractSqlNoPrior.append(" limit ");
		extractSqlNoPrior.append(num);
		logger.debug("榨取数据SQL  = " + extractSqlNoPrior.toString());
		return extractSqlNoPrior.toString();
	}
}
