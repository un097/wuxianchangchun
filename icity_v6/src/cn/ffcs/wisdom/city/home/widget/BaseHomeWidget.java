package cn.ffcs.wisdom.city.home.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * <p>Title:  首页baseWidget           </p>
 * <p>Description: 
 * 1、mContext已赋值可以直接使用        
 * 2、请将刷新方法写在refresh里面，外部直接调用可以刷新整个widget
 * 2、mLayoutInflater用于获取xml文件布局
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-4-3           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class BaseHomeWidget extends LinearLayout {

	/**
	 * 用于获取xml布局
	 */
	public LayoutInflater mLayoutInflater;

	public abstract void refresh();// 刷新方法，下层必须把刷新整个控件的方法写在这

	public Context mContext;

	public abstract int setContentView();

	public BaseHomeWidget(Context context) {
		super(context);
		init(context);
	}

	public BaseHomeWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	private void init(Context context) {
		this.mContext = context;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mLayoutInflater.inflate(setContentView(), this, true);
	}
}
