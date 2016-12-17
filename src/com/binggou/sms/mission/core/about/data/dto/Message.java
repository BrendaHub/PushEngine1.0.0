package com.binggou.sms.mission.core.about.data.dto;

public class Message {
	private String MSG_ID;// 短信id

	private String serviceID;// 服务代码

	private String fromNum;// 发送人

	private String MSG_CONTEXT;// 短信内容
	
	private String channelId;//通道编号

	public String getFromNum() {
		return fromNum;
	}

	public void setFromNum(String fromNum) {
		this.fromNum = fromNum;
	}

	public String getMSG_CONTEXT() {
		return MSG_CONTEXT;
	}

	public void setMSG_CONTEXT(String msg_context) {
		MSG_CONTEXT = msg_context;
	}

	public String getMSG_ID() {
		return MSG_ID;
	}

	public void setMSG_ID(String msg_id) {
		MSG_ID = msg_id;
	}

	public String getServiceID() {
		return serviceID;
	}

	public void setServiceID(String serviceID) {
		this.serviceID = serviceID;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(" fromNum = " + fromNum);
		sb.append(" , serviceID = " + serviceID);
		sb.append(" , MSG_CONTEXT = " + MSG_CONTEXT);
		sb.append(" , channelId = " + channelId);
		
		return sb.toString();

	}

}
