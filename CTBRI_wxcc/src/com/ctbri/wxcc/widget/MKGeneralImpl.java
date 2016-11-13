package com.ctbri.wxcc.widget;

import android.content.Context;
import android.widget.Toast;

import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;
import com.wookii.tools.comm.LogS;

public class MKGeneralImpl implements MKGeneralListener {

	public MKGeneralImpl(Context c) {
		this.context = c;
	}
	
	private Context context;
	@Override
    public void onGetNetworkState(int iError) {
        if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
            Toast.makeText(context, "您的网络出错啦！",
                Toast.LENGTH_LONG).show();
        }
        else if (iError == MKEvent.ERROR_NETWORK_DATA) {
            Toast.makeText(context, "输入正确的检索条件！",
                    Toast.LENGTH_LONG).show();
        }
        // ...
    }

    @Override
    public void onGetPermissionState(int iError) {
    	//非零值表示key验证未通过
        if (iError != 0) {
            //授权Key错误：
            Toast.makeText(context, 
                    "请在 DemoApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
        }
        else{
        	LogS.i("BMapManager", 
                    "key认证成功");
        }
    }

}
