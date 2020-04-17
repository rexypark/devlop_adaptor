package com.indigo.esb.adaptor.socket.server;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.util.Assert;

import com.indigo.esb.adaptor.socket.factory.DefaultPipelineFactory;
import com.indigo.esb.config.InterfaceInfo;

public class DefaultSocketServer implements InitializingBean{
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	@Resource(name="onSignalPatternMap")
	private List<InterfaceInfo> interfaceList;

	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;

	private ServerBootstrap serverBootstrap;
	private DefaultPipelineFactory pipeLineFactory;
	private InterfaceInfo interfaceInfo;
	private Map options;
	private String interfaceId;
	private int listenPort;

	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}
	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}
	public void setOption(Map options) {
		this.options = options;
	}
	public void setInterfaceInfo(InterfaceInfo interfaceInfo) {
		this.interfaceInfo = interfaceInfo;
	}
	public InterfaceInfo getInterfaceInfo() {
		return interfaceInfo;
	}
	public void setJmsTemplate(JmsTemplate jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	public void setPipeLineFactory(
			DefaultPipelineFactory pipeLineFactory) {
		this.pipeLineFactory = pipeLineFactory;
	}
	public DefaultPipelineFactory getPipeLineFactory() {
		return pipeLineFactory;
	}
	public void setServerBootstrap(ServerBootstrap serverBootstrap) {
		this.serverBootstrap = serverBootstrap;
	}
	public ServerBootstrap getServerBootstrap() {
		return serverBootstrap;
	}
	public void bind() throws Exception{
		ChannelFactory factory =
	            new NioServerSocketChannelFactory(
	                    Executors.newCachedThreadPool(),
	                    Executors.newCachedThreadPool(),16);
		serverBootstrap = new ServerBootstrap(factory);
		//Assert.notNull(interfaceList, "Property 'interfaceList' is Required");
		logger.info("Interface List  : " + interfaceList.size());
		serverBootstrap.setPipelineFactory(pipeLineFactory);
		serverBootstrap.setOptions(options);
		serverBootstrap.bind(new InetSocketAddress(listenPort));
	}
	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		//logger.info("Interface List  : " + interfaceList.size());
	}
}
