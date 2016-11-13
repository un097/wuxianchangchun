package cn.ffcs.wisdom.base;

import android.database.DataSetObservable;
import android.database.DataSetObserver;

/**
 * 数据管理
 * 
 * <p>作用：<br/>
 * 1. 缓存数据,该类必须为单例模式<br/>
 * 2. 数据变化监听<br/>
 * </p>
 * 
 * <p>使用：
 * 1. 继承该类，把类设计成单例模式
 * 2. 需要监听数据是否变化，请调用方法registerDataSetObserver注册一个观察者
 * </p>
 * 
 * @author  caijj
 * @version 1.00, 2012-4-6
 */
public abstract class DataManager implements IObserver {

	private final DataSetObservable mDataSetObservable = new DataSetObservable();

	@Override
	public void registerDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.registerObserver(observer);
	}

	@Override
	public void unregisterDataSetObserver(DataSetObserver observer) {
		mDataSetObservable.unregisterObserver(observer);
	}

	/**
	 * Notifies the attached View that the underlying data has been changed
	 * and it should refresh itself.
	 */
	public void notifyDataSetChanged() {
		mDataSetObservable.notifyChanged();
	}

	public void notifyDataSetInvalidated() {
		mDataSetObservable.notifyInvalidated();
	}

}
