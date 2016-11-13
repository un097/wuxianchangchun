package cn.ffcs.wisdom.base;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

/**
 * <p> 业务逻辑处理基础类 </p>
 * 
 * <p> 将业务逻辑处理方法，数据获取，数据填充放在业务逻辑层处理。 </p><br/>
 * 
 * @author  caijj
 * @version 1.00, 2012-5-22
 */
public class BaseBo {

	protected Activity mActivity;
	protected HttpCallBack<BaseResp> icall;

	public BaseBo(Activity activity) {
		this.mActivity = activity;
	}
	
	public BaseBo(Activity act, HttpCallBack<BaseResp> icall) {
		this.mActivity = act;
		this.icall = icall;
	}

	/**
	 * 根据id查找view
	 * @param resourceId 资源id
	 * @return
	 */
	protected View findViewById(int resourceId) {
		return mActivity.findViewById(resourceId);
	}

	protected Drawable getDrawable(int resourceId) {
		return mActivity.getResources().getDrawable(resourceId);
	}

	protected ColorStateList getColorStateList(int resourceId) {
		return mActivity.getResources().getColorStateList(resourceId);
	}
}
