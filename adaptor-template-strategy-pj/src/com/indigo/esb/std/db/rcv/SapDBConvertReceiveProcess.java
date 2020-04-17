package com.indigo.esb.std.db.rcv;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;

/**
 * 한글 필드 String to hexStr convert
 * 
 * @author clupine
 *
 */
public class SapDBConvertReceiveProcess extends OnMessageSpacenameDBSupport {

	@Resource
	Map<String, List<String>> sapDBConvertKeyMap;
	
	String encodingType = null;
	
	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}
	
	@Override
	public void process(IndigoMessageResult indigoMessageResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		List<String> convertKeyList = sapDBConvertKeyMap.get(dbInterfaceInfo.getInterfaceId());

		if(convertKeyList == null){
			return;
		}
		
		log.debug("convert  Start . " + dbInterfaceInfo.getInterfaceId());
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> list = (List<Map<String, Object>>) indigoMessageResult.getDataObj();
		
		for (Map<String, Object> map : list) {
			for (String key : convertKeyList) {
				Object data = map.get(key);
				if (data instanceof String) {
					String hanStr = (String) data;
					if(!(hanStr == null || hanStr.length() <= 0)){
					String hexStr = null;
						if(encodingType==null){
							hexStr = byteArrayToHex(hanStr.getBytes());
						}else{
							hexStr = byteArrayToHex(hanStr.getBytes(Charset.forName(encodingType)));
						}
						map.put(key, hexStr);
					}
				}
			}
		}
	}
	
	
	public String byteArrayToHex(byte[] ba) {
		if (ba == null || ba.length == 0) {
			return null;
		}

		StringBuffer sb = new StringBuffer(ba.length * 2);
		String hexNumber;
		for (int x = 0; x < ba.length; x++) {
			hexNumber = "0" + Integer.toHexString(0xff & ba[x]);

			sb.append(hexNumber.substring(hexNumber.length() - 2));
		}
		return sb.toString();
	}
	
}
