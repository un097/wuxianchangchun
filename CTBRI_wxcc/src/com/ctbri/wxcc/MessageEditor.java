package com.ctbri.wxcc;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;

import com.wookii.tools.comm.Encryption;

public class MessageEditor {

	private static final String EMPTY_TOEKN = null;

	public static void setHotLineMd5(Context context, String value) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ctbri_file", Context.MODE_PRIVATE);
		String md5 = Encryption.stringMd5(value);
		Editor edit = sharedPreferences.edit();
		edit.putString("md5", md5);
		edit.commit();
	}

	public static String getHotLineMd5(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ctbri_file", Context.MODE_PRIVATE);
		return sharedPreferences.getString("md5", EMPTY_TOEKN);
	}

	public static String getUserId(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ctbri_file", Context.MODE_PRIVATE);
		return sharedPreferences.getString("user_id", EMPTY_TOEKN);
	}

	public static String getUserName(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ctbri_file", Context.MODE_PRIVATE);
		return sharedPreferences.getString("user_name", null);
	}

	public static String getTel(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ctbri_file", Context.MODE_PRIVATE);
		return sharedPreferences.getString("tel", EMPTY_TOEKN);
	}

	public static String getUserUrl(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ctbri_file", Context.MODE_PRIVATE);
		return sharedPreferences.getString("user_url", "");
	}

	/*	
	*//**
	 * 初始化
	 * 
	 * @param userId
	 *            用户Id
	 * @param userName
	 *            用户名称
	 * @param url
	 *            用户头像的url地址
	 */
	/*
	 * public static void initCTBRI(Context context, String userId, String
	 * userName, String url, String tel){ SharedPreferences sharedPreferences =
	 * context.getSharedPreferences("ctbri_file", Context.MODE_PRIVATE); Editor
	 * edit = sharedPreferences.edit(); edit.putString("user_id", userId);
	 * edit.putString("user_name", userName); edit.putString("user_url", url);
	 * edit.putString("tel", url); edit.commit(); }
	 */
	/**
	 * 如果返回的是true，表示已登陆
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isLogin(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ctbri_file", Context.MODE_PRIVATE);
		String user_id = sharedPreferences.getString("user_id", null);
		return !TextUtils.isEmpty(user_id);
	}

	/**
	 * 初始化或者更新
	 * 
	 * @param context
	 * @param userId
	 * @param userName
	 * @param url
	 * @param tel
	 */
	public static void initOrUpdateCTBRI(Context context, String userId,
			String userName, String url, String tel) {
		if (userId != null) {
			Log.e("sb", userId + "        --------------");
		}
		
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ctbri_file", Context.MODE_PRIVATE);
		Editor edit = sharedPreferences.edit();
		if (!TextUtils.isEmpty(userId)) {
			edit.putString("user_id", userId);
		}

		if (!TextUtils.isEmpty(userName)) {
			edit.putString("user_name", userName);
		}

		if (!TextUtils.isEmpty(url)) {
			edit.putString("user_url", url);
		}
		if (!TextUtils.isEmpty(tel)) {
			edit.putString("tel", tel);
		}
		edit.commit();
	}

	/**
	 * 注销
	 * 
	 * @param context
	 */
	public static void unregisterCTBRI(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"ctbri_file", Context.MODE_PRIVATE);
		Editor edit = sharedPreferences.edit();
		edit.putString("user_id", "");
		edit.putString("user_name", "");
		edit.putString("user_url", "");
		edit.putString("tel", "");
		edit.commit();
	}
}
