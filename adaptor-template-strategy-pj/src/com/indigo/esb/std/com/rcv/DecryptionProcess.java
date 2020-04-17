package com.indigo.esb.std.com.rcv;

import java.io.ByteArrayInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoMessageResult;
import com.indigo.esb.adaptor.strategy.OnMessageStrategy;
import com.indigo.esb.crypto.ARIACipher;
import com.indigo.esb.crypto.CryptoType;
import com.indigo.esb.crypto.Seed128Cipher;

/**
 * 수신 복호화
 * 
 * @author clupine
 *
 */
public class DecryptionProcess implements OnMessageStrategy{

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private String key = "AdaptorTemplate1";

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public void process(IndigoMessageResult indigoMessageResult)
			throws Exception {

		String cryptoTypeStr = indigoMessageResult.getProperty("CryptoType");

		if (cryptoTypeStr == null) {
			return;
		}

		byte[] decBytes = null;
		
		switch (CryptoType.valueOf(cryptoTypeStr)) {

		case ARIA128: {
			byte[] dataObj = (byte[]) indigoMessageResult.getDataObj();

			ByteArrayInputStream bis = null;

			try {
				ARIACipher aria128 = new ARIACipher();
				aria128.setPassword(key);
				decBytes = aria128.decrypt(dataObj);
				bis = new ByteArrayInputStream(decBytes);
				ObjectInput in = new ObjectInputStream(bis);
				
				indigoMessageResult.setDataObj(in.readObject());
			} finally {
				bis.close();
			}
		 }
			break;
			
		case JASYPT: {
			byte[] dataObj = (byte[]) indigoMessageResult.getDataObj();

			ByteArrayInputStream bis = null;

			try {
				StandardPBEByteEncryptor jasypt = new StandardPBEByteEncryptor();
				jasypt.setAlgorithm("PBEWithSHA1AndDESede");
				jasypt.setPassword(key);
				decBytes = jasypt.decrypt(dataObj);
				bis = new ByteArrayInputStream(decBytes);
				ObjectInput in = new ObjectInputStream(bis);
				
				indigoMessageResult.setDataObj(in.readObject());
			} finally {
				bis.close();
			}
		}
			break;
			
		case SEED128: {
			byte[] dataObj = (byte[]) indigoMessageResult.getDataObj();

			ByteArrayInputStream bis = null;

			try {
				decBytes = Seed128Cipher.decrypt(dataObj, key.getBytes());
				bis = new ByteArrayInputStream(decBytes);
				ObjectInput in = new ObjectInputStream(bis);
				
				indigoMessageResult.setDataObj(in.readObject());
			} finally {
				bis.close();
			}
		}
			break;

		default:

			break;
		}
	}

}
