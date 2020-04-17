package file;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StartMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DOMConfigurator.configureAndWatch("log4j.xml");
		new ClassPathXmlApplicationContext("file/snd/bean.xml");
//		new ClassPathXmlApplicationContext("file/rcv/bean.xml");
	}

}
