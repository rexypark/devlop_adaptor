package sqlGen;

import java.util.HashMap;
import java.util.Map;

import com.indigo.esb.velocity.StandardXmlMapperGenerator;
import com.indigo.esb.velocity.StandardXmlMapperGenerator2;

/**
 * @author ikarus2k
 *
 */
public class StdOracleMultiTableGen {
	
	public static void main(String[] args) throws Exception {
		genSource();
		genTarget();
	}

	private static void genSource() throws Exception {
		
		StandardXmlMapperGenerator2 gen = new StandardXmlMapperGenerator2();
		
		/**
		 * JDBC 설정
		 */
		gen.setId("DBO");
		gen.setPw("DBO");
		gen.setJdbcName("oracle.jdbc.OracleDriver");
		gen.setJdbcUrl("jdbc:oracle:thin:@127.0.0.1:32769:xe");
		gen.setSchemaName("DBO"); 			//USER ID
		gen.setTableName("SC_TB_ESB01,SC_TB_ESB02,SC_TB_ESB03");  //TABLE
		
		/**
		 * EAI 기본 설정
		 */
		gen.setIfid("IF_ACC_RACC_M_R_001,IF_ACC_RACC_M_R_002,IF_ACC_RACC_M_R_003"); 			   //인터페이스 ID
		gen.setType("SOURCE");
		
		/**
		 * EAI 컬럼 지정
		 */
		gen.setStateColumn("ESB_STATE_CD");   	   //연계 상태 코드 컬럼
//		gen.setMessageColumn("EAI_MESSAGE");   //연계 결과 메시지 컬럼
		gen.setTxidColumn("ESB_TX_ID");        //트렌잭션 id 컬럼
		gen.setDateColumn("ESB_TIME");         //연계 시간
		gen.setSuccessCode("S");               //성공시 
		gen.setFailureCode("F");               //실패시
		gen.setProcessingCode("P");            //처리중
		
		gen.generateQuery();
		
	}
	
	private static void genTarget() throws Exception {
		
		StandardXmlMapperGenerator2 gen = new StandardXmlMapperGenerator2();
		
		/**
		 * JDBC 설정
		 */
		gen.setId("SCRJESB");
		gen.setPw("SCRJESB");
		gen.setJdbcName("oracle.jdbc.OracleDriver");
		gen.setJdbcUrl("jdbc:oracle:thin:@127.0.0.1:32769:xe");
		gen.setSchemaName("SCRJESB"); 			//USER ID
		gen.setTableName("TB_ESB01,TB_ESB02,TB_ESB03");  //TABLE
		
		/**
		 * EAI 기본 설정
		 */
		gen.setIfid("IF_ACC_RACC_M_R_001,IF_ACC_RACC_M_R_002,IF_ACC_RACC_M_R_003"); 			   //인터페이스 ID
		gen.setType("TARGET");
		
		/**
		 * EAI 컬럼 지정
		 */
		gen.setStateColumn("ESB_STATE_CD");   	   //연계 상태 코드 컬럼
//		gen.setMessageColumn("EAI_MESSAGE");   //연계 결과 메시지 컬럼
		gen.setTxidColumn("ESB_TX_ID");        //트렌잭션 id 컬럼
		gen.setDateColumn("ESB_TIME");         //연계 시간
		gen.setSuccessCode("S");               //성공시 
		gen.setFailureCode("F");               //실패시
		gen.setProcessingCode("P");            //처리중
//		Map<String,String> map = new HashMap<String,String>();
//		map.put("TN_EAI_TRNS_ACPT", "PKG_IFDT085.SP_IF_EAI_TRNS_ACPT()");
//		map.put("TN_EAI_DEP_RECV", "PKG_IFDT086.SP_IF_EAI_DEP_RECV()");
//		map.put("TN_EAI_STATS", "PKG_IFDT087.SP_IF_EAI_STATS()");
//		map.put("TN_EAI_DSPS_POT", "PKG_IFDT091.SP_IF_EAI_DSPS_POT()");
//		gen.setProcedureMap(map);
		gen.generateQuery();
		
	}
}
