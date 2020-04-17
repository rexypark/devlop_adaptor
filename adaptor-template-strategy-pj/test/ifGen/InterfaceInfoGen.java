package ifGen;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ifGen.IndigoCodeGen.OrgInfo;

public class InterfaceInfoGen {

	public static void main(String[] args) throws IOException {

//		List<String> ifOrgCodes = Arrays.asList("MCST", "CUST", "MFDS", "KIPO", "PLC", "MOEL", "FSC", "NAQS", "SEL",
//				"BAI");

		List<String> rcvifIds = Arrays.asList("060", "061", "063");
		String rcvIfTemplate = FileUtils.readFileToString(new File("./test/ifGen/rcvIfTemplate.txt"));

		int seq = 0;
		// 수신, 기관별, IF별
		for (String rcvIfIdNum : rcvifIds) {
			for (OrgInfo orgInfo : IndigoCodeGen.outOrgInfos) {
				System.out.println(replaceRcv(rcvIfTemplate, rcvIfIdNum, orgInfo));
				seq++;
			}
			System.out.println("--수신 " + rcvIfIdNum + ": " + seq + "건\n\n\n");
			seq = 0;
		}

		List<String> sndifIds = Arrays.asList("064", "065", "067", "070");

		// 송신, 기관별, IF별
		for (String sndIfIdNum : sndifIds) {
			String sndIfTemplate = FileUtils.readFileToString(new File("./test/ifGen/sndIfTemplate"+sndIfIdNum+".txt"));
			for (OrgInfo orgInfo : IndigoCodeGen.outOrgInfos) {
				System.out.println(replaceSnd(sndIfTemplate, sndIfIdNum, orgInfo));
				seq++;
			}
			System.out.println("--송신 " + sndIfIdNum + ": " + seq + "건\n\n\n");
			seq = 0;
		}
		
		//송신,  결과수신 Selector, Schedule
		for (String sndIfIdNum : sndifIds) {
			StringBuilder rsltSelector = new StringBuilder();
			rsltSelector.append("if_id IN (");
			int sec = 0;
			StringBuilder scheduleSb = new StringBuilder();
			for (OrgInfo orgInfo : IndigoCodeGen.outOrgInfos) {
				//if_id='P03_IF064_NDFC_PLC' or
				rsltSelector.append("'P03_IF"+sndIfIdNum+"_NDFC_"+orgInfo.abbr+"',");
				scheduleSb.append("				<value>[cronExpression="+sec+" * * * * ?,args=P03_IF"+sndIfIdNum+"_NDFC_"+orgInfo.abbr+",concurrent=false]</value>\n");
				sec +=6;
			}
			rsltSelector.delete(rsltSelector.lastIndexOf(","), rsltSelector.length() );
			rsltSelector.append(")");
			System.out.println("\n\n"+sndIfIdNum+"\n\n");
			System.out.println(rsltSelector.toString());
			System.out.println(scheduleSb.toString());
			
		}
	}

	private static String replaceRcv(String rcvIfTemplate, String ifIdNum, OrgInfo sndOrg) {
		String str = rcvIfTemplate.replaceAll("\\{snd\\}", sndOrg.abbr);
		str = str.replaceAll("\\{id\\}", ifIdNum);
		str = str.replaceAll("\\{orgCode\\}", sndOrg.comment);
		str = str.replaceAll("\\{orgName\\}", sndOrg.name);
		return str;
	}

	private static String replaceSnd(String sndIfTemplate, String ifIdNum, OrgInfo rcvOrg) {
		String str = sndIfTemplate.replaceAll("\\{rcv\\}", rcvOrg.abbr);
		str = str.replaceAll("\\{id\\}", ifIdNum);
		str = str.replaceAll("\\{rcv3\\}", rcvOrg.abbr.substring(0, 3));
		str = str.replaceAll("\\{orgCode\\}", rcvOrg.comment);
		str = str.replaceAll("\\{orgName\\}", rcvOrg.name);
		return str;
	}

}
