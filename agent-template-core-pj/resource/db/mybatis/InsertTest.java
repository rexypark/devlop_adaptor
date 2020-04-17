package db.mybatis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class InsertTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			// Connection con =
			// DriverManager.getConnection("jdbc:Altibase://127.0.0.1:20300/asdfas"
			// +
			// "", "snd", "snd");
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@192.168.56.101:1521:xe", "snd", "snd");
			// Connection con =
			// DriverManager.getConnection("jdbc:oracle:thin:@10.1.3.19:1521:xe",
			// "snd", "snd");
			PreparedStatement pst = null;
			pst = con.prepareStatement(" delete from snd");
			pst.executeUpdate();

			pst = con
					.prepareStatement(" INSERT INTO SND ( LAST_CHANGE_ILSI, ESB_TRST_SER, ESB_TRST_FG, FILE_NM, DATAS, DATA_SIZE, USR_NAME  ) VALUES ( sysdate , ? , ? , ? , ?, ?, ?)");
			for (int i = 0; i < 3; i++) {
				pst.setString(1, "" + i);
				pst.setString(2, "N");
				pst.setString(3, "test.xlsx");
				pst.setString(4, "askfjkaf_" + i);
				pst.setString(5, "");
				pst.setString(6, "lee jae hee");
				pst.addBatch();
			}
			pst.executeBatch();
			//
			// // pst = con.prepareStatement("select * from rcv");
			// // ResultSet rs = pst.executeQuery();
			// // while(rs.next()){
			// //
			// System.out.println("=============================================");
			// // byte[] a = rs.getBytes("ZZZ");
			// // System.out.println("-------euckr-"+new
			// String(a)+"--------------------------");
			// //
			// System.out.println("=============================================");
			// // }
			con.commit();
			pst.close();
			// if(con != null)
			// con.close();
			// //
			// // byte[] ar = FileUtils.readFileToByteArray(new
			// File("c:/test.txt"));
			// // System.out.println(new String(ar, "MS949"));
			// //
			// // String enc = new
			// java.io.OutputStreamWriter(System.out).getEncoding();
			// //
			// // System.out.println("default encoding = " + enc);
			//

			System.out.println("ends" + con.getMetaData().getUserName());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}
}
