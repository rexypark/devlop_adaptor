package db.target;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Starter {
	
	public static void main(String[] args) {
        DOMConfigurator.configureAndWatch("log4j.xml");
        new ClassPathXmlApplicationContext("db/target/bean.xml");
	}

}
