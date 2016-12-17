package com.binggou.sms.mission.core.about.data.dto;

import java.util.Date;

/**
 * 短信对象
 * 
 * @author Administrator
 * 
 */
public class SmsSendDto {

	private String MSG_ID;

	private String channel;

	private String toNum;

	private String MSG_STAT;
	
	private String message;

	private String sequence;

	private int PAGE_COUNT;
	
	private int infoType = 0;//1-提交响应;2-上行;3-状态报告
	
	private String mobileFrom ="";
	
	private String extendNo = "";
	/**
	 * 平台提交短信时间
	 */
	private Date send_time;

	/**
	 * 网关确认提交时间
	 */
	private Date con_rtn_time;

	/**
	 * 重发时间
	 */
	private Date send_time2;

	/**
	 * 重发后，返回结果时间
	 */
	private Date con_rtn_time2;

	/**
	 * 接收状态报告时间
	 */
	private Date report_time;

	public Date getSend_time() {
		return send_time;
	}

	public void setSend_time(Date send_time) {
		this.send_time = send_time;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public Date getReport_time() {
		return report_time;
	}

	public void setReport_time(Date report_time) {
		this.report_time = report_time;
	}

	public Date getCon_rtn_time() {
		return con_rtn_time;
	}

	public void setCon_rtn_time(Date con_rtn_time) {
		this.con_rtn_time = con_rtn_time;
	}

	public Date getCon_rtn_time2() {
		return con_rtn_time2;
	}

	public void setCon_rtn_time2(Date con_rtn_time2) {
		this.con_rtn_time2 = con_rtn_time2;
	}

	public Date getSend_time2() {
		return send_time2;
	}

	public void setSend_time2(Date send_time2) {
		this.send_time2 = send_time2;
	}

	public String getMSG_ID() {
		return MSG_ID;
	}

	public void setMSG_ID(String msg_id) {
		MSG_ID = msg_id;
	}

	public String getToNum() {
		return toNum;
	}

	public void setToNum(String toNum) {
		this.toNum = toNum;
	}

	public String getMSG_STAT() {
		return MSG_STAT;
	}

	public void setMSG_STAT(String msg_stat) {
		MSG_STAT = msg_stat;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}	

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public synchronized int getPAGE_COUNT() {
		return PAGE_COUNT;
	}

	public synchronized void setPAGE_COUNT(int page_count) {
		PAGE_COUNT = page_count;
	}

	/**
	 * 1-提交响应;2-上行;3-状态报告
	 * @return
	 */
	public int getInfoType() {
		return infoType;
	}

	/**
	 * 1-提交响应;2-上行;3-状态报告
	 * @param infoType
	 */
	public void setInfoType(int infoType) {
		this.infoType = infoType;
	}

	public String getExtendNo() {
		return extendNo;
	}

	public void setExtendNo(String extendNo) {
		this.extendNo = extendNo;
	}

	public String getMobileFrom() {
		return mobileFrom;
	}

	public void setMobileFrom(String mobileFrom) {
		this.mobileFrom = mobileFrom;
	}

}
