package com.indigo.esb.log;

public class LogByteConvert {

	public static String byteArrayToHex(byte[] ba) {
		if ((ba == null) || (ba.length == 0)) {
			return null;
		}

		StringBuffer sb = new StringBuffer(ba.length * 2);

		for (int x = 0; x < ba.length; ++x) {
			String hexNumber = "0" + Integer.toHexString(0xFF & ba[x]);

			sb.append(hexNumber.substring(hexNumber.length() - 2)).append(" ");
		}
		return sb.toString().trim();
	}

	public static void main(String[] args) {
		String data = "fsadfadasfadsfadsffdas1213142412";
		System.out.println("["+LogByteConvert.byteArrayToHex(data.getBytes())+"]");
	}
}
