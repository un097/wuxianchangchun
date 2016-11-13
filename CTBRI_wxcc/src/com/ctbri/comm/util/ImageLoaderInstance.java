package com.ctbri.comm.util;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageLoaderInstance {

	public static ImageLoader getInstance(Context context){
		ImageLoader loader = ImageLoader.getInstance();
		if(!loader.isInited()){
			loader.init(ImageLoaderConfiguration.createDefault(context.getApplicationContext()));
		}
		return loader;
	}
}
