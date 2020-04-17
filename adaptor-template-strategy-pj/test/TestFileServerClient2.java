import com.indigo.fileserver.client.IndigoFileTransferAPI;


public class TestFileServerClient2{
	
	public static void xtestSimpleSend(){
		IndigoFileTransferAPI api = new IndigoFileTransferAPI("127.0.0.1", 4321);
		api.setRepository("c:\\temp\\z99\\send");
		try {
			String file = "D:\\xxxxx\\시연_시나리오_20150527.pptx";
			api.simpleFilePut("",file);
		} catch (Exception e) {
			e.printStackTrace(); 
		}
	}
	
	public static void main(String[] args) {
		xtestSimpleSend();
	}
	
//	public  void testSimpleReceive(){
//		//IndigoFileServerAPI api = new IndigoFileServerAPI("TEST.001", "10.1.3.183", 4321);
//		IndigoFileTransferAPI api = new IndigoFileTransferAPI( "127.0.0.1", 4321);
//		api.setRepository("d:\\");
//		api.setChunkSize(1024*10);
//		try {
//			//api.simpleFileGet("tomcat-juli.jar");
//			api.simpleFileGet("xxxxx","시연_시나리오_20150527.pptx",true);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
}
