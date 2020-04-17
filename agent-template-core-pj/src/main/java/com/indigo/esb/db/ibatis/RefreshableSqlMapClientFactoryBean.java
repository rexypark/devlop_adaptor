package com.indigo.esb.db.ibatis;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.io.Resource;
import org.springframework.orm.ibatis.SqlMapClientFactoryBean;

import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.engine.impl.ExtendedSqlMapClient;

/**
 * iBATIS sqlmap Ŭ���̾�Ʈ�� sqlMap �� sqlMapConfig ������ ������ ����, �ǽð� �����ϴ� ���丮 ��.
 * 
 * <h2>����</h2>
 * iBATIS + Spring ���߽� ���� ���� ������ ����Ǹ� �����ø����̼� ������ ��⵿�ؾ� ������ �ƾ���. �̷��� ������ ���ֱ�
 * ���� ���� ���� ������ �ǽð����� ����, �����ϴ� ����� �����Ѵ�.<br />
 * 
 * ���� ��� �� ����� iBATIS sqlmap Ŭ���̾�Ʈ�� sqlMap �� sqlMapConfig ������ ������ ����, �ǽð� �������ش�.<br />
 * 
 * <h2>�������</h2>
 * ���� ��� ���ϵ��� ��ŸƮ�� ��ÿ� �����ȴ�. �׷��Ƿ� �߰��� ���ϵ鿡 ���ؼ��� ������ ���� �ʰ�, ������ ���ϵ鿡 ���ؼ��� ���
 * �޽����� ���´�. ���� ���, sqlMapConfig�� sqlMap ������ �߰��ǰų� �ϸ� �ش� ���� ����Ǳ�� ������, �ǽð� ���� ����
 * ������� �߰������� �ʴ´�.<br />
 * 
 * <h2>�䱸����</h2>
 * iBATIS sqlmap 2.3.0, Java 1.4, Spring 2.5 �̻� �Ǵ� iBATIS sqlmap 2.3.2 �̻�,<br />
 * Java 1.5 �̻�, Spring 2.5.5 �̻�<br />
 * 
 * <h2>���� ����</h2>
 * 1. Spring�� applicationContext ���� ���� �� sqlMapClient�� ��� ����
 * SqlMapClientFactory ���� �ű� Ŭ������ ��ü�Ѵ�. <br />
 * 2. ���� ���� �ð� ���� (1000���� 1�� ����)�� �����Ѵ�.<br />
 * <PRE>
 * &lt;bean id="sqlMapClient" class="jcf.dao.ibatis.sqlmap.RefreshableSqlMapClientFactoryBean"&gt; 
 *     &lt;!-- &lt;bean id="sqlMapClient" class="org.springframework.orm.ibatis.SqlMapClientFactoryBean"&gt;--&gt; 
 *     &lt;property name="configLocation" value="classpath:jcf/dao/ibatis/sqlmap/sqlmap-config.xml" /&gt; 
 *     &lt;property name="dataSource" ref="dataSource" /&gt;
 * 
 *     &lt;!-- Java 1.5 or higher and iBATIS 2.3.2 or higher REQUIRED --&gt; 
 *     &lt;property name="mappingLocations" value="jcf/dao/\*\*\/T\*.xml" /&gt; 
 *     &lt;!-- &lt;property name="mappingLocations" value="file:///D:/Type.xml" /&gt;--&gt;
 * 
 *     &lt;property name="checkInterval" value="1000" /&gt; 
 * &lt;/bean&gt;
 * </PRE>
 *
 * 3. ���̺귯�� �߰� <br />
 * backport-util-concurrent-3.1.jar<br />
 * 
 * �׽�Ʈ ������ applicationContext�� ��ϵǾ� �ִ� sqlMapClient�� �ش��ϴ� mappingFile���� �����ϰų�,
 * sqlMapConfig ���� �Ǵ� �� ���Ͽ� ��ϵ� mappingFile(sqlMap ����)���� �����ϸ� �־��� ���氨�� �ð� �� ��������� ����ȴ�.<br />
 * <br />
 * 
 * @author setq
 */
