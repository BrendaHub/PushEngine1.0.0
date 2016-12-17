package com.binggou.sms.mission.core.about.util;

import com.binggou.sms.mission.core.about.data.dao.SmsDao;

/**
 * 提交响应信息队列处理线程
 * @author chenhj(brenda)
 * @since 2009-05-12
 * @version 0.1
 */
public final class GatewaySubmitResponseDealThread implements Runnable {

	private com.binggou.sms.mission.core.about.data.dto.SmsSendDto dto = null;
	
	private SmsDao dao = null;
	
	private int infoType = -1;
	
	private int state = -1;
	
	public GatewaySubmitResponseDealThread() {
		dao = new SmsDao();
	}

	public void run() {
		try{
			while(true){
				if(System.currentTimeMillis() - SmsplatGlobalVariable.LAST_SUBMIT_TIME >=5000){}
				dto = GatewaySubmitResponseInfo.popTask();
				try {
					if(null != dto){
						infoType = dto.getInfoType();
						if( infoType == 1){//提交响应
							state = Integer.parseInt( dto.getMSG_STAT() );
							switch(state){
								case 0://提交成功
									dao.updateSendSequence(dto.getMSG_ID(), dto.getSequence(), dto.getPAGE_COUNT());
									break;
								case 1://提交失败
									dao.updateFailStateById(dto.getMSG_ID(), "提交失败");
									break;
								case 2://格式不正确
									dao.updateFormatErrorStateById(dto.getMSG_ID(), "格式不正确");
									break;
								case 3://余额不足
									dao.updateBalanceNotEnoughStateById(dto.getMSG_ID(), "余额不足");
									break;
								case 4://短信已存在
									dao.updateSentStateById(dto.getMSG_ID());
									break;
								case 7://网关黑名单
									dao.updateStateBySequence(dto);
									break;
								default:
									break;
							}
						}
						else if( infoType == 2) {//上行
							dao.receiveMessage(dto);
						}
						else if( infoType == 3) {//状态报告
							dao.updateStateBySequenceCache(dto);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					GatewaySubmitResponseInfo.putTask(dto);
				}
				
				Thread.sleep(200);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			try {
				Thread.sleep(1000);
			}
			catch (Exception ex){
				ex.printStackTrace();
			}
		}
	}

}
