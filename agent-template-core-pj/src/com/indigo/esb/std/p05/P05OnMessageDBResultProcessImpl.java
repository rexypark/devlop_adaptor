package com.indigo.esb.std.p05;

import static com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants.ESB_IF_DATA_TYPE;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.config.InterfaceDataType;
import com.indigo.esb.db.QueryStatementType;

public class P05OnMessageDBResultProcessImpl extends P05OnMessageDBSupport {
	
	@Override
	public void process(IndigoMessageResult indigoMessageResult)
			throws Exception {
		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) indigoMessageResult.getInterfaceInfo();

		List<Map<String, Object>> result = (List<Map<String, Object>>) indigoMessageResult.getDataObj();

		String sql = null;
		Map<String, String> headerMap = indigoMessageResult.getProperties().getHeaderInfoMap();
		String data_type = headerMap.get(ESB_IF_DATA_TYPE);
		switch (InterfaceDataType.valueOf(data_type)) {
		case DATA:
			sql = queryProvider.getQueryByTableName(dbInterfaceInfo.getSourceTableName(),
					QueryStatementType.MERGE);
			break;
		case RESULT:
			sql = queryProvider.getQueryByTableName(dbInterfaceInfo.getSourceTableName(),
					QueryStatementType.SOURCE_RESULT_UPDATE);
			break;
		default:
			throw new IllegalArgumentException(
					"수신한 Message 'esb_if_data_type' property는 반드시 있어야합니다. [InterfaceDataType.DATA | InterfaceDataType.RESULT]");
		}
		SqlParameterSource[] batch = SqlParameterSourceUtils
				.createBatch(makeArray(result));
		int[] affectedCounts = sjt.batchUpdate(sql, batch);

		log.info(data_type + " 수신 처리 완료:" + dbInterfaceInfo.getInterfaceId());
	}
	
	protected Map[] makeArray(List<Map<String, Object>> list) {
		Map[] valueMaps = new Map[list.size()];
		int i = 0;
		for (Map<String, Object> map : list) {
			valueMaps[i++] = map;
		}
		return valueMaps;
	}

}
