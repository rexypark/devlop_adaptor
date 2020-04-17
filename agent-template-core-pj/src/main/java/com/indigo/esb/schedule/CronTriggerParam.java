package com.indigo.esb.schedule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CronTriggerParam {
	private static Log log = LogFactory.getLog(CronTriggerParam.class);
	private String cronExpression;
	private String argrument;
	private boolean concurrent = true;
	private String targetMethod = "onSignal";

	private final String cronExpressionKey = "cronExpression";
	private final String argsKey = "args";
	private final String concurrentKey = "concurrent";
	private final String targetMethodKey = "targetMethod";
	
	public CronTriggerParam(String paramStr) throws Exception {
		log.debug("param=>" + paramStr);
		System.out.println("param=>" + paramStr);

		if (paramStr.charAt(0) == '[')
			parsingNameValue(paramStr);
		else {
			parsingDelimiter(paramStr);
		}

		if ((this.cronExpression == null) || (this.cronExpression.equals(""))) {
			log.error("cron schedule cronExpression is null or nullstring");
			throw new Exception("cron schedule cronExpression is null or nullstring");
		}

		log.debug("[cronExpression=" + this.cronExpression + "," + "argrument=" + this.argrument + "," + "concurrent="
				+ this.concurrent + "," + "targetMethod=" + this.targetMethod + "]");
		System.out.println("[cronExpression=" + this.cronExpression + "," + "argrument=" + this.argrument + "," + "concurrent="+ this.concurrent + "," + "targetMethod=" + this.targetMethod + "]");
	}

	private void parsingNameValue(String paramStr) {
		
		paramStr = paramStr.replace("[", "").replace("]", "");
		
		int cronExpIdx = paramStr.indexOf(cronExpressionKey);
		int argsIdx = paramStr.indexOf(argsKey);
		int concurrentIdx = paramStr.indexOf(concurrentKey);
		int targetMethodIdx = paramStr.indexOf(targetMethodKey);
		
		Map<Integer,String> indexMap = new HashMap<Integer,String>();
		indexMap.put(cronExpIdx, cronExpressionKey);
		indexMap.put(argsIdx, argsKey);
		indexMap.put(concurrentIdx, concurrentKey);
		indexMap.put(targetMethodIdx, targetMethodKey);
		Set<Integer> indexSet = indexMap.keySet();
		List<Integer> indexList = new ArrayList<Integer>(indexSet);
		Collections.sort(indexList);

		int finalIdx = indexList.size() - 1;
		for (int i = 0; i < indexList.size(); i++) {
			
			int nowIndex = indexList.get(i);
			int nextIndex = -1;
			
			if(finalIdx!=i){
				nextIndex = indexList.get(i+1);
			}
			
			if(nowIndex != -1){
				String key = indexMap.get(nowIndex);
				String value = "";
				if(nextIndex==-1){
					value = paramStr.substring(nowIndex);
				}else{
					value = paramStr.substring(nowIndex,nextIndex-1);
				}
				value = value.split("=")[1].trim();
				
				if(value.lastIndexOf(",") == value.length()-1){
					value = value.substring(0, value.lastIndexOf(",")-1);
					value = value.trim();
				}
				
				if(key.equals(cronExpressionKey)){
					cronExpression = value;
				}else if(key.equals(argsKey)){
					argrument = value;
				}else if(key.equals(concurrentKey)){
					concurrent = Boolean.valueOf(value);
				}else if(key.equals(targetMethodKey)){
					targetMethod = value;
				}
			}
		}
	}
	
//	private void parsingNameValue(String paramStr) {
//		int idxLastBrace = paramStr.indexOf("]");
//		String[] contents = paramStr.substring(1, idxLastBrace).split(",");
//		
//		for (int i = 0; i < contents.length; ++i) {
//			
//			String[] keyvalue = contents[i].trim().split("=");
//			
//			if ("cronExpression".equalsIgnoreCase(keyvalue[0].trim())) {
//				if (keyvalue.length != 2)
//					continue;
//				this.cronExpression = keyvalue[1].trim();
//			} else if ("args".equalsIgnoreCase(keyvalue[0].trim())) {
//				if (keyvalue.length != 2)
//					continue;
//				setArgrument(keyvalue[1].trim());
//			} else if ("concurrent".equalsIgnoreCase(keyvalue[0].trim())) {
//				if ("true".equalsIgnoreCase(keyvalue[1].trim()))
//					setConcurrent(true);
//			} else if (("targetMethod".equalsIgnoreCase(keyvalue[0].trim())) && (keyvalue.length == 2)) {
//				setTargetMethod(keyvalue[1].trim());
//			}
//		}
//	}

	private void parsingDelimiter(String paramStr) {
		String[] params = paramStr.split(",");

		if (params.length == 1) {
			setCronExpression(paramStr.trim());
		} else if (params.length == 2) {
			setCronExpression(params[0].trim());
			setArgrument(params[1].trim());
		} else if (params.length == 3) {
			setCronExpression(params[0].trim());
			setArgrument(params[1].trim());
			if ("true".equalsIgnoreCase(params[2].trim()))
				setConcurrent(true);
		} else if (params.length == 4) {
			setCronExpression(params[0].trim());
			setArgrument(params[1].trim());
			if ("true".equalsIgnoreCase(params[2].trim())) {
				setConcurrent(true);
			}
			if (!("".equalsIgnoreCase(params[3].trim())))
				setTargetMethod(params[3].trim());
		}
	}

	public String getCronExpression() {
		return this.cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getArgrument() {
		return this.argrument;
	}

	public void setArgrument(String argrument) {
		this.argrument = argrument;
	}

	public boolean getConcurrent() {
		return this.concurrent;
	}

	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}

	public String getTargetMethod() {
		return this.targetMethod;
	}

	public void setTargetMethod(String targetMethod) {
		this.targetMethod = targetMethod;
	}
	
	
	//Test
	public static void main(String[] args) throws Exception {
		String data = "[args=TEST-DB-IF1    ,   cronExpression=0/10 5,10,11,12 * * * ?]";
		CronTriggerParam param = new CronTriggerParam(data);
		System.out.println(param.cronExpression);
	}
}