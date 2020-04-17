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
 * Socket Client event�� ���Ź޾� ó���ϴ�  CLASS
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
	 * ������ ������ ��� ó���ϴ� method
	 * clientManager�� �翬��� �����Ǿ� �ִ� ��� ����� ä���� ä��queue���� ã�� �����ϰ�
	 * �����ð�(reconnectDelay�� ������ ���ð�) ����� �翬���� �õ� �Ѵ�
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
	 * exception ó��
	 * excepton�� �߻��ϸ� ü���� ������ ���´�
	 * @param ChannelHandlerContext ctx, ChannelStateEvent e
	 * @return Void
	 */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		// TODO Auto-generated method stub
		log.error( "Exception �߻�!!"+ e.getCause());

		e.getChannel().close();
		super.exceptionCaught(ctx, e);
	}

	/**
	 * ä�ηκ��� �޽����� ���Ž� ó���ϴ� �κ�
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
     * ä�η� �޽����� �۽��� �� ó���ϴ� �κ�
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
     * ���Ÿ޽����� ó���ϴ� �κ�
     * @param result
     * @return result
 * @throws Exception
     */
    public IndigoSignalResult onMessage(IndigoSignalResult result) throws Exception{
        return result;
    }
    /**
     * ĳ���κ��� �۽��������� header�� �����´�
     * @param IndigoSignalResult result
     * @return Object
     */
    public Object getCache(IndigoSignalResult result){
        String key = result.getProperty("TRAN_SEQ");
        return getCache(key);
    }
    /**
     * ĳ���κ��� �۽��������� header�� �����´�
     * @param String key
     * @return Object
     */
    public Object getCache(String key){
        Element obj = channelCache.get(key);
        return obj.getObjectValue();
    }
    /**
     * ä�ηκ��� ������ �޽����� ������ MQ�� ���� ����޽����� �����
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
     * ĳ���� �޽�������� �����Ѵ�. �̶� Ű�� result�� property�� "TRAN_SEQ"�� ����Ѵ�
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
