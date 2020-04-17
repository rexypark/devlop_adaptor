package msggen;

import java.util.Map;

import jeus.util.LinkedHashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.mb.mci.common.transducer.tool.TypeConversion;
import com.mb.mci.common.util.DateUtil;
import com.mb.mci.common.util.HexViewer;

/**
 * @author clupine-mb
 */
public class MessageGen {
	
	public static void main(String[] args) throws Exception {
		BAF0010();
//		BAF0020();
//		BAF0030();
//		BAF0040();
//		BAF0050();
//		SYS_MONITORING();
	}

	private static void BAF0010() throws Exception {
		
		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
		
		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6)); 
		buff.writeBytes(TypeConversion.toTypeBytes("BAF0010", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST00112345523", "A", 32)); //uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); //FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); //송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); //에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); //에러코드
		
		/**
		 * Body Context
		 */
		Map<String,String> bodyMap = new LinkedHashMap();
		
		
		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMAGCRTYCD", "01");                 //2              C       영상수집자산유형코드
		bodyMap.put("S1_MSNNO", "0002");                      //20             V       임무번호
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ", "0003");        //22             I       영상판독보고서순번
		bodyMap.put("S1_DSTRBTN_YN", "Y");                 //1              C       배포여부
		bodyMap.put("S1_DN", "0005");                         //30             V       문서번호
		bodyMap.put("S1_MSN_TTL", "0006");                    //300            V       임무명칭
		bodyMap.put("S1_MSN_START_DTTM", null );                 //　             	   DT      임무시작일시
		bodyMap.put("S1_MSN_FNSH_DTTM", null );                  //　            		   DT      임무종료일시
		bodyMap.put("S1_CLTN_SNSR_TYPE_NM", "0009");              //50             V       수집감지기유형명
		bodyMap.put("S1_DCPHRMT_PART_DCK_NM", "0010");            //50             V       판독부분DECK명
		bodyMap.put("S1_IMAGE_QLTY_EXPLN", "0011");               //100            V       영상질설명
		bodyMap.put("S1_SHTNG_ANG", "0012");                      //10             F       촬영각도
		bodyMap.put("S1_MSN_ROUTE", "0013");                      //100            V       임무경로
		bodyMap.put("S1_MSN_DTL_ROUTE_EXPLN", "0014");            //1000           V       임무상세경로설명
		bodyMap.put("S1_MSN_TRVLV", "0015");                      //8,2            F       임무최저고도
		bodyMap.put("S1_MSN_HGHST_ALT", "0016");                  //8,2            F       임무최고고도
		bodyMap.put("S1_MSN_LWST_VLCT", "0017");                  //8,2            F       임무최저속도
		bodyMap.put("S1_MSN_HGHST_VLCT", "0018");                 //8,2            F       임무최고속도
		bodyMap.put("S1_RPRT_SMRY_HANGL_CTNT", "0019");           //2000           V       보고서요약한글내용
		bodyMap.put("S1_RPRT_SMRY_ENGLSH_CTNT", "0020");          //2000           V       보고서요약영문내용
		bodyMap.put("S1_RGST_DTTM", "0021");                      //　           		   D       등록일시
		bodyMap.put("S1_FNL_RNWL_DTTM", "0022");                  //　           		   D       최종갱신일시
		bodyMap.put("S1_REGSTR_SRVNO", "0023");                   //15             V       등록자군번
		bodyMap.put("S1_FNL_RNWPS_SRVNO", "0024");                //15             V       최종갱신자군번
		
		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();
		
		for (String field : bodyMap.keySet()) {
			 fieldName.append(field).append("|");
			 Object value =   bodyMap.get(field);
			 if(value!=null){
				 data.append(value.toString()).append("|");
			 }else{
				 data.append("|");
			 }
		}
		
		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length-1];
		byte[] dataBytes = new byte[data.toString().getBytes().length-1];
		System.arraycopy(fieldName.toString().getBytes(), 0,  fieldBytes, 0,fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0,  dataBytes, 0,dataBytes.length);
		
		
		byte[] fieldNameLen = TypeConversion.toTypeBytes(String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(String.valueOf(dataBytes.length), "N", 6);
		
		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);
		
		
		int lengthField = buff.readableBytes()-6;
		buff.setBytes(0, TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));
		
		
		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0,viewBytes);
