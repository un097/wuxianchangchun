package cn.ffcs.wisdom.image;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import android.graphics.Bitmap;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 图片缓存管理 </p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-29             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class BitmapCache {

	private HashMap<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();

	public void saveBitmap(String key, Bitmap bitmap) {
		if (bitmap != null && !imageCache.containsKey(key))
			imageCache.put(key, new SoftReference<Bitmap>(bitmap));
	}

	public Bitmap getBitmap(String key) {
		if (StringUtil.isEmpty(key))
			return null;
		if (imageCache.containsKey(key)) {
			SoftReference<Bitmap> soft = imageCache.get(key);
			return soft.get();
		}
		return null;
	}

	/**
	 * 根据缓存的key，释放单张图片
	 * @param key
	 */
	public void release(String key) {
		Bitmap bitmap = getBitmap(key);
		if (bitmap != null) {
			try {
				bitmap.recycle();
			} catch (RuntimeException e) {
				Log.e("bitmap release error.", e);
				// e.printStackTrace();
			}
			bitmap = null;
			imageCache.remove(key);
		}
	}

	/**
	 * 释放所有图片
	 */
	public void releaseAll() {
		synchronized (imageCache) {
			if (!imageCache.isEmpty()) {
				Bitmap bitmap = null;
				Set<String> keys = imageCache.keySet();
				for (Iterator<String> it = keys.iterator(); it.hasNext();) {
					String key = (String) it.next();
					bitmap = imageCache.get(key).get();
					if (bitmap != null) {
						try {
							bitmap.recycle();
						} catch (RuntimeException e) {
							Log.e("bitmap release error.", e);
							// e.printStackTrace();
						}
						bitmap = null;
					}
				}

				imageCache.clear();
				imageCache = null;
				imageCache = new HashMap<String, SoftReference<Bitmap>>();
			}
		}
	}

}
