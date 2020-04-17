package com.indigo.esb.adaptor.socket.client;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import com.indigo.esb.adaptor.socket.Listener.DefaultChanelFutuerListener;
import com.indigo.esb.config.SocketInfo;
/**
 * Socket Client Manager
 * @author Lim.k.b
 * @version v1.0
 * @since 2014.02.03
 */
public class FileServerClientManager {
    private final Logger              log           = Logger.getLogger(this.getClass());
	private boolean connectOnStartup = false;

	private List<SocketInfo> socketInfoList;
	/**
	 * 연결시 채널을 저장할 queue
	 */
	private LinkedBlockingQueue<Channel> channelQueue = new LinkedBlockingQueue<Channel>();

	private ClientBootstrap clientBootStrap;

	private boolean reconnect = false;

	private int reconnectDelay = 5000;
	/**
	 * 재연결시 대기시간(miliseconds)
	 * defalut:5000
	 */
	public void setReconnectDelay(int reconnectDelay) {
		this.reconnectDelay = reconnectDelay;
	}
	public int getReconnectDelay() {
		return reconnectDelay;
	}
	/**
	 * 재연결여부
	 * default : false
	 */
	public void setReconnect(boolean reconnect) {
		this.reconnect = reconnect;
	}
	public boolean isReconnect() {
		return reconnect;
	}

	public boolean isConnectOnStartup() {
		return connectOnStartup;
	}
	/**
	 * 연결시 사용할 ClientBootstrap
	 */
	public void setClientBootStrap(ClientBootstrap clientBootStrap) {
		this.clientBootStrap = clientBootStrap;
	}
	/**
	 * 구동시 서버연결 여부
	 * default : false
	 *
	 */
	public void setConnectOnStartup(boolean connectOnStartup) {
		this.connectOnStartup = connectOnStartup;
	}
	/**
	 * 연결할 서버정보 list
	 */
	public void setSocketInfo(List<SocketInfo> socketInfoList) {
		this.socketInfoList = socketInfoList;
	}
	public LinkedBlockingQueue<Channel> getChannelQueue() {
		return channelQueue;
	}
	/**
	 * client Manager를 구동한다
	 * connectOnStartup=true인 경우
	 * socketInfoList에 있는 서버정보에 해당하는 서버에 연결한 후 채널을 queue에 저장한다
	 */
	public void start(){
		ChannelFactory factory =
	            new NioClientSocketChannelFactory(
	                    Executors.newCachedThreadPool(),
	                    Executors.newCachedThreadPool());
		clientBootStrap.setFactory(factory);
		if(connectOnStartup){
			for (SocketInfo info : socketInfoList) {
				for (int i = 0; i < info.getConnectCount(); i++) {
					ChannelFuture future = clientBootStrap.connect(new InetSocketAddress(info.getServerIp(), info.getServerPort()));
					future.addListener(new ChannelFutureListener() {
						public void operationComplete(ChannelFuture future) throws InterruptedException {
							if (future.isSuccess()) {
								channelQueue.put(future.getChannel());
							}
						}
					});
				}
			}
		}
	}
	/**
	 * 서버에 연결한다
	 * @param host 연결할 ip
	 * @param port 연결할 port
	 */
	public void getChannel(String host, int port){
		ChannelFuture future = clientBootStrap.connect(new InetSocketAddress(host, port));
	}
	public void getChannel(SocketAddress socketAddress){
		ChannelFuture future = clientBootStrap.connect(socketAddress);
	}
	/**
	 * socket연결정보중에서 idx번째 연결을 한다
	 * @param idx
	 * @return Channel
	 */
	public Channel getChannel(int idx){
		SocketInfo info = socketInfoList.get(idx);

		ChannelFuture future = clientBootStrap.connect(new InetSocketAddress(info.getServerIp(), info.getServerPort()));
		if(future.awaitUninterruptibly(5000)){
			return future.getChannel();
		}
		return null;
	}
	/**
	 * socket연결정보중에서 1번째 연결을 한다
	 * @return Channel
	 */
	public Channel getChannel(){
		return getChannel(0);
	}

	public boolean sendSocket(String filePath, String fileName){
	      SocketInfo info = socketInfoList.get(0);
	      log.info("#### ESB FileServer Connecting #####");
	      log.info("Server I  P:"+info.getServerIp());
	      log.info("Server PORT:"+info.getServerPort());
	      log.info("####################################");
	      clientBootStrap.connect(new InetSocketAddress(info.getServerIp(), info.getServerPort())).
                addListener(new DefaultChanelFutuerListener(filePath,fileName));

	      return true;
	}

	/**
	 * 채널queue에서 정상적인 채널을 하나 가져온다
	 * @return Channel
	 * @throws Exception
	 */
	public Channel getChannelAsync() throws Exception{
		Channel channel = null;
		while(!channelQueue.isEmpty()){
			channel = channelQueue.take();
			if(channel.isConnected() && channel.isWritable()){
				break;
			}
			channel = null;
		}
		return channel;
	}

}
