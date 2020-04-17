/*
 * <file description>
 *
 * $Id: MTUtil.java,v 1.2 2012/05/24 07:08:46 mci Exp $
 */
package com.indigo.esb.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

public class MTUtil {
	static Log logger = LogFactory.getLog(MTUtil.class);

	public static final byte SO = (byte) 0x0E;
	public static final byte SI = (byte) 0x0F;

	public static void main(String[] aa) {
		// System.out.println(MTUtil.toFullChar("BK"));
		// System.out.println(MTUtil.toKSC5601String("BK", 4));
		int maxFrameLength = 4096;
		int lengthFieldOffset = 0;
		int lengthFieldLength = 6;
		int lengthFieldEndOffset = 6;
		int lengthAdjustment = 0;
		int initialBytesToStrip = 0;

		byte[] sBytes = "METABUILDABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890".getBytes();
		byte[] totalBytes = new byte[sBytes.length + lengthFieldLength - initialBytesToStrip];
		//int totalLength = sBytes.length;
		int lenField = totalBytes.length - lengthFieldEndOffset - lengthAdjustment;
		byte[] lenBytes = MTUtil.toNumberString(lenField, lengthFieldLength).getBytes();
		System.arraycopy(sBytes, initialBytesToStrip, totalBytes, 0, lengthFieldOffset);
		System.arraycopy(lenBytes, 0, totalBytes, lengthFieldOffset, lengthFieldLength);
		System.arraycopy(sBytes, lengthFieldOffset + initialBytesToStrip, totalBytes, lengthFieldEndOffset, sBytes.length-lengthFieldOffset-initialBytesToStrip);
		ChannelBuffer buf = ChannelBuffers.dynamicBuffer(ByteOrder.BIG_ENDIAN,totalBytes.length);
		buf.writeBytes(totalBytes);
		System.out.println(new String(totalBytes));
		System.out.println(totalBytes.length);
/*		String a = "aaaaaaaaa_ATT_AAAAAAAAA";
		if(a.indexOf("_ATT_") > 0){
		    System.out.println(a);
		}
*/
		//System.out.println(leftPad("895", 8, (byte) 0x30));
		/*String data = "aaaa{toAsciiString,abc,10}ccc";
		String usrFunc = data.substring(data.indexOf("{")+1, data.indexOf("}")).trim();
		System.out.println("userFunc: " + usrFunc);
		String[] arg = usrFunc.split("\\,");
		HashMap map = new HashMap();
		for (int i = 0; i < arg.length; i++) {
			System.out.println("arg["+i+"]: " + arg[i]);
		}
		try {
			Class handler = Class.forName("com.mb.mci.common.util.MTUtil");
			Method[] method = handler.getMethods();
			handler.getMethod(arg[0], HashMap.class);
			for (int i = 0; i < method.length; i++) {
				Method mthd = method[i];
				int paramCount = mthd.getParameterTypes().length;
				//mthd.invoke(obj, args)
				System.out.println("mthd :" + mthd.getName());
				if(mthd.getName().equals(arg[0])){
					String ret = (String)mthd.invoke(null,arg[1], Integer.parseInt(arg[2]));
					System.out.println("ret :" + ret);
					break;
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/


		// try {
		// byte[] aaa = a.getBytes();
		// System.out.println(HexDumper.dump(aaa));
		// byte[] e = MTUtil.ascii2Ebcdic(aaa);
		// System.out.println(HexDumper.dump(e));
		// } catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		// e.printStackTrace();
		// }

	}
	public static byte[] getMciValue(String nattr, String fattr, int len, String data, String prev){
		byte[] value = null;
//		if(data.toLowerCase().startsWith("{auto")){
//			String[] arg = data.split("\\,");
//			if(prev ==  null || prev.equals("")){
//				value = makeMTBytes(arg[1].trim(), fattr, len);
//			}else{
//				int tmp =  Integer.parseInt(arg[1].trim()) + Integer.parseInt(arg[2].trim());
//				value = makeMTBytes(String.valueOf(tmp), fattr, len);
//			}
//		}else if(data.toLowerCase().startsWith("{yyyymmddhhmmsssss}")){
//			String tmp = DateUtil.getDateTimeMilli();
//			value = makeMTBytes(tmp, fattr, len);
//		}else if(data.toLowerCase().startsWith("{yyyymmddhhmmss}")){
//			String tmp = DateUtil.getDateTime();
//			value = makeMTBytes(tmp, fattr, len);
//		}else if(data.toLowerCase().startsWith("{yyyymmdd}")){
//			String tmp = DateUtil;
//			value = makeMTBytes(tmp, fattr, len);
//		}

		return value;
	}
	public static String rightPad(String in, int len, byte fillByte) {
		if (in != null) {
			if (in.getBytes().length == len) {
				return in;
			}
		}

		byte[] bytes = new byte[len];
		Arrays.fill(bytes, fillByte);
		if (in != null) {
			try {
				int fillLen = in.getBytes().length;
				if (fillLen > len) {
					System.arraycopy(in.getBytes(), 0, bytes, 0, len);
				} else
					System.arraycopy(in.getBytes(), 0, bytes, 0, fillLen);
			} catch (ArrayIndexOutOfBoundsException e) {
				logger.info("str:" + in + " length:" + len);
				throw e;
			}
		}
		return new String(bytes);
	}

	public static String rightPadAndCut(String in, int len, byte fillByte) {
		if (in != null) {
			if (in.getBytes().length == len) {
				return in;
			}
		}
		byte[] bytes = new byte[len];
		Arrays.fill(bytes, fillByte);
		if (in != null) {
			try {
				int fillLen = in.getBytes().length;
				if (fillLen > len) {
					System.arraycopy(in.getBytes(), 0, bytes, 0, len);
				} else
					System.arraycopy(in.getBytes(), 0, bytes, 0, fillLen);
			} catch (ArrayIndexOutOfBoundsException e) {
				logger.info("str:" + in + " length:" + len);
				throw e;
			}
		}
		logger.debug(in + ":" + HexDumper.dump(bytes));
		return new String(bytes);
	}

	public static String leftPad(String in, int len, byte fillByte) {
		if (in.getBytes().length == len) {
			return in;
		}
		byte[] bytes = new byte[len];
		Arrays.fill(bytes, fillByte);
		if (in != null) {
			try {
				System.arraycopy(in.getBytes(), 0, bytes, bytes.length
						- in.getBytes().length, in.getBytes().length);
			} catch (ArrayIndexOutOfBoundsException e) {
				logger.info("str:|" + in + "| length:" + len);
				throw e;
			}
		}
		// logger.debug(in + ":" + HexDumper.dump(bytes));
		return new String(bytes);
	}

	public static String toAsciiStringIgnore(String in, int len) {
//		logger.info("in size : " + in.length() + " len : " + len);
		if (in == null || in.length() > len) {
			byte[] bytes = new byte[len];
			System.arraycopy(in.getBytes(), 0, bytes, 0, len);
			in = new String(bytes);
		}
		return rightPad(in, len, (byte) 0x20);
	}
	public static String toStringFillNull(String in, int len){
		if (in == null || in.length() > len) {
			byte[] bytes = new byte[len];
			System.arraycopy(in.getBytes(), 0, bytes, 0, len);
			in = new String(bytes);
		}
		return rightPad(in, len, (byte) 0x00);
	}
	public static String toAsciiString(String in, int len) {
		if (in == null) {
			byte[] bytes = new byte[len];
			Arrays.fill(bytes, (byte) 0x20);
			return new String(bytes);
		}else if(in.length() > len){
			byte[] bytes = new byte[len];

			System.arraycopy(in.getBytes(), 0, bytes, 0, len);
			return new String(bytes);
		}else{
			return rightPad(in, len, (byte) 0x20);
		}
	}

	public static byte[] toAsciiBytes(byte[] in, int len) {
		byte[] bytes = new byte[len];
		Arrays.fill(bytes, (byte) 0x20);
		if (in != null) {
			if(len < in.length){
				System.arraycopy(in, 0, bytes, 0, len);
			}else{
				System.arraycopy(in, 0, bytes, 0, in.length);
			}
		}
		return bytes;
	}

	public static byte[] toEbcdicBytes(byte[] in, int len) {
		byte[] bytes = new byte[len];
		Arrays.fill(bytes, (byte) 0x40);
		if (in != null) {
			System.arraycopy(in, 0, bytes, 0, in.length);
		}
		return bytes;
	}

	public static String toKSC5601String(String in, int len) {
		if (in == null) {
			byte[] bytes = new byte[len];
			Arrays.fill(bytes, (byte) 0xA1);
			return new String(bytes);
		}
		return rightPadKor(in, len);
	}

	public static String rightPadKor(String in, int len) {
		String fullIn = toFullChar(in);
		byte[] b = fullIn.getBytes();
		byte[] nb = new byte[len];
		Arrays.fill(nb, (byte) 0xA1);
		if (in != null) {
			int fillLen = b.length;
			if (fillLen > len) {
				System.arraycopy(b, 0, nb, 0, len);
			} else
				System.arraycopy(b, 0, nb, 0, fillLen);
		}
		logger.debug(fullIn + ":" + HexDumper.dump(nb));
		return new String(nb);
	}

	/**
	 * 반각문자로 변경한다
	 *
	 * @param src
	 *            변경할값
	 * @return String 변경된값
	 */
	public static String toHalfChar(String src) {
		StringBuffer strBuf = new StringBuffer();
		char c = 0;
		int nSrcLength = src.length();
		for (int i = 0; i < nSrcLength; i++) {
			c = src.charAt(i);
			// 영문이거나 특수 문자 일경우.
			if (c >= '！' && c <= '～') {
				c -= 0xfee0;
			} else if (c == '　') {
				c = 0x20;
			}
			// 문자열 버퍼에 변환된 문자를 쌓는다
			strBuf.append(c);
		}
		return strBuf.toString();
	}

	/**
	 * 전각문자로 변경한다.
	 *
	 * @param src
	 *            변경할값
	 * @return String 변경된값
	 */
	public static String toFullChar(String src) {
		// 입력된 스트링이 null 이면 null 을 리턴
		if (src == null)
			return null;
		// 변환된 문자들을 쌓아놓을 StringBuffer 를 마련한다
		StringBuffer strBuf = new StringBuffer();
		char c = 0;
		int nSrcLength = src.length();
		for (int i = 0; i < nSrcLength; i++) {
			c = src.charAt(i);
			// 영문이거나 특수 문자 일경우.
			if (c >= 0x21 && c <= 0x7e) {
				c += 0xfee0;
			}
			// 공백일경우
			else if (c == 0x20) {
				c = 0x3000;
			}
			// 문자열 버퍼에 변환된 문자를 쌓는다
			strBuf.append(c);
		}
		return strBuf.toString();
	}

	public static String toNumberString(String in, int len) {
		if (in == null) {
			byte[] bytes = new byte[len];
			Arrays.fill(bytes, (byte) 0x30);
			return new String(bytes);
		}

		long num = NumberUtils.toLong(in);
		return leftPad(String.valueOf(num), len, (byte) 0x30);
	}

	public static String toNumberString(int in, int len) {
		return leftPad(String.valueOf(in), len, (byte) 0x30);
	}

	public static String toNumberString(long in, int len) {
		return leftPad(String.valueOf(in), len, (byte) 0x30);
	}

	public static String getMTValue(byte[] mt, int curr, int len, String type) {
		byte[] value = new byte[len];
		for (int i = curr, k = 0; i < (curr + len); i++, k++) {
			value[k] = mt[i];
		}
		String ret = null;
		if (type.equals("AHN")) {
			ret = new String(value);
		}
		// TODO type이 N일때 number로 바꾸지 않아. 맞나?
		// else if (type.equals("N")) {
		// ret = String.valueOf(NumberUtils.toInt(new String(value)));
		// }
		else {
			ret = new String(value);
		}
		return ret;
	}

	public static String getEbcdicMTValue(byte[] mt, int curr, int len,
			String type) {
		byte[] value = new byte[len];
		boolean isOnlySpace = true;
		for (int i = curr, k = 0; i < curr + len; i++, k++) {
			value[k] = mt[i];
			if (value[k] != (byte) 0x20) {
				isOnlySpace = false;
			}
		}
		if (isOnlySpace) {
			return "";
		}
		String ret = null;
		if (type.indexOf("H") != -1) {
			ret = new String(ebcdic2Kr(value, len));
		} else {
			ret = new String(ebcdic2Ascii(value));
		}
		return ret;
	}

	public static byte[] makeMTBytes(String value, String type, String len) {
		return makeMTString(value, type, NumberUtils.toInt(len)).getBytes();
	}

	public static byte[] toHdrBytes(String value, String type, int len) {
		byte[] bytes = new byte[len + 1];
		System.arraycopy(makeMTString(value, type, len).getBytes(), 0, bytes,
				0, len);
		bytes[len] = (byte) 0x00;
		return bytes;
	}

	public static byte[] toHdrBytesIgnore(String value, String type, int len) {
		byte[] bytes = new byte[len + 1];
		System.arraycopy(makeMTStringIgnore(value, type, len).getBytes(), 0, bytes,
				0, len);
		bytes[len] = (byte) 0x00;
		return bytes;
	}

	public static byte[] toHdrBytesFill(String value,  int len , byte fillCode) {
		byte[] bytes = new byte[len + 1];
		Arrays.fill(bytes, fillCode);
		System.arraycopy(value.getBytes(), 0, bytes,0, value.getBytes().length);
		bytes[len] = (byte) 0x00;
		return bytes;
	}

	public static byte[] makeMTBytes(String value, String type, int len) {
		return makeMTString(value, type, len).getBytes();
	}

	public static String makeMTString(String value, String type, String len) {
		return makeMTString(value, type, NumberUtils.toInt(len));
	}

	public static String makeMTString(String value, String type, int len) {
		String ret = null;
		if (len == 0) {
			return value;
		}

		if (type.equals("AN") || type.equals("A")) {
			ret = MTUtil.toAsciiString(value, len);
		} else if (type.equals("N")) {
			ret = MTUtil.toNumberString(value, len);
		} else if (type.indexOf("H") != -1) {
			ret = MTUtil.toKSC5601String(value, len);
		} else if (type.equals("V")) {
			ret = value;
		} else if(type.equals("S")){
			ret = MTUtil.toStringFillNull(value, len);
		}
		return ret;
	}


	public static String makeMTStringIgnore(String value, String type, int len) {
		String ret = null;
		if (len == 0) {
			return value;
		}

		if (type.equals("AN") || type.equals("A")) {
			ret = MTUtil.toAsciiStringIgnore(value, len);
		} else if (type.equals("N")) {
			ret = MTUtil.toNumberString(value, len);
		} else if (type.indexOf("H") != -1) {
			ret = MTUtil.toKSC5601String(value, len);
		} else if (type.equals("V")) {
			ret = value;
		}
		return ret;
	}

	public static String toString(byte[] mt) {
		return new String(mt);
	}

	//
	// public static String getMTValue(byte[] rBytes, MTElement mtElement) {
	// String type = mtElement.getDataType();
	// int len = mtElement.getLength();
	// int curr = mtElement.getCumulativeLength();
	// return MTUtil.getMTValue(rBytes, curr - len, len, type);
	// }

	public static byte[] ebcdic2Ascii(byte[] ebcdicBytes) {
		int len = ebcdicBytes.length;
		byte[] eucBytes = null;
		try {
			eucBytes = new String(ebcdicBytes, "CP933").getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		// 컨버젼이 이상하게 되는게 있어 사이즈가 0으로 되면서.
		if (eucBytes.length == 0) {
			eucBytes = new byte[len];
			Arrays.fill(eucBytes, (byte) 0x20);
		}
		return eucBytes;
	}

	public static byte[] ebcdic2Kr(byte[] ebcdicBytes) {
		int len = ebcdicBytes.length;
		byte[] eucBytes = null;
		try {
			eucBytes = new String(ebcdicBytes, "CP933").getBytes("euc-kr");
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		// 컨버젼이 이상하게 되는게 있어 사이즈가 0으로 되면서.
		if (eucBytes.length == 0) {
			eucBytes = new byte[len];
			Arrays.fill(eucBytes, (byte) 0xA1);
		}
		return eucBytes;
	}

	public static byte[] ascii2Ebcdic(byte[] eucBytes) {
		byte[] ebcdicBytes = null;
		try {
			ebcdicBytes = new String(eucBytes, "euc-kr").getBytes("CP933");
		} catch (UnsupportedEncodingException e) {
			logger.error(e);
		}
		return ebcdicBytes;
	}

	public static byte[] ascii2EbcdicNoSoSi(byte[] eucBytes) {
		// logger.debug("len:" + eucBytes.length + " euc: "
		// + HexDumper.dump(eucBytes));
		byte[] ebcdicBytes = ascii2Ebcdic(eucBytes);
		// logger.debug("len:" + ebcdicBytes.length + " ebc:"
		// + HexDumper.dump(ebcdicBytes));
		ChannelBuffer bb = ChannelBuffers.dynamicBuffer(ebcdicBytes.length);
		int totalSosi = 0;
		for (int i = 0; i < ebcdicBytes.length; i++) {
			byte b = ebcdicBytes[i];
			if (b == SO || b == SI) {
				totalSosi++;
			} else {
				bb.writeByte(b);
			}
		}
		byte[] noSoSi = new byte[ebcdicBytes.length - totalSosi];
		// logger.debug("sosi Cnt:" + totalSosi);
		bb.readBytes(noSoSi);
		// logger.debug("len:" + noSoSi.length + " noS:" +
		// HexDumper.dump(noSoSi));
		return noSoSi;
	}

	public static byte[] ebcdic2Kr(byte[] ebcdicBytes, int len) {
		byte[] sosiBytes = new byte[len + 2];
		sosiBytes[0] = SO;
		sosiBytes[len] = SI;
		System.arraycopy(ebcdicBytes, 0, sosiBytes, 1, len);
		return ebcdic2Kr(sosiBytes);
	}

	public static byte[] makeMTNumberBytes(String i, int len) {
		byte[] bytes = new byte[len];
		Arrays.fill(bytes, (byte) 0x30);
		int nLen = i.getBytes().length;
		System.arraycopy(i.getBytes(), 0, bytes, bytes.length - nLen, nLen);
		return bytes;
	}

	public static byte[] toIpInfoToHex(String ipAddress){
		String pattern = "\\.";
		String[] arrIp = ipAddress.split(pattern);
		String retString ="";

		for( int ii=0; ii < arrIp.length; ii++){
			String tmp = "0" + Integer.toHexString(Integer.parseInt(arrIp[ii]));
			retString = retString + tmp.substring(tmp.length()-2,tmp.length());
		}
		return retString.getBytes();
	}
	public static byte[] toHexToIp(byte[] hexBytes){
		String hexString = new String(hexBytes);
		String retIp =  String.valueOf(Integer.parseInt(hexString.substring(0,2),16)) + "."
		+ String.valueOf(Integer.parseInt(hexString.substring(2,4),16)) + "."
		+ String.valueOf(Integer.parseInt(hexString.substring(4,6),16)) + "."
		+ String.valueOf(Integer.parseInt(hexString.substring(6,8),16));
		return retIp.getBytes();
	}
	public static String replaceChr(String inStr, char pattern, String replace){
        StringBuffer result = new StringBuffer();
        int ss = inStr.indexOf(pattern);
        result.append(inStr.substring(0,ss));
        result.append(replace);
        result.append(inStr.substring(ss+1,inStr.length()));
        String rr = result.toString();
        return rr;
	}
	public static String replace(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;
        StringBuffer result = new StringBuffer();
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            break;
            //s = e+pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

	public static byte[] makeMTNumberBytes(String i,
			MCIMTMappingTypeEnum mtType, int len) {
		byte[] bytes = new byte[len];
		int nLen = i.getBytes().length;
		switch (mtType) {
		case A:
			Arrays.fill(bytes, (byte) 0x20);
			System.arraycopy(i.getBytes(), 0, bytes, 0, nLen);
			break;
		case N:
			Arrays.fill(bytes, (byte) 0x30);
			System.arraycopy(i.getBytes(), 0, bytes, bytes.length - nLen, nLen);
			break;
		}
		return bytes;
	}

	public static byte[] makeMTBytes(byte[] b, MCIMTMappingTypeEnum mtType,
			int len) {

		if (mtType == MCIMTMappingTypeEnum.V) {
			return b;
		}

		byte[] ret = null;
		if (len == 0) {
			return ret;
		}

		ret = new byte[len];
		switch (mtType) {
		case A:
			b = (new String(b)).trim().getBytes();		//null로 들어온값도 처리하기위해서.
			Arrays.fill(ret, (byte) 0x20);
			if (b.length > len) {
				System.arraycopy(b, 0, ret, 0, len);
			} else {
				System.arraycopy(b, 0, ret, 0, b.length);
			}
			break;
		case KHH: // 반각 한글
			Arrays.fill(ret, (byte) 0x20);
			if (b.length > len) {
				System.arraycopy(b, 0, ret, 0, len);
			} else {
				System.arraycopy(b, 0, ret, 0, b.length);
			}
			break;
		case KFH: // 전각 한글
			Arrays.fill(ret, (byte) 0xA1);
			String fullIn = toFullChar(new String(b));
			byte[] fb = fullIn.getBytes();
			if (fb.length > len) {
				System.arraycopy(b, 0, ret, 0, len);
			} else {
				System.arraycopy(b, 0, ret, 0, b.length);
			}
			break;
		case EH: // EBCIDIC
			ret = toEbcdicBytes(ascii2Ebcdic(b), len);
			break;
		case EHNOSOSI: // EBCIDIC NO SOSI
			ret = toEbcdicBytes(ascii2EbcdicNoSoSi(b), len);
			break;
		}
		return ret;
	}

	public static String replaceNullChar(String orginal){
		return orginal.replace((char)0x00, '^');
	}

	/**
	 *
	 * @param dataStr : MCI String Data
	 * @param idx : (ex) 0,6 or 97
	 * @return substring String data
	 */
	public static String getMciString(String dataStr, String idx){
	      //logger.debug("#########dataStr:"+dataStr);
	      //logger.debug("#########idx:"+idx);
	      String [] index = idx.split(",");
	      int beginIndex=0;
	      int endIndex=0;
	      String returnData=null;
	      if(index.length==2){
	            beginIndex = Integer.parseInt(index[0]);
	            endIndex = Integer.parseInt(index[1]);
	            returnData=dataStr.substring(beginIndex, beginIndex+endIndex);
	      }else{
	            beginIndex = Integer.parseInt(index[0]);
	            returnData=dataStr.substring(beginIndex, dataStr.length());
	      }

	      return returnData;
	}

}
