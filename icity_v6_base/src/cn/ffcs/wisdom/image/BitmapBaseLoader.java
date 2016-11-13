package cn.ffcs.wisdom.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import cn.ffcs.wisdom.http.ImageGetter;
import cn.ffcs.wisdom.http.ProgressListener;
import cn.ffcs.wisdom.http.entity.BitmapProgress;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.MD5;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 图片加载器 								 	 </p>
 * <p>Description: 									 </p>
 * <p>@author: caijj                				 </p>
 * <p>Copyright: Copyright (c) 2012    				 </p>
 * <p>Company: FFCS Co., Ltd.          				 </p>
 * <p>Create Time: 2013-1-18            			 </p>
 * <p>Update Time:                     				 </p>
 * <p>Updater:                         				 </p>
 * <p>Update Comments:  建议统一使用BaseImageLoader类               </p>
 */
@Deprecated
public abstract class BitmapBaseLoader {

	private Context mContext;
	
	/**
	 * 默认图片资源id，如：R.android.drawable.ic_launcher
	 */
	public int mDefResourceId;

	/**
	 * 图片监听
	 */
	private ProgressListener<BitmapProgress> mProgressListener;

	/**
	 * 图片所存路径
	 */
	private String mSdcardDir;
	
	/**
	 * 图片缓存key，图片名
	 */
	private String key;
	
	/**
	 * 图片下载地址 
	 */
	private String mUrl;
	
	/**
	 * 需要设置图片的控件
	 */
	private View mView;

	/**
	 * 是否需要压缩，默认不压缩
	 */
	public boolean isCompress;
	
	/**
	 * 压缩比例（宽）
	 */
	public int junst;

	/**
	 * whether to cache, the default is false;
	 */
	public boolean mToCache;

	/**
	 * 设置图片模式
	 * IMAGE_MODE_SRC： 设置src
	 * IMAGE_MODE_BACKGROUND： 设置背景
	 */
	public int imageMode;

	public final static int IMAGE_MODE_SRC = 0x1;
	public final static int IMAGE_MODE_BACKGROUND = 0x2;

	public BitmapBaseLoader(Context context, View view) {
		this.mContext = context;
		this.mView = view;

		mSdcardDir = getSdcardDir();
		mToCache = false;

		imageMode = IMAGE_MODE_BACKGROUND;
	}

	protected abstract String getSdcardDir();

	public abstract void cacheDrawable(Drawable drawable, String key);

	public abstract Drawable getCache(String key);

	public abstract String patternUrl(String url);

	/**
	 * 调用该方法，获取图片
	 * 1、本地存在图片，从本地获取
	 *    1-1、从缓存获取
	 *    1-2、缓存不存在，从sdcard获取
	 * 2、本地不存在，网络下载
	 * @param url
	 * @return
	 */
	public Drawable loadBitmap(String url) {

		String patternUrl = patternUrl(url);
		if (!StringUtil.isEmpty(patternUrl)) {
			url = patternUrl;
		}

		mUrl = url;

		Drawable drawable = null;
		if (StringUtil.isEmpty(mUrl)) {
			return drawable;
		}

		// 从本地获取图片
		key = getKey();
		if (SdCardTool.isMounted()) {
			drawable = getDrawableLocal(key);
		}

		// 从网络下载图片
		if (drawable == null) {
			getDrawableNetWork(url, key);
		} else {
			setImageView(drawable);
		}

		return drawable;
	}

	protected void getDrawableNetWork(String url, String key) {
		Log.d("download bitmap, url: " + url);

		ImageGetter getter = null;
		if (SdCardTool.isMounted()) { // sd卡挂载
			getter = new ImageGetter(mSdcardDir, key);
		} else { // sd卡未挂载
			Log.w("sdcard unmounted.");
			getter = new ImageGetter(mSdcardDir, key);
		}

		getter.setProgressListener(transmitListener);
		getter.execute(url);
	}

	private Drawable getDrawableLocal(String key) {
		Drawable drawable = null;
		if (mToCache) {
			drawable = getCache(key);
		}

		if (drawable == null) {
			drawable = getDrawableSdcard(key);
		}
		return drawable;
	}

	protected Drawable getDrawableSdcard(String key) {
		Drawable drawable = null;

		String path = mSdcardDir + "/" + key;
		File file = new File(path);

		FileInputStream is;
		try {
			is = new FileInputStream(file);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			if(isCompress){
				bitmap = BitmapUtil.zoom(bitmap, junst);
			}
			drawable = new BitmapDrawable(mContext.getResources(),bitmap);
		} catch (FileNotFoundException e) {
			Log.e("file not fount, file path " + path);
			// e.printStackTrace();
		}

		return drawable;
	}

	protected void setImageView(Drawable drawable) {
		if (mView != null) {
			if (drawable == null && mDefResourceId > 0) {
				drawable = new BitmapDrawable(BitmapFactory.decodeResource(mContext.getResources(),
						mDefResourceId));
			}

			// if (mView instanceof ImageView) {
			// ((ImageView) mView).setImageDrawable(drawable);
			// } else {
			if (drawable != null) {

				if (mView instanceof ImageView) {
					if (imageMode == IMAGE_MODE_SRC) {
						ImageView img = (ImageView) mView;
						img.setImageDrawable(drawable);
						return;
					}
				}

				mView.setBackgroundDrawable(drawable);
			}

			// }
		}
	}

	private void toCacheBitmap(Bitmap bitmap) {
		if (mToCache && bitmap != null) {
			cacheDrawable(new BitmapDrawable(bitmap), key);
		}
	}

	protected String getKey() {
		return MD5.getMD5Str(mUrl);
	}

	public void setSdcardDir(String dir) {
		this.mSdcardDir = dir;
	}

	private ProgressListener<BitmapProgress> transmitListener = new ProgressListener<BitmapProgress>() {

		@Override
		public void onProgressUpdate(BitmapProgress progress) {
			if (mProgressListener != null) {
				mProgressListener.onProgressUpdate(progress);
			}
		}

		@Override
		public void onPreExecute(BitmapProgress progress) {
			if (mProgressListener != null) {
				mProgressListener.onPreExecute(progress);
			}
		}

		@Override
		public void onPostExecute(BitmapProgress progress) {

			Bitmap result = progress.getBitmap();
			BitmapDrawable drawable = null;
			if (result != null)
				drawable = new BitmapDrawable(mContext.getResources(), result);

			setImageView(drawable);

			if (mProgressListener != null) {
				if (isCompress) {

					if (result != null) {
						result = BitmapUtil.zoom(result, junst);
						progress.setBitmap(result);
						progress.setUrlStr(mUrl);//add by liaodl
					}
				}

				toCacheBitmap(progress.getBitmap());

				mProgressListener.onPostExecute(progress);
			}
		}
	};

	public void setProgressListener(ProgressListener<BitmapProgress> l) {
		this.mProgressListener = l;
	}
}
