package cn.ffcs.wisdom.application;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;

import dalvik.system.DexClassLoader;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.os.Build;

public class baseApplication extends Application{

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
}
