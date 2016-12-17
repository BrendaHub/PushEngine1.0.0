package com.binggou.sms.mission.core.about.util;

import java.util.LinkedList;

/**
 * 提交响应信息队列
 * @author chenhj(brenda)
 * @since 2009-04-27
 */
public final class GatewaySubmitResponseInfo {
	private static LinkedList<com.binggou.sms.mission.core.about.data.dto.SmsSendDto> taskList = new LinkedList<com.binggou.sms.mission.core.about.data.dto.SmsSendDto>();
	
	/**
	 * 从队列头部取得信息
	 * @return 信息对象
	 */
	public synchronized static com.binggou.sms.mission.core.about.data.dto.SmsSendDto popTask(){
        //如果还没有待处理任务，直接返回null
        return taskList.poll();
	}
	
	/**
	 * 置入信息到队列尾部
	 * @param dto 信息对象
	 */
	public synchronized static void putTask(com.binggou.sms.mission.core.about.data.dto.SmsSendDto dto){
		taskList.addLast(dto);
	}
	
	/**
	 * 队列长度
	 * @return 队列长度
	 */
	public synchronized static int getSize(){
		return taskList.size();
	}
	
}
