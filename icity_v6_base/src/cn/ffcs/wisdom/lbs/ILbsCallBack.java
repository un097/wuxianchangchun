package cn.ffcs.wisdom.lbs;

import com.baidu.location.BDLocation;

/**
 * <h3>LBS定位回调接口 </h3>
 * <p>基于百度LBS服务进行定位</p>
 */
public interface ILbsCallBack {

	/**
	 * 回调方法
	 * @param location 
	 *  		位置信息
	 */
	public void call(BDLocation location);
}
