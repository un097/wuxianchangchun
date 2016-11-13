package com.ctbri.wxcc.community;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class ObjectAdapter<T> extends BaseAdapter {

	public ObjectAdapter(Activity activity, List<T> data_){
		this.data = new LinkedList<T>(); 
		if(data_!=null)
			this.data.addAll(data_);
		this.activity = activity;
		this.inflater = activity.getLayoutInflater();
	}
	
	protected LayoutInflater inflater;
	protected Activity activity;
	private LinkedList<T> data;
	@Override
	public int getCount() {
		
		return null == data ? 0 : data.size();
	}
	
	public void addAll(List<T> data_){
		if(data_!=null)
		this.data.addAll(data_);
	}
	
	public void addOne(T item){
		this.data.add(item);
	}
	public void addOne(T item,int location){
		this.data.add(location, item);
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}
	public void clearData(){
		if(data==null)return;
		data.clear();
	}
	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void remove(T item){
		if(data==null)return;
		data.remove(item);
	}
	public void removeAll(List<T> objs){
		if(data==null)return;
		data.removeAll(objs);
	}

	@Override
	public abstract View getView(int position, View convertView, ViewGroup parent) ;

}
