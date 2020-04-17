package com.indigo.esb.jms;

import com.indigo.esb.config.InterfaceInfo;

public interface JmsDestinationNameMaker{
	String getSendDestinationName(InterfaceInfo interfaceInfo);
	String getReceiveDestinationName(InterfaceInfo interfaceInfo);
}
