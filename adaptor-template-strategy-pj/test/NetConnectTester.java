
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

public class NetConnectTester implements Runnable, DisposableBean {

	private static final Logger logger = LoggerFactory.getLogger(NetConnectTester.class);

	boolean isConnect = false;

	boolean isRun = true;

	public void setConnect(boolean isConnect) {
		this.isConnect = isConnect;
	}

	public boolean isConnect() {
		return isConnect;
	}

	String ip;
	int port;

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void run() {

		while (true) {

			netCheck();
				if (isRun) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				} else {
					logger.info("NetConnectTester Stopped");
					return;
				}
		}

	}

	private void netCheck() {
		Socket clientSocket = new Socket();

		try {
			clientSocket.connect(new InetSocketAddress(ip, port));
			isConnect = clientSocket.isConnected();
		} catch (IOException e) {
			isConnect = false;
			logger.error("NetConnectTester Error : ", e);
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public void destroy() throws Exception {
		isRun = false;
	}

	
	public static void main(String[] args) throws InterruptedException {
		NetConnectTester conn = new NetConnectTester();
		conn.setIp("127.0.0.1");
		conn.setPort(24211);
		
		Thread t = new Thread(conn);
		
		t.start();
		while(true){
			Thread.sleep(1000L);
			System.out.println(conn.isConnect());
		}
	}
}
