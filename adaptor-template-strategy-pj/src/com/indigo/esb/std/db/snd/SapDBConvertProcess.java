package com.indigo.esb.std.db.snd;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.DBInterfaceInfo;

/**
 * 사용
 * 
 * @author clupine
 *
 */
public class SapDBConvertProcess extends OnSignalSpacenameDBSupport {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource
	Map<String, List<String>> sapDBConvertKeyMap;
	
	String encodingType = "euc-kr";
	
	public void setEncodingType(String encodingType) {
		this.encodingType = encodingType;
	}

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();

		List<String> convertKeyList = sapDBConvertKeyMap.get(dbInterfaceInfo.getInterfaceId());
		
		if(convertKeyList == null){
			return;
		}
		
		@SuppressWarnings("unchecked")
		final List<Map<String, Object>> list = (List<Map<String, Object>>) onSignalResult.getPollResultDataObj();

		log.debug("convert  Start . " + dbInterfaceInfo.getInterfaceId());

		for (Map<String, Object> map : list) {
			for (String key : convertKeyList) {
				Object data = map.get(key);
				if (data instanceof String) {
					String hexStr = (String) data;
					if((hexStr.length() != 0)  && hexStr.length() % 2 == 0){
						byte[] cvtBytes = hexToByteArray(hexStr, key);
						map.put(key, new String(cvtBytes,Charset.forName(encodingType)));
					}
				}else if(data instanceof byte[]){
					map.put(key, new String((byte[])data,Charset.forName(encodingType)));
				}
			}
		}
	}

	public byte[] hexToByteArray(String hex, String key) throws NumberFormatException {
		if (hex == null || hex.length() == 0) {
			return null;
		}

		byte[] ba = new byte[hex.length() / 2];
		
		try{
			for (int i = 0; i < ba.length; i++) {
				ba[i] = (byte) Integer
						.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
			}
		}catch(NumberFormatException e){
			logger.info("key : " + key + ", hex : " + hex);
			throw e;
		}
		return ba;
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
