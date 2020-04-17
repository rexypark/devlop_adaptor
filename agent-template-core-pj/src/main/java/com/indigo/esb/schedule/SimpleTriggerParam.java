package com.indigo.esb.schedule;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleTriggerParam {
	private Log log = LogFactory.getLog(SimpleTriggerParam.class);
	private int startDelay;
	private int interval;
	private String argrument;
	private boolean concurrent = true;
	private String targetMethod = "onSignal";

	public SimpleTriggerParam(String paramStr) throws Exception {
		this.log.debug("receive param=>" + paramStr);

		if (paramStr.charAt(0) == '[')
			parseNameValue(paramStr);
		else {
			parseDelimiter(paramStr);
		}

		if (getInterval() == 0) {
			this.log.error("schedule interval is 0 or null");
			throw new Exception("schedule interval is 0 or null");
		}

		this.log.debug("[startDelay=" + this.startDelay + "," + "interval=" + this.interval + "," + "args="
				+ this.argrument + "," + "concurrent=" + this.concurrent + "," + "targetMethod=" + this.targetMethod
				+ "]");
	}

	private void parseNameValue(String paramStr) {
		int idxLastBrace = paramStr.indexOf("]");
		String[] contents = paramStr.substring(1, idxLastBrace).split(",");

		for (int i = 0; i < contents.length; ++i) {
			String[] keyvalue = contents[i].trim().split("=");

			if ("startDelay".equalsIgnoreCase(keyvalue[0].trim())) {
				if (keyvalue.length != 2)
					continue;
				setStartDelay(Integer.parseInt(keyvalue[1].trim()));
			} else if ("interval".equalsIgnoreCase(keyvalue[0].trim())) {
				if (keyvalue.length != 2)
					continue;
				setInterval(Integer.parseInt(keyvalue[1].trim()));
			} else if ("args".equalsIgnoreCase(keyvalue[0].trim())) {
				if (keyvalue.length != 2)
					continue;
				setArgrument(keyvalue[1].trim());
			} else if ("concurrent".equalsIgnoreCase(keyvalue[0].trim())) {
				if ("false".equalsIgnoreCase(keyvalue[1].trim()))
					setConcurrent(false);
			} else if (("targetMethod".equalsIgnoreCase(keyvalue[0].trim())) && (keyvalue.length == 2)) {
				setTargetMethod(keyvalue[1].trim());
			}
		}
	}

	private void parseDelimiter(String paramStr) {
		String[] params = paramStr.split(",");

		if (params.length == 1) {
			setInterval(Integer.parseInt(paramStr.trim()));
		} else if (params.length == 2) {
			setStartDelay(Integer.parseInt(params[0].trim()));
			setInterval(Integer.parseInt(params[1].trim()));
		} else if (params.length == 3) {
			setStartDelay(Integer.parseInt(params[0].trim()));
			setInterval(Integer.parseInt(params[1].trim()));
			setArgrument(params[2].trim());
		} else if (params.length == 4) {
			setStartDelay(Integer.parseInt(params[0].trim()));
			setInterval(Integer.parseInt(params[1].trim()));
			setArgrument(params[2].trim());
			if ("false".equalsIgnoreCase(params[3].trim()))
				setConcurrent(false);
		}
	}

	public int getStartDelay() {
		return this.startDelay;
	}

	public void setStartDelay(int startDelay) {
		this.startDelay = startDelay;
	}

	public int getInterval() {
		return this.interval;
	}

	public void setInterval(int interval) {
		this.interval = interval;
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