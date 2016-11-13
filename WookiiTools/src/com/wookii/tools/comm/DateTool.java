package com.wookii.tools.comm;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

/**
 * @title DateUtils
 * @author Chen Wu
 * @version time: 2010-1-12 上午9:50:44
 * @copyright (c) www.sosgps.net.cn
 * @description 关于操作时间的工具组件<br>
 *              Date 类实际上只是一个包裹类, <br>
 *              它包含的是一个长整型数据, <br>
 *              表示的是从GMT(格林尼治标准时间)1970年,1月 1日00:00:00这一刻之前或者是之后经历的毫秒数.
 * 
 */
public class DateTool {

	private static DateTool instance = null;
	private static List<String> mFormatStr = null;
	private static Internationalization mInternat = new EN();
	private static final String[] WEEK_CN_NAME = { "周一", "周二", "周三", "周四",
		"周五", "周六", "周日" };
	private DateTool() {
	}

	public synchronized static DateTool getInstance() {
		if (instance == null) {
			instance = new DateTool();
		}
		return instance;
	}
	/**
	 * 设置显示的时间，将会是不同时间的展现形式，提供了默认的实现
	 * @param internat
	 * @return
	 * @see Internationalization
	 */
	public DateTool setInternat(Internationalization internat) {
		// TODO Auto-generated method stub
		mInternat = internat;
		return instance;
	}

	/**
	 * 日期加时间"yyyy-MM-dd HH:mm:ss"
	 */
	public static final String D_T_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 日期"yyyy-MM-dd"
	 */
	public static final String D_FORMAT = "yyyy-MM-dd";
	/**
	 * 时间"HH:mm:ss"
	 */
	public static final String T_FORMAT = "HH:mm:ss";
	/**
	 * just year
	 */
	public static final String YEAR = "yyyy";
	/**
	 * just MONTH
	 */
	public static final String MONTH = "MM";
	/**
	 * just day
	 */
	public static final String DAY = "dd";
	/**
	 * one day millisecond
	 */
	private static final long ONE_DAY = 86400000l;
	
	/**
	 * log flag
	 *
	 */
	private static final String TAG = "DateUtils";

	/**
	 * 获取系统当前时间
	 * 
	 * @param {@link #D_T_FORMAT}<br>
	 *        {@link #D_FORMAT}<br>
	 *        {@link #T_FORMAT}
	 * @return String类型的时间表示如 2010-3-4 13:24:53秒，返回的格式由dateFormat来决定
	 */
	public static String getCurrentTime(String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		
		String str = sdf.format(new Date());
		return str;
	}

	/**
	 * 通过GMT获取当前时间，
	 * 
	 * @param milliSecond
	 *            格林尼治标准时间可以通过{@link #getLongMilli()}获取。
	 * @return 默认格式"yyyy-MM-dd HH:mm:ss"的日期字符串
	 */
	public static String getCurrentTime(long milliSecond) {
		SimpleDateFormat sdf = new SimpleDateFormat(D_T_FORMAT);
		String str = sdf.format(new Date());
		return str;
	}

	/**
	 * 获取GMT(格林尼治标准时间)1970年,1月 1日00:00:00这一刻之前或者是之后经历的毫秒数
	 * 
	 * @return long
	 */
	public static long getLongMilli() {
		return new Date().getTime();
	}
	
	/**
	 * 获取星期
	 * @param sDate
	 * @return
	 */
	public static String getDayInWeek(String sDate, String format) {
		Date date = strToDate(sDate, format);
		SimpleDateFormat df = new SimpleDateFormat("EEE");
		String s = df.format(date);
		return s;
	}
	
