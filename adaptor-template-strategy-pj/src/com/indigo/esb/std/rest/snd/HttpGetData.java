package com.indigo.esb.std.rest.snd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.db.OnSignalSpacenameDBSupport;
import com.indigo.esb.config.RestInterfaceInfo;

/**
 * Rest Get
 * 
 * @author clupine
 *
 */
public class HttpGetData extends OnSignalSpacenameDBSupport {

	HttpClient client = new DefaultHttpClient();

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		@SuppressWarnings("rawtypes")
		List<Map<String, Object>> pollDataList = new ArrayList<Map<String, Object>>();

		RestInterfaceInfo<Map<String, Object>> info = (RestInterfaceInfo) onSignalResult.getInterfaceInfo();
		List<List<NameValuePair>> paramGroupList = info.getParamList();

		for (List<NameValuePair> paramList : paramGroupList) {

			String queryStr = URLEncodedUtils.format(paramList,	info.getEncoding());
			HttpGet request = new HttpGet(info.getUrl() + "?" + queryStr);
			HttpResponse response = client.execute(request);
			HttpEntity entity = response.getEntity();

			String xml = null;
			
			if (entity != null) {
				xml = IOUtils.toString(entity.getContent());
			}
			pollDataList.addAll(info.getListConvertor().convert(xml));

		}

		onSignalResult.setPollResultDataObj(pollDataList);
		
		if (pollDataList != null) {
			onSignalResult.setResultCount(pollDataList.size());
			log.info("onSinalResult : " + onSignalResult.getTemplateMessage());
		}
	}

}
