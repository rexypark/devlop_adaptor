import java.io.IOException;
import java.io.PrintWriter;
import java.net.SocketException;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FtpsTest {
	
	private static final Logger logger = LoggerFactory.getLogger(FtpsTest.class);

	public static void main(String[] args) throws SocketException, IOException {
		DOMConfigurator.configureAndWatch("log4j.xml");
		FTPSClient ftps	= new FTPSClient("SSL" , true);
		ftps.setAuthValue("indigo");
		ftps.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out)));
		ftps.connect("clupine.ipost.kr", 24400);
		ftps.login("indigo", "indigo");
		int reply = ftps.getReplyCode();
		
		logger.info("reply Code : " + reply);
		logger.info("reply Code : " + ftps.listNames());
		FTPFile[] files = ftps.listFiles();
		logger.info("file size : " + files.length);
		for (FTPFile ftpFile : files) {
			logger.info(ftpFile.getName());
		}
		ftps.logout();
		ftps.disconnect();
		
	}
}