	/**
	 * 获取星期
	 * @param Date
	 * @return
	 */
	public static String getDayInWeek(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("EEE");
		String s = df.format(date);
		return s;
	}
	/**
	 * 获取年，需要使用{@link #showTime()}来转换成字符串
	 * 
	 * @return DateUtils
	 */
	public static synchronized DateTool addYear() {
		if (mFormatStr == null) {
			mFormatStr = new ArrayList<String>();
			Log.i(TAG, "new Map");
		}
		mFormatStr.add(YEAR + mInternat.getYear());
		Log.i(TAG, "mInternat.year:" + mInternat.getYear());
		return instance;
	}

	/**
	 * 获取日，需要使用{@link #showTime()}来转换成字符串
	 * 
	 * @return DateUtils
	 */
	public static synchronized DateTool addDay() {
		if (mFormatStr == null) {
			mFormatStr = new ArrayList<String>();
			Log.i(TAG, "new Map");
		}
		mFormatStr.add(DAY + mInternat.getDay());
		return instance;
	}

	/**
	 * 获取月，需要使用{@link #showTime()}来转换成字符串
	 * 
	 * @return DateUtils
	 */
	public static synchronized DateTool addMonth() {
		if (mFormatStr == null) {
			mFormatStr = new ArrayList<String>();
			Log.i(TAG, "new Map");
		}
		mFormatStr.add(MONTH + mInternat.getMonth());
		return instance;
	}

	/**
	 * 显示当期系统时间，可以配合{@link #addMonth()}<br/>
	 * {@link #addDay()}<br/>
	 * {@link #getYear()} 方法使用，也可以单独使用。
	 * 
	 * @return
	 */
	public static synchronized String showTime() {
		StringBuilder builder = new StringBuilder();
		if (mFormatStr == null || mFormatStr.size() == 0) {
			builder.append(D_T_FORMAT);// 使用默认的日期格式
		} else {
			int index = 0;
			int length = mFormatStr.size();
			Log.i(TAG, mFormatStr.size() + "");
			Iterator<String> iterator = mFormatStr.iterator();
			while (iterator.hasNext()) {
				String values = iterator.next();
				builder.append(values);
			}
		}
		if (mInternat instanceof EN) {
			builder.deleteCharAt(builder.length() - 1);
		}
		SimpleDateFormat sdf = new SimpleDateFormat(builder.toString());
		return sdf.format(new Date());
	}
	
	/**
	 * 返回月份之间的差。
	 * 
	 * @param startYear
	 * @param startMonth
	 * @param endYear
	 * @param endMonth
	 * @return
	 */
	public static int differMonth(String startYear, String startMonth,
			String endYear, String endMonth) {
		return (Integer.parseInt(endYear) - Integer.parseInt(startYear)) * 12
				+ (Integer.parseInt(endMonth) - Integer.parseInt(startMonth));

	}
	
