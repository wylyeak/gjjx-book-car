package org.wylyeak.gjjx.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
	public static final String YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	public static Date parseStr(String str, String formatStr) {
		try {
			DateFormat format = new SimpleDateFormat(formatStr);
			return format.parse(str);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String getDayAfter(Date date, int day) {
		DateFormat format = new SimpleDateFormat(YYYY_MM_DD);
		return format.format(new Date(date.getTime() + day * 24 * 3600 * 1000));
	}
}
