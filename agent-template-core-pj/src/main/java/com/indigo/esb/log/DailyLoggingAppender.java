package com.indigo.esb.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.QuietWriter;
import org.apache.log4j.spi.LoggingEvent;

public class DailyLoggingAppender extends WriterAppender {
	protected boolean fileAppend;
	protected String fileName;
	String filenameExt;
	protected boolean bufferedIO;
	protected int bufferSize;
	String datePattern = "yyyyMMdd";
	String nowFileName;
	String header;
	SimpleDateFormat sdf = new SimpleDateFormat(datePattern);

	public DailyLoggingAppender() throws IOException {
		this.fileAppend = true;
		this.bufferedIO = false;
		this.bufferSize = 8192;
		layout = new PatternLayout("%m%n");
	}

	public void setDatePattern(String datePattern) {
		this.datePattern = datePattern;
		sdf = new SimpleDateFormat(datePattern);
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setFilenameExt(String filenameExt) {
		this.filenameExt = filenameExt;
	}

	public boolean getAppend() {
		return this.fileAppend;
	}

	public String getFile() {
		return this.fileName;
	}

	protected void closeFile() {
		if (this.qw == null)
			return;
		try {
			this.qw.close();
		} catch (IOException e) {
			LogLog.error("Could not close " + this.qw, e);
		}
	}

	@Override
	protected void subAppend(LoggingEvent event) {
		String newFileName = fileName + sdf.format(new Date());
		if (!nowFileName.equals(newFileName)) {
			try {
				setFile(newFileName);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		super.subAppend(event);
	}

	public boolean getBufferedIO() {
		return this.bufferedIO;
	}

	public int getBufferSize() {
		return this.bufferSize;
	}

	public void setAppend(boolean flag) {
		this.fileAppend = flag;
	}

	public void setBufferedIO(boolean bufferedIO) {
		this.bufferedIO = bufferedIO;
		if (bufferedIO)
			this.immediateFlush = false;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public void start() throws IOException {
		setFile(fileName + sdf.format(new Date()));
	}

	public synchronized void setFile(String newFileName) throws IOException {
		nowFileName = newFileName;
		if (bufferedIO) {
			super.setImmediateFlush(false);
		}
		reset();
		FileOutputStream ostream = null;
		
		File file = new File(nowFileName);
		boolean isExist = file.exists();
		
		try {
			ostream = new FileOutputStream(nowFileName, fileAppend);
		} catch (FileNotFoundException ex) {
			String parentName = new File(nowFileName).getParent();
			if (parentName != null) {
				File parentDir = new File(parentName);
				if ((!(parentDir.exists())) && (parentDir.mkdirs()))
					ostream = new FileOutputStream(nowFileName, fileAppend);
				else
					throw ex;
			} else {
				throw ex;
			}
		}
		Writer fw = super.createWriter(ostream);
		if (bufferedIO) {
			fw = new BufferedWriter(fw, bufferSize);
		}
		setQWForFiles(fw);
		if (header != null && (!isExist)) {
			this.qw.write(header+"\n");
		}
	}

	protected void setQWForFiles(Writer writer) {
		this.qw = new QuietWriter(writer, this.errorHandler);
	}

	protected void reset() {
		closeFile();
		// this.fileName = null;
		super.reset();
	}
}
