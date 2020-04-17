package com.indigo.esb.route;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;

import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

public class DBRouteBean implements InitializingBean {

	protected transient Logger logger = LoggerFactory.getLogger(DBRouteBean.class);
	
	protected Map<String, List<Map<String, Object>>> oldRouteMap;

	protected CountDownLatch latch;

	protected SqlSession sqlSession;
	
	protected Properties routeProp;
	
	protected boolean discardMessage = true;
	
	protected String failRedirectQueue = "ESB.ROUTE.FAIL";

	protected String propertiesPath = "./service/conf/hub-trace-route.properties";
	
	final String DISCARD_MESSAGE_KEY = "route.discard.message";
	
	final String FAIL_REDIRECT_QUEUE_KEY = "route.fail.redirect.queue";
	
	protected String discardMessageId = "discardMessage";
	
	final AtomicInteger noChangeCnt = new AtomicInteger(0);

	final AtomicInteger noChangeLogCnt = new AtomicInteger(0);
	

	public void afterPropertiesSet() throws Exception {
		try {
			routePropertiesRefresh();
			List<Map<String, Object>> routeList = getSqlSession().selectList("ROUTE_SELECT");
			this.oldRouteMap = makeRouteInfomation(routeList);
			logger.info("### Route InforMation Init , discardMessage = " + discardMessage + "  , failRedirectQueue = "
					+ failRedirectQueue);
		} catch (Exception localException) {
			logger.error("초기화에러",localException);
		}
	}

	@Scheduled(fixedRate = 60000)
	public void refreshRouteData() throws Exception {
		List<Map<String, Object>> routeList = getSqlSession().selectList("ROUTE_SELECT");
		Map<String, List<Map<String, Object>>> newRouteMap = makeRouteInfomation(routeList);

		MapDifference<String, List<Map<String, Object>>> md = Maps.difference(oldRouteMap, newRouteMap);
		if (md.areEqual() == false) {
			latch = new CountDownLatch(1);
			oldRouteMap = newRouteMap;
			latch.countDown();
			logger.info("### Route InforMation refresh , discardMessage = " + discardMessage + "  , failRedirectQueue = "
					+ failRedirectQueue);
			if (md.entriesOnlyOnLeft().size() > 0) {
				logger.info("삭제 RouteInfo:" + md.entriesOnlyOnLeft());
			}
			if (md.entriesOnlyOnRight().size() > 0) {
				logger.info("추가 RouteInfo:" + md.entriesOnlyOnRight());
			}
			if (md.entriesDiffering().size() > 0) {
				logger.info("변경 RouteInfo:" + md.entriesDiffering());
			}
			noChangeCnt.set(0);
			noChangeLogCnt.set(0);

		} else {
			noChangeCnt.incrementAndGet();
			int exp = (int) Math.round(Math.pow(2, noChangeLogCnt.get()));
			//1, 2, 4, 8, 16, 32, 64
			if(noChangeCnt.get() >= exp) {
				logger.info("### Route InforMation refresh : no Change");
				noChangeLogCnt.incrementAndGet();
				if(noChangeLogCnt.get() > 6) {
					noChangeLogCnt.set(6);
					noChangeCnt.set(0);
				}
			}
			
		}
		routePropertiesRefresh();

	}

	private void routePropertiesRefresh() {
		File routePropFile = new File(propertiesPath);

		if (routePropFile.exists()) {
			routeProp = new Properties();
			try {
				routeProp.load(new FileInputStream(routePropFile));
				discardMessage = Boolean.valueOf(routeProp.getProperty(DISCARD_MESSAGE_KEY, "true"));
				failRedirectQueue = routeProp.getProperty(FAIL_REDIRECT_QUEUE_KEY, "ESB.ROUTE.FAIL");
			} catch (FileNotFoundException e) {
				logger.error("routePropertiesRefresh",e);
			} catch (IOException e) {
				logger.error("routePropertiesRefresh",e);
			}
		}
	}

	public Map<String, List<Map<String, Object>>> makeRouteInfomation(List<Map<String, Object>> routeList)
			throws Exception {
		Map<String, List<Map<String, Object>>> ret = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> routeData : routeList) {
			addRouteMap(ret, routeData.get("IF_ID").toString(), routeData);
		}
		return ret;
	}

	public void addRouteMap(Map<String, List<Map<String, Object>>> map, String if_id, Map<String, Object> routeData) {
		if (map.get(if_id) != null) {
			List<Map<String, Object>> list = map.get(if_id);
			list.add(routeData);
			map.put(if_id, list);
		} else {
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			list.add(routeData);
			map.put(if_id, list);
		}
	}
	
	public Map<String, List<Map<String, Object>>> getRouteMap() {
		if (latch == null)
			return oldRouteMap;
		try {
			latch.await(1000, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			logger.error("",e);
		}
		return oldRouteMap;
	}


	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
	}

	public SqlSession getSqlSession() {
		return sqlSession;
	}

	/*
	 * public void setRouteMap(Map<String, List<Map<String, Object>>> routeMap) {
	 * this.routeMap = routeMap; }
	 */

	public String getFailRedirectQueue() {
		return failRedirectQueue;
	}

	public boolean isDiscardMessage() {
		return discardMessage;
	}

}