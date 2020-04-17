package com.indigo.esb.jms;

import com.indigo.esb.config.InterfaceInfo;

public class InterfaceIdJmsDestinationNameMaker implements
		JmsDestinationNameMaker {

	@Override
	public String getSendDestinationName(InterfaceInfo interfaceInfo) {
		return interfaceInfo.getInterfaceId()+"_DATA";
	}

	@Override
	public String getReceiveDestinationName(InterfaceInfo interfaceInfo) {
		return interfaceInfo.getInterfaceId()+"_RSLT";
	}

}
