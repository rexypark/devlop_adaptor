import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class JDBCTest {

	public static void main(String[] args) {

		String driver = "com.edb.Driver";
		String url = "jdbc:edb://10.188.165.57:5444/g4kcipdb";
		String user = "indigo";
		String password = "indigo";

		String tbName = "TB_PNZ90_IF01"; //테이블 이름이 대소문자 가리는지? 

		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		String temparary = null;

		// Establish the connection.
		try {
			Class.forName(driver);
			con = DriverManager.getConnection(url, user, password);
			DatabaseMetaData meta = con.getMetaData();
			String dbUserName = meta.getUserName();
			System.out.println("###\tUserName => " + dbUserName + "###\ttbName => " + tbName);

			rs = meta.getPrimaryKeys(null, null, tbName);

			// rs = meta.getPrimaryKeys(null, user, tbName); //schema  이름을 꼭 넣어야 하는지

			while (rs.next()) {
				temparary = temparary + rs.getString(4).trim() + "||";
			}
			temparary = temparary.substring(0, temparary.lastIndexOf("||"));
		} catch (Exception e) {
			System.out.println("XXXXXX\tgetPk fail\tXXXXXX:" + e.toString());
			e.printStackTrace();
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (Exception e) {
				}
			if (stmt != null)
				try {
					stmt.close();
				} catch (Exception e) {
				}
			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
				}
		}
		System.out.println("###\ttemparary = " + temparary);
	}

}