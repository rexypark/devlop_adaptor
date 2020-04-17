package com.indigo.esb.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class DelimiterdFileResultSetExtractorTest {

	@Test
	public void paddingTest() throws Exception {
		// BufferedWriter bw = null;
		// String filePath = "./test.txt";
		// File file = new File(filePath);
		// try {
		// bw = new BufferedWriter(new FileWriter(file));
		//
		// while (rs.next()) {
		// for (int i = 1; i < colCnt; i++) {
		// int colLength = rsmd.getColumnDisplaySize(i);
		// int colType = rsmd.getColumnType(i);
		// String val = rs.getString(i);
		//
		// switch (colType) {
		// case Types.BIGINT:
		// case Types.DECIMAL:
		// case Types.DOUBLE:
		// case Types.FLOAT:
		// case Types.INTEGER:
		// if (val == null)
		// val = "0";
		System.out.println(StringUtils.leftPad("20.0", 10, '0'));
		// break;
		// case Types.VARCHAR:
		// if (val == null)
		// val = " ";
		System.out.println(StringUtils.rightPad("a", 10) + "|");
		// break;
		// }
		//
		// }
		// bw.newLine();
		// bw.flush();
		// }
		// } catch (IOException e) {
		// e.printStackTrace();
		// } finally {
		// IOUtils.closeQuietly(bw);
		// }
	}

}