//		System.out.println(HexViewer.view(viewBytes));
		System.out.println("["+new String(viewBytes)+"]");
	}
	
	/**
	 * 첨부파일 포함
	 * @throws Exception
	 */
	private static void BAF0020() throws Exception {
		
		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
		
		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6)); 
		buff.writeBytes(TypeConversion.toTypeBytes("BAF0020", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("BAF002012345678", "A", 32)); //uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("image1.jpg", "A", 128)); //FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); //송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); //에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); //에러코드
		
		/**
		 * Body Context
		 */
		Map<String,String> bodyMap = new LinkedHashMap();

		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMGIDNO             ".trim(), "0001");    
		bodyMap.put("S1_IMGTYCD             ".trim(), "02");      
		bodyMap.put("S1_IMAGCRTYCD          ".trim(), "03");      
		bodyMap.put("S1_MSNNO               ".trim(), "0004");    
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ ".trim(), "0005");    
		bodyMap.put("S1_INDCTN_SVNC_TGT_BENO".trim(), "0006");    
		bodyMap.put("S1_REFRC_RGST_YN       ".trim(), "Y" );      
		bodyMap.put("S1_RGST_DTTM           ".trim(), null );     
		bodyMap.put("S1_FNL_RNWL_DTTM       ".trim(), "0009");    
		bodyMap.put("S1_REGSTR_SRVNO        ".trim(), "0010");    
		bodyMap.put("S1_FNL_RNWPS_SRVNO     ".trim(), "0011");    
		
		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();
		
		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value =   bodyMap.get(field);
			if(value!=null){
				data.append(value.toString()).append("|");
			}else{
				data.append("|");
			}
		}
		
		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length-1];
		byte[] dataBytes = new byte[data.toString().getBytes().length-1];
		System.arraycopy(fieldName.toString().getBytes(), 0,  fieldBytes, 0,fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0,  dataBytes, 0,dataBytes.length);
		
		byte[] fieldNameLen = TypeConversion.toTypeBytes(String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(String.valueOf(dataBytes.length), "N", 6);
		
		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);
		
		int lengthField = buff.readableBytes()-6;
		buff.setBytes(0, TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));
		
		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0,viewBytes);
//		System.out.println(HexViewer.view(viewBytes));
		System.out.println("["+new String(viewBytes)+"]");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private static void BAF0030() throws Exception {
		
		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
		
		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6)); 
		buff.writeBytes(TypeConversion.toTypeBytes("BAF0030", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST00112345524", "A", 32)); //uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); //FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); //송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); //에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); //에러코드
		
		/**
		 * Body Context
		 */
		Map<String,String> bodyMap = new LinkedHashMap();
		
		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMAGCRTYCD             ".trim(), "01");      //   C    2
		bodyMap.put("S1_MSNNO                  ".trim(), "002");      //   V    20
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ    ".trim(), "003");      //   I    22
		bodyMap.put("S1_INDCTN_SVNC_TGT_BENO   ".trim(), "004");      //   V    10
		bodyMap.put("S1_IDTF_SEQ               ".trim(), "005");      //   I    22
		bodyMap.put("S1_OBTYPECD               ".trim(), "006");      //   C    3
		bodyMap.put("S1_IDTF_DATE              ".trim(), "007");      //   V    8
		bodyMap.put("S1_IDTF_DNT               ".trim(), "008");      //   D    　
		bodyMap.put("S1_IDTF_CNTUNT            ".trim(), "009");      //   I    7
		bodyMap.put("S1_VLDT_EXPLN             ".trim(), "0010");      //   V    100
		bodyMap.put("S1_HANGL_EQP_TTL          ".trim(), "0020");      //   V    100
		bodyMap.put("S1_ENGLSH_EQP_TTL         ".trim(), "0030");      //   V    100
		bodyMap.put("S1_ENMYFAMC               ".trim(), "0040");      //   C    8
		bodyMap.put("S1_IDTF_PLC_HANGL_TTL     ".trim(), "0050");      //   V    100
		bodyMap.put("S1_IDTF_PLC_ENGLSH_TTL    ".trim(), "0060");      //   I    100
		bodyMap.put("S1_IDTF_CTNT              ".trim(), "0070");      //   V    4000
		bodyMap.put("S1_RGST_DTTM              ".trim(), "0080");      //   D
		bodyMap.put("S1_FNL_RNWL_DTTM          ".trim(), "0090");      //   D
		bodyMap.put("S1_REGSTR_SRVNO           ".trim(), "0100");      //   V    15
		bodyMap.put("S1_FNL_RNWPS_SRVNO        ".trim(), "0101");      //   V    15
		
		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();
		
		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value =   bodyMap.get(field);
			if(value!=null){
				data.append(value.toString()).append("|");
			}else{
				data.append("|");
			}
		}
		
		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length-1];
		byte[] dataBytes = new byte[data.toString().getBytes().length-1];
		System.arraycopy(fieldName.toString().getBytes(), 0,  fieldBytes, 0,fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0,  dataBytes, 0,dataBytes.length);
		
		byte[] fieldNameLen = TypeConversion.toTypeBytes(String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(String.valueOf(dataBytes.length), "N", 6);
		
		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);
		
		int lengthField = buff.readableBytes()-6;
		buff.setBytes(0, TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));
		
		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0,viewBytes);
