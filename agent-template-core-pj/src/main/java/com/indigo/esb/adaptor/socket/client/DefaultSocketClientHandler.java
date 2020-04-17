package com.indigo.esb.adaptor.socket.client;

import java.util.List;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.util.MTUtil;
/**
 *
 * Socket Client event를 수신받아 처리하는  CLASS
 *
 */
public class DefaultSocketClientHandler extends SimpleChannelHandler{
	@Autowired(required = false)
	protected JmsTemplate jmsTemplate;
	@Autowired(required = false)
	protected Ehcache channelCache;
	@Autowired(required = false)
	protected DefaultClientManager clientManger;
	protected final Logger log = LoggerFactory.getLogger(getClass());
	/**
	 * 연결이 끊겼을 경우 처리하는 method
	 * clientManager가 재연결로 설정되어 있는 경우 연결된 채널을 채널queue에서 찾아 삭제하고
	 * 일정시간(reconnectDelay로 설정된 대기시간) 대기후 재연결을 시도 한다
	 * @param ChannelHandlerContext ctx, ChannelStateEvent e
	 * @return void
	 */
	@Override
	public void channelClosed(ChannelHandlerContext ctx, ChannelStateEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		if(clientManger.isReconnect()){
			clientManger.getChannelQueue().remove(e.getChannel());
			Thread.sleep(clientManger.getReconnectDelay());
			clientManger.getChannel(e.getChannel().getRemoteAddress());
		}


		super.channelClosed(ctx, e);
	}
	/**
	 * exception 처리
	 * excepton이 발생하면 체널의 연결을 끊는다
	 * @param ChannelHandlerContext ctx, ChannelStateEvent e
	 * @return Void
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		log.error( "Exception 발생!!"+ e.getCause());

		e.getChannel().close();
		super.exceptionCaught(ctx, e);
	}

	/**
	 * 채널로부터 메시지를 수신시 처리하는 부분
	 * @param ChannelHandlerContext ctx, MessageEvent e
	 * @return Void
	 */
    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
            throws Exception {
        // TODO Auto-generated method stub
        log.info("############ TP Message Receive");
        if(!(e.getMessage() instanceof IndigoSignalResult)){
            log.error("Receive Messeage is not IndigoSignalResult type!!!");
            return;
        }

        IndigoSignalResult result = (IndigoSignalResult)e.getMessage();
        if(result.getProperty("TRAN_SEQ")==null){
              result.addProperty("TRAN_SEQ", e.getChannel().getId()+"");
        }
        result = onMessage(result);
        String sendDestinationName = result.getInterfaceInfo().getTargetDestinationName();
        this.jmsTemplate.convertAndSend(sendDestinationName, result.getTemplateMessage(), result.getProperties());
        if(!clientManger.isConnectOnStartup()){
            e.getChannel().close();
        }
        super.messageReceived(ctx, e);
    }
    /**
     * 채널로 메시지를 송신한 후 처리하는 부분
     * @param ChannelHandlerContext ctx, WriteCompletionEvent e
     * @return Void
     */
    @Override
    public void writeComplete(ChannelHandlerContext ctx, WriteCompletionEvent e)
            throws Exception {
        // TODO Auto-generated method stub
        log.debug("writeComplete :" + e.getWrittenAmount());
        super.writeComplete(ctx, e);
    }
    /**
     * 수신메시지를 처리하는 부분
     * @param result
     * @return result
 * @throws Exception
     */
    public IndigoSignalResult onMessage(IndigoSignalResult result) throws Exception{
        return result;
    }
    /**
     * 캐쉬로부터 송신한전문의 header를 가져온다
     * @param IndigoSignalResult result
     * @return Object
     */
    public Object getCache(IndigoSignalResult result){
        String key = result.getProperty("TRAN_SEQ");
        return getCache(key);
    }
    /**
     * 캐쉬로부터 송신한전문의 header를 가져온다
     * @param String key
     * @return Object
     */
    public Object getCache(String key){
        Element obj = channelCache.get(key);
        return obj.getObjectValue();
    }
    /**
     * 채널로붙터 수신한 메시지를 가지고 MQ로 보낼 응답메시지를 만든다
     * @param IndigoSignalResult result
     * @return IndigoSignalResult
     */
    public IndigoSignalResult makeReturnMessage(IndigoSignalResult result){
        IndigoSignalResult msg = result;
        Map<String, String> header = (Map<String, String>)getCache(result);
        log.info("MQ Response Message Create ");
        log.debug("header map:" + header);
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_IF_TYPE, "TP");
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_IF_ID, header.get(IndigoHeaderJMSPropertyConstants.ESB_IF_ID));
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_TX_ID, header.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID));
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_DESTINATION_ID, header.get(IndigoHeaderJMSPropertyConstants.ESB_DESTINATION_ID));
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE, "RESULT");
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_REPLY_QEUEE_NAME, header.get(IndigoHeaderJMSPropertyConstants.ESB_REPLY_QEUEE_NAME));
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_RPL_CH_ID, header.get(IndigoHeaderJMSPropertyConstants.ESB_RPL_CH_ID));
        /*msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_ORG_FILE_NAME, header.get(IndigoHeaderJMSPropertyConstants.ESB_ORG_FILE_NAME));*/
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_FILE_NAME, header.get(IndigoHeaderJMSPropertyConstants.ESB_FILE_NAME));
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_FILE_SIZE, header.get(IndigoHeaderJMSPropertyConstants.ESB_FILE_SIZE));
        msg.getProperties()
        .addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_SOCKET_TX_ID, result.getProperty(IndigoHeaderJMSPropertyConstants.ESB_SOCKET_TX_ID));
        return msg;
    }
    /**
     * 캐쉬에 메시지헤더를 저장한다. 이때 키는 result의 property중 "TRAN_SEQ"를 사용한다
     * @param IndigoSignalResult result
     * @param Channel channel
     * @throws Exception
     */
    public void registMessage(IndigoSignalResult result, Channel channel) throws Exception{
          String txId = result.getProperty("TRAN_SEQ");
        Element ele = new Element(txId, channel);
        channelCache.put(ele);
    }
}
