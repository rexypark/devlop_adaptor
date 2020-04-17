package com.indigo.esb.jms;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;
import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_ID;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.util.Assert;

import com.indigo.esb.config.InterfaceDataType;
public class IndigoDataMappingConverter extends SimpleMessageConverter
		implements InitializingBean {

	private static final Log logger = LogFactory
			.getLog(IndigoDataMappingConverter.class);

	Map<String, BidiMap> mappingInfo;

	@Override
	public Object fromMessage(Message message) throws JMSException,
			MessageConversionException {
		String ifId = message.getStringProperty(ESB_IF_ID);
		BidiMap bidiMap = mappingInfo.get(ifId);
		boolean toBeMapped = false;
		if (bidiMap == null) {
			logger.debug("매핑정보 없음");
		} else {
			toBeMapped = true;
			logger.debug("매핑정보:" + bidiMap);
		}

		String dataType = message.getStringProperty(ESB_IF_DATA_TYPE);
		boolean isReturnedResult = false;
		if (InterfaceDataType.RESULT == InterfaceDataType.valueOf(dataType)) {
			isReturnedResult = true;
		}
		Object obj = super.fromMessage(message);

		if (toBeMapped == false)
			return obj;

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> srcList = (List<Map<String, Object>>) obj;
		List<Map<String, Object>> mappedList = new ArrayList<Map<String, Object>>();
		for (Map<String, Object> srcRow : srcList) {
			logger.debug("매핑전:" + srcRow);
			Map<String, Object> targetRow = map(srcRow, bidiMap,
					isReturnedResult);
			logger.debug("매핑후:" + targetRow);
			mappedList.add(targetRow);
		}
		return mappedList;
	}

	private Map<String, Object> map(Map<String, Object> srcRow,
			BidiMap bidiMap, boolean reverse) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		for (Map.Entry<String, Object> entry : srcRow.entrySet()) {
			String colName = entry.getKey();
			String key = reverse ? (String) bidiMap.getKey(colName)
					: (String) bidiMap.get(colName);
			if (key == null)
				key = colName;
			map.put(key, entry.getValue());
		}
		return map;
	}

	public void setMappingInfo(Map<String, BidiMap> mappingInfo) {
		this.mappingInfo = mappingInfo;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(mappingInfo, "Property 'mappingInfo' is Required");
	}
}
