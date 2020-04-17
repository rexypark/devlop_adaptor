package com.indigo.esb.std.p02;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBRowHandlerUpdate implements ResultHandler {
	public int cnt;
	protected String spitChar;
	protected FileWriter fw;
	public String tx_id;
	public String path;
	public File file;
	public SqlSession sqlSession;
	public String sql_update;
	protected final Logger log = LoggerFactory.getLogger(getClass());

	public DBRowHandlerUpdate(String path, String spitChar, String tx_id, String sql_update, SqlSession sqlSession) throws IOException {
		this.tx_id = tx_id;
		this.path = path;
		this.cnt = 0;
		this.file = new File(path, tx_id);
		this.spitChar = spitChar;
		this.fw = new FileWriter(file);
		this.sqlSession = sqlSession;
		this.sql_update = sql_update;
		log.info("### File Full Path  : " + file.getAbsolutePath());
	}

	@Override
	public void handleResult(ResultContext map) {
		if (map.getResultObject() != null) {
			try {
				Map<String, Object> dataMap = (Map<String, Object>) map.getResultObject();
				Iterator<String> column = dataMap.keySet().iterator();
				if (cnt == 0) {

					StringBuffer sb = new StringBuffer();
					while (column.hasNext()) {
						sb.append(column.next());
						if (column.hasNext())
							sb.append(spitChar);
						log.info("sb:" + sb.toString()); 
					}
					fw.write(sb.toString() + "\n");
					log.info("### File Key : " + sb.toString());
				}
				Iterator<String> value = dataMap.keySet().iterator();
				StringBuffer sb = new StringBuffer();
				while (value.hasNext()) {
					sb.append(dataMap.get(value.next()));
					if (value.hasNext())
						sb.append(spitChar);
				}
				fw.write(sb.toString() + "\n");
				log.info("sql_update : " + sql_update);
				if(dataMap != null && !dataMap.isEmpty()){
					sqlSession.update(sql_update, dataMap);
				}
				cnt++;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	public FileWriter getFileWriter() {
		return this.fw;
	}

	public int getTotalRowCount() {
		return this.cnt;
	}

	public File getDBFile() {
		return this.file;
	}

}
