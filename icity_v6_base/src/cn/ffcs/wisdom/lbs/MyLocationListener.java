package cn.ffcs.wisdom.lbs;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;

/**
 * <p>定位时间监听器</p>
 * <p>基于百度定位事件监听器</p>
 */
public class MyLocationListener implements BDLocationListener {
	
	private ILbsCallBack icall;
	
	public MyLocationListener(ILbsCallBack icall) {
		this.icall = icall;
	}

	@Override
	public void onReceiveLocation(BDLocation location) {
		icall.call(location);
	}

//	@Override
//	public void onReceivePoi(BDLocation location) {
//		
//	}
}
