package com.indigo.esb.config;

import org.jboss.netty.channel.ChannelPipelineFactory;

import com.indigo.esb.adaptor.socket.server.DefaultSocketServer;


public class TPInterfaceInfo extends InterfaceInfo {

	protected String serverIp;
	protected int serverPort;
	protected boolean useLib;
	protected int listenPort;
	protected ChannelPipelineFactory clntFactory;
	protected DefaultSocketServer serverFactory;
	public void setServerFactory(DefaultSocketServer serverFactory) {
		this.serverFactory = serverFactory;
	}
	public DefaultSocketServer getServerFactory() {
		return serverFactory;
	}
	public ChannelPipelineFactory getClntFactory() {
		return clntFactory;
	}
	public void setClntFactory(ChannelPipelineFactory clntFactory) {
		this.clntFactory = clntFactory;
	}
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}
	public int getListenPort() {
		return listenPort;
	}
	public String getServerIp() {
	    return serverIp;
	}
	public void setServerIp(String serverIp) {
	    this.serverIp = serverIp;
	}

	public void setServerPort(int serverPort) {
	    this.serverPort = serverPort;
	}
	public int getServerPort() {
	    return serverPort;
	}
	public boolean isUseLib() {
	    return useLib;
	}
	public void setUseLib(boolean useLib) {
	    this.useLib = useLib;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		setInterfaceType(InterfaceType.TP);
	}

}
