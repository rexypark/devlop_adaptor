package msggen;

import java.util.Map;

import jeus.util.LinkedHashMap;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.mb.mci.common.transducer.tool.TypeConversion;
import com.mb.mci.common.util.DateUtil;
import com.mb.mci.common.util.HexViewer;

public class MessageGen_M_MIMS {
	
	public static void main(String[] args) throws Exception {
//		MMI0010();
//		MMI0020();
//		MMI0030();
//		SYS_MONITORING();
	}


	private static void MMI0010() throws Exception {

		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();

		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6));
		buff.writeBytes(TypeConversion.toTypeBytes("MMI0010", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST00112345523", "A", 32)); // uniquekey
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
		bodyMap.put("S1_DCPHRMT_RPRT_SN", "01"); // 2 V 판독보고서일련번호
		bodyMap.put("S1_MSN_RGST_UC", "0002"); // 20 C 임무등록부대코드
		bodyMap.put("S1_CLSLVCD", "A"); // 22 C 영상판독보고서순번
		bodyMap.put("S1_RPRT_TITLE", "0004"); // 1 V 배포여부
		bodyMap.put("S1_IMAGE_NUMSHT", "0005"); // 30 V 문서번호
		bodyMap.put("S1_MN_IDTF_MATR_CTNT", "0006"); // 300 V 임무명칭
		bodyMap.put("S1_DETLS_IDTF_CTNT", "0007"); // 　 DT 임무시작일시
		bodyMap.put("S1_RPRT_FLNM", "0008"); // 　 DT 임무종료일시
		bodyMap.put("S1_PYSCL_RPRT_FILE_ROUTE", "0009"); // 50 V 수집감지기유형명
		bodyMap.put("S1_RPRT_FLSZ", "10"); // 50 V 판독부분DECK명
		bodyMap.put("S1_RPRT_SMRY_CTNT", "0011"); // 100 V 영상질설명
		bodyMap.put("S1_KDIC_REPTNO", "0012"); // 10 F 촬영각도
		bodyMap.put("S1_INTINFO_SEQ", "13"); // 100 V 임무경로
		bodyMap.put("S1_DCPHRMT_RPRT_DVS_NM", "0014"); // 8,2 F 임무최저고도
		bodyMap.put("S1_AF_RPRT_SN", "0015"); // 8,2 F 임무최고고도
		bodyMap.put("S1_AF_MSNNO", "0016"); // 8,2 F 임무최저속도

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

	private static void MMI0020() throws Exception {

		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();

		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6));
		buff.writeBytes(TypeConversion.toTypeBytes("MMI0020", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST00112312312", "A", 32)); // uniquekey
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
		bodyMap.put("S1_DCPHRMT_RPRT_SN", "01"); // 2 V 판독보고서일련번호
		bodyMap.put("S1_MSN_RGST_UC", "0002"); // 20 C 임무등록부대코드
		bodyMap.put("S1_CLSLVCD", "A"); // 22 C 영상판독보고서순번
		bodyMap.put("S1_ABSTRACT_FILE_NAME", "0004"); // 1 V 배포여부
		bodyMap.put("S1_FILE_NAME", "0005"); // 30 V 문서번호
		bodyMap.put("S1_FILE_PATH", "0006"); // 300 V 임무명칭

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

	private static void MMI0030() throws Exception {

		ChannelBuffer buff = ChannelBuffers.dynamicBuffer();

		/**
		 * Header
		 */
		buff.writeBytes(TypeConversion.toTypeBytes("", "N", 6));
		buff.writeBytes(TypeConversion.toTypeBytes("MMI0030", "A", 16));
		buff.writeBytes(TypeConversion.toTypeBytes("TEST123122233", "A", 32)); // uniquekey
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
		bodyMap.put("S1_DCPHRMT_IMAGE_SN", "01"); // 2 V 판독보고서일련번호
		bodyMap.put("S1_RGST_UC", "0002"); // 20 C 임무등록부대코드
		bodyMap.put("S1_CLSLVCD", "A"); // 22 C 영상판독보고서순번
		bodyMap.put("S1_RGST_CNTR_NM", "0004"); // 1 V 배포여부
		bodyMap.put("S1_SCINF_NM", "0005"); // 30 V 문서번호
		bodyMap.put("S1_CLRS_NM", "0006"); // 300 V 임무명칭
		bodyMap.put("S1_DCPHRMT_IMAGE_NM", "0007"); // 　 DT 임무시작일시
		bodyMap.put("S1_SERVICE_FLNM", "0008"); // 　 DT 임무종료일시
		bodyMap.put("S1_PYSCL_SERVICE_FILE_ROUTE", "0009"); // 50 V 수집감지기유형명
		bodyMap.put("S1_SERVICE_FLSZ", "10"); // 50 V 판독부분DECK명
		bodyMap.put("S1_SERVICE_FILE_TYPE_NM", "0011"); // 100 V 영상질설명
		bodyMap.put("S1_IMAGE_FLNM", "0012"); // 10 F 촬영각도
		bodyMap.put("S1_PYSCL_IMAGE_FILE_ROUTE", "0013"); // 100 V 임무경로
		bodyMap.put("S1_IMAGE_FLSZ", "14"); // 1000 V 임무상세경로설명
		bodyMap.put("S1_IMAGE_FILE_TYPE_NM", "0015"); // 8,2 F 임무최저고도
		bodyMap.put("S1_MILGRD", "0016"); // 8,2 F 임무최고고도
		bodyMap.put("S1_GGPFT_COORD", "0017"); // 8,2 F 임무최저속도
		bodyMap.put("S1_PHOTO_EXPLN", "0018"); // 50 V 판독부분DECK명
		bodyMap.put("S1_SHTNG_DTTM", "20150623192039123"); // 100 V 영상질설명
		bodyMap.put("S1_IMGTYCD", "AA"); // 10 F 촬영각도
		bodyMap.put("S1_IMGIDNO", "0021"); // 100 V 임무경로
		bodyMap.put("S1_CYCLE_IDTF_CTNT", "0022"); // 1000 V 임무상세경로설명
		bodyMap.put("S1_KDIC_IMAGE_SN", "0023"); // 8,2 F 임무최저고도
		bodyMap.put("S1_AF_IMAGE_SN", "0024"); // 8,2 F 임무최고고도
		bodyMap.put("S1_AF_IMAGE_TYPE_NM", "0025"); // 8,2 F 임무최저속도
		bodyMap.put("S1_IMAGE_BENO", "0026"); // 8,2 F 임무최저고도
		bodyMap.put("S1_AF_IMAGE_DMPINO", "0027"); // 8,2 F 임무최고고도
		bodyMap.put("S1_FRM_NMBR", "28"); // 8,2 F 임무최저속도
		bodyMap.put("S1_ABSTRACT_FILE_NAME", "0029"); // 1 V 배포여부
		bodyMap.put("S1_FILE_NAME", "0030"); // 30 V 문서번호
		bodyMap.put("S1_FILE_PATH", "0031"); // 300 V 임무명칭

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