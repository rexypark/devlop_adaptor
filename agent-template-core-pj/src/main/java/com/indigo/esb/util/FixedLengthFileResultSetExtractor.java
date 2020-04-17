package com.indigo.esb.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

public class FixedLengthFileResultSetExtractor implements ResultSetExtractor<File> {

	final String filePath;

	public FixedLengthFileResultSetExtractor(String filePath) {
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
					int colLength = rsmd.getColumnDisplaySize(i);
					int colType = rsmd.getColumnType(i);
					String val = rs.getString(i);

					// FIXME 타입에 맞게 확인이 필요해.
					switch (colType) {
					case Types.BIGINT:
					case Types.DECIMAL:
					case Types.DOUBLE:
					case Types.FLOAT:
					case Types.INTEGER:
						if (val == null)
							val = "0";
						bw.write(StringUtils.leftPad(val, colLength, '0'));
						break;
					case Types.VARCHAR:
						if (val == null)
							val = " ";
						bw.write(StringUtils.rightPad(val, colLength));
						break;
					}

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
