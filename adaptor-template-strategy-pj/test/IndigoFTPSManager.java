import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author clupine-mb
 *
 */
public class IndigoFTPSManager {

	private static final Logger logger = LoggerFactory.getLogger(IndigoFTPSManager.class);

	int port = 24400;
	String ip = "clupine.ipost.kr";
	String user = "indigo";
	String password = "indigo";
	String authValue = "indigo";
	boolean binaryMode = true;
	boolean passiveMode = true;

	public IndigoFTPSManager(String ip, int port, String user, String password) {
		this.ip = ip;
		this.port = port;
		this.user = user;
		this.password = password;
	}

	private FTPSClient connect() {

		FTPSClient ftps = new FTPSClient("SSL", true);
		ftps.setAuthValue(authValue);
		ftps.addProtocolCommandListener(new PrintCommandListener(new LogPrintWriter(logger)));

		try {
			int reply;
			ftps.connect(ip, port);
			logger.info("Connected to " + ip + ".");
			reply = ftps.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				ftps.disconnect();
				logger.error("FTP server refused connection.");
				return null;
			}
		} catch (IOException e) {
			if (ftps.isConnected()) {
				try {
					ftps.disconnect();
				} catch (IOException f) {
				}
			}
			logger.error("Could not connect to server.");
			logger.error("Exception : ", e);
			return null;
		}

		try {

			ftps.setBufferSize(8096);

			if (!ftps.login(user, password)) {
				ftps.logout();
				logger.info("Login fail");
				return null;
			}

			logger.info("Remote system is " + ftps.getSystemName());

			if (binaryMode) {
				ftps.setFileType(FTP.BINARY_FILE_TYPE);
			}

			if (passiveMode) {
				ftps.enterLocalPassiveMode();
			} else {
				ftps.enterLocalActiveMode();
			}
		} catch (FTPConnectionClosedException e) {
			logger.error("Server closed connection.", e);
		} catch (IOException e) {
			logger.error("IOException : ", e);
		}

		if (ftps.isConnected()) {
			return ftps;
		} else {
			return null;
		}
	}

	public boolean putFile(String loadFilePath, String remoteDir, String remoteFileName, boolean binaryMode, int connectTryCnt) throws Exception {

		FTPSClient ftps = null;

		for (int i = 1; i <= connectTryCnt; i++) {
			ftps = connect();
			if (!(ftps == null))
				break;
			logger.info("FileServer try Count : " + i);
			Thread.sleep(3000L);
		}

		if (ftps == null) {
			throw new Exception("connect Retry Fail");
		}
		logger.info("is connect? " + ftps.isConnected());

		if (ftps.changeWorkingDirectory(remoteDir)) {
			logger.info("Directory Change Succesfull  ==> " + remoteDir);
		}
		InputStream is = new FileInputStream(new File(loadFilePath));
		boolean result = ftps.storeFile(remoteFileName, is);
		ftps.logout();
		ftps.disconnect();
		is.close();
		return result;
	}

	public boolean getFile(String saveFilePath, String remoteDir, String remoteFileName, boolean binaryMode, int connectTryCnt) throws Exception {
		FTPSClient ftps = null;

		for (int i = 1; i <= connectTryCnt; i++) {
			ftps = connect();
			if (!(ftps == null))
				break;
			logger.info("FileServer try Count : " + i);
			Thread.sleep(3000L);
		}

		if (ftps == null) {
			throw new Exception("connect Retry Fail");
		}
		logger.info("is connect? " + ftps.isConnected());

		if (ftps.changeWorkingDirectory(remoteDir)) {
			logger.info("Directory Change Succesfull  ==> " + remoteDir);
		}
		OutputStream os = new FileOutputStream(new File(saveFilePath));
		boolean result = ftps.retrieveFile(remoteFileName, os);
		ftps.logout();
		ftps.disconnect();
		os.close();
		return result;

	}

	public static void main(String[] args) throws Exception {
		IndigoFTPSManager mgr = new IndigoFTPSManager("clupine.ipost.kr", 24400, "indigo", "indigo");
//		boolean result = mgr.putFile("c:/test/sample.csv", "/home/indigo/project/uav/share/001", "targetFile.csv", true, 3);
		boolean result = mgr.getFile("c:/test/sample111.csv", "/home/indigo/project/uav/share/001", "targetFile.csv", true, 3);
		System.out.println("result ==> " + result);
	}
}

class LogPrintWriter extends PrintWriter {

	/**
	 * Internal buffer.
	 */
	private StringBuffer buffer = new StringBuffer();

	/**
	 * Logger for message output.
	 */
	private Logger log;

	/**
	 * Creates a new <code>LogPrintWriter</code> which is based on a
	 * <code>Writer</code>.
	 *
	 * @param out
	 *            the base <code>Writer</code>.
	 */
	public LogPrintWriter(Writer out) {
		super(out);
	}

	/**
	 * Creates a new <code>LogPrintWriter</code> which is based on a
	 * <code>Logger</code>.
	 *
	 * @param log
	 *            the base <code>Logger</code>.
	 */
	public LogPrintWriter(Logger log) {
		super(new NullWriter());
		this.log = log;
	}

	/**
	 * Sets a new output <code>Writer</code>. Calling this method will flush
	 * this <code>LogPrintWriter</code> before the new <code>Writer</code>
	 * <code>out</code> is set.
	 *
	 * @param out
	 *            the <code>Writer</code> to use for output.
	 */
	public void setWriter(Writer out) {
		flushBuffer();
		this.out = out;
		this.log = null;
	}

	/**
	 * Sets a new <code>Logger</code>. Calling this method will flush this
	 * <code>LogPrintWriter</code> before the new <code>Logger</code> is set.
	 *
	 * @param log
	 *            the new <code>Logger</code> to use for output.
	 */
	public void setLogger(Logger log) {
		flushBuffer();
		out = new NullWriter();
		this.log = log;
	}

	// ------------------< overrides from PrintWriter >-------------------------

	public void close() {
		flushBuffer();
		super.close();
	}

	public void flush() {
		flushBuffer();
		super.flush();
	}

	public void write(int c) {
		buffer.append(c);
	}

	public void write(char cbuf[], int off, int len) {
		buffer.append(cbuf, off, len);
	}

	public void write(String str, int off, int len) {
		buffer.append(str.substring(off, off + len));
	}

	public void println() {
		if (log == null) {
			buffer.append('\n');
		}
		flushBuffer();
	}

	// -----------------------< private methods
	// >--------------------------------

	private void flushBuffer() {
		if (buffer.length() == 0) {
			return;
		}
		if (log != null) {
			log.info(buffer.toString().trim());
		} else {
			try {
				out.write(buffer.toString());
			} catch (IOException e) {
				this.setError();
			}
		}
		// reset buffer
		buffer.setLength(0);
	}

	// ------------------------< inter classes
	// >---------------------------------

	/**
	 * Implements a Writer that simply ignores all calls.
	 */
	private static class NullWriter extends Writer {

		public void close() throws IOException {
			// ignore
		}

		public void flush() throws IOException {
			// ignore
		}

		public void write(char cbuf[], int off, int len) throws IOException {
			// ignore
		}
	}
}