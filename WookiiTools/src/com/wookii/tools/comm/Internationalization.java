package com.wookii.tools.comm;

/**
 * 一个国际化的接口，里面提供了获取日期表现形式的方法。你也可以自己实现这个接口，来实现自己的日期表现形式
 * 
 * @author Chen Wu
 * @see  CH
 * @see  EN
 */
public interface Internationalization {
	public abstract String getYear();

	public abstract String getMonth();

	public abstract String getDay();

	public abstract String getHour();

	public abstract String getMinute();

	public abstract String getSecond();
}
