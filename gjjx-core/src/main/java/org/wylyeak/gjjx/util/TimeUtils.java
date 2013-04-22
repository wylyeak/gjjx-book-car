package org.wylyeak.gjjx.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	public static final String YYYY_MM_DD = "yyyy-MM-dd";
	public static SimpleDateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD);
	
	public static String dateToStr(Date date){
		return dateFormat.format(date);
	}
	
	public static Date strToDate(String str){
		try {
			return dateFormat.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}
	
	public static Date nowToDateWithOutTime(){
		Date date = new Date(System.currentTimeMillis());
		return strToDate(dateToStr(date));
	}
}
