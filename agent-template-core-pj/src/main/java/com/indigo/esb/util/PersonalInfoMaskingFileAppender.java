package com.indigo.esb.util;

import java.util.regex.Pattern;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.LoggingEvent;

public class PersonalInfoMaskingFileAppender extends FileAppender {

	private Pattern juminPatern = Pattern.compile("(\\d{6})([- \\t\\n\\x0B\\f\\r])[1234]\\d{6}");

	private Pattern cellPhonePatern = Pattern
			.compile("(01[0|1|7|8|9])([- \\t\\n\\x0B\\f\\r]*)(\\d{3,4})([- \\t\\n\\x0B\\f\\r]*)\\d{4}");

	@Override
	protected void subAppend(LoggingEvent event) {
		this.qw.write(mask(this.layout.format(event)));

		if (layout.ignoresThrowable()) {
			String[] s = event.getThrowableStrRep();
			if (s != null) {
				int len = s.length;
				for (int i = 0; i < len; i++) {
					this.qw.write(s[i]);
					this.qw.write(Layout.LINE_SEP);
				}
			}
		}

		if (shouldFlush(event)) {
			this.qw.flush();
		}
	}

	protected String mask(String format) {
		String result = format;
		try {
			// 주민 번호 뒷자리 마스킹 처리.
			result = juminPatern.matcher(format).replaceAll("$1$2*******");

			// 휴대폰 번호 마스킹 처리.
			result = cellPhonePatern.matcher(result).replaceAll("$1$2$3$4****");
		} catch (Exception e) {
		}
		return result;
	}
}
