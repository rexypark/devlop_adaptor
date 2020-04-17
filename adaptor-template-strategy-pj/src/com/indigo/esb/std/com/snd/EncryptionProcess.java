package com.indigo.esb.std.com.snd;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.indigo.esb.adaptor.context.IndigoSignalResult;
import com.indigo.esb.adaptor.strategy.OnSignalStrategy;
import com.indigo.esb.crypto.ARIACipher;
import com.indigo.esb.crypto.CryptoType;
import com.indigo.esb.crypto.Seed128Cipher;

/**
 * 송신 암호화
 * 
 * @author clupine
 *
 */
public class EncryptionProcess implements OnSignalStrategy {
 
	private String key = "AdaptorTemplate1";
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * NONE, SEED128, ARIA128 , JASYPT
	 * @param cryptoType
	 */
	public void setCryptoType(String cryptoType) {
		this.cryptoType = CryptoType.valueOf(cryptoType);
	}

	CryptoType cryptoType = CryptoType.NONE;

	@Override
	public void onStart(IndigoSignalResult onSignalResult) throws Exception {

		onSignalResult.getProperties().addHeaderInfo("CryptoType",	cryptoType.name());
		
		byte[] encBytes = null;
		
		switch (cryptoType) {

		case ARIA128:
					{
						Object dataObj = onSignalResult.getPollResultDataObj();
						
						ByteArrayOutputStream bos = null;
						
						try{
							bos = new ByteArrayOutputStream();
							ObjectOutput out = new ObjectOutputStream(bos);   
							out.writeObject(dataObj);
							 ARIACipher aria128 = new ARIACipher();
							 aria128.setPassword(key);
							encBytes = aria128.encrypt(bos.toByteArray());
							onSignalResult.setPollResultDataObj(encBytes);
						}finally{
							bos.close();
						}
					}
			break;
			
		case JASYPT:
					{
						Object dataObj = onSignalResult.getPollResultDataObj();
						
						ByteArrayOutputStream bos = null;
						
						try{
							bos = new ByteArrayOutputStream();
						ObjectOutput out = new ObjectOutputStream(bos);   
						out.writeObject(dataObj);
						
						StandardPBEByteEncryptor jasypt = new StandardPBEByteEncryptor();

						jasypt.setAlgorithm("PBEWithSHA1AndDESede");
						jasypt.setPassword(key);
						
						encBytes = jasypt.encrypt(bos.toByteArray());
						onSignalResult.setPollResultDataObj(encBytes);
						}finally{
							bos.close();
						}
					}
			break;
			
		case SEED128:
					{
						Object dataObj = onSignalResult.getPollResultDataObj();
						
						ByteArrayOutputStream bos = null;
						
						try{
							bos = new ByteArrayOutputStream();
						ObjectOutput out = new ObjectOutputStream(bos);   
						out.writeObject(dataObj);
						
						encBytes = Seed128Cipher.encrypt(bos.toByteArray(), key.getBytes());
						onSignalResult.setPollResultDataObj(encBytes);
						}finally{
							bos.close();
						}
					}
			break;

		default:

			break;
		}
		
	}
}
