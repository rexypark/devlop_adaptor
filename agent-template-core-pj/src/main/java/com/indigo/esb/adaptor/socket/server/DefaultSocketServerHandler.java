package com.indigo.esb.adaptor.socket.server;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.WriteCompletionEvent;
import org.jboss.netty.handler.timeout.ReadTimeoutException;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import InterfaceNotFoundException.InterfaceNotFoundException;
import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.socket.type.SocketFileResponseCode;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.util.MTUtil;
import com.indigo.esb.adaptor.socket.type.ITSMciCode;

public class DefaultSocketServerHandler extends SimpleChannelHandler{

      @Autowired(required = false)
      protected JmsTemplate       jmsTemplate;
      @Autowired(required = false)
      protected Ehcache           channelCache;
      @Autowired(required = false)
      protected final Logger      log     = LoggerFactory.getLogger(getClass());

      private final static String DATA    = "DATA";
      public void channelBound( ChannelHandlerContext ctx, ChannelStateEvent e ) throws Exception{

            // TODO Auto-generated method stub
            super.channelBound(ctx, e);
      }

      @Override
      public void channelClosed( ChannelHandlerContext ctx, ChannelStateEvent e ) throws Exception{

            // TODO Auto-generated method stub
            super.channelClosed(ctx, e);
      }

      @Override
      public void channelDisconnected( ChannelHandlerContext ctx, ChannelStateEvent e ) throws Exception{

            // TODO Auto-generated method stub
            super.channelDisconnected(ctx, e);
      }

      @Override
      public void channelOpen( ChannelHandlerContext ctx, ChannelStateEvent e ) throws Exception{

            // TODO Auto-generated method stub
            super.channelOpen(ctx, e);
      }

      @Override
      public void exceptionCaught( ChannelHandlerContext ctx, ExceptionEvent e ) throws Exception{

            // TODO Auto-generated method stub

            log.error("Exception !!" + e.getCause());
            //Timeoute 발생
            String tranResp= SocketFileResponseCode.SUCCESS; //응답코드 : default = 0000
            if (e.getCause() instanceof ReadTimeoutException)
                  tranResp=SocketFileResponseCode.TIMEOUT;
            else if (e.getCause() instanceof FileNotFoundException)
                  tranResp=SocketFileResponseCode.AP_FILE_NOT_FOUND;
            else if (e.getCause() instanceof InterfaceNotFoundException)
                  tranResp=SocketFileResponseCode.IFID_NOT_FOUND;

            //캐쉬에 저장했던 AP요청 메시지 get
            String reqMessage = String.valueOf(channelCache.get(e.getChannel().getId() + "_msg").getObjectValue());

            //AP에 전송할 에러메시지 생성
            String tranLen = MTUtil.toNumberString(e.getCause().toString().length() + 91, 6); //메시지 길이
            String errMsgLen = MTUtil.toNumberString(e.getCause().toString().length(), 4); //에러 메시지 길이
            String data = e.getCause().toString(); //에러 메시지
            String repsMessage = tranLen + reqMessage.substring(6, 89) + tranResp + errMsgLen + data;

            log.info("AP Response error message :" + repsMessage);
            e.getChannel().write((repsMessage).getBytes()).addListener(new ChannelFutureListener(){

                  @Override
                  public void operationComplete( ChannelFuture future ) throws Exception{
                        log.info("write ok.");
                  }
            });
            super.exceptionCaught(ctx, e);
      }

      @Override
      public void messageReceived( ChannelHandlerContext ctx, MessageEvent e ) throws Exception{

            log.info("############## AP Receive");
            // TODO Auto-generated method stub
            if (!(e.getMessage() instanceof IndigoSignalResult)){ return; }
            IndigoSignalResult result = (IndigoSignalResult) e.getMessage();


            List<Map<String, byte[]>> objData = (List<Map<String, byte[]>>) result.getPollResultDataObj();
            String reqMessage = new String(objData.get(0).get(DATA));
            String timeoutIndex = result.getInterfaceInfo().getAddDataMap().get(ITSMciCode.TIMEOUT);
            log.info("############## AP Receive Message :" + reqMessage);
            Element ele = new Element(e.getChannel().getId() + "_msg", reqMessage);
            channelCache.put(ele);

            result = onMessage(result, e);


            if (timeoutIndex != null){
                  int timeout = Integer.parseInt(MTUtil.getMciString(reqMessage, timeoutIndex));
                  log.info("@@ Timeout setting : " + timeout);
                  this.addTimeoutHandler(ctx, e, timeout);
            }

            String sendDestinationName = result.getInterfaceInfo().getTargetDestinationName();
            result.getProperties().addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_IF_TYPE, "TP");
            this.jmsTemplate.convertAndSend(sendDestinationName, result.getTemplateMessage(), result.getProperties());
            super.messageReceived(ctx, e);

      }

      public void addTimeoutHandler( ChannelHandlerContext ctx, MessageEvent e, int timeout ){

            ctx.getPipeline().addFirst("timouthandler", new ReadTimeoutHandler(new HashedWheelTimer(), timeout));
            ctx.sendUpstream(e);
      }

      @Override
      public void writeComplete( ChannelHandlerContext ctx, WriteCompletionEvent e ) throws Exception{

            // TODO Auto-generated method stub
            log.info("writeComplete :" + e.getWrittenAmount());
            super.writeComplete(ctx, e);
      }

      public IndigoSignalResult onMessage( IndigoSignalResult result, MessageEvent e ) throws Exception{
            return result;
      }

      public Object getCache( IndigoSignalResult result ){

            String key = result.getProperty("TRAN_SEQ");
            return getCache(key);

      }

      public Object getCache( String key ){

            Element obj = channelCache.get(key);
            return obj.getObjectValue();
      }

      public IndigoSignalResult makeReturnMessage( IndigoSignalResult result ){

            IndigoSignalResult msg = result;
            Map<String, String> header = (Map<String, String>) getCache(result);
            log.info("header map:" + header);
            msg.getProperties().addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_IF_TYPE, "TP");
            msg.getProperties().addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_TX_ID, header.get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID));
            msg.getProperties().addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_DESTINATION_ID,
                        header.get(IndigoHeaderJMSPropertyConstants.ESB_DESTINATION_ID));
            msg.getProperties().addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE, "RESULT");
            msg.getProperties().addHeaderInfo(IndigoHeaderJMSPropertyConstants.ESB_REPLY_QEUEE_NAME,
                        header.get(IndigoHeaderJMSPropertyConstants.ESB_REPLY_QEUEE_NAME));
            return msg;
      }

      public void registMessage( IndigoSignalResult result, Channel channel ) throws Exception{

            log.info("#################### Request Value Regist.......... ####################");
            String key = channel.getId() + "";
            log.info("Request CHANNEL ID:" + key);
            Element ele = new Element(key, channel);
            result.addProperty(IndigoHeaderJMSPropertyConstants.ESB_RPL_CH_ID, key);
            channelCache.put(ele);
      }
}
