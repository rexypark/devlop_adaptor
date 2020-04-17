package sqlGen;

import com.indigo.esb.velocity.StandardXmlMapperGenerator;

public class StdOracleGen2 {
	
	public static void main(String[] args) throws Exception {
		genSource();
		genTarget();
	}

	private static void genSource() throws Exception {
		
		StandardXmlMapperGenerator gen = new StandardXmlMapperGenerator();
		
		/**
		 * JDBC 설정
		 */
		gen.setId("tdmadm");
		gen.setPw("tdmadmdevpw");
		gen.setJdbcName("oracle.jdbc.OracleDriver");
		gen.setJdbcUrl("jdbc:oracle:thin:@192.168.0.211:1521:TDEMS");
		gen.setSchemaName("TDMADM"); 			//USER ID
		gen.setTableName("T_IFL_STAB_STUDY");  //TABLE
		
		/**
		 * EAI 기본 설정
		 */
		gen.setIfid("DD_ERP_SMP_002"); 			   //인터페이스 ID
		gen.setType("SOURCE");
		
		/**
		 * EAI 컬럼 지정
		 */
		gen.setStateColumn("EAI_FLAG");   	   //연계 상태 코드 컬럼
		gen.setMessageColumn("EAI_MESSAGE");   //연계 결과 메시지 컬럼
		gen.setTxidColumn("EAI_TX_ID");        //트렌잭션 id 컬럼
		gen.setDateColumn("EAI_DATE");         //연계 시간
		gen.setSuccessCode("S");               //성공시 
		gen.setFailureCode("F");               //실패시
		gen.setProcessingCode("P");            //처리중
		
		gen.generateQuery();
		
	}
	
	private static void genTarget() throws Exception {
		
		StandardXmlMapperGenerator gen = new StandardXmlMapperGenerator();
		
		/**
		 * JDBC 설정
		 */
		gen.setId("O_CMS4JA");
		gen.setPw("O_CMS4JADEV1!");
		gen.setJdbcName("oracle.jdbc.OracleDriver");
		gen.setJdbcUrl("jdbc:oracle:thin:@52.2.42.200:1521:ITQMSDEV");
		gen.setSchemaName("O_CMS4JA"); 			//USER ID
		gen.setTableName("IF_TESTMST");  //TABLE
		
		/**
		 * EAI 기본 설정
		 */
		gen.setIfid("PSD_LITEA_QSDAA_TESTMST_01"); 			   //인터페이스 ID
		gen.setType("TARGET");
		
		/**
		 * EAI 컬럼 지정
		 */
		gen.setStateColumn("EAI_FLAG");   	   //연계 상태 코드 컬럼
		gen.setMessageColumn("EAI_MESSAGE");   //연계 결과 메시지 컬럼
		gen.setTxidColumn("EAI_TX_ID");        //트렌잭션 id 컬럼
		gen.setDateColumn("EAI_DATE");         //연계 시간
		gen.setSuccessCode("S");               //성공시 
		gen.setFailureCode("F");               //실패시
		gen.setProcessingCode("P");            //처리중
		gen.setProcedure("PROCEDURE(#{TX_ID})");
		gen.generateQuery();
		
	}
}
