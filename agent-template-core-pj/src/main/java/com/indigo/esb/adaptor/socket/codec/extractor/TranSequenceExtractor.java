package com.indigo.esb.adaptor.socket.codec.extractor;

import org.jboss.netty.buffer.ChannelBuffer;

public interface TranSequenceExtractor {
	String extract(String tranCode, ChannelBuffer buf) ;
}
