package com.indigo.esb.adaptor.socket.type;

public class SocketFileResponseCode{

      public static final String SUCCESS           = "0000"; //성공
      public static final String TIMEOUT           = "0001"; //타임아웃
      public static final String AP_FILE_NOT_FOUND = "0002"; //송신측에서 보낼 파일이 없음
      public static final String TP_FILE_NOT_FOUND = "0003"; //수신측에서 보낼 파일이 없음
      public static final String TP_NOT_CONNECT    = "0004"; //TP Demon not connect
      public static final String IFID_NOT_FOUND    = "0005"; //TP Demon not connect
/*      public static final String HEADER_LENGTH_CHECK    = "0005"; //Header길이 맞지않음
      public static final String APP_ERROR    = "0006"; //응용프로그램단 에러
*/
}
