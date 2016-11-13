package com.ctbri.wxcc.entity;

import java.io.Serializable;

public class GsonContainer<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7037335170747543897L;
	
	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	private T data;
	
}
