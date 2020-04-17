package rfdb.target;

import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Starter {
	
	protected static final Logger logger = LoggerFactory.getLogger("Starter");
	
	public static void main(String[] args) {
		
        DOMConfigurator.configureAndWatch("log4j.xml");
        new ClassPathXmlApplicationContext("rfdb/target/bean.xml");
//        new ClassPathXmlApplicationContext("rfdb/target/dummy.xml");
	}
	
// [2014-11-26 01:39:06.968] 
// [2014-11-26 01:43:17.466] 4:11  4000건  
	
//	[2014-11-26 01:45:48.365] 4:24 1만건
//	[2014-11-26 01:50:12.879] 
	
//[2014-11-26 02:07:55.037]
	
//	[2014-11-26 16:25:53.122]	INFO	New I/O worker #1	 channelConnected (ObjectReceiveHandler.java:27) 	연결[id: 0xf99d0a25, /127.0.0.1:9317 => /127.0.0.1:5234]
	
	
//	[2014-11-26 16:31:03.195]	INFO	New I/O worker #1	 channelConnected (ObjectReceiveHandler.java:27) 	연결[id: 0xc4ca856c, /127.0.0.1:9830 => /127.0.0.1:5234]
//	[2014-11-26 16:31:43.226]	INFO	pool-4-thread-39	 doInTransactionWithoutResult (DBInsertService.java:100) 	commit
}
