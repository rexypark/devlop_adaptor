package sqlGen;

import com.indigo.esb.velocity.StandardXmlMapperGenerator;

/**
 * 사용하지 말것 다시 만들어야함
 * @author clupine-mb
 *
 */
public class AsisStdOracleGen {
	
	public static void main(String[] args) throws Exception {
		//genSource();
		genTarget();
	}

	private static void genSource() throws Exception {
		
		StandardXmlMapperGenerator gen = new StandardXmlMapperGenerator();
		
		/**
		 * JDBC 설정
		 */
		gen.setId("ops$penlims");
		gen.setPw("penlims1");
		gen.setJdbcName("oracle.jdbc.OracleDriver");
		gen.setJdbcUrl("jdbc:oracle:thin:@52.2.42.173:1521:qap");
		gen.setSchemaName("INF_EAI"); 			//USER ID
		gen.setTableName("SAPSR3.ZSDTI702");  //TABLE
		
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
		gen.setId("smp");
		gen.setPw("smp1!");
		gen.setJdbcName("oracle.jdbc.OracleDriver");
		gen.setJdbcUrl("jdbc:oracle:thin:@52.2.42.214:1521:ehns");
		gen.setSchemaName("SMP"); 			//USER ID
		gen.setTableName("IF_ZSDTI702");  //TABLE
		
		/**
		 * EAI 기본 설정
		 */
		gen.setIfid("DD_ERP_SMP_002"); 			   //인터페이스 ID
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
