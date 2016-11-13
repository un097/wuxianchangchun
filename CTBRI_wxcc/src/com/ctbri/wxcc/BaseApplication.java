package com.ctbri.wxcc;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.baidu.mapapi.BMapManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.wookii.tools.comm.LogS;

public class BaseApplication extends Application {
	public static String filePath; 
	public static ImageLoader imageLoader = ImageLoader.getInstance();
	public static final String oldKey = "6rstkDGQseGTVE0lhLFInaMF";
    public BMapManager mapManager = null;
    
	public static DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(false)
			.cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565)
			.build();
	
	@Override  
    protected void attachBaseContext(Context base) {  
        super.attachBaseContext(base);  
    } 
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		final Context mContext = this;  
        new Runnable() {  
  
            @Override  
            public void run() {  
                // put your logic here!  
                // use the mContext instead of this here  
            	LogS.written2Disk = LogS.ACCESS_WRITE;
        		LogS.name = "wxcc_log";
        		LogS.show = true;
        		ImageLoaderConfiguration createDefault = ImageLoaderConfiguration.createDefault(getApplicationContext());
        		imageLoader.init(createDefault);
        		
        		//初始化电台播放状态存储路径
        		filePath = getFilesDir().getAbsolutePath()+ "/audio_play_statu.txt"; 
            }  
        }.run();  
		
		
//		Log.e("sb", filePath);
		//初始化百度地图
//		SDKInitializer.initialize(this);
	}
}
