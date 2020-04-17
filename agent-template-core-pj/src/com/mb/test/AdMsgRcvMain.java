package com.mb.test;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AdMsgRcvMain {

	/**
	 * @param args
	 */
    
	public static void main(String[] args) {
		DOMConfigurator.configureAndWatch("configure/log4j.xml");
		new ClassPathXmlApplicationContext("db/mybatis/test_rcv_bean.xml");
		//new ClassPathXmlApplicationContext("file/rcv_bean.xml");
	}

}
