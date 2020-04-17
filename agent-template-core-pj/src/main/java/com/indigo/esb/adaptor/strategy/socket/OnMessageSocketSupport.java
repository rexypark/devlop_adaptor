package com.indigo.esb.adaptor.strategy.socket;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.socket.client.DefaultClientManager;
import com.indigo.esb.adaptor.socket.type.SocketFileResponseCode;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.config.InterfaceType;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;
import com.indigo.esb.util.MTUtil;

/**
 * MQ�� ���� ������ ���Ž� ���
 *
 * @since 2014.02.03
 * @author Lim.k.b
 *
 */
public abstract class OnMessageSocketSupport implements OnMessageStrategy, InitializingBean{
      private final static String DATA    = "DATA";
      protected final Logger         log = LoggerFactory.getLogger(getClass());
      @Autowired(required = false)
      protected JmsTemplate          jmsTemplate;
      @Autowired(required = false)
      protected DefaultClientManager clientManager;
      @Autowired(required = false)
      protected Ehcache              clientCache;
      @Autowired(required = false)
      protected Ehcache              channelCache;

      public OnMessageSocketSupport() {

            // TODO Auto-generated constructor stub
      }

      /**
       * ĳ�ڿ� �޽��� ����� ������ �� ���ϼ����� �����͸� �����Ѵ�.
       *
       * @param result
       * @throws Exception
       */
      public void requestMessage( IndigoMessageResult result, boolean auto ) throws Exception{

            List<Map<String, byte[]>> objData = (List<Map<String, byte[]>>) result.getDataObj();
            String reqMessage = new String(objData.get(0).get(DATA));
            Channel channel = clientManager.getChannel();

            log.debug("############## AD_MSG_RCV Receive Message :" + reqMessage);

            if (channel == null || !channel.isConnected() || !channel.isWritable()){
                  this.sendErrorJmsMessage(result, reqMessage);
                  log.error("CHANNEL NOT CONNECTED");
                  throw new Exception("CHANNEL NOT CONNECTED");
            }

            Map header = result.getProperties().getHeaderInfoMap();
            String key2 = channel.getId() + "";
            Element element = new Element(key2, header);
            channelCache.put(element);
            channel.write(result);

      }


      public void sendErrorJmsMessage( IndigoMessageResult result, String reqMessage) throws Exception{
            log.info("Error MQ Message Create.");
            //ĳ���� �����ߴ� AP��û �޽��� get
            List<Map<String, byte[]>> objData = (List<Map<String, byte[]>>) result.getDataObj();

            //AP�� ������ �����޽��� ����
            String data = "TP DEMON NOT CONNECTED"; //���� �޽���
            String trenResp=SocketFileResponseCode.TP_NOT_CONNECT; //�����ڵ�
            String tranLen = MTUtil.toNumberString(data.length() + 91, 6); //�޽��� ����
            String errMsgLen = MTUtil.toNumberString(data.length(), 4); //���� �޽��� ����
            String repsMessage = tranLen + reqMessage.substring(6, 89) + trenResp + errMsgLen + data;
            log.info("send error mesage : " + repsMessage);
            objData.get(0).put("DATA", repsMessage.getBytes());

            result.getInterfaceInfo().setInterfaceType(InterfaceType.TP);
            IndigoSignalResult signalResult = new IndigoSignalResult(result.getInterfaceInfo());
            signalResult.setPollResultDataObj(objData);
            signalResult.setProperties(result.getProperties());

            log.info("########## error jms send");
            log.info("########## target queue :" +signalResult.getInterfaceInfo().getTargetDestinationName());

            this.jmsTemplate.convertAndSend(signalResult.getInterfaceInfo().getTargetDestinationName(), signalResult.getTemplateMessage(),
                        result.getProperties());
      }


      /**
       * ĳ�ڿ� �޽��� ����� �������� �ʰ� ���ϼ����� �����͸� �����Ѵ�.
       *
       * @param result
       * @throws Exception
       */
      public void sendMessage( IndigoMessageResult result ) throws Exception{
            Channel channel = null;
            if (clientManager.isConnectOnStartup()){
                  channel = clientManager.getChannelAsync();
            }
            else{
                  channel = clientManager.getChannel();
            }
            channel.write(result);

      }

      /**
       * MQ�� ���� ������ �޽ý� ó������� ������ ���� �۽��ߴ� ä���� ã�� ������ �����Ѵ�
       *
       * @param result
       * @throws Exception
       */
      public void responseMessage( IndigoMessageResult result ) throws Exception{

            log.info("#################### ResponseData Receive !! ####################");
            String rplChannelId = result.getProperty(IndigoHeaderJMSPropertyConstants.ESB_RPL_CH_ID); //����ä�ξ��̵�
            ArrayList<Map<String,byte[]>> data = (ArrayList<Map<String,byte[]>>)result.getDataObj();
            Map<String,byte[]> map = data.get(0);
            String repsData = new String(map.get("DATA")); //����ä�ξ��̵�
            log.debug("Response CHANNEL ID:" + rplChannelId);
            log.debug("Response message:" + repsData);
            log.info("#################### write ................ ####################");
            Channel channel = (Channel) channelCache.get(rplChannelId).getObjectValue();
            channel.write(repsData.getBytes());

      }

      /**
       * �������� �� AP���� Connect�� ���� ���� �޼ҵ�
       * Ÿ�Ӿƿ�Exception�߻��� �������� ����ߴ� ReadTimeoutHandler�� �����ϰ�
       * AP���� ������ ���´�.
       *
       * @param result
       * @throws Exception
       */
      public void completeProcess( IndigoMessageResult result ) throws Exception{

            log.debug("Remove Timeout Handler");
            String rplChannelId = result.getProperty(IndigoHeaderJMSPropertyConstants.ESB_RPL_CH_ID); //����ä�ξ��̵�
            Channel channel = (Channel) channelCache.get(rplChannelId).getObjectValue();
            channel.getPipeline().removeFirst();
            //channel.close();
      }

      @Override
      public void afterPropertiesSet() throws Exception{

      }

}
