package com.binggou.sms.mission.core.about.util;

import com.binggou.sms.mission.core.about.data.dao.SmsDao;

public class SequenceUtil {
	private static int sequence=0;
	public static int getSequence(){
		return sequence++;
	}
	public static int getNextSequence(){
		SmsDao dao = new SmsDao();
		return dao.getNextSequence();
	}
}