//		System.out.println(HexViewer.view(viewBytes));
		System.out.println("["+new String(viewBytes)+"]");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private static void BAF0040() throws Exception {
		
		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
		
		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6)); 
		buff.writeBytes(TypeConversion.toTypeBytes("BAF0040", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST00112345524", "A", 32)); //uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); //FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); //송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); //에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); //에러코드
		
		/**
		 * Body Context
		 */
		Map<String,String> bodyMap = new LinkedHashMap();
		
		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMAGCRTYCD             ".trim(), "01");     //  C         2
		bodyMap.put("S1_MSNNO                  ".trim(), "02");     //  V         20
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ    ".trim(), "03");     //  I         22
		bodyMap.put("S1_INDCTN_SVNC_TGT_BENO   ".trim(), "04");     //  V         10
		bodyMap.put("S1_ADDTN_TGT_YN           ".trim(), "Y");     //  C         1
		bodyMap.put("S1_MN_IDTF_YN             ".trim(), "Y");     //  C         1
		bodyMap.put("S1_IDTF_TGT_SEQ           ".trim(), "01224");     //  I         22
		bodyMap.put("S1_IDTF_TGTPT_NM          ".trim(), "008");     //  V         100
		bodyMap.put("S1_ACTVT_STATE_TYPE_EXPLN ".trim(), "009");     //  V         100
		bodyMap.put("S1_ACTVT_CHG_TYPE_EXPLN   ".trim(), "010");     //  V         100
		bodyMap.put("S1_ODNR_IDTF_CTNT         ".trim(), "011");     //  V         500
		bodyMap.put("S1_HANGL_ACTVT_CTNT       ".trim(), "012");     //  V         4000
		bodyMap.put("S1_ENGLSH_ACTVT_CTNT      ".trim(), "013");     //  V         500
		bodyMap.put("S1_ETDVSCD                ".trim(), "0");     //  C         1
		bodyMap.put("S1_ENMY_CORPS_TYPE_EXPLN  ".trim(), "015");     //  V         100
		bodyMap.put("S1_TGSTCD                 ".trim(), "0016");     //  C         4
		bodyMap.put("S1_DTL_TGSTCD             ".trim(), "0017");     //  C         4
		bodyMap.put("S1_INSTLTN_HANGL_TTL      ".trim(), "018");     //  V         100
		bodyMap.put("S1_INSTLTN_ENGLSH_TTL     ".trim(), "019");     //  V         100
		bodyMap.put("S1_GGPFT_COORD            ".trim(), "020");     //  V         50
		bodyMap.put("S1_MGRS_COORD             ".trim(), "021");     //  V         15
		bodyMap.put("S1_FKSGNCD                ".trim(), "022");     //  C         5
		bodyMap.put("S1_NCB                    ".trim(), "02");     //  C         2
		bodyMap.put("S1_RGST_DTTM              ".trim(), "024");     //  D         
		bodyMap.put("S1_FNL_RNWL_DTTM          ".trim(), "025");     //  D         
		bodyMap.put("S1_REGSTR_SRVNO           ".trim(), "026");     //  V         15
		bodyMap.put("S1_FNL_RNWPS_SRVNO        ".trim(), "027");     //  V         15
		
		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();
		
		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value =   bodyMap.get(field);
			if(value!=null){
				data.append(value.toString()).append("|");
			}else{
				data.append("|");
			}
		}
		
		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length-1];
		byte[] dataBytes = new byte[data.toString().getBytes().length-1];
		System.arraycopy(fieldName.toString().getBytes(), 0,  fieldBytes, 0,fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0,  dataBytes, 0,dataBytes.length);
		
		byte[] fieldNameLen = TypeConversion.toTypeBytes(String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(String.valueOf(dataBytes.length), "N", 6);
		
		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);
		
		int lengthField = buff.readableBytes()-6;
		buff.setBytes(0, TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));
		
		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0,viewBytes);
