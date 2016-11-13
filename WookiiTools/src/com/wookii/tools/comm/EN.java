package com.wookii.tools.comm;

/**
 * year = "-"; <br>month = "-"; <br>day = "-"; <br>hour = ":"; 
 * <br>minute = ":"; <br>second = ":"
 * 
 * @author Chen Wu
 * 
 */
public class EN implements Internationalization {
	public String year = "-";
	public String month = "-";
	public String day = "-";
	public String hour = ":";
	public String minute = ":";
	public String second = ":";

	@Override
	public String getYear() {
		// TODO Auto-generated method stub
		return year;
	}

	@Override
	public String getMonth() {
		// TODO Auto-generated method stub
		return month;
	}

	@Override
	public String getDay() {
		// TODO Auto-generated method stub
		return day;
	}

	@Override
	public String getHour() {
		// TODO Auto-generated method stub
		return hour;
	}

	@Override
	public String getMinute() {
		// TODO Auto-generated method stub
		return minute;
	}

	@Override
	public String getSecond() {
		// TODO Auto-generated method stub
		return second;
	}

}
