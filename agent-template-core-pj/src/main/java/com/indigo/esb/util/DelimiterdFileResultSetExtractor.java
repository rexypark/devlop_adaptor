package com.indigo.esb.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.apache.commons.io.IOUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DelimiterdFileResultSetExtractor implements ResultSetExtractor<File> {

	char seperatorChar;
	final String filePath;

	public DelimiterdFileResultSetExtractor(char seperatorChar, String filePath) {
		this.seperatorChar = seperatorChar;
		this.filePath = filePath;

	}

	@Override
	public File extractData(ResultSet rs) throws SQLException, DataAccessException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int colCnt = rsmd.getColumnCount();
		BufferedWriter bw = null;
		File file = new File(filePath);
		try {
			bw = new BufferedWriter(new FileWriter(file));

			while (rs.next()) {
				for (int i = 1; i < colCnt; i++) {
					
					String val = rs.getString(i);
					bw.write(val);
					bw.write(seperatorChar);
				}
				bw.newLine();
				bw.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(bw);
		}
		return file;
	}

}
