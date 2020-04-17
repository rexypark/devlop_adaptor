package ifGen;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class IndigoCodeGen {

	public static List<OrgInfo> outOrgInfos = Arrays.asList(new OrgInfo(12, "BAI", "감사원", "0900100"),
			new OrgInfo(8, "SEL", "서울시", "2150101"), new OrgInfo(11, "NAQS", "농산물품질관리원", "2140102"),
			new OrgInfo(9, "FSC", "금융위원회", "0900110"), new OrgInfo(10, "MOEL", "고용노동부", "1490000"),
			new OrgInfo(7, "PLC", "경찰청", "00001"), new OrgInfo(5, "KIPO", "특허청", "2250101"),
			new OrgInfo(6, "MFDS", "식품처", "2240101"), new OrgInfo(4, "CUST", "관세청", "2010101"),
			new OrgInfo(3, "MCST", "문체부", "2130112"));
	static {
		Collections.sort(outOrgInfos);
	}

	public static List<OrgInfo> inOrgInfos = Arrays.asList(new OrgInfo(1, "SPO", "검찰연계서버", "00000"),
			new OrgInfo(2, "NDFC", "송치", "99999"));
	static {
		Collections.sort(inOrgInfos);
	}

	public static void main(String[] args) {
		List<String> ifOrgCodes = Arrays.asList("MCST", "CUST", "MFDS", "KIPO", "PLC", "MOEL", "FSC", "NAQS", "SEL",
				"BAI");

		List<IfInfo> inIfInfos = Arrays.asList(new IfInfo("P02", "IF-DT-060", "디지털증거 데이터팩 메타정보 수신", ""),
				new IfInfo("P02", "IF-DT-061", "디지털증거 데이터팩 생성결과 수신      ", ""),
				new IfInfo("P02", "IF-DT-063", "프로그램 상태정보 수신                 ", ""),
				new IfInfo("P02", "IF-DT-P07", "(경찰) DEP 생성결과 수신               ", ""));

		List<IfInfo> outIfInfos = Arrays.asList(new IfInfo("P03", "IF-DT-064", "디지털증거 데이터팩 메타정보 수신 결과 전송", ""),
				new IfInfo("P03", "IF-DT-065", "프로그램 업데이트 정보 전송            ", "IF-DT-066 업데이트파일 전송도 같이 처리"),
				new IfInfo("P03", "IF-DT-067", "기관코드 전송                          ", "IF-DT-069 기관코드파일 전송도 같이 처리"),
				new IfInfo("P03", "IF-DT-070", "압수물처분결과 전송                    ", "IF-DT-071 처분결과파일 전송도 같이 처리"));

		List<IfInfo> outDbIfInfos = Arrays.asList(new IfInfo("P01", "IF-DT-085", "(경찰) 송치/오프라인수리-전송          ", ""),
				new IfInfo("P01", "IF-DT-086", "(경찰) DEP수신-전송                    ", ""),
				new IfInfo("P01", "IF-DT-087", "(경찰) 통계정보-전송                   ", ""),
				new IfInfo("P01", "IF-DT-091", "(경찰) 처분결과-전송                   ", ""));

		List<IfInfo> onlineIfInfos = Arrays.asList(new IfInfo("", "IF-DT-072", "디지털증거 보관확인서 발급요청", ""),
				new IfInfo("", "IF-DT-090", "기관별 환경설정 정보 조회", ""));

		// String inboundCSVTemplate =
		// "01,{PATTERN}_{IF_ID}_{ORG_CD}_NDFC,{IF_NAME},\"{COMMENT}\",{SEQ},Y";
		//
		// String outboundCSVTemplate =
		// "01,{PATTERN}_{IF_ID}_NDFC_{ORG_CD},{IF_NAME},\"{COMMENT}\",{SEQ},Y";
		//
		// String onlineCSVTemplate =
		// "01,{IF_ID},{IF_NAME},\"{COMMENT}\",{SEQ},Y";

		String inboundSQLTemplate = "INSERT INTO INDIGO.INDIGO_CODE (CODE_DIV,CODE_ID,CODE_NAME,CODE_INFO,CODE_ORDER,USE_YN) VALUES ("
				+ " '01','{PATTERN}_{IF_ID}_{ORG_CD}_NDFC','{IF_NAME}','\"{COMMENT}\"',{SEQ},'Y');";

		String outboundSQLTemplate = "INSERT INTO INDIGO.INDIGO_CODE (CODE_DIV,CODE_ID,CODE_NAME,CODE_INFO,CODE_ORDER,USE_YN) VALUES ("
				+ " '01','{PATTERN}_{IF_ID}_NDFC_{ORG_CD}','{IF_NAME}','\"{COMMENT}\"',{SEQ},'Y');";

		String onlineSQLTemplate = "INSERT INTO INDIGO.INDIGO_CODE (CODE_DIV,CODE_ID,CODE_NAME,CODE_INFO,CODE_ORDER,USE_YN) VALUES ("
				+ " '01','{IF_ID}','{IF_NAME}','\"{COMMENT}\"',{SEQ},'Y');";

		String orgCodeSQLTemplate = "INSERT INTO INDIGO.INDIGO_CODE (CODE_DIV,CODE_ID,CODE_NAME,CODE_INFO,CODE_ORDER,USE_YN) VALUES ("
				+ " '02','{ORG_ABBR}','{ORG_NAME}','\"{COMMENT}\"',{SEQ},'Y');";

		int seq = 0;
		// 수신, 기관별
		for (IfInfo ifInfo : inIfInfos) {
			for (String orgCd : ifOrgCodes) {
				seq++;
				System.out.println(replace(ifInfo, inboundSQLTemplate, orgCd, seq));
				// System.out.println(replace(ifInfo, inboundCSVTemplate, orgCd,
				// seq));
			}
		}

		// 송신, 기관별
		for (IfInfo ifInfo : outIfInfos) {
			for (String orgCd : ifOrgCodes) {
				seq++;
				System.out.println(replace(ifInfo, outboundSQLTemplate, orgCd, seq));
				// System.out.println(replace(ifInfo, outboundCSVTemplate,
				// orgCd, seq));
			}
		}

		// 송신, 경찰만
		for (IfInfo ifInfo : outDbIfInfos) {
			seq++;
			System.out.println(replace(ifInfo, outboundSQLTemplate, "PLC", seq));
			// System.out.println(replace(ifInfo, outboundCSVTemplate, "PLC",
			// seq));
		}

		// 온라인
		for (IfInfo ifInfo : onlineIfInfos) {
			seq++;
			System.out.println(replaceOnline(ifInfo, onlineSQLTemplate, seq));
			// System.out.println(replaceOnline(ifInfo, onlineCSVTemplate,
			// seq));
		}

		// 기관코드
		for (OrgInfo orgInfo : inOrgInfos) {
			seq++;
			System.out.println(replaceOrg(orgInfo, orgCodeSQLTemplate, seq));
		}
		for (OrgInfo orgInfo : outOrgInfos) {
			seq++;
			System.out.println(replaceOrg(orgInfo, orgCodeSQLTemplate, seq));
		}
	}

	private static String replaceOrg(OrgInfo orgInfo, String template, int seq) {
		String str = template.replaceFirst("\\{ORG_ABBR\\}", orgInfo.abbr.substring(0, 3));
		str = str.replaceFirst("\\{ORG_NAME\\}", orgInfo.name == null ? "" : orgInfo.name.trim());
		str = str.replaceFirst("\\{COMMENT\\}", orgInfo.comment == null ? "" : orgInfo.comment);
		str = str.replaceFirst("\\{SEQ\\}", String.valueOf(seq));
		return str;
	}

	private static String replaceOnline(IfInfo ifInfo, String template, int seq) {
		String str = template.replaceFirst("\\{IF_ID\\}", ifInfo.ifId);
		str = str.replaceFirst("\\{IF_NAME\\}", ifInfo.name == null ? "" : ifInfo.name.trim());
		str = str.replaceFirst("\\{COMMENT\\}", ifInfo.comment == null ? "" : ifInfo.comment);
		str = str.replaceFirst("\\{SEQ\\}", String.valueOf(seq));
		return str;
	}

	private static String replace(IfInfo ifInfo, String template, String orgCd, int seq) {
		String str = template.replaceFirst("\\{PATTERN\\}", ifInfo.pattern == null ? "" : ifInfo.pattern);
		str = str.replaceFirst("\\{IF_ID\\}", ifInfo.ifId.replace("-DT-", ""));
		str = str.replaceFirst("\\{ORG_CD\\}", orgCd);
		str = str.replaceFirst("\\{IF_NAME\\}", ifInfo.name == null ? "" : ifInfo.name.trim());
		str = str.replaceFirst("\\{COMMENT\\}", ifInfo.comment == null ? "" : ifInfo.comment);
		str = str.replaceFirst("\\{SEQ\\}", String.valueOf(seq));
		return str;
	}

	static class IfInfo {
		String pattern;
		String ifId;
		String name;
		String comment;

		public IfInfo(String pattern, String ifId, String name, String comment) {
			this.pattern = pattern;
			this.ifId = ifId;
			this.name = name;
			this.comment = comment;
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}

	}

	static class OrgInfo implements Comparable<OrgInfo> {
		int order;
		String abbr;
		String name;
		String comment;

		public OrgInfo(int order, String abbr, String name, String comment) {
			this.order = order;
			this.abbr = abbr;
			this.name = name;
			this.comment = comment;
		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}

		@Override
		public int compareTo(OrgInfo o) {
			return this.order - o.order;
		}

	}

}
