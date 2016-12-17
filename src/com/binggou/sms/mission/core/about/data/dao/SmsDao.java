package com.binggou.sms.mission.core.about.data.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import com.eaio.uuid.UUID;
//import com.binggou.mission.common.Logable;
import com.binggou.sms.SQLBridge;
import com.binggou.sms.mission.core.about.data.dto.Message;
import com.binggou.sms.mission.core.about.util.DateUtils;
import com.binggou.sms.mission.core.about.util.SmsplatGlobalVariable;

/**
 * 短信的数据库访问
 * @author Administrator
 *
 */
public class SmsDao {
	 /**
     * 数据库操作代理对象
     */
    private SQLBridge sqlBridge = new SQLBridge();
        
//    /**
//     * 保存日志对象句柄
//     */
//    protected Logable log = new MissionLog();
    
    /**
     * 更新的条数
     */
	private int updateRows = 0;
//
//	public Logable getLog() {
//		return log;
//	}
//
//	public void setLog(Logable log) {
//		this.log = log;
//	}

	/**
	 * 构造函数
	 */
	public SmsDao(){
    }

	/**
	 * 更新短信状态和状态报告时间
	 *
	 */
	public void updateStateBySequence(com.binggou.sms.mission.core.about.data.dto.SmsSendDto dto) {
		int index = 1;
		try {
			sqlBridge.openConnect();
			// 根据sequence更新状态和状态报告返回时间
			if((""+SmsplatGlobalVariable.SEND_STATE).equals(dto.getMSG_STAT()))//发送成功
			{
				sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = ?, REPORT_TIME = ? WHERE SEQUENCE = ?");
				sqlBridge.setString(index++, dto.getMSG_STAT());
				sqlBridge.setTimestamp(index++, new Timestamp(dto.getReport_time().getTime()));
				sqlBridge.setString(index++, dto.getSequence());
				sqlBridge.executePreparedUpdate();
//				log.runtime("更新短信状态，短信id是: " + dto.getSequence()  + ", 状态报告确认时间: " + dto.getReport_time());
			}
			else if((""+SmsplatGlobalVariable.FAIL_SEND_STATE).equals(dto.getMSG_STAT()))//发送失败
			{
				sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = ?, REPORT_TIME = ? ,ERR_MSG = ? WHERE SEQUENCE = ?");
				sqlBridge.setString(index++, dto.getMSG_STAT());
				sqlBridge.setTimestamp(index++, new Timestamp(dto.getReport_time().getTime()));
				sqlBridge.setString(index++, dto.getMessage());				
				sqlBridge.setString(index++, dto.getSequence());
				sqlBridge.executePreparedUpdate();
//				log.runtime("更新短信状态，短信id是: " + dto.getSequence()  + ", 状态报告确认时间: " + dto.getReport_time());
			}
			
			
		} catch (SQLException e) {
			e.printStackTrace();
//			log.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
			//		e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}

	/**
	 * 更新短信状态和状态报告时间
	 *
	 */
	public void updateStateBySequenceLike(com.binggou.sms.mission.core.about.data.dto.SmsSendDto dto) {
		int index = 1;
		try {
			sqlBridge.openConnect();
			// 根据sequence更新状态和状态报告返回时间
			if((""+SmsplatGlobalVariable.SEND_STATE).equals(dto.getMSG_STAT()))//发送成功
			{
				sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = ?, REPORT_TIME = ? WHERE SEQUENCE like '%?%'");
				sqlBridge.setString(index++, dto.getMSG_STAT());
				sqlBridge.setTimestamp(index++, new Timestamp(dto.getReport_time().getTime()));
				sqlBridge.setString(index++, dto.getSequence());
				sqlBridge.executePreparedUpdate();
//				log.runtime("更新短信状态，短信id是1: " + dto.getSequence()  + ", 状态报告确认时间: " + dto.getReport_time());
			}
			else if((""+SmsplatGlobalVariable.FAIL_SEND_STATE).equals(dto.getMSG_STAT()))//发送失败
			{
				sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = ?, REPORT_TIME = ? ,ERR_MSG = ? WHERE SEQUENCE like '%?%'");
				sqlBridge.setString(index++, dto.getMSG_STAT());
				sqlBridge.setTimestamp(index++, new Timestamp(dto.getReport_time().getTime()));
				sqlBridge.setString(index++, dto.getMessage());				
				sqlBridge.setString(index++, dto.getSequence());
				sqlBridge.executePreparedUpdate();
//				log.runtime("更新短信状态，短信id是2: " + dto.getSequence()  + ", 状态报告确认时间: " + dto.getReport_time());
			}			
			
		} catch (SQLException e) {
			e.printStackTrace();
//			log.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}	
	
	/**
	 * 更新sequence
	 * @param oldSequence
	 * @param newSequence
	 */
	public void updateSequence(String oldSequence, String newSequence) {
		int index = 1;
		try {
			sqlBridge.openConnect();
			// 更新sequence
			sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_ID = ? WHERE SEND_ID = ?");
			sqlBridge.setString(index++, newSequence);
			sqlBridge.setString(index++, oldSequence);
			sqlBridge.executePreparedUpdate();
//			log.runtime("更新短信id, 原id是 " + oldSequence  + ", 新id是: " + newSequence);
		} catch (SQLException e) {
			e.printStackTrace();
			//log.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
					//e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}
	
	/**
	 * 标记正在发送的短信的状态
	 * @param id
	 * @param
	 */
	public int  updateExtractorMessage(String id) {
		int index = 1;
		try {
			sqlBridge.openConnect();
			// 更新sequence
			sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = " + SmsplatGlobalVariable.SENDING_STATE + " WHERE SEND_ID = ?");
			sqlBridge.setString(index++, id);
			updateRows = sqlBridge.executePreparedUpdate();
			//log.runtime("更新短信状态, id是 " + id  + ",状态是: " + SmsplatGlobalVariable.SENDING_STATE);
		} catch (SQLException e) {
			e.printStackTrace();
			//log.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
				//	e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
		return updateRows;
	}
	
	/**
	 * 接收短信，记录入库
	 * @param message 收到的信息
	 */
	public void receiveMessage(Message message) {
		int index = 1;
		String uuid = new UUID().toString();
		try {
			sqlBridge.openConnect();
			// 更新sequence
//			sqlBridge.prepareExecuteUpdate("insert into RECEIVE (RECEIVE_ID,MOBILE_FROM,RECEIVE_MSG,EXTEND_NUMBER) values(?,?,?,?)");
			sqlBridge.prepareExecuteUpdate("insert into RECEIVE(" +
					"RECEIVE_ID,MOBILE_FROM,RECEIVE_MSG,SP_NO," +
					"SP_SERV_NO,RECEIVE_STATUS,CHANNEL_ID) " +
					"values(?,?,?,?,?,?,?)");
			sqlBridge.setString(index++, uuid);
			sqlBridge.setString(index++, message.getFromNum());
			sqlBridge.setString(index++, message.getMSG_CONTEXT());
			sqlBridge.setString(index++, message.getServiceID());
			sqlBridge.setString(index++, message.getServiceID().substring(message.getServiceID().length()-2));
			sqlBridge.setInt(index++, 0);
			sqlBridge.setInt(index++, 1);
			System.out.println("id:" + uuid + ", from:"+message.getFromNum()+", content:" + message.getMSG_CONTEXT()
					+", sid:" + message.getServiceID() +", ext:" + message.getServiceID().substring(message.getServiceID().length()-2));
			sqlBridge.executePreparedUpdate();
			//log.runtime("接收到短信, 内容是 " + message.getMSG_CONTEXT()  + ",源号码是: " + message.getFromNum());
		} catch (SQLException e) {
			e.printStackTrace();
//			log.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}
	
	/**
	 * 接收短信，记录入库
	 * @param  //收到的信息
	 */
	public void receiveMessage(com.binggou.sms.mission.core.about.data.dto.SmsSendDto dto) {
		int index = 1;
		try {
			sqlBridge.openConnect();
			// 更新sequence
			sqlBridge.prepareExecuteUpdate("insert into RECEIVE (RECEIVE_ID,MOBILE_FROM,RECEIVE_MSG,SP_SERV_NO) values(?,?,?,?)");
			sqlBridge.setString(index++, new UUID().toString());
			sqlBridge.setString(index++, dto.getMobileFrom());
			sqlBridge.setString(index++, dto.getMessage());
			sqlBridge.setString(index++, dto.getExtendNo());
			sqlBridge.executePreparedUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
//			SmsplatGlobalVariable.missionLog.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}
	
	/**
	 * 查找指定序列号的短信的重发次数
	 * @param sequence
	 * @return
	 */
	public int getResendTimesBySequence(String sequence) {
		int times = 0;
		try {
			sqlBridge.openConnect();
			String proC = "SELECT RESEND_TIMES FROM SEND WHERE SEND_ID = " + sequence;
			sqlBridge.executeQuery(proC);
				
			while(sqlBridge.next()){
				times = Integer.parseInt(sqlBridge.getFieldString("RESEND_TIMES"));
			}				
		} catch (Exception e) {
			e.printStackTrace();
//			SmsplatGlobalVariable.missionLog.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}

		return times;
	}
	
	/**
	 * 根据序列号重发短信
	 * @param sequence
	 */
	public void resendMessageBySequence(String sequence, int times, long time) {
		int index = 1;
		try {
			sqlBridge.openConnect();
			sqlBridge.prepareExecuteUpdate("update SEND t set SEND_STATUS = 0 , pre_send_time = ? ,RESEND_TIMES = ? where SEND_STATUS = 6 and SEND_ID = ? ");
			sqlBridge.setInt(index++, times+1);
			sqlBridge.setTimestamp(index++, new Timestamp(new Date().getTime() + time));
			sqlBridge.setString(index++, sequence);
			sqlBridge.executePreparedUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}
	
	/** 更新短信状态和状态报告时间
	 *
	 */
	public void updateFailStateById(String id, String info) {
		int index = 1;
		try {
			sqlBridge.openConnect();
			sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = " + SmsplatGlobalVariable.FAIL_SEND_STATE + ", REPORT_TIME = ? ,ERR_MSG = ? WHERE SEND_ID = ?");
			sqlBridge.setTimestamp(index++, new Timestamp(new Date().getTime()));
			sqlBridge.setString(index++, info);				
			sqlBridge.setString(index++, id);
			sqlBridge.executePreparedUpdate();
//			log.runtime("更新短信状态，短信id是: " + id  + ", 时间: " + new Date());
		} catch (SQLException e) {
			e.printStackTrace();
//			log.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}

	/**
	 * 根据序列号重发短信
	 * @param sequence
	 */
	public String getOrganizationIdBySequence(String sequence) {
		int index = 1;
		String r = null;
		try {
			sqlBridge.openConnect();
			sqlBridge.executeQuery("select USER_ORGANIZATION from SEND t where SEQUENCE = '" + sequence + "'");
			while(sqlBridge.next()){							   
				r = sqlBridge.getFieldString("USER_ORGANIZATION");
			}
		} finally {
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
		return r;
	}
	
	/**
	 * 根据序列号重发短信
	 * @param //sequence
	 */
	public int getNextSequence() {

		int r = 0;
		try {
			sqlBridge.openConnect();
			sqlBridge.executeQuery("select GST_SEQUENCE.NEXTVAL from dual");
			while(sqlBridge.next()){
				try{
					r = Integer.parseInt(sqlBridge.getFieldString(1));
				}
				catch(Exception e){
					r = (int)System.currentTimeMillis();
				}
			}
		} finally {
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
		return r;
	}
	
	/**
	 * 更新sequence
	 * @param oldSequence
	 * @param newSequence
	 */
	public void updateGSTSequence(String oldSequence, String newSequence) {
		int index = 1;
		try {
			sqlBridge.openConnect();
			// 更新sequence
			sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEQUENCE = ? WHERE SEQUENCE = ?");
			sqlBridge.setString(index++, newSequence);
			sqlBridge.setString(index++, oldSequence);
			sqlBridge.executePreparedUpdate();
//			log.runtime("更新短信id, 原id是 " + oldSequence  + ", 新id是: " + newSequence);
		} catch (SQLException e) {
			e.printStackTrace();
//			log.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}
	
	/**
	 * 用户未登录时
	 * @param id
	 * @return
	 */
	public int updateNotLoginState(String id) {
		updateRows = 0;
		int updateRows = 0;
		int index = 1;
		try {
			sqlBridge.openConnect();
			// 更新sequence
			sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = ?  WHERE SEND_ID = ? ");
			sqlBridge.setInt(index++, SmsplatGlobalVariable.NOT_SEND_STATE);
			updateRows = sqlBridge.executePreparedUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
		return updateRows;
	}	
	
	/**
	 * 更新sequence
	 * @param //oldSequence
	 * @param //newSequence
	 */
	public void updateSendSequence(String send_id, String newSequence, int quantity) throws SQLException{
		int index = 1;
		updateRows = 0;
		try {
			sqlBridge.openConnect();
			long begin =System.currentTimeMillis();
			sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEQUENCE = ?, MOBILE_QUANTITIES = ? WHERE SEND_ID = ? ");
			sqlBridge.setString(index++, newSequence);
			sqlBridge.setInt(index++, quantity);
			sqlBridge.setString(index++, send_id);
			updateRows = sqlBridge.executePreparedUpdate();
			long end =System.currentTimeMillis();
//			SmsplatGlobalVariable.missionLog.debug("更新短信 " + send_id + " 的gw_sequence为 " + newSequence + "，用时 " + (end-begin) + " 毫秒");
		} catch (SQLException e) {
//			e.printStackTrace();
//			SmsplatGlobalVariable.missionLog.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
			throw e;
		} 
		finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}

	
	/** 更新短信状态和状态报告时间
	 * @param
	 */
	public void updateFormatErrorStateById(String id, String info) throws SQLException {
		int index = 1;
		try {
			sqlBridge.openConnect();
			sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = 10, REPORT_TIME = ? ,ERR_MSG = ? WHERE SEND_ID = ?");
			sqlBridge.setTimestamp(index++, new Timestamp(new Date().getTime()));
			sqlBridge.setString(index++, info);				
			sqlBridge.setString(index++, id);
			sqlBridge.executePreparedUpdate();
//			SmsplatGlobalVariable.missionLog.debug("更新短信 " + id  + " 状态为失败,原因是：" + info);
			
		} catch (SQLException e) {
//			SmsplatGlobalVariable.missionLog.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
			throw e;
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}
	
	/** 更新短信状态和状态报告时间
	 *
	 */
	public void updateBalanceNotEnoughStateById(String id, String info) throws SQLException {
		int index = 1;
		try {
			sqlBridge.openConnect();
			sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = 11, REPORT_TIME = ? ,ERR_MSG = ? WHERE SEND_ID = ?");
			sqlBridge.setTimestamp(index++, new Timestamp(new Date().getTime()));
			sqlBridge.setString(index++, info);				
			sqlBridge.setString(index++, id);
			sqlBridge.executePreparedUpdate();
//			SmsplatGlobalVariable.missionLog.debug("更新短信 " + id  + " 状态为失败,原因是：" + info);
		} catch (SQLException e) {
//			e.printStackTrace();
//			SmsplatGlobalVariable.missionLog.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
			throw e;
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}

	/** 更新已提交过的短信的状态
	 *
	 */
	public void updateSentStateById(String id) throws SQLException {
		int index = 1;
		try {
			sqlBridge.openConnect();
			String sql = "";
			sql = "UPDATE SEND SET SEND_TATUS = ? WHERE  SEND_ID = ?";
			sqlBridge.prepareExecuteUpdate(sql);
			sqlBridge.setInt(index++, SmsplatGlobalVariable.SENT_STATE);
			sqlBridge.setString(index++, id);
			sqlBridge.executePreparedUpdate();
//			SmsplatGlobalVariable.missionLog.debug("更新短信 " + id  + " 状态为已提交");

//			System.out.println(sql + "\nupdate rows = " + rows);
		} catch (SQLException e) {
//			SmsplatGlobalVariable.missionLog.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
			throw e;
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}
	
	/**
	 * 更新短信状态和状态报告时间
	 *
	 */
	public int updateStateBySequenceCache(com.binggou.sms.mission.core.about.data.dto.SmsSendDto dto) throws SQLException {
		int index = 1;
		updateRows = 0;
		try {
			sqlBridge.openConnect();
			// 根据sequence更新状态和状态报告返回时间
			if((""+SmsplatGlobalVariable.SEND_STATE).equals(dto.getMSG_STAT()))//发送成功
			{
				sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = ?, REPORT_TIME = ? WHERE SEQUENCE = ?");
				sqlBridge.setString(index++, dto.getMSG_STAT());
				sqlBridge.setTimestamp(index++, new Timestamp(dto.getReport_time().getTime()));
				sqlBridge.setString(index++, dto.getSequence());
				updateRows = sqlBridge.executePreparedUpdate();
//				SmsplatGlobalVariable.missionLog.debug("更新gw_sequence为 " + dto.getSequence()  + " 的短信状态为发送成功(5), 状态报告确认时间: " + dto.getReport_time());
			}
			else if((""+SmsplatGlobalVariable.FAIL_SEND_STATE).equals(dto.getMSG_STAT()))//发送失败
			{
				sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = ?, REPORT_TIME = ? ,ERR_MSG = ? WHERE SEQUENCE = ?");
				sqlBridge.setString(index++, dto.getMSG_STAT());
				sqlBridge.setTimestamp(index++, new Timestamp(dto.getReport_time().getTime()));
				sqlBridge.setString(index++, dto.getMessage());				
				sqlBridge.setString(index++, dto.getSequence());
				updateRows = sqlBridge.executePreparedUpdate();
//				SmsplatGlobalVariable.missionLog.debug("更新gw_sequence为 " + dto.getSequence()  + " 的短信状态为发送失败(6), 原因是："+dto.getMessage()+"。 状态报告确认时间: " + dto.getReport_time());
			}
			else if((""+SmsplatGlobalVariable.BLACH_SEND_STATE).equals(dto.getMSG_STAT()))//发送失败
			{
				sqlBridge.prepareExecuteUpdate("UPDATE SEND SET SEND_STATUS = ?, REPORT_TIME = ? ,ERR_MSG = ? WHERE SEND_ID = ?");
				sqlBridge.setString(index++, dto.getMSG_STAT());
				sqlBridge.setTimestamp(index++, new Timestamp(dto.getReport_time().getTime()));
				sqlBridge.setString(index++, dto.getMessage());				
				sqlBridge.setString(index++, dto.getMSG_ID());
				updateRows = sqlBridge.executePreparedUpdate();
//				SmsplatGlobalVariable.missionLog.debug("更新短信 " + dto.getMSG_ID() + " 的短信状态为发送失败(7), 原因是："+dto.getMessage());
			}
		} catch (SQLException e) {
			e.printStackTrace();
//			SmsplatGlobalVariable.missionLog.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
			throw e;
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
		return updateRows;
	}
	
	public void autoMonitorMessage(String mobileTo) {
		try {
			int index = 1;			
			sqlBridge.openConnect();		
			sqlBridge.prepareExecuteUpdate("insert into SEND (SEND_STATUS, SEND_ID, MOBILE_TO, SEND_MSG,PRIOR_ID,CHANNEL_ID) values (0, ?,?,?,3,8)");
			sqlBridge.setString(index++, new UUID().toString());
			sqlBridge.setString(index++, mobileTo);
			sqlBridge.setString(index++, "引擎测试 " + DateUtils.getCurrTime());
			sqlBridge.executePreparedUpdate();
		}
		catch (SQLException e) {
			e.printStackTrace();
//			SmsplatGlobalVariable.missionLog.error(e.getClass().toString().substring(6, e.getClass().toString().length()) + ": " + e.getLocalizedMessage() ,
//					e.getStackTrace());
		} finally
		{
			sqlBridge.clearResult();
			sqlBridge.closeConnect();
		}
	}
	
}
