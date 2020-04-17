package com.indigo.esb.std.dfile.handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.eclipse.jetty.util.log.Log;
import org.mybatis.spring.SqlSessionTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.db.QueryStatementType;

public class DB2FileWriteHandler implements ResultHandler {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	File file;
	FileWriter fw;
	boolean first = true;
	SqlSessionTemplate sqlSession;
	long count = 0;
	int updateCnt = 0;
	Map headerMap;
	String SqlId;
	
	public DB2FileWriteHandler(File file, SqlSessionTemplate sqlSession, Map headerMap, String SqlId) {
		logger.debug("=======================test1");
		this.file = file;
		this.sqlSession = sqlSession;
		this.headerMap = headerMap;
		this.SqlId = SqlId;
	}
	
	@Override
	public void handleResult(ResultContext context) {
		logger.debug("=======================test2");
		if(first){
			try {
				file.createNewFile();
			fw = new FileWriter(file);
			} catch (IOException e) {
			}
			first = false;
		}
		
		try {
			Map dataMap = (Map) context.getResultObject() ;
			fw.write(dataMap.toString()+"\n");
			fw.flush();
//			IOUtils.write(data, output);
			count++;
			
			dataMap.putAll(headerMap);
			logger.debug("data : " + dataMap.toString());
			updateCnt += sqlSession.update(SqlId, dataMap);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int update(){
		if (sqlSession.getExecutorType() == ExecutorType.BATCH) {
			logger.info("AfterSend Batch Update Count : " + sqlSession.flushStatements().get(0).getUpdateCounts().length);
		} else {
			logger.info("AfterSend Update Count : " + updateCnt);
		}
		return updateCnt;
	}
	
	public long getCount() {
		return count;
	}
	
	public void close(){
		try {
			fw.close();
		} catch (IOException e) {
		}
	}

}
