package msggen;

import java.util.Map;

import jeus.util.LinkedHashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.mb.mci.common.transducer.tool.TypeConversion;
import com.mb.mci.common.util.DateUtil;
import com.mb.mci.common.util.HexViewer;

public class MessageGen_M_AFCCS {

	public static void main(String[] args) throws Exception {
		MAF0010();
		MAF0020();
		MAF0030();
		MAF0040();
		MAF0050();
		MAF0060();
//		SYS_MONITORING();
	}
	
	
	private static void MAF0010() throws Exception {

		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();

		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6));
		buff.writeBytes(TypeConversion.toTypeBytes("MAF0010", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST21535421", "A", 32)); // uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); // FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(
				DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); // 송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); // 에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); // 에러코드

		/**
		 * Body Context
		 */
		Map<String, String> bodyMap = new LinkedHashMap();

		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMAGCRTYCD", "AA"); // 2 V 판독보고서일련번호
		bodyMap.put("S1_MSNNO", "0002"); // 20 C 임무등록부대코드
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ", "0003"); // 22 C 영상판독보고서순번
		bodyMap.put("S1_DSTRBTN_YN", "B"); // 1 V 배포여부
		bodyMap.put("S1_DN", "0005"); // 30 V 문서번호
		bodyMap.put("S1_MSN_TTL", "0006"); // 300 V 임무명칭
		bodyMap.put("S1_MSN_START_DTTM", "20150630110211254"); // 　 DT 임무시작일시
		bodyMap.put("S1_MSN_FNSH_DTTM", "20150630110245352"); // 　 DT 임무종료일시
		bodyMap.put("S1_CLTN_SNSR_TYPE_NM", "0009"); // 50 V 수집감지기유형명
		bodyMap.put("S1_DCPHRMT_PART_DCK_NM", "0010"); // 50 V 판독부분DECK명
		bodyMap.put("S1_IMAGE_QLTY_EXPLN", "0011"); // 100 V 영상질설명
		bodyMap.put("S1_SHTNG_ANG", "12"); // 10 F 촬영각도
		bodyMap.put("S1_MSN_ROUTE", "0013"); // 100 V 임무경로
		bodyMap.put("S1_MSN_DTL_ROUTE_EXPLN", "0014"); // 8,2 F 임무최저고도
		bodyMap.put("S1_MSN_TRVLV", "15"); // 8,2 F 임무최고고도
		bodyMap.put("S1_MSN_HGHST_ALT", "16"); // 8,2 F 임무최저속도
		bodyMap.put("S1_MSN_LWST_VLCT", "17"); // 　 DT 임무종료일시
		bodyMap.put("S1_MSN_HGHST_VLCT", "18"); // 50 V 수집감지기유형명
		bodyMap.put("S1_RPRT_SMRY_HANGL_CTNT", "0019"); // 50 V 판독부분DECK명
		bodyMap.put("S1_RPRT_SMRY_ENGLSH_CTNT", "0020"); // 100 V 영상질설명
		bodyMap.put("S1_RGST_DTTM", "20150630110256124"); // 10 F 촬영각도
		bodyMap.put("S1_FNL_RNWL_DTTM", "20150630110312356"); // 100 V 임무경로
		bodyMap.put("S1_REGSTR_SRVNO", "0023"); // 8,2 F 임무최저고도
		bodyMap.put("S1_FNL_RNWPS_SRVNO", "0024"); // 8,2 F 임무최고고도
		

		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();

		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value = bodyMap.get(field);
			if (value != null) {
				data.append(value.toString()).append("|");
			} else {
				data.append("|");
			}
		}

		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length - 1];
		byte[] dataBytes = new byte[data.toString().getBytes().length - 1];
		System.arraycopy(fieldName.toString().getBytes(), 0, fieldBytes, 0,
				fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0, dataBytes, 0,
				dataBytes.length);

		byte[] fieldNameLen = TypeConversion.toTypeBytes(
				String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(
				String.valueOf(dataBytes.length), "N", 6);

		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);

		int lengthField = buff.readableBytes() - 6;
		buff.setBytes(0,
				TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));

		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0, viewBytes);
		// System.out.println(HexViewer.view(viewBytes));
		System.out.println("[" + new String(viewBytes) + "]");
	}

	private static void MAF0020() throws Exception {

		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();

		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6));
		buff.writeBytes(TypeConversion.toTypeBytes("MAF0020", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST31548754", "A", 32)); // uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); // FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(
				DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); // 송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); // 에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); // 에러코드

		/**
		 * Body Context
		 */
		Map<String, String> bodyMap = new LinkedHashMap();

		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMGIDNO", "0001"); // 2 V 판독보고서일련번호
		bodyMap.put("S1_IMGTYCD", "A"); // 20 C 임무등록부대코드
		bodyMap.put("S1_IMAGCRTYCD", "B"); // 22 C 영상판독보고서순번
		bodyMap.put("S1_MSNNO", "0004"); // 1 V 배포여부
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ", "0005"); // 30 V 문서번호
		bodyMap.put("S1_INDCTN_SVNC_TGT_BENO", "0006"); // 300 V 임무명칭
		bodyMap.put("S1_REFRC_RGST_YN", "C"); // 　 DT 임무시작일시
		bodyMap.put("S1_RGST_DTTM", "20150630110053124"); // 　 DT 임무종료일시
		bodyMap.put("S1_FNL_RNWL_DTTM", "20150630110123554"); // 50 V 수집감지기유형명
		bodyMap.put("S1_REGSTR_SRVNO", "0010"); // 50 V 판독부분DECK명
		bodyMap.put("S1_FNL_RNWPS_SRVNO", "0011"); // 100 V 영상질설명
		bodyMap.put("S1_ABSTRACT_FILE_NAME", "0012"); // 10 F 촬영각도
		bodyMap.put("S1_FILE_NAME", "0013"); // 100 V 임무경로
		bodyMap.put("S1_FILE_PATH", "0014"); // 8,2 F 임무최저고도

		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();

		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value = bodyMap.get(field);
			if (value != null) {
				data.append(value.toString()).append("|");
			} else {
				data.append("|");
			}
		}

		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length - 1];
		byte[] dataBytes = new byte[data.toString().getBytes().length - 1];
		System.arraycopy(fieldName.toString().getBytes(), 0, fieldBytes, 0,
				fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0, dataBytes, 0,
				dataBytes.length);

		byte[] fieldNameLen = TypeConversion.toTypeBytes(
				String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(
				String.valueOf(dataBytes.length), "N", 6);

		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);

		int lengthField = buff.readableBytes() - 6;
		buff.setBytes(0,
				TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));

		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0, viewBytes);
		// System.out.println(HexViewer.view(viewBytes));
		System.out.println("[" + new String(viewBytes) + "]");
	}


	private static void MAF0030() throws Exception {

		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();

		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6));
		buff.writeBytes(TypeConversion.toTypeBytes("MAF0030", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST785465215", "A", 32)); // uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); // FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(
				DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); // 송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); // 에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); // 에러코드

		/**
		 * Body Context
		 */
		Map<String, String> bodyMap = new LinkedHashMap();

		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMAGCRTYCD", "AA"); // 2 V 판독보고서일련번호
		bodyMap.put("S1_MSNNO", "0002"); // 20 C 임무등록부대코드
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ", "0003"); // 22 C 영상판독보고서순번
		bodyMap.put("S1_INDCTN_SVNC_TGT_BENO", "0004"); // 1 V 배포여부
		bodyMap.put("S1_IDTF_SEQ", "0005"); // 30 V 문서번호
		bodyMap.put("S1_OBTYPECD", "B"); // 300 V 임무명칭
		bodyMap.put("S1_IDTF_DATE", "0007"); // 　 DT 임무시작일시
		bodyMap.put("S1_IDTF_DNT", "20150630105963452"); // 　 DT 임무종료일시
		bodyMap.put("S1_IDTF_CNTUNT", "0009"); // 50 V 수집감지기유형명
		bodyMap.put("S1_VLDT_EXPLN", "0010"); // 50 V 판독부분DECK명
		bodyMap.put("S1_HANGL_EQP_TTL", "0011"); // 100 V 영상질설명
		bodyMap.put("S1_ENGLSH_EQP_TTL", "0012"); // 10 F 촬영각도
		bodyMap.put("S1_ENMYFAMC", "C"); // 100 V 임무경로
		bodyMap.put("S1_IDTF_PLC_HANGL_TTL", "0014"); // 8,2 F 임무최저고도
		bodyMap.put("S1_IDTF_PLC_ENGLSH_TTL", "0015"); // 8,2 F 임무최고고도
		bodyMap.put("S1_IDTF_CTNT", "0016"); // 8,2 F 임무최저속도
		bodyMap.put("S1_RGST_DTTM", "20150630105986124"); // 　 DT 임무종료일시
		bodyMap.put("S1_FNL_RNWL_DTTM", "20150630110088324"); // 50 V 수집감지기유형명
		bodyMap.put("S1_REGSTR_SRVNO", "0019"); // 50 V 판독부분DECK명
		bodyMap.put("S1_FNL_RNWPS_SRVNO", "0020"); // 100 V 영상질설명
		

		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();

		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value = bodyMap.get(field);
			if (value != null) {
				data.append(value.toString()).append("|");
			} else {
				data.append("|");
			}
		}

		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length - 1];
		byte[] dataBytes = new byte[data.toString().getBytes().length - 1];
		System.arraycopy(fieldName.toString().getBytes(), 0, fieldBytes, 0,
				fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0, dataBytes, 0,
				dataBytes.length);

		byte[] fieldNameLen = TypeConversion.toTypeBytes(
				String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(
				String.valueOf(dataBytes.length), "N", 6);

		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);

		int lengthField = buff.readableBytes() - 6;
		buff.setBytes(0,
				TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));

		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0, viewBytes);
		// System.out.println(HexViewer.view(viewBytes));
		System.out.println("[" + new String(viewBytes) + "]");
	}


	private static void MAF0040() throws Exception {

		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();

		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6));
		buff.writeBytes(TypeConversion.toTypeBytes("MAF0040", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST998651251", "A", 32)); // uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); // FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(
				DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); // 송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); // 에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); // 에러코드

		/**
		 * Body Context
		 */
		Map<String, String> bodyMap = new LinkedHashMap();

		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMAGCRTYCD", "AA"); // 2 V 판독보고서일련번호
		bodyMap.put("S1_MSNNO", "0002"); // 20 C 임무등록부대코드
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ", "0003"); // 22 C 영상판독보고서순번
		bodyMap.put("S1_INDCTN_SVNC_TGT_BENO", "0004"); // 1 V 배포여부
		bodyMap.put("S1_ADDTN_TGT_YN", "B"); // 30 V 문서번호
		bodyMap.put("S1_MN_IDTF_YN", "C"); // 300 V 임무명칭
		bodyMap.put("S1_IDTF_TGT_SEQ", "0007"); // 　 DT 임무시작일시
		bodyMap.put("S1_IDTF_TGTPT_NM", "0008"); // 　 DT 임무종료일시
		bodyMap.put("S1_ACTVT_STATE_TYPE_EXPLN", "0009"); // 50 V 수집감지기유형명
		bodyMap.put("S1_ACTVT_CHG_TYPE_EXPLN", "0010"); // 50 V 판독부분DECK명
		bodyMap.put("S1_ODNR_IDTF_CTNT", "0011"); // 100 V 영상질설명
		bodyMap.put("S1_HANGL_ACTVT_CTNT", "0012"); // 10 F 촬영각도
		bodyMap.put("S1_ENGLSH_ACTVT_CTNT", "0013"); // 100 V 임무경로
		bodyMap.put("S1_ETDVSCD", "D"); // 8,2 F 임무최저고도
		bodyMap.put("S1_ENMY_CORPS_TYPE_EXPLN", "0015"); // 8,2 F 임무최고고도
		bodyMap.put("S1_TGSTCD", "E"); // 8,2 F 임무최저속도
		bodyMap.put("S1_DTL_TGSTCD", "F"); // 　 DT 임무종료일시
		bodyMap.put("S1_INSTLTN_HANGL_TTL", "0018"); // 50 V 수집감지기유형명
		bodyMap.put("S1_INSTLTN_ENGLSH_TTL", "0019"); // 50 V 판독부분DECK명
		bodyMap.put("S1_GGPFT_COORD", "0020"); // 100 V 영상질설명
		bodyMap.put("S1_MGRS_COORD", "0021"); // 10 F 촬영각도
		bodyMap.put("S1_FKSGNCD", "G"); // 100 V 임무경로
		bodyMap.put("S1_NCB", "H"); // 8,2 F 임무최저고도
		bodyMap.put("S1_RGST_DTTM", "20150630105824521"); // 8,2 F 임무최고고도
		bodyMap.put("S1_FNL_RNWL_DTTM", "20150630105827456"); // 100 V 임무경로
		bodyMap.put("S1_REGSTR_SRVNO", "0026"); // 8,2 F 임무최저고도
		bodyMap.put("S1_FNL_RNWPS_SRVNO", "0027"); // 8,2 F 임무최고고도
		

		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();

		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value = bodyMap.get(field);
			if (value != null) {
				data.append(value.toString()).append("|");
			} else {
				data.append("|");
			}
		}

		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length - 1];
		byte[] dataBytes = new byte[data.toString().getBytes().length - 1];
		System.arraycopy(fieldName.toString().getBytes(), 0, fieldBytes, 0,
				fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0, dataBytes, 0,
				dataBytes.length);

		byte[] fieldNameLen = TypeConversion.toTypeBytes(
				String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(
				String.valueOf(dataBytes.length), "N", 6);

		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);

		int lengthField = buff.readableBytes() - 6;
		buff.setBytes(0,
				TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));

		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0, viewBytes);
		// System.out.println(HexViewer.view(viewBytes));
		System.out.println("[" + new String(viewBytes) + "]");
	}



	private static void MAF0050() throws Exception {

		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();

		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6));
		buff.writeBytes(TypeConversion.toTypeBytes("MAF0050", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST8457321", "A", 32)); // uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); // FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(
				DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); // 송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); // 에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); // 에러코드

		/**
		 * Body Context
		 */
		Map<String, String> bodyMap = new LinkedHashMap();

		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMAGCRTYCD", "AA"); // 2 V 판독보고서일련번호
		bodyMap.put("S1_MSNNO", "0002"); // 20 C 임무등록부대코드
		bodyMap.put("S1_IMAGE_DCPHRRPRT_SEQ", "0003"); // 22 C 영상판독보고서순번
		bodyMap.put("S1_INDCTN_SVNC_TGT_BENO", "0004"); // 1 V 배포여부
		bodyMap.put("S1_DTA_SEQ", "0005"); // 30 V 문서번호
		bodyMap.put("S1_IMAGE_IDTF_DATE", "0006"); // 300 V 임무명칭
		bodyMap.put("S1_PRDCN_CD_TYPENO", "07"); // 　 DT 임무시작일시
		bodyMap.put("S1_IMAGE_MSNNO", "0008"); // 　 DT 임무종료일시
		bodyMap.put("S1_IMGTYCD", "BB"); // 50 V 수집감지기유형명
		bodyMap.put("S1_SNSR_LOCTN", "0010"); // 50 V 판독부분DECK명
		bodyMap.put("S1_SRC_IMGIDNO", "0011"); // 100 V 영상질설명
		bodyMap.put("S1_IMAGE_IDTF_DNT", "20150630105544142"); // 10 F 촬영각도
		bodyMap.put("S1_COVR_SCOPE_DVS_NM", "0013"); // 100 V 임무경로
		bodyMap.put("S1_IMAGE_RDSCD", "CC"); // 8,2 F 임무최저고도
		bodyMap.put("S1_MN_WTHR_SITATN_EXPLN", "0015"); // 8,2 F 임무최고고도
		bodyMap.put("S1_ETC_WTHR_SITATN_EXPLN", "0016"); // 8,2 F 임무최저속도
		bodyMap.put("S1_COVR_ANG", "17"); // 　 DT 임무종료일시
		bodyMap.put("S1_COVR_SCOPE_PCTG", "18"); // 50 V 수집감지기유형명
		bodyMap.put("S1_RGST_DTTM", "20150630105624123"); // 50 V 판독부분DECK명
		bodyMap.put("S1_FNL_RNWL_DTTM", "20150630105624225"); // 100 V 영상질설명
		bodyMap.put("S1_REGSTR_SRVNO", "0021"); // 10 F 촬영각도
		bodyMap.put("S1_FNL_RNWPS_SRVNO", "0022"); // 100 V 임무경로
		bodyMap.put("S1_ABSTRACT_FILE_NAME", "0023"); // 8,2 F 임무최저고도
		bodyMap.put("S1_FILE_NAME", "0024"); // 8,2 F 임무최고고도
		bodyMap.put("S1_FILE_PATH", "0025"); // 100 V 임무경로
		

		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();

		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value = bodyMap.get(field);
			if (value != null) {
				data.append(value.toString()).append("|");
			} else {
				data.append("|");
			}
		}

		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length - 1];
		byte[] dataBytes = new byte[data.toString().getBytes().length - 1];
		System.arraycopy(fieldName.toString().getBytes(), 0, fieldBytes, 0,
				fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0, dataBytes, 0,
				dataBytes.length);

		byte[] fieldNameLen = TypeConversion.toTypeBytes(
				String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(
				String.valueOf(dataBytes.length), "N", 6);

		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);

		int lengthField = buff.readableBytes() - 6;
		buff.setBytes(0,
				TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));

		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0, viewBytes);
		// System.out.println(HexViewer.view(viewBytes));
		System.out.println("[" + new String(viewBytes) + "]");
	}
	
	private static void MAF0060() throws Exception {

		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();

		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6));
		buff.writeBytes(TypeConversion.toTypeBytes("MAF0060", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST623514321", "A", 32)); // uniquekey
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 128)); // FILE_PATH
		buff.writeBytes(TypeConversion.toTypeBytes(
				DateUtil.getDateFormat("yyyyMMddHHmmss"), "A", 14)); // 송신시각
		buff.writeBytes(TypeConversion.toTypeBytes("0000", "A", 4)); // 에러코드
		buff.writeBytes(TypeConversion.toTypeBytes("", "A", 200)); // 에러코드

		/**
		 * Body Context
		 */
		Map<String, String> bodyMap = new LinkedHashMap();

		/**
		 * Column Data
		 */
		bodyMap.put("S1_IMGIDNO", "0001"); // 2 V 판독보고서일련번호
		bodyMap.put("S1_IMGTYCD", "G"); // 20 C 임무등록부대코드
		bodyMap.put("S1_IMAGCRTYCD", "F"); // 22 C 영상판독보고서순번
		bodyMap.put("S1_SESN_IMAGE_YN", "E"); // 1 V 배포여부
		bodyMap.put("S1_CAM_IMAGE_YN", "D"); // 30 V 문서번호
		bodyMap.put("S1_LRA_YN", "C"); // 300 V 임무명칭
		bodyMap.put("S1_IMAGE_BENO", "0007"); // 　 DT 임무시작일시
		bodyMap.put("S1_IMAGE_DMPINO", "0008"); // 　 DT 임무종료일시
		bodyMap.put("S1_IMAGE_HNGNM", "0009"); // 50 V 수집감지기유형명
		bodyMap.put("S1_IMAGE_ENGNM", "0010"); // 50 V 판독부분DECK명
		bodyMap.put("S1_IMAGE_GGPFT_COORD", "0011"); // 100 V 영상질설명
		bodyMap.put("S1_IMAGE_MGRS_COORD", "0012"); // 10 F 촬영각도
		bodyMap.put("S1_IMAGE_CLTN_DATE", "0013"); // 100 V 임무경로
		bodyMap.put("S1_IMAGE_SHTNG_DRCTN", "0014"); // 8,2 F 임무최저고도
		bodyMap.put("S1_WTPTDVCD", "B"); // 8,2 F 임무최고고도
		bodyMap.put("S1_IMAGE_CLSLVCD", "A"); // 8,2 F 임무최저속도
		bodyMap.put("S1_SERVICE_FLNM", "0017"); // 　 DT 임무종료일시
		bodyMap.put("S1_SERVICE_ORIGIN_FLNM", "0018"); // 50 V 수집감지기유형명
		bodyMap.put("S1_SERVICE_FLSZ", "19"); // 50 V 판독부분DECK명
		bodyMap.put("S1_PRVW_FLNM", "0020"); // 100 V 영상질설명
		bodyMap.put("S1_PRVW_ORIGIN_FLNM", "0021"); // 10 F 촬영각도
		bodyMap.put("S1_PRVW_FLSZ", "22"); // 100 V 임무경로
		bodyMap.put("S1_ORGINL_FLNM", "0023"); // 8,2 F 임무최저고도
		bodyMap.put("S1_ORGINL_ORIGIN_FLNM", "0024"); // 8,2 F 임무최고고도
		bodyMap.put("S1_ORGINL_FLSZ", "25"); // 100 V 임무경로
		bodyMap.put("S1_RGST_DTTM", "20150630130255125"); // 10 F 촬영각도
		bodyMap.put("S1_FNL_RNWL_DTTM", "20150630130255124"); // 100 V 임무경로
		bodyMap.put("S1_REGSTR_SRVNO", "0028"); // 8,2 F 임무최저고도
		bodyMap.put("S1_FNL_RNWPS_SRVNO", "0029"); // 8,2 F 임무최고고도
		bodyMap.put("S1_IMAGE_INFO_RMRK", "0030"); // 100 V 임무경로
		

		StringBuilder fieldName = new StringBuilder();
		StringBuilder data = new StringBuilder();

		for (String field : bodyMap.keySet()) {
			fieldName.append(field).append("|");
			Object value = bodyMap.get(field);
			if (value != null) {
				data.append(value.toString()).append("|");
			} else {
				data.append("|");
			}
		}

		byte[] fieldBytes = new byte[fieldName.toString().getBytes().length - 1];
		byte[] dataBytes = new byte[data.toString().getBytes().length - 1];
		System.arraycopy(fieldName.toString().getBytes(), 0, fieldBytes, 0,
				fieldBytes.length);
		System.arraycopy(data.toString().getBytes(), 0, dataBytes, 0,
				dataBytes.length);

		byte[] fieldNameLen = TypeConversion.toTypeBytes(
				String.valueOf(fieldBytes.length), "N", 6);
		byte[] dataLen = TypeConversion.toTypeBytes(
				String.valueOf(dataBytes.length), "N", 6);

		buff.writeBytes(fieldNameLen);
		buff.writeBytes(fieldBytes);
		buff.writeBytes(dataLen);
		buff.writeBytes(dataBytes);

		int lengthField = buff.readableBytes() - 6;
		buff.setBytes(0,
				TypeConversion.toTypeBytes(String.valueOf(lengthField), "N", 6));

		int size = buff.readableBytes();
		byte[] viewBytes = new byte[size];
		buff.getBytes(0, viewBytes);
		// System.out.println(HexViewer.view(viewBytes));
		System.out.println("[" + new String(viewBytes) + "]");
	}
}