	// 获得当前日期与本周一相差的天数
    private static int getMondayPlus() {
        Calendar cd = Calendar.getInstance();
        // 获得今天是一周的第几天，星期日是第一天，星期二是第二天......
        int dayOfWeek = cd.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == 1) {
            return -6;
        } else {
            return 2 - dayOfWeek;
        }
    }

    /**
     * 获得相应周的周一的日期
     * @param weeks
     * @return
     */
    public static String getMonday(int weeks) {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }

    /**
     * 获得相应周的周日的日期
     * @param weeks
     * @return
     */
    public static String getSunday(int weeks) {
        int mondayPlus = getMondayPlus();
        GregorianCalendar currentDate = new GregorianCalendar();
        currentDate.add(GregorianCalendar.DATE, mondayPlus + 7 * weeks + 6);
        Date monday = currentDate.getTime();
        DateFormat df = DateFormat.getDateInstance();
        String preMonday = df.format(monday);
        return preMonday;
    }  
    /**
     * 通过阿拉伯数字对应的中文显示星期几
     * @param dateNum
     * @return
     */
    public static String getDateWeekCnName(int dateNum) {
    	String s = "";
    	try {
    		s = WEEK_CN_NAME[dateNum];
		} catch (Exception e) {
			// TODO: handle exception
		}
		return s;
	}
    
    /**
     * 获取指定的Date对象，中文显示的星期。
     * @param dateNum
     * @return
     */
    public static String getDateWeekCnName(Date date) {
    	String s = "";
    	int dateNum = getDateWeekNum(date);
    	try {
    		s = WEEK_CN_NAME[dateNum - 1];
		} catch (Exception e) {
			// TODO: handle exception
		}
		return s;
	}
    /**
     * 获取当前指定date所对应的在此星期中是第几天
     * @param date
     * @return
     */
    public static int getDateWeekNum(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int dayForWeek = 0;
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			dayForWeek = 7;
		} else {
			dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
		}
		return dayForWeek;
	}
    
    /**
     * 通过string如‘2011-1-1’，返回Date对象,如果传入的格式不正确将发生异常，将会返回当前日期的Date对象。
     * @param str'2011-1-1'
     * @param pattern'yyyy-MM-dd'
     * @return
     */
    public static Date strToDate(String str, String pattern) {
		Date date = null;
		if (str != null) {
			SimpleDateFormat df = new SimpleDateFormat(pattern);
			try {
				date = df.parse(str);
			} catch (ParseException e) {
				date = new Date();
			}
		}
		return date;
	}
    
    /**
	 * 在原有的日期上面加i天
	 * 
	 * @param date
	 * @param i
	 * @return
	 */
	public static Date add(Date date, int i) {
		date = new Date(date.getTime() + i * ONE_DAY);
		return date;
	}
	
	/**
	 * 返回相差分钟数
	 * 
	 * @param endDate
	 * @param startDate
	 * @return
	 */
	public static int differMinute(Date endDate, Date startDate) {
		if (endDate == null || startDate == null)
			return 0;

		Calendar c1 = Calendar.getInstance();
		c1.setTime(endDate);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(startDate);
		long l = c1.getTimeInMillis() - c2.getTimeInMillis();

		return (int) (l / (1000 * 60));
	}

	/**
	 * 返回相差秒数数
	 * 
	 * @param endDate
	 * @param startDate
	 * @return
	 */
	public static int differSecond(Date endDate, Date startDate) {
		if (endDate == null || startDate == null)
			return 0;

		Calendar c1 = Calendar.getInstance();
		c1.setTime(endDate);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(startDate);
		long l = c1.getTimeInMillis() - c2.getTimeInMillis();

		return (int) (l / (1000));
	}
	
	/**
	 * 求日期之差 ，驶入指定格式的日期显示形式，这里只能输入yyyy-MM-dd的日期形式<br>
	 * 比如：<b>compareDate("2011-2-1", "2011-3-1");</b>
	 * @param sDate1 
	 * @param sDate2
	 * @return 返回求差后的天数
	 * 
	 */
	public static int differDate(String sDate1, String sDate2) {

		Date date1 = null;
		Date date2 = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

		try {
			date1 = dateFormat.parse(sDate1);
			date2 = dateFormat.parse(sDate2);
		} catch (ParseException e) {

		}

		long dif = 0;
		if (date2.after(date1))
			dif = (date2.getTime() - date1.getTime()) / 1000 / 60 / 60 / 24;
		else
			dif = (date1.getTime() - date2.getTime()) / 1000 / 60 / 60 / 24;

		return (int) dif;
	}
	
	/**
	 * 加1天
	 * 
	 * @param date
	 * @return
	 */
	public static Date addOneDay(Date date) {
		return add(date, 1);
	}

	/**
	 * 减1天
	 * 
	 * @param date
	 * @return
	 */
	public static Date subOneDay(Date date) {
		return add(date, -1);
	}
	/**
	 * 
	 * @param date
	 * @param count 负数
	 * @return
	 */
	public static Date subDay(Date date, int count){
		return add(date, -count);
	}
	
	/**
	 * 
	 * @param date
	 * @param dateFormat
	 * @return
	 */
	public static String dateToString(Date date, String dateFormat){
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		String str = sdf.format(date);
		return str;
	}
}
