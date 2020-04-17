package com.indigo.esb.db;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service("myBatisSupport")
public class MyBatisSupport {

 @Autowired(required = false)
 @Qualifier("sqlSession")
 protected SqlSessionTemplate sqlSession;

 @Autowired
 ApplicationContext applicationContext;

 public FileToDbTransactionManager getTransactionManager() {
  return applicationContext.getBean(FileToDbTransactionManager.class);
 }
}