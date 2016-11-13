package com.ctbri.wxcc.entity;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class AnimatorBean {
	private View view;
	private static final String KEY_WIDTH = "getWidth";
	private static final String KEY_SET_WIDTH = "setWidth";
	
	private static final String KEY_HEIGHT = "getHeight";
	private static final String KEY_SET_HEIGHT = "setHeight";
	private HashMap<String, Invoker> methods;
	public AnimatorBean(View v) {
		this.view = v;
		methods = new HashMap<String, Invoker>();
	}
	
	public void setWidth(int width){
		
		LayoutParams lp  = view.getLayoutParams();
		if(lp==null){
			Invoker method = getInvoker(KEY_SET_WIDTH, Integer.TYPE);
			method.invoke(view, method.retType, width);
		}else{
			lp.width = width;
			view.setLayoutParams(lp);
		}
	}
	public int getWidth(){
		Invoker getwidth = getInvoker(KEY_WIDTH);
		return getwidth.invoke(this.view, Integer.TYPE);
	}
	
	public void setHeight(int height){
		LayoutParams lp  = view.getLayoutParams();
		if(lp==null){
			Invoker method = getInvoker(KEY_SET_HEIGHT, Integer.TYPE);
			method.invoke(view, method.retType, height);
		}else{
			lp.height = height;
			view.setLayoutParams(lp);
		}
	}
	
	public int getHeight(){
		Invoker getwidth = getInvoker(KEY_HEIGHT);
		return getwidth.invoke(this.view, Integer.TYPE);
	}
	
	private Invoker getInvoker(String key,Class<?> ...args){
		Invoker invoker = methods.get(key);
		if(invoker==null)
		{
			Method method = getMethodByName(key,args);
			invoker = new Invoker();
			invoker.method = method;
			invoker.retType = method.getReturnType();
			
			methods.put(key, invoker);
		}
		return invoker;
	}
	
	class Invoker{
		Method method;
		Class<?> retType;
		@SuppressWarnings("unchecked")
		public <T> T invoke(Object o,Class<T> cls,Object ...args){
			
			if(method==null)return null;
			
			try {
				return (T)method.invoke(o,args);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			return null;
		}
	}
	
	private Method getMethodByName(String name,Class<?>...args){
		Method method = null;
		try {
			method = view.getClass().getMethod(name,args);
			method.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return method;
	}
}
