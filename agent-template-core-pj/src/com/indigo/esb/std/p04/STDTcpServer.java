package com.indigo.esb.std.p04;

import java.util.Map;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.indigo.esb.adaptor.socket.factory.StringLengthFieldBasedPipelineFactory;

public class STDTcpServer {
	private ServerBootstrap severBootstrap;
	private StringLengthFieldBasedPipelineFactory pipeLineFactory;
	private Map Option;
	public void setOption(Map option) {
		Option = option;
	}
	public void setPipeLineFactory(
			StringLengthFieldBasedPipelineFactory pipeLineFactory) {
		this.pipeLineFactory = pipeLineFactory;
	}
	public void setSeverBootstrap(ServerBootstrap severBootstrap) {
		this.severBootstrap = severBootstrap;
	}
	public void bind(){
		ChannelFactory factory =
	            new NioServerSocketChannelFactory(
	                    Executors.newCachedThreadPool(),
	                    Executors.newCachedThreadPool(),16);
		severBootstrap = new ServerBootstrap(factory);
		severBootstrap.setPipelineFactory(pipeLineFactory);
		severBootstrap.setOptions(Option);
		severBootstrap.bind();
	}
}
