package cn.ffcs.wisdom.base;

import android.database.DataSetObserver;

/**
 * 观察者模式接口
 * 
 * @author  caijj
 * @version 1.00, 2012-4-6
 */
public interface IObserver {

	void registerDataSetObserver(DataSetObserver observer);

	void unregisterDataSetObserver(DataSetObserver observer);

}
