package com.indigo.esb.adaptor.socket.codec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;
import org.springframework.beans.factory.annotation.Autowired;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

public class EAIMessageEncoder extends OneToOneEncoder {
	private final Log log = LogFactory.getLog(getClass());
	private String interfaceId;
	private List<InterfaceInfo> interfaceList;
	private Map<String, InterfaceInfo> interfaceMap;
	@Autowired (required = false)
	protected Ehcache channelCache;
	public void setInterfaceList(List<InterfaceInfo> interfaceList) {
		this.interfaceList = interfaceList;
		interfaceMap = new HashMap<String,InterfaceInfo>();
		for (InterfaceInfo interfaceInfo : interfaceList) {
			interfaceMap.put(interfaceInfo.getInterfaceId(), interfaceInfo);
		}
	}
	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}
	protected Object encode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		if (!(msg instanceof IndigoMessageResult)) {
			log.error("msg not IndigoMessageResult!!!!!");
			return msg;
		}
		log.debug("encoder received");
		IndigoMessageResult indigoMessageResult = (IndigoMessageResult)msg;
		List<Map<String, Object>> rowMapList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		Map mapMsg = null;
		for (Map<String, Object> map : rowMapList) {
			mapMsg = map;
		}
		byte[] data = null;
		if(mapMsg.containsKey("DATA")){
		    data = (byte[])mapMsg.get("DATA");
		}
		InterfaceInfo info = indigoMessageResult.getInterfaceInfo();
		if(info == null) {
			throw new Exception("INTERFACE INFO NOT FOUND");
		}
		/*ChannelBuffer buf = ChannelBuffers.dynamicBuffer(data.length);
		buf.writeBytes(data);*/

		return data;
	}

}