public class RefreshableSqlMapClientFactoryBean extends SqlMapClientFactoryBean
		implements SqlMapClientRefreshable, DisposableBean {
	
	private static final Log logger = LogFactory
			.getLog(RefreshableSqlMapClientFactoryBean.class);
	
	private SqlMapClient proxy;
	private int interval;
	
	private Timer timer;
	private TimerTask task;
	
	private Resource[] configLocations;
	
	private Resource[] mappingLocations;
	
	/**
	 * ���� ���� �����尡 ���������� ����.
	 */
	private boolean running = false;
	
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();
	
	@Override
	public void setConfigLocation(Resource configLocation) {
		super.setConfigLocation(configLocation);
		this.configLocations = (configLocation != null ? new Resource[] { configLocation }
				: null);
	}
	
	@Override
	public void setConfigLocations(Resource[] configLocations) {
		super.setConfigLocations(configLocations);
		this.configLocations = configLocations;
	}
	
	@Override
	public void setMappingLocations(Resource[] mappingLocations) {
		super.setMappingLocations(mappingLocations);
		this.mappingLocations = mappingLocations;
	}
	
	/**
	 * iBATIS ������ �ٽ� �о���δ�.<br /> SqlMapClient �ν��Ͻ� ��ü�� ���� �����Ͽ� ��ü�Ѵ�.
	 * 
	 * @throws Exception
	 */
	@Override
	public void refresh() throws Exception {
		/*
		 * WRITE LOCK.
		 */
		w.lock();
		try {
			super.afterPropertiesSet();
			
		} finally {
			w.unlock();
		}
	}
	
	/**
	 * �̱��� ����� SqlMapClient ���� ��� ���Ͻ÷� �����ϵ��� �������̵�.
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();
		
		setRefreshable();
	}
	
	private void setRefreshable() {
		proxy = (SqlMapClient) Proxy.newProxyInstance(SqlMapClient.class
				.getClassLoader(), new Class[] { SqlMapClient.class,
				ExtendedSqlMapClient.class }, new InvocationHandler() {
			
			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				
				return method.invoke(getParentObject(), args);
			}
			
		});
		
		task = new TimerTask() {
			
			private Map map = new HashMap();
			
			@Override
			public void run() {
				
				if (isModified()) {
					try {
						refresh();
					}
					catch (Exception e) {
						logger.error("caught exception", e);
					}
				}
				
			}
			
			private boolean isModified() {
				boolean retVal = false;
				
				for (int i = 0; i < configLocations.length; i++) {
					Resource configLocation = configLocations[i];
					retVal |= findModifiedResource(configLocation);
				}
				
				if (mappingLocations != null) {
					for (int i = 0; i < mappingLocations.length; i++) {
						Resource mappingLocation = mappingLocations[i];
						retVal |= findModifiedResource(mappingLocation);
					}
				}
				
				return retVal;
			}
			
			private boolean findModifiedResource(Resource resource) {
				boolean retVal = false;
				List modifiedResources = new ArrayList();
				
				try {
					long modified = resource.lastModified();
					
					if (map.containsKey(resource)) {
						long lastModified = ((Long) map.get(resource)).longValue();
						
						if (lastModified != modified) {
							map.put(resource, new Long(modified));
							modifiedResources.add(resource.getDescription());
							retVal = true;
						}
						
					}
					else {
						map.put(resource, new Long(modified));
					}
				}
				catch (IOException e) {
					logger.error("caught exception", e);
				}
				
				if (retVal) {
//					if (logger.isInfoEnabled()) {
//						logger.info("modified files : " + modifiedResources);
//					}
					logger.info("modified files : " + modifiedResources);
				}
				return retVal;
			}
			
		};
		
		timer = new Timer(true);
		resetInterval();
		
		
		List mappingLocationList = extractMappingLocations(configLocations);
		
		if (this.mappingLocations != null) {
			mappingLocationList.addAll(Arrays.asList(this.mappingLocations));
		}
		this.mappingLocations = (Resource[]) mappingLocationList.toArray(new Resource[0]);
	}
	
	private List extractMappingLocations(Resource[] configLocations) {
		List mappingLocationList = new ArrayList();
		SqlMapExtractingSqlMapConfigParser configParser = new SqlMapExtractingSqlMapConfigParser();
		for (int i = 0; i < configLocations.length; i++) {
			try {
				InputStream is = configLocations[i].getInputStream();
				mappingLocationList.addAll(configParser.parse(is));
			}
			catch (IOException ex) {
				logger.warn("Failed to parse config resource: "
						+ configLocations[i], ex.getCause());
			}
		}
		return mappingLocationList;
	}

	private Object getParentObject() {
		/*
		 * READ LOCK.
		 */
		r.lock();
		try {
			return super.getObject();
			
		} finally {
			r.unlock();
		}
	}
	
	@Override
	public SqlMapClient getObject() {
		return this.proxy;
	}
	
	@Override
	public Class getObjectType() {
		return (this.proxy != null ? this.proxy.getClass() : SqlMapClient.class);
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}
	
	@Override
	public void setCheckInterval(int ms) {
		interval = ms;
		
		if (timer != null) {
			resetInterval();
		}
	}
	
	private void resetInterval() {
		if (running) {
			timer.cancel();
			running = false;
		}
		if (interval > 0) {
			timer.schedule(task, 0, interval);
			running = true;
		}
	}

	@Override
	public void destroy() throws Exception {
		timer.cancel();
	}
	
}

