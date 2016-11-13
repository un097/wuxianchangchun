package cn.ffcs.wisdom.city.simico.image;

import net.tsz.afinal.FinalBitmap;
import net.tsz.afinal.bitmap.display.Displayer;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.SparseArray;
import android.widget.ImageView;

/**
 * <p>Title:     图片加载base          </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-22           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class BaseImageLoader {

	public Context mContext;
	public static SparseArray<Bitmap> bitMap = new SparseArray<Bitmap>();
	public FinalBitmap finalBitmap;
	private String nativeDir = "/imageCache/";
	private float memoryCacheSize = 0;
	private int theadCount = 0;
	private String dir = "";
	private BaseDisplayer baseDisplayer = new BaseDisplayer();

	/**
	 * 图片存放目录
	 * @return
	 */
	public abstract String getImageDir();

	/**
	 * 加载失败图片,一定要使用bitmap防止重复decode
	 * @return
	 */
	public abstract Bitmap getLoadFailImage();

	/**
	 * 内存占用百分比
	 * @return defalut is 0.3f
	 */
	public abstract float getMemoryCacheSizePercent();

	/**
	 * 使用线程数
	 * @return default is 3
	 */
	public abstract int getThreadCount();

	/**
	 * 获取diaplayer
	 * @return
	 */
	public Displayer getDisplayer() {
		return baseDisplayer;
	}

	/**
	 * 格式化图片地址
	 * @return
	 */
	public abstract String patternUrl(String url);

	public BaseImageLoader(Context context) {
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		if (finalBitmap == null) {
			if (memoryCacheSize == 0) {
				memoryCacheSize = getMemoryCacheSizePercent();
				if (memoryCacheSize == 0) {
					memoryCacheSize = 0.3f;
				}
			}

			if (theadCount == 0) {
				theadCount = getThreadCount();
				if (theadCount == 0) {
					theadCount = 3;
				}
			}

			if (TextUtils.isEmpty(dir)) {
				dir = getImageDir();
				if (TextUtils.isEmpty(dir)) {
					
				}
			}
//			finalBitmap = FinalBitmap.create(context, patternDir(dir),
//					memoryCacheSize, theadCount);
			finalBitmap = FinalBitmap.create(context);
			finalBitmap.configDiskCachePath(patternDir(dir));
			finalBitmap.configBitmapLoadThreadSize(theadCount);
			finalBitmap.configMemoryCachePercent(memoryCacheSize);
			finalBitmap.configRecycleImmediately(true);
			finalBitmap.configDisplayer(getDisplayer());
		}
	}

	/**
	 * 获取图片  Bitmap类型的
	 * @param key
	 * @return
	 */
	public Bitmap getBitmapFormCache(String key){
		Bitmap bitmap = finalBitmap.getBitmapFromCache(key);
		return bitmap;
	}
	/**
	 * 图片加载
	 * @param imageView 图片控件
	 * @param url 图片地址
	 */
	public void loadUrl(ImageView imageView, String url) {
		String mUrl = patternUrl(url);
		if (!TextUtils.isEmpty(mUrl)) {
			finalBitmap.display(imageView, mUrl, getLoadFailImage(), getLoadFailImage());
		} else {
			finalBitmap.display(imageView, url, getLoadFailImage(), getLoadFailImage());
		}
	}

	/**
	 * 图片加载，限制宽高
	 * @param imageView
	 * @param url
	 * @param imageWidth
	 * @param ImageHeight
	 */
	public void loadUrl(ImageView imageView, String url, int imageWidth, int ImageHeight) {
		String mUrl = patternUrl(url);
		if (!TextUtils.isEmpty(mUrl)) {
			finalBitmap.display(imageView, mUrl, imageWidth, ImageHeight, getLoadFailImage(),
					getLoadFailImage());
		} else {
			finalBitmap.display(imageView, url, imageWidth, ImageHeight, getLoadFailImage(),
					getLoadFailImage());
		}
	}

	// /**
	// * 加载配置
	// * @param url
	// * @return
	// */
	// private String initConfig(String url) {
	// Bitmap failImage = getLoadFailImage();
	// if (failImage != null) {
	// finalBitmap.configLoadingImage(failImage);
	// finalBitmap.configLoadfailImage(failImage);
	// }
	// String mUrl = patternUrl(url);
	// return mUrl;
	// }

	public String patternDir(String dir) {
		String cache = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ? dir
				: mContext.getCacheDir().getPath() + nativeDir;
		return cache;
	}

	/**
	 * 清除能清楚的内存缓存
	 */
	public void clearMemeryCache() {
		if (finalBitmap != null) {
			finalBitmap.clearMemoryCache();
		}
	}

	/**
	 * 清除定的内存缓存
	 * @param key is image url
	 */
	public void clearMemeryCacheByKey(String key) {
		if (finalBitmap != null) {
			finalBitmap.clearMemoryCache(key);
		}
	}

	/**
	 * 暂停加载
	 */
	public void stopLoading() {
		if (finalBitmap != null) {
			finalBitmap.pauseWork(true);
		}
	}

	/**
	 * 开始加载
	 */
	public void startLoading() {
		if (finalBitmap != null) {
			finalBitmap.pauseWork(false);
		}
	}
}
