import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MSGTest {

	public static void main(String[] args) throws Exception {
		
		System.out.println("172.00.1.2".replaceAll("\\.", "_"));

//		byte[] reqSoap = Files.readAllBytes(Paths.get("d:/40.indigo", "reqSoap.xml"));
		byte[] reqSoap = ("<SOAP:aaa>\r\n" + 
				"<ds:SignedInfo>\r\n" + 
				"</ds:SignedInfo>\r\n" + 
				"<다른태그들></다른태그들>\r\n" + 
				"<ds:KeyValue>\r\n" + 
				"<ds:RSAKeyValue>\r\n" + 
				"1.KeyValue\r\n" + 
				"</ds:RSAKeyValue>\r\n" + 
				"</ds:KeyValue>\r\n" + 
				"<ds:X509Data>\r\n" + 
				"<ds:X509SubjectName>\r\n" + 
				"2.X509Data\r\n" + 
				"</ds:X509SubjectName>\r\n" + 
				"</ds:X509Data>\r\n" + 
				"<다른태그들></다른태그들>\r\n" + 
				"</SOAP:aaa>").getBytes();
		System.out.println("원본 Start----------------");
		System.out.println(new String(reqSoap));
		System.out.println("원본 End----------------");
		
		new MSGTest().test(reqSoap);

	}

	public void test(byte[] reqSoap) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		baos.write(reqSoap);

		byte[] newEbxmlBytes = reArrangeKeyvalueAndX509DataElement(baos);

		System.out.println("\n변환 후 Start----------------");
		System.out.println(new String(newEbxmlBytes));
		System.out.println("변환 후 End----------------");
		
	}

	private byte[] reArrangeKeyvalueAndX509DataElement(ByteArrayOutputStream baos) {
		byte[] oldEbxmlBytes = baos.toByteArray();

		String old = new String(oldEbxmlBytes);

		String keyValStart = "<ds:KeyValue>";
		String keyValEnd = "</ds:KeyValue>";
		int keyValStartIdx = old.indexOf(keyValStart);
		int keyValEndIdx = old.indexOf(keyValEnd);

		String x509Start = "<ds:X509Data>";
		String x509End = "</ds:X509Data>";
		int x509StartIdx = old.indexOf(x509Start);
		int x509EndIdx = old.indexOf(x509End);

		// 만약 element가 하나라도 없으면 그대로 리턴
		if (keyValStartIdx < 0 || x509StartIdx < 0) {
			return oldEbxmlBytes;
		}

		// keyVal 태그가 X509 태그 뒤에 있으면 그대로 리턴
		if (keyValStartIdx > x509StartIdx) {
			return oldEbxmlBytes;
		}

		StringBuffer sb = new StringBuffer();
		sb.append(old.substring(0, keyValStartIdx));
		sb.append(old.substring(x509StartIdx, x509EndIdx + x509End.length()));
		//keyValue End tag 와 X509Data Start tag 사이의 내용추가
		sb.append(old.substring(keyValEndIdx + keyValEnd.length(), x509StartIdx));
		sb.append(old.substring(keyValStartIdx, keyValEndIdx + keyValEnd.length()));
		sb.append(old.substring(x509EndIdx + x509End.length()));
		
		String newEbxmlStr = sb.toString();
		return newEbxmlStr.getBytes();
	}
}