//		System.out.println(HexViewer.view(viewBytes));
		System.out.println("["+new String(viewBytes)+"]");
	}
	
	/**
	 * 
	 * @throws Exception
	 */
	private static void BAF0050() throws Exception {
		
		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
		
		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6)); 
		buff.writeBytes(TypeConversion.toTypeBytes("BAF0050", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST00112345524", "A", 32)); //uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("image1.jpg", "A", 128)); //FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); //송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); //에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); //에러코드
		
		/**
		 * Body Context
		 */
		Map<String,String> bodyMap = new LinkedHashMap();
		
		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMAGCRTYCD           ".trim(), "01");           //      C      2
		bodyMap.put("S1_MSNNO                ".trim(), "0002");           //      V      20
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ  ".trim(), "0003");           //      I      22
		bodyMap.put("S1_INDCTN_SVNC_TGT_BENO ".trim(), "0004");           //      V      10
		bodyMap.put("S1_DTA_SEQ              ".trim(), "0005");           //      I      22
		bodyMap.put("S1_IMAGE_IDTF_DATE      ".trim(), "0006");           //      V      8
		bodyMap.put("S1_PRDCN_CD_TYPENO      ".trim(), "007");           //      V      3
		bodyMap.put("S1_IMAGE_MSNNO          ".trim(), "0008");           //      V      20
		bodyMap.put("S1_IMGTYCD              ".trim(), "09");           //      C      2
		bodyMap.put("S1_SNSR_LOCTN           ".trim(), "0010");           //      V      12
		bodyMap.put("S1_SRC_IMGIDNO          ".trim(), "0020");           //      V      20
		bodyMap.put("S1_IMAGE_IDTF_DNT       ".trim(), "0030");           //      D      　
		bodyMap.put("S1_COVR_SCOPE_DVS_NM    ".trim(), "0040");           //      V      50
		bodyMap.put("S1_IMAGE_RDSCD          ".trim(), "50");           //      C      2
		bodyMap.put("S1_MN_WTHR_SITATN_EXPLN ".trim(), "0100");           //      V      100
		bodyMap.put("S1_ETC_WTHR_SITATN_EXPLN".trim(), "0101");           //      V      100
		bodyMap.put("S1_COVR_ANG             ".trim(), "0");           //      F      3
		bodyMap.put("S1_COVR_SCOPE_PCTG      ".trim(), "123");           //      F      5,2
		bodyMap.put("S1_RGST_DTTM            ".trim(), "456");           //      D      　
		bodyMap.put("S1_FNL_RNWL_DTTM        ".trim(), "789");           //      D      　
		bodyMap.put("S1_REGSTR_SRVNO         ".trim(), "aaaa");           //      V      15
		bodyMap.put("S1_FNL_RNWPS_SRVNO      ".trim(), "bbbb");           //      V      15
		
		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();
		
		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value =   bodyMap.get(field);
			if(value!=null){
				data.append(value.toString()).append("|");
			}else{
				data.append("|");
			}
		}
		
		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length-1];
		byte[] dataBytes = new byte[data.toString().getBytes().length-1];
		System.arraycopy(fieldName.toString().getBytes(), 0,  fieldBytes, 0,fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0,  dataBytes, 0,dataBytes.length);
		
		byte[] fieldNameLen = TypeConversion.toTypeBytes(String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(String.valueOf(dataBytes.length), "N", 6);
		
		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);
		
		int lengthField = buff.readableBytes()-6;
		buff.setBytes(0, TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));
		
		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0,viewBytes);
//		System.out.println(HexViewer.view(viewBytes));
		System.out.println("["+new String(viewBytes)+"]");
	}
	
	private static void SYS_MONITORING() throws Exception {
		
		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();
		
		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6)); 
		buff.writeBytes(TypeConversion.toTypeBytes("SYS_MONITORING", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST00112345524", "A", 32)); //uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); //FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); //송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); //에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); //에러코드
		
		int lengthField = buff.readableBytes()-6;
		buff.setBytes(0, TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));
		
		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0,viewBytes);
		System.out.println(HexViewer.view(viewBytes));
		System.out.println("["+new String(viewBytes)+"]");
	}

}
