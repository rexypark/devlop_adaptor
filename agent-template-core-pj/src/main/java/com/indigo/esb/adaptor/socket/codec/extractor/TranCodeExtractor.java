package com.indigo.esb.adaptor.socket.codec.extractor;

import org.jboss.netty.buffer.ChannelBuffer;

public interface TranCodeExtractor {

	 String extract(ChannelBuffer buffer);

}
