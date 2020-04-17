package com.indigo.esb.config;

public class SocketInfo {
	protected String serverIp;
	protected int serverPort;
	protected int connectCount;
	protected int ListenPort;
	public void setConnectCount(int connectCount) {
		this.connectCount = connectCount;
	}
	public void setListenPort(int listenPort) {
		ListenPort = listenPort;
	}
	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}
	public int getConnectCount() {
		return connectCount;
	}
	public int getListenPort() {
		return ListenPort;
	}
	public String getServerIp() {
		return serverIp;
	}
	public int getServerPort() {
		return serverPort;
	}
}
