package com.ctbri.wxcc.entity;

import java.io.Serializable;

import android.text.TextUtils;
import android.util.Log;

import com.baidu.platform.comapi.basestruct.GeoPoint;

public class CommonPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1237902108061732933L;

	private String lat;
	private String lng;
	private String title;

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public CommonPoint(String lng, String lat, String title) {
		super();
		this.lat = lat;
		this.lng = lng;
		this.title = title;
	}

	public CommonPoint(String lat, String lng) {
		super();
		this.lat = lat;
		this.lng = lng;
	}
	public CommonPoint() {
	}

	private static double parseDouble(String n) {
		double num = 0;
		try {
			num = Double.parseDouble(n);
		} catch (Exception e) {
			Log.e("parseDouble", "CommonPoint parseDouble error" + n);
		}
		return num;
	}

	public static GeoPoint parseGeoPoint(CommonPoint point) {
		if (point == null)
			return null;
		
		int lat = (int)( parseDouble(point.lat) * 1E6 );
		int lng = (int)( parseDouble(point.lng) * 1E6 );
		
		return new GeoPoint(lat, lng);
	}
	
	public static CommonPoint parseCommonPoint(String location, String token, String title){
		if(TextUtils.isEmpty(location))return null;
		String ptStr = "^\\d+\\.*?\\d*"+ token +"\\d+\\.*?\\d*$";
		if(location.matches(ptStr)){
			String args [] = location.split(token);
			return new CommonPoint(args[0], args[1], title);
		}
		return null;
	}
	

}
