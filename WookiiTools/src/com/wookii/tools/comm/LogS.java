package com.wookii.tools.comm;

import java.io.PrintWriter;
import java.io.StringWriter;

import android.os.Environment;
import android.util.Log;

/**
 * @title SOSLog
 * @version time:2010-10-18 下午17:29:44
 * @copyright (c) www.sosgps.net.cn
 * @author wuchen
 * @description 记录显示系统日志
 *
 */
public class LogS {
	public static final boolean ACCESS_WRITE = true;
	public static final boolean INTERDICT_WRITE = false;
	private LogS(){};
	public static String filePath = "";
	public static String name = "logs";
	public static boolean show = false;
	public static boolean written2Disk = INTERDICT_WRITE;
	public static void i(String title, String message) {
		if(show){
			Log.i(title, message);
		}
		
		if(written2Disk){
			writeLog(title, message);
		}
		
	}
	public static void t(String title, Exception message) {
		message.printStackTrace();
		if(written2Disk){
			writeLog(title, message);
		}
		
	}
	public static void w(String title, String message) {
		if(show){
			Log.w(title, message);
		}
		if(written2Disk){
			writeLog(title, message);
		}
		
	}
	public static void v(String title, String message) {
		if(show){
			Log.v(title, message);
		}
		if(written2Disk){
			writeLog(title, message);
		}
		
	}
	
	public static void e(String title, String message) {
		if(show){
			Log.e(title, message);
		}
		if(written2Disk ){
			writeLog(title, message);
		}
	}
	
	
	private static void writeLog(String title, String message) {
		String systime = DateTool.getCurrentTime(DateTool.D_T_FORMAT);
		StringBuilder data = new StringBuilder();
		data.append(systime + " ").append(title).append(" " + message)
				.append("\r\n");
		
		String saveLog = getLogFilePath();

		try {
			RWTool.writeFileData(saveLog, name + "_" + DateTool.getCurrentTime(DateTool.D_FORMAT) + ".log", data.toString(), true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private static void writeLog(String title, Exception message) {
		String systime = DateTool.getCurrentTime(DateTool.D_T_FORMAT);
		StringBuilder data = new StringBuilder();
		data.append(systime + " ").append(title).append(" " + message)
				.append("\r\n");
		
		String saveLog = getLogFilePath();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);  
		message.printStackTrace(pw);
		try {
			RWTool.writeFileData(saveLog, name + "_" + DateTool.getCurrentTime(DateTool.D_FORMAT) + ".log", sw.toString(), true);
			//Log.w("writeLog", sw.toString());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	/**
	 * 用户获取的子目录名称如："/myLogFile/File/....."
	 * @param filePath
	 */
	public static String getLogFilePath(){
		return getStorageBoot() + filePath;
	}
	
	/**
	 * 获取设备外部存储的根目录
	 * @return
	 */
	public static String getStorageBoot(){
		String bootPath = "";
		try {
			if(DeviceTool.isHasSDCard()){
				bootPath = Environment
				.getExternalStorageDirectory().getAbsolutePath();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bootPath;
	}
	

	


}
