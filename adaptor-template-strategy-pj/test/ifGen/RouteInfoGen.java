package ifGen;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class RouteInfoGen {

	public static void main(String[] args) {

		List<String> ifOrgCodes = Arrays.asList("MCST", "CUST", "MFDS", "KIPO", "PLC", "MOEL", "FSC", "NAQS", "SEL",
				"BAI");

		List<RouteInfo> outRouteInfos = Arrays.asList(new RouteInfo("P03_IF064", "디지털증거 데이터팩 메타정보 수신 결과 전송"),
				new RouteInfo("P03_IF065", "프로그램 업데이트 정보 전송"), new RouteInfo("P03_IF067", "기관코드 전송"),
				new RouteInfo("P03_IF070", "압수물처분결과 전송"));

		String routeInfoSQLTemplate = "INSERT INTO ROUTE_INFO (IF_ID,DEST_ID,DATA_ARR_QUEUE,DATA_DEST_QUEUE,RESULT_ARR_QUEUE,RESULT_DEST_QUEUE,REMOTE_YN,CHANGE_DATE,DESCRIPTION,USING_IP,USE_YN,SND_CD,RCV_CD)"
				+ " VALUES ('{ifIdPrefix}_{snd}_{rcv}','{ifIdPrefix}_{snd}_{rcv}','ROUTE.IN.OTO','{rcv}.IN','RETURN.IN.OTO','{snd}.OUT','N','20170308152953','{comment}','127.0.0.1','Y','{snd3}','{rcv3}');";

		int seq =0;
		// 송신, 기관별
		for (RouteInfo rInfo : outRouteInfos) {
			String snd = "NDFC";
			for (String orgCd : ifOrgCodes) {
				String rcv = orgCd;
				System.out.println(replace(rInfo, routeInfoSQLTemplate, snd, rcv));
				seq++;
			}
		}

		List<RouteInfo> inRouteInfos = Arrays.asList(new RouteInfo("P02_IF060", "디지털증거 데이터팩 메타정보 수신"),
				new RouteInfo("P02_IF061", "디지털증거 데이터팩 생성결과 수신"), new RouteInfo("P02_IF063", "프로그램 상태정보 수신"));

		// 수신, 기관별
		for (RouteInfo rInfo : inRouteInfos) {
			String rcv = "NDFC";
			for (String orgCd : ifOrgCodes) {
				String snd = orgCd;
				System.out.println(replace(rInfo, routeInfoSQLTemplate, snd, rcv));
				seq++;
			}
		}

		List<RouteInfo> dbRouteInfos = Arrays.asList(new RouteInfo("P01_IF085", "(경찰) 송치/오프라인수리-전송"),
				new RouteInfo("P01_IF086", "(경찰) DEP수신-전송"), new RouteInfo("P01_IF087", "(경찰) 통계정보-전송"),
				new RouteInfo("P01_IF091", "(경찰) 처분결과-전송"));

		// 송신, 경찰 DB2DB
		for (RouteInfo rInfo : dbRouteInfos) {
			String snd = "NDFC";
			String rcv = "PLC";
			System.out.println(replace(rInfo, routeInfoSQLTemplate, snd, rcv));
			seq++;
		}
		System.out.println("--총: "+seq+"건");
	}

	private static String replace(RouteInfo rInfo, String template, String snd, String rcv) {
		String str = template.replaceAll("\\{ifIdPrefix\\}", rInfo.ifIdPrefix == null ? "" : rInfo.ifIdPrefix);
		str = str.replaceAll("\\{snd\\}", snd);
		str = str.replaceAll("\\{rcv\\}", rcv);
		str = str.replaceAll("\\{snd3\\}", snd.substring(0, 3));
		str = str.replaceAll("\\{rcv3\\}", rcv.substring(0, 3));
		str = str.replaceAll("\\{comment\\}", rInfo.comment == null ? "" : rInfo.comment);
		return str;
	}

	static class RouteInfo {
		String ifIdPrefix;

		String comment;

		public RouteInfo(String ifIdPrefix, String comment) {
			this.ifIdPrefix = ifIdPrefix;
			this.comment = comment;

		}

		@Override
		public String toString() {
			return ReflectionToStringBuilder.toString(this);
		}

	}

}
