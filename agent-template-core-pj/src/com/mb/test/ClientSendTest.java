package com.mb.test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Date;

import org.apache.commons.lang.time.DateFormatUtils;

import com.indigo.esb.util.MTUtil;

public class ClientSendTest {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientSendTest sender = new ClientSendTest();
		byte[] sBytes = sender.makeBody("0800", "001");
		try {
			sender.requestMsg(sBytes);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String makeCommon(String tranCode, String tranGubun){
		String currTime = DateFormatUtils.format(new Date(), "yyyyMMddHHmmssSSS");
		String sMsg = "TRANSACTIONS"
				+"VAS"
				+"1"
				+ MTUtil.toAsciiString("BKDR001", 10)
				+ MTUtil.toAsciiString("BKDR001", 10)
				+ MTUtil.toNumberString(9, 12)
				+ "00001320"
				+ "99"
				+ tranCode
				+ tranGubun
				+ currTime.substring(11,17)
				+ currTime.substring(0,8)
				+ currTime.substring(8,14)
				+ "0000"
				+  MTUtil.toAsciiString(" ", 31);
		return sMsg;
	}
	public byte[] makeBody(String tranCode, String tranGubun){
		String strCommon = makeCommon(tranCode, tranGubun);
		String strBody = null;
		if(tranCode.startsWith("0800")){
			strBody =  MTUtil.toAsciiString(" ", 174)
					+ MTUtil.toNumberString(999, 6)
					+ "Y"
					+ "099"
					+ MTUtil.toAsciiString(" ", 46);
		}
		return (strCommon + strBody).getBytes();
	}

	public void requestMsg(byte[] tranData) throws Exception {
		int errCode = 0;
		InputStream esbInput = null;
		OutputStream esbOutput = null;
		Socket eaiCon = null;
		try {
			System.out.println("sndData:" + new String(tranData));
			eaiCon =  new Socket("127.0.0.1",9100);
			esbInput = eaiCon.getInputStream();
			esbOutput = eaiCon.getOutputStream();
			eaiCon.setReceiveBufferSize(32236);
			eaiCon.setSoTimeout(1000 * 10);
			esbOutput.write(tranData);
			byte[] readBuf = new byte[tranData.length];
			esbInput.read(readBuf);
			System.out.println("rcvData:" + new String(readBuf));
		} catch (Exception e) {
			System.out.println("errMsg:" + e.getMessage());
		} finally {
			if (eaiCon != null)
				eaiCon.close();
		}
	}
}
