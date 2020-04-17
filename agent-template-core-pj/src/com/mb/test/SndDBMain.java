package com.mb.test;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SndDBMain {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DOMConfigurator.configureAndWatch("log4j.xml");
		new ClassPathXmlApplicationContext("p04/snd/bean.xml");
		// new ClassPathXmlApplicationContext("db/mybatis/p02_rcv_bean.xml");
	}

}
