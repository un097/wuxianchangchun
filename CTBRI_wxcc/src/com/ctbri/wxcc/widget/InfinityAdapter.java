package com.ctbri.wxcc.widget;

import java.util.concurrent.atomic.AtomicInteger;

import com.wookii.tools.comm.LogS;

import android.support.v4.view.PagerAdapter;
import android.view.ViewGroup;

public abstract class InfinityAdapter extends PagerAdapter {

	private	AtomicInteger itemIncreInteger = new AtomicInteger(10);
	/**
	 * 获取全部平移滑动的虚拟条数
	 * @return
	 */
	public int getRepeatCount(){
		
		return getRealCount()  * (((Double)Math.pow(2, itemIncreInteger.get())).intValue() + 1);
	}
	/**
	 * 获取实际的条数
	 * @return
	 */
	public abstract int getRealCount();
	
	@Override
	public int getCount() {
		return getRepeatCount();
	}
	@Override
	public final Object instantiateItem(ViewGroup container, int repeatPosition) {
		int position = repeatPosition % getRealCount();
		return getView(container, position);
	}
	public abstract Object getView(ViewGroup container, int position);
	/**
	 * 获取当前初始化时显示的索引
	 * @return
	 */
	public int getCurrentItem() {
		LogS.i("getCurrentItm", " rept ="+ getRepeatCount() + "realCount=" + getRealCount());
		return (getRepeatCount()-getRealCount())/2 ;
	}
	/**
	 * 生成虚拟化的页面
	 */
	public void generateRepeatCount(){
		itemIncreInteger.incrementAndGet();
	};
	
}
