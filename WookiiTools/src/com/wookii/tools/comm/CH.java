package com.wookii.tools.comm;

/**
 * year = "年";<br>
 * month = "月";<br>
 * day = "日";<br>
 * hour = "时";<br>
 * minute = "分";<br>
 * second = "秒"<br>
 * 
 * @author Chen Wu
 * 
 */
public class CH implements Internationalization {
	public String year = "年";
	public String month = "月";
	public String day = "日";
	public String hour = "时";
	public String minute = "分";
	public String second = "秒";

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
