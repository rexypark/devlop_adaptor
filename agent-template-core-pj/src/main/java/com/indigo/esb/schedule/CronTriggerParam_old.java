package com.indigo.esb.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CronTriggerParam_old {
	private static Log log = LogFactory.getLog(CronTriggerParam_old.class);
	private String cronExpression;
	private String argrument;
	private boolean concurrent = true;
	private String targetMethod = "onSignal";

	public CronTriggerParam_old(String paramStr) throws Exception {
		log.debug("param=>" + paramStr);

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
	}

	private void parsingNameValue(String paramStr) {
		int idxLastBrace = paramStr.indexOf("]");
		String[] contents = paramStr.substring(1, idxLastBrace).split(",");

		for (int i = 0; i < contents.length; ++i) {
			String[] keyvalue = contents[i].trim().split("=");

			if ("cronExpression".equalsIgnoreCase(keyvalue[0].trim())) {
				if (keyvalue.length != 2)
					continue;
				this.cronExpression = keyvalue[1].trim();
			} else if ("args".equalsIgnoreCase(keyvalue[0].trim())) {
				if (keyvalue.length != 2)
					continue;
				setArgrument(keyvalue[1].trim());
			} else if ("concurrent".equalsIgnoreCase(keyvalue[0].trim())) {
				if ("true".equalsIgnoreCase(keyvalue[1].trim()))
					setConcurrent(true);
			} else if (("targetMethod".equalsIgnoreCase(keyvalue[0].trim())) && (keyvalue.length == 2)) {
				setTargetMethod(keyvalue[1].trim());
			}
		}
	}

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
}