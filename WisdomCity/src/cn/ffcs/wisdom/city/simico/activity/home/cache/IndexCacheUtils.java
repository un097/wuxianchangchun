package cn.ffcs.wisdom.city.simico.activity.home.cache;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.json.JSONArray;

import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;

public class IndexCacheUtils {

	private static final String TAG = IndexCacheUtils.class.getSimpleName();

	public static void saveCache(JSONArray array, String prefix) {
		if (array == null || array.length() <= 0)
			return;
		String content = array.toString();

		File cacheDir = new File(Constants.CACHE_DIR);
		cacheDir.mkdirs();
		File cacheFile = new File(cacheDir, prefix + Application.getCurrentUser()
				+ Application.getCurrentCity());

		if (cacheFile.exists()) {
			cacheFile.delete();
		}

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(cacheFile);
			os.write(content.getBytes());
			os.flush();
			TLog.log(TAG, "已保存缓存");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
	}

	public static String getCache(String prefix) {
		File cacheFile = new File(Constants.CACHE_DIR, prefix + Application.getCurrentUser()
				+ Application.getCurrentCity());
		if (!cacheFile.exists()) {
			return null;
		}
		FileInputStream is = null;
		ByteArrayOutputStream bos = null;
		try {
			is = new FileInputStream(cacheFile);
			byte[] bytes = new byte[1024];
			bos = new ByteArrayOutputStream();
			while (is.read(bytes) != -1) {
				bos.write(bytes, 0, bytes.length);
			}
			return new String(bos.toByteArray());
		} catch (IOException e) {

		} finally {
			try {
				if (is != null)
					is.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {

			}
		}
		return null;
	}
	
	public static void saveCacheNoUser(JSONArray array, String prefix) {
		if (array == null)
			return;
		String content = array.toString();

		File cacheDir = new File(Constants.CACHE_DIR);
		cacheDir.mkdirs();
		File cacheFile = new File(cacheDir, prefix);

		if (cacheFile.exists()) {
			cacheFile.delete();
		}

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(cacheFile);
			os.write(content.getBytes());
			os.flush();
			TLog.log(TAG, "已保存缓存");
		} catch (IOException e) {
			//e.printStackTrace();
		} finally {
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
					// e.printStackTrace();
				}
			}
		}
	}

	public static String getCacheNoUser(String prefix) {
		File cacheFile = new File(Constants.CACHE_DIR, prefix);
		if (!cacheFile.exists()) {
			return null;
		}
		FileInputStream is = null;
		ByteArrayOutputStream bos = null;
		try {
			is = new FileInputStream(cacheFile);
			byte[] bytes = new byte[1024];
			bos = new ByteArrayOutputStream();
			while (is.read(bytes) != -1) {
				bos.write(bytes, 0, bytes.length);
			}
			return new String(bos.toByteArray());
		} catch (IOException e) {

		} finally {
			try {
				if (is != null)
					is.close();
				if (bos != null)
					bos.close();
			} catch (IOException e) {

			}
		}
		return null;
	} 

}
