package com.indigo.esb.config;

public class DBInterfaceInfoURLCall extends DBInterfaceInfo {
	String targetURL;
	boolean useURLCall;
	
	public String getTargetURL() {
		return targetURL;
	}
	public void setTargetURL(String targetURL) {
		this.targetURL = targetURL;
	}
	public boolean isUseURLCall() {
		return useURLCall;
	}
	public void setUseURLCall(boolean useURLCall) {
		this.useURLCall = useURLCall;
	}
	
}
