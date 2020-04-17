package com.indigo.esb.std.transform;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.jasypt.encryption.pbe.StandardPBEByteEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.SerializationUtils;

import com.indigo.esb.crypto.ARIACipher;
import com.indigo.esb.crypto.CryptoType;
import com.indigo.esb.crypto.Seed128Cipher;
import com.indigo.indigomq.MessageTransformer;

/**
 * JMS ObjectMessage 변환
 * @author clupine
 *
 */
public class CryptographyMessageTransformer implements MessageTransformer {

	private String key = "AdaptorTemplate1";

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	ARIACipher aria128 = new ARIACipher();
	StandardPBEByteEncryptor jasypt = new StandardPBEByteEncryptor();
	
	public void setKey(String key) {
		this.key = key;
	}

	/**
	 * NONE, SEED128, ARIA128 , JASYPT
	 * 
	 * @param cryptoType
	 */
	public void setCryptoType(String cryptoType) {
		this.cryptoType = CryptoType.valueOf(cryptoType);
	}

	CryptoType cryptoType = CryptoType.NONE;

	public Message producerTransform(Session session, MessageProducer producer, Message message) throws JMSException {
		
		if (message instanceof ObjectMessage) {
			ObjectMessage msg = ((ObjectMessage) message);
			Serializable dataObj = msg.getObject();
			byte[] plainBytes = SerializationUtils.serialize(dataObj);
			msg.setStringProperty("CryptoType", cryptoType.name());

			switch (cryptoType) {

			case ARIA128: {
				aria128.setPassword(key);
				msg.setObject(aria128.encrypt(plainBytes));
			}
				break;

			case JASYPT: {
				jasypt.setAlgorithm("PBEWithSHA1AndDESede");
				jasypt.setPassword(key);
			}
				break;

			case SEED128: {
				msg.setObject(Seed128Cipher.encrypt(plainBytes, key.getBytes()));
			}
				break;

			default:

				break;
			}
		
			return msg;
		}

		return message;
	}

	public Message consumerTransform(Session session, MessageConsumer consumer, Message message) throws JMSException {
		
		if (message instanceof ObjectMessage) {
			
			ObjectMessage msg = ((ObjectMessage) message);
			String cryptoTypeStr = message.getStringProperty("CryptoType");
			byte[] dataObj = (byte[]) msg.getObject();
			
			switch (CryptoType.valueOf(cryptoTypeStr)) {

			case ARIA128: {

				aria128.setPassword(key);
				Serializable original = (Serializable) SerializationUtils.deserialize(aria128.decrypt(dataObj));
				((ObjectMessage) message).setObject(original);
			 }
				break;
				
			case JASYPT: {

				jasypt.setAlgorithm("PBEWithSHA1AndDESede");
				jasypt.setPassword(key);
				Serializable original =  (Serializable) SerializationUtils.deserialize(jasypt.decrypt(dataObj));
				((ObjectMessage) message).setObject(original);
			}
				break;
				
			case SEED128: {

				Serializable original =  (Serializable) SerializationUtils.deserialize(Seed128Cipher.decrypt(dataObj, key.getBytes()));
				((ObjectMessage) message).setObject(original);
			}
				break;

			default:

				break;
			}
			
		}
		
		
		return message;
	}
}
