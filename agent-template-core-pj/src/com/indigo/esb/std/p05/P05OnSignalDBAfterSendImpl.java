package com.indigo.esb.std.p05;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.config.DBInterfaceInfo;
import com.indigo.esb.db.QueryStatementType;

public class P05OnSignalDBAfterSendImpl extends P05OnsignalDBSupport {

	@SuppressWarnings("unchecked")
	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		DBInterfaceInfo dbInterfaceInfo = (DBInterfaceInfo) onSignalResult.getInterfaceInfo();
		String sql = queryProvider.getQueryByInterfaceId(
				dbInterfaceInfo.getInterfaceId(),
				QueryStatementType.SOURCE_UPDATE);
		List<Map<String, Object>> list = (List<Map<String, Object>>) onSignalResult.getPollResultDataObj();
		SqlParameterSource[] batch = SqlParameterSourceUtils
				.createBatch(makeArray(list));
		
		@SuppressWarnings("deprecation")
		int[] result = sjt.batchUpdate(sql, batch);
		onSignalResult.setResultCount(result.length);
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
