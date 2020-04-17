package hive.target;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Starter {
	
	public static void main(String[] args) {
        DOMConfigurator.configureAndWatch("log4j.xml");
        new ClassPathXmlApplicationContext("hive/target/bean.xml");
	}

}
