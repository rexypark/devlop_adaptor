package DB_DB_RECV_01;

import org.apache.log4j.xml.DOMConfigurator;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Starter {
	
	public static void main(String[] args) throws Exception {
        DOMConfigurator.configureAndWatch("log4j.xml");
        
        ApplicationContext ctx = new ClassPathXmlApplicationContext("bean.xml");
        BeanDefinitionRegistry  registry = (BeanDefinitionRegistry) ctx.getAutowireCapableBeanFactory();
        registry.removeBeanDefinition("adaptorBean");
        
        
        
	}
	



}
