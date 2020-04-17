package com.indigo.esb.adaptor.socket.codec;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipelineCoverage;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.socket.codec.extractor.TranCodeExtractor;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.config.InterfaceInfo;
import com.indigo.esb.db.QueryStatementType;
import com.indigo.esb.util.BufferUtils;


@ChannelPipelineCoverage("all")
public class EAIMessageDecoder extends OneToOneDecoder {
	@Resource(name="interfaceList")
	protected List<InterfaceInfo> interfaceList;
	private final Log log = LogFactory.getLog(EAIMessageDecoder.class);
	private String interfaceId;
	TranCodeExtractor tranCodeExtractor;
	private Map<String, InterfaceInfo> interfaceMap;
	public void setInterfaceId(String interfaceId) {
		this.interfaceId = interfaceId;
	}
	public void setTranCodeExtractor(TranCodeExtractor tranCodeExtractor) {
		this.tranCodeExtractor = tranCodeExtractor;
	}
	protected Object decode(ChannelHandlerContext ctx, Channel channel,
			Object msg) throws Exception {
		log.info("Tmax ==> ¾î´ðÅÍ, Çì´õ ÆÄ½Ì");
		if (!(msg instanceof ChannelBuffer)) {
			return msg;
		}
		ChannelBuffer buffer = (ChannelBuffer) msg;
		if(interfaceList == null){
			throw new Exception("INTERFACE ID NOT SET");
		}
		if(tranCodeExtractor != null){
			interfaceId = tranCodeExtractor.extract(buffer);
		}
		byte[] all = new byte[buffer.readableBytes()];
		if (log.isDebugEnabled()) {
			buffer.getBytes(0, all);
			log.debug("ENCODER RECV:|" + new String(all) + "|");
		}
		HashMap mapMsg = new HashMap();
		byte[] data = new byte[buffer.readableBytes()];
		buffer.readBytes(data);
		mapMsg.put("DATA", data);

		InterfaceInfo info = interfaceId == null ? interfaceList.get(0):getInterfaceInfo(interfaceId);
		if(info == null){
			throw new Exception("INTERFACE INFO NOT FOUND");
		}
		IndigoSignalResult onSignalResult = new IndigoSignalResult(info, data);

		List<Map<String, Object>> pollDataList = new ArrayList();
		pollDataList.add(mapMsg);
		onSignalResult.setPollResultDataObj(pollDataList);
		if (pollDataList != null) {
			onSignalResult.setResultCount(pollDataList.size());
			log.info("onSinalResult : "  + onSignalResult.getTemplateMessage());
		}
		log.info("Çì´õ ÆÄ½Ì¿Ï·á.");
		return onSignalResult;
	}
	private InterfaceInfo getInterfaceInfo(String interfaceId){
		for (InterfaceInfo info : interfaceList) {
			if(info.getInterfaceId().equals(interfaceId)){
				return info;
			}
		}
		return null;
	}

}
