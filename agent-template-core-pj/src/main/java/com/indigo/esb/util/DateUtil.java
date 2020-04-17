package com.indigo.esb.util;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.BasicConfigurator;
import org.aspectj.weaver.patterns.ThisOrTargetAnnotationPointcut;

/**
 * @author YOON JONG HOON
 * 
 */
public class DateUtil {

	public static String getTodayTimeString() {
		Format format = new SimpleDateFormat("yyyyMMddHHmmssaa", Locale.US);
		String todayStr = format.format(new Date());
		return todayStr;
	}
	
	public static String getTodayTimeMilli() {
		Format format = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US);
		String todayStr = format.format(new Date());
		return todayStr;
	}
	public static String getTodayDateFormat(String formatStr) {
		Format format = new SimpleDateFormat(formatStr, Locale.US);
		String todayStr = format.format(new Date());
		return todayStr;
	}
	
	public static String getTodayTime7DigitString() {
		Format format = new SimpleDateFormat("yyMMddH", Locale.US);
		String todayStr = format.format(new Date());
		return todayStr;
	}
	
	public static String getYear() {
		Format format = new SimpleDateFormat("yyyy", Locale.US);
		String todayStr = format.format(new Date());
		return todayStr;
	}
	
	public static String getMonth() {
		Format format = new SimpleDateFormat("MM", Locale.US);
		String todayStr = format.format(new Date());
		return todayStr;
	}
	public static String getDay() {
		Format format = new SimpleDateFormat("dd", Locale.US);
		String todayStr = format.format(new Date());
		return todayStr;
	}

	public static String getDateTime() {
		Format format = new SimpleDateFormat("yyyyMMddHHmmss", Locale.US);
		String todayStr = format.format(new Date());
		return todayStr;
	}

	public static String getCurrentTimeString() {
		Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss S", Locale.US);
		String todayStr = format.format(new Date());
		return todayStr;
	}

	public static String getTodayOfWeek() {
		Format format = new SimpleDateFormat("E", Locale.KOREA);
		String todayStr = format.format(new Date()) + "요일";
		return todayStr;
	}

	public static String getTodayMonth() {
		Format format = new SimpleDateFormat("MMM", Locale.KOREA);
		String todayStr = format.format(new Date());
		return todayStr;
	}

	public static String getTodayOfWeekInMonth() {
		Format format = new SimpleDateFormat("F", Locale.KOREA);
		String todayStr = format.format(new Date()) + "주차";
		return todayStr;
	}

	public static String getTodayString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd",
				Locale.KOREA);
		return formatter.format(new Date());
	}
	
	public static String getDateFormat(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format,
				Locale.KOREA);
		return formatter.format(new Date());
	}
	
	public static String getTodayString(String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.KOREA);
		return formatter.format(new Date());
	}

	public static String addDays(int amount) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd",
				Locale.KOREA);
		Date date = new Date();
		date.setTime(date.getTime() + ((long) amount * 1000 * 60 * 60 * 24));
		return formatter.format(date);
	}
	public static String addSeconds(int amount) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.KOREA);
		Date date = new Date();
		date.setTime(date.getTime() + ((long) amount * 1000));
		return formatter.format(date);
	}
	public static String getPastTimeString(int amount) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US);
		Date date = new Date();
		date.setTime(date.getTime() + ((long) amount * 1000 * 60 * 60 * 24));
		return formatter.format(date);
	}
	
	public static String getYesterdayTimeString(int amount) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US);
		Date date = new Date();
		date.setTime(date.getTime() - ((long) amount * 1000 * 60 * 60 * 24));
		return formatter.format(date);
	}	
	
	public static String getPastMinString(int amount) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US);
		Date date = new Date();
		date.setTime(date.getTime() - ((long) amount * 1000 * 60));
		return formatter.format(date);
	}	

	public static String getYesterdayString() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd",
				java.util.Locale.KOREA);
		Date date = new Date();
		date.setTime(date.getTime() + ((long) -1 * 1000 * 60 * 60 * 24));
		return formatter.format(date);
	}

	public static String addDaysFromYesterday(int amount) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd",
				java.util.Locale.KOREA);
		Date date = new Date();
		int term = amount - 1;
		date.setTime(date.getTime() + ((long) term * 1000 * 60 * 60 * 24));
		return formatter.format(date);
	}

	public static String getDateTime(long milli) {
		Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS",
				Locale.US);
		String todayStr = format.format(new Date(milli));
		return todayStr;
	}
	
	public static String getDateTimeMilli() {
		Format format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss SSS",
				Locale.US);
		String todayStr = format.format(new Date(System.currentTimeMillis()));
		return todayStr;
	}

	public static String getDateString(long milli) {
		Date date = new Date(milli);
		Format format = new SimpleDateFormat("yyyyMMdd", Locale.US);
		String todayStr = format.format(date);
		return todayStr;
	}

	/*
	 * by sung 2005.11.30
	 */
	/**
	 * <pre>
	 *     기능 : 오늘 날짜로부터 정해진 일수를 뺀 날짜를 가져온다. 
	 *     작성 : 2005/11/30/오성애/메타빌드
	 * </pre>
	 * 
	 * @param amount
	 *            일 수
	 */
	public static String subDays(int amount) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd",
				Locale.KOREA);
		Date date = new Date();
		date.setTime(date.getTime() - ((long) amount * 1000 * 60 * 60 * 24));
		return formatter.format(date);
	}

	/**
	 * <pre>
	 *     기능 : 지정한 날짜로 부터 정해진 일수를 뺀 날짜를 가져온다. 
	 *     작성 : 2006/01/08/오성애/메타빌드
	 * </pre>
	 * 
	 * @param amount
	 *            일 수
	 * @param date
	 *            기준 날짜
	 */
	public static String subDays(int amount, Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd",
				Locale.KOREA);
		date.setTime(date.getTime() - ((long) amount * 1000 * 60 * 60 * 24));
		return formatter.format(date);
	}

	/**
	 * <pre>
	 *     기능 : 오늘 연동할 첨부파일 폴더 날짜 가져오기. 
	 *     작성 : 2006/01/08/오성애/메타빌드
	 * </pre>
	 * 
	 * @return 연동할 첨부파일 폴더 날짜
	 * @throws ParseException
	 */
	public static String getTodaySendDate() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss",
				Locale.KOREA);
		String todayStr = DateUtil.getTodayString();

		Date compareDate1 = format.parse(todayStr + " " + "08:00:00");
		Date compareDate2 = format.parse(todayStr + " " + "23:59:59");
		Date today = new Date();

		String sendDate = null;
		if (today.after(compareDate1) && today.before(compareDate2)) {
			sendDate = todayStr;
		} else if (today.before(compareDate1)) {
			sendDate = DateUtil.getYesterdayString();
		}
		return sendDate;
	}// end of getTodaySendDate()

	/**
	 * <pre>
	 *     기능 : 두 날짜 사이의 일수 차를 가져온다. 
	 *     작성 : 2006/01/08/오성애/메타빌드
	 * </pre>
	 * 
	 * @param date1
	 *            날짜1
	 * @param date2
	 *            날짜2
	 * @param format
	 *            날짜1, 날짜2의 포맷
	 * @throws ParseException
	 */
	public static int getGapOfDate(String date1, String date2,
			SimpleDateFormat format) throws ParseException {
		Date d1 = format.parse(date1);
		Date d2 = format.parse(date2);

		long duration = d2.getTime() - d1.getTime();
		long amount = duration / (1000 * 60 * 60 * 24);

		return (int) amount;
	}// end of getGapOfDate()

	public static int getGapOfDateTime(String dttm1, String dttm2)
			throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",
				Locale.US);
		Date d1 = format.parse(dttm1);
		Date d2 = format.parse(dttm2);
		long duration = d2.getTime() - d1.getTime();
		long amount = duration / (1000 * 60 * 60);
		return (int) amount;
	}// end of getGapOfDateTime()
	public static int getGapOfSeconds(String dttm1, String dttm2)
		throws ParseException {
	    	SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss",Locale.US);
	    	Date d1 = format.parse(dttm1);
	    	Date d2 = format.parse(dttm2);
	    	long duration = d2.getTime() - d1.getTime();
	    	long amount = duration / 1000;
	    	return (int) amount;
	}// end of getGapOfDateTime()


	/**
	 * 현재 시간을 리턴한다. (00 - 23)
	 * 
	 * <pre>
	 *   기능 : 
	 *   작성 : 2006. 1. 9./윤종훈/메타빌드
	 * </pre>
	 * 
	 * @return 시간
	 */
	public static int getCurrentHour() {
		Date date = new Date();
		Format format = new SimpleDateFormat("HH", Locale.US);
		String todayStr = format.format(date);
		int hour = Integer.parseInt(todayStr);
		return hour;
	}

	public static String date2Str(Date date, String dateFormatStr) {
		return (new SimpleDateFormat(dateFormatStr)).format(date);
	}
	
	public static Date str2Date(String strDate, String dateFormatStr) {
		try {
			return (new SimpleDateFormat(dateFormatStr)).parse(strDate);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static String str2DateStr(String strDate, String srcdateFormatStr , String dateFormatStr) {
		try {
		Date date = 	new SimpleDateFormat(srcdateFormatStr).parse(strDate);
		return (new SimpleDateFormat(dateFormatStr)).format(date);
		} catch (ParseException e) {
			return null;
		}
	}
	public static String getCurrentTimeStr() {
		Date date = new Date();
		Format format = new SimpleDateFormat("HHmmss", Locale.US);
		String todayStr = format.format(date);
		return todayStr;
	}
	public static void main(String[] aa) throws ParseException {
	        
	    	String intime = "TEST-FILE-IF1_20130918040033127474";
	    	String sttime = intime.substring(intime.lastIndexOf("_")+1, intime.lastIndexOf("_")+15);
	    	System.out.println("sttime:" + sttime);
		System.out.println(getGapOfSeconds(sttime, DateUtil.getDateTime()));
		System.out.println(getGapOfSeconds(DateUtil.getDateTime(), sttime));
		System.out.println("currtime: " +  DateUtil.getDateTime() + " add 30 : " + addSeconds(30) + " gap:" +
			getGapOfSeconds(DateUtil.getDateTime(), addSeconds(30)));
		/*System.out.println(getGapOfDateTime(DateUtil.getDateTime(),"20120515115544"));
		System.out.println(str2DateStr("20120515120029735", "yyyyMMddHHmmssSSS", "yyyy-MM-dd HH:mm:ss"));*/
		
		
	}

}