package rfdb.source;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author clupine-mb
 * 대용량 db to db를 jms를 안거치고 소켓 베이스 
 */
public class Starter {
	
	public static void main(String[] args) {
        DOMConfigurator.configureAndWatch("log4j.xml");
        new ClassPathXmlApplicationContext("rfdb/source/bean.xml");
	}

}
