import org.apache.commons.codec.binary.BinaryCodec;

import com.mb.mci.common.util.HexViewer;



public class BinaryTest {
	
	public static void main(String[] args) {
		
		String bitStr = "00000011";
		byte[] data = BinaryCodec.fromAscii(bitStr.getBytes());
		try {
			System.out.println(HexViewer.view(data));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String binStr = BinaryCodec.toAsciiString(data);
		
		System.out.println(binStr);
		
	}
	
	

}
