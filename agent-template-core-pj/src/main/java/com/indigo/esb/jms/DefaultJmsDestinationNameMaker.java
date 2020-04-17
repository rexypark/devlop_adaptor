package com.indigo.esb.jms;

import com.indigo.esb.config.InterfaceInfo;

public class DefaultJmsDestinationNameMaker implements JmsDestinationNameMaker {

	@Override
	public String getSendDestinationName(InterfaceInfo interfaceInfo) {
		return interfaceInfo.getTargetDestinationName() + "_DATA";
	}

	@Override
	public String getReceiveDestinationName(InterfaceInfo interfaceInfo) {
		return interfaceInfo.getTargetDestinationName() + "_RSLT";
	}

}
