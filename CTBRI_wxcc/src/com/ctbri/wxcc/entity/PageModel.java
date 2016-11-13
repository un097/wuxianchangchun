package com.ctbri.wxcc.entity;

import java.util.Locale;

/**
 * 分页 实体，便于分页使用  
 * <hr />
 * 没有实现 geter setter
 * @author yanyadi
 *
 */
public class PageModel {
	public PageModel(){}
	public PageModel(int start, int count) {
		this.start = start;
		this.count = count;
	}

	public int start;
	public int count;
	
	public String toString(){
		return String.format(Locale.CHINA,"start=%d&count=%d", start,count);
	}
	/**
	 * 把分页参数 追加至 url 的尾部
	 * @param str
	 * @return
	 */
	public String appendToTail(String str){
		if(str.lastIndexOf("?") < 0)
			return str + "?" + this;
		else
			return str + "&" + this;
	}
}
