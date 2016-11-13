package com.ctbri.comm.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {

	private static SimpleDateFormat commentPattern;
	private static SimpleDateFormat datePattern;
	static{
		datePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		commentPattern = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
	}
	public static String toCommentDate(String date){
		
		Date dateObj = null;
		try {
			dateObj = datePattern.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
			return "";
		}
		return commentPattern.format(dateObj);
	}
}
