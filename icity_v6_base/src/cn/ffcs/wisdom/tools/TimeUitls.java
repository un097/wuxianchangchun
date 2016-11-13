package cn.ffcs.wisdom.tools;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.text.TextUtils;

/**
 * <p>Title:  日期时间工具类                                          </p>
 * <p>Description:  包括
 * 1. 获取当前时间
 * 2. 比较两个时间点的时间差，返回分钟
 * 3. 格式化日期
 * 4. 取得一天后的当前时间
 * </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-4-12           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TimeUitls {

	public static String getCurrentTime(){
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sDateFormat.format(new Date()); 
		return date;
	}
	
	/**
	 * 比较时间差，如：相差5分钟
	 * @param time1   当前时间
	 * @param time2	要比较的时间
	 * @return
	 */
	public static long compareTime(String time1, String time2) {
		long minutes = 0L;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date d1 = df.parse(time1);
			Date d2 = df.parse(time2);
			long diff = d1.getTime() - d2.getTime();
			minutes = diff / (60 * 1000);
		} catch (Exception e) {
		}
		return minutes;
	}
	
	/**
	 * 用法：Date d = getDate("2013-01-18 16:16:43.0", "yyyy-MM-dd HH:mm:ss");
	 * @param dateStr
	 * @param pattern
	 * @return
	 * @throws ParseException
	 */
	public static Date getDate(String dateStr, String pattern)
			throws ParseException {

		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		return formatter.parse(dateStr);
	}

	/**
	 * 用法: formatDate(d, "yyyy-MM-dd HH:mm:ss");
	 * @param currentDate
	 * @param pattern
	 * @return
	 */
	public static String formatDate(Date currentDate, String pattern) {
		if (currentDate == null || pattern == null) {
			throw new NullPointerException("The arguments are null !");
		}
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(currentDate);
	}
	
	/**
	 * 取得一天后的时间
	 * @return
	 */
	public static String getTomorrowTime() {
		Format formatter = new SimpleDateFormat("yyyy-MM-dd hh:MM:ss");
		Calendar Cal = Calendar.getInstance();
		Cal.setTime(new Date());
		Cal.add(java.util.Calendar.HOUR_OF_DAY, 24);
		return formatter.format(Cal.getTime());
	}

	
	public static String getdate(String timeStr) {
		if (null != timeStr && !timeStr.equals("") && !timeStr.equals("0")) {
			StringBuffer sb = new StringBuffer();
			long t = Long.parseLong(timeStr) / 1000;
			long time = System.currentTimeMillis() - (t * 1000);
			// long mill = (long) Math.ceil(time / 1000);// 秒前
			long minute = (long) Math.ceil(time / 60 / 1000.0f);// 分钟前
			// long hour = (long) Math.ceil(time / 60 / 60 / 1000.0f);// 小时
			// long day = (long) Math.ceil(time / 24 / 60 / 60 / 1000.0f);// 天前
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd");
			// 已-拆分时间字符串
			String serverTimeArray[] = simpleDateFormat.format(
					new Date(Long.parseLong(timeStr) * 1000)).split("-");
			String localTimeArray[] = simpleDateFormat.format(
					new Date(System.currentTimeMillis())).split("-");
			int serverYear = Integer.parseInt(serverTimeArray[0]);
			int serverMonth = Integer.parseInt(serverTimeArray[1]);
			int serverDay = Integer.parseInt(serverTimeArray[2]);
			int localYear = Integer.parseInt(localTimeArray[0]);
			int localMonth = Integer.parseInt(localTimeArray[1]);
			int localDay = Integer.parseInt(localTimeArray[2]);

			// 当天内
			if (serverYear == localYear && serverMonth == localMonth
					&& ((localDay - serverDay) == 0)) {
				if (minute - 1 <= 0) {
					sb.append("刚刚");
				} else if (minute - 60 <= 0) {
					if (minute == 60) {
						sb.append("60分钟前");
					} else {
						sb.append(minute + "分钟前");
					}
				} else {
					DateFormat df = new SimpleDateFormat("HH:mm");
					Date date = new Date(t * 1000);
					return df.format(date);
				}
			} else if (serverYear == localYear && serverMonth == localMonth
					&& ((localDay - serverDay) == 1)) // 超过一天
			{
				DateFormat df = new SimpleDateFormat("HH:mm");
				Date date = new Date(t * 1000);
				return "昨天" + " " + df.format(date);
			} else if (serverYear == localYear && serverMonth == localMonth
					&& ((localDay - serverDay) == 2)) // 超过2天
			{
				DateFormat df = new SimpleDateFormat("HH:mm");
				Date date = new Date(t * 1000);
				return "前天" + " " + df.format(date);
			} else
			// 超过2天以后直接显示日期
			{
				Date date = new Date(t * 1000);
				return simpleDateFormat.format(date);
			}
			return sb.toString();
		} else {
			return "";
		}
	}
	
	private static SimpleDateFormat commentPattern;
	private static SimpleDateFormat datePattern;
	static{
		datePattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		commentPattern = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
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
	
	
   public static String dataStringFormat(String date) {
       if (TextUtils.isEmpty(date)) {
           return "";
       }
       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       try {
           Date parse = format.parse(date);
           return parse.toString();
       } catch (ParseException e) {
           e.printStackTrace();
       }
       return "";
   }
   
   /**
    * Long 转 String
    * @param date
    * @return
    */
   public static String LongToString(long date) {
       SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
       Date date2 = new Date(date);
       return format.format(date2);
   }
	
}
