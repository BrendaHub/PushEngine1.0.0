package com.binggou.sms.mission.core.about.data.dto;

public final class ValidateMobileDto {
	private boolean isRtNum = false;

	private String errorNumber = "";

	private String errorDesc = "";

	public String getErrorDesc() {
		return errorDesc;
	}

	public void reset(){
		this.errorDesc="";
		this.errorNumber="";
		this.isRtNum = false;
	}
	
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}

	public String getErrorNumber() {
		return errorNumber;
	}

	public void setErrorNumber(String errorNumber) {
		this.errorNumber = errorNumber;
	}

	public boolean isRtNum() {
		return isRtNum;
	}

	public void setRtNum(boolean isRtNum) {
		this.isRtNum = isRtNum;
	}
}
