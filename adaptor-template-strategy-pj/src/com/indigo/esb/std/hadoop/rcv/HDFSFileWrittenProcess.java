package com.indigo.esb.std.hadoop.rcv;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.db.OnMessageSpacenameDBSupport;
import com.indigo.esb.config.HiveInterfaceInfo;
import com.indigo.esb.jms.IndigoHeaderJMSPropertyConstants;

/**
 * 데이터를 받아서 HDFS 파일로 기록
 * 
 * @author clupine
 *
 */
public class HDFSFileWrittenProcess extends OnMessageSpacenameDBSupport {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void process(IndigoMessageResult indigoMessageResult)
			throws Exception {

		HiveInterfaceInfo ifInfo = (HiveInterfaceInfo) indigoMessageResult
				.getInterfaceInfo();

		@SuppressWarnings("unchecked")
		List<Map<String, Object>> rowMapList = (List<Map<String, Object>>) indigoMessageResult.getDataObj();

		Set<String> keySet = ifInfo.getKeySet();

		logger.info("Data Size : "+ rowMapList.size());
		logger.info("HDFS Name : "+ ifInfo.getFsName());
		logger.info("HDFS URL  : "+ ifInfo.getHdfsUrl());
		FileSystem dfs = null;
		FSDataOutputStream  fsos = null;
			
		try {
			
		Configuration conf = new Configuration();
		conf.set(ifInfo.getFsName(), ifInfo.getHdfsUrl());
		dfs = FileSystem.get(conf);
		
		String tx_id = indigoMessageResult.getProperties().getHeaderInfoMap().get(IndigoHeaderJMSPropertyConstants.ESB_TX_ID);
		
		Path savePath = new Path(ifInfo.getHdfsSaveDir()+"/"+tx_id);
		
		 fsos = dfs.create(savePath);
		boolean sampleBool = true;
			for (Map<String, Object> rowMap : rowMapList) {
//				logger.info("DATA ROW : " + rowMap);
				StringBuilder sb = new StringBuilder();
				for (String key : keySet) {
					Object value = rowMap.get(key);
					if(value != null){
						sb.append(rowMap.get(key).toString()).append(",");
					}else{
						sb.append(",");
					}
				}
				sb.delete(sb.lastIndexOf(","), sb.lastIndexOf(",")+1);
				if(sampleBool){
					logger.info("First Data : " + sb.toString());
					sampleBool = false;
				}
				fsos.write(sb.toString().getBytes(Charset.forName("UTF-8")));
				fsos.write("\n".getBytes());
				fsos.flush();
			}
			logger.info("Saved File HDFS URL : {} " ,savePath.toUri());
		} catch (Exception e) {
			logger.error("HDFS Writen Error : ",e);
		}finally{
			if(fsos != null){
				fsos.close();
			}
		}
	}
}
