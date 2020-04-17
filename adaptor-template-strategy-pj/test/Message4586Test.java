import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;

import com.mb.mci.common.util.HexViewer;


public class Message4586Test {
	
	public static void main(String[] args) throws Exception {
		
		/**
		 * HEADER
		 */
		byte[] sequence          = { (byte) 0xFF, (byte) 0xFF };
		byte[] messageLength     = new byte[2];
		byte[] sourceId          = { (byte) 0xFF, (byte) 0xFF , (byte) 0xFF, (byte) 0xFF};
		byte[] destinationId     = { (byte) 0xFF, (byte) 0xFF , (byte) 0xFF, (byte) 0xFF};
		byte[] messageType       = { (byte) 0xFF, (byte) 0xFF };
		byte[] messageProperties = { (byte) 0xFF, (byte) 0xFF };
		
		/**
		 * DATA FIELDS
		 */
		byte[] timeStamp          = new byte[5];
		byte[] totalBit           = new byte[1];
		byte[] swVersion          = new byte[4];
		byte[] lru_bit 	          = new byte[2];
		byte[] sw_bit  	          = new byte[2];
		byte[] memoryUsageAvg01	  = new byte[1];
		byte[] cpuUsageAverage01  = new byte[1];
		byte[] memoryUsageAvg02	  = new byte[1];
		byte[] cpuUsageAverage02  = new byte[1];
		byte[] netBit			  = new byte[1];
		byte[] netUsageAverage01  = new byte[1];
		byte[] netUsageAverage02  = new byte[1];
		byte[] netUsageAverage03  = new byte[1];
		byte[] checksum           = new byte[2];
		
		
		ChannelBuffer cb = ChannelBuffers.dynamicBuffer();
		cb.writeBytes(sequence          );
		cb.writeBytes(messageLength     );
		cb.writeBytes(sourceId          );
		cb.writeBytes(destinationId     );
		cb.writeBytes(messageType       );
		cb.writeBytes(messageProperties );
		cb.writeBytes(timeStamp         );
		cb.writeBytes(totalBit          );
		cb.writeBytes(swVersion         );
		cb.writeBytes(lru_bit 	        );
		cb.writeBytes(sw_bit  	        );
		cb.writeBytes(memoryUsageAvg01	);
		cb.writeBytes(cpuUsageAverage01 );
		cb.writeBytes(memoryUsageAvg02	);
		cb.writeBytes(cpuUsageAverage02 );
		cb.writeBytes(netBit			);
		cb.writeBytes(netUsageAverage01 );
		cb.writeBytes(netUsageAverage02 );
		cb.writeBytes(netUsageAverage03 );
		cb.writeBytes(checksum          );
		
		byte[] genMsg = new byte[cb.readableBytes()];
		cb.getBytes(0,genMsg);
		
		System.out.println(HexViewer.view(genMsg));
		
		
				

	}
}
