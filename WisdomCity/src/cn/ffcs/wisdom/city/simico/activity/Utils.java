package cn.ffcs.wisdom.city.simico.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.ffcs.wisdom.city.simico.base.Constants;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;

public class Utils {

	private static final String TAG = Utils.class.getSimpleName();

	public static void saveCache(String content, String name) {

		File cacheDir = new File(Constants.BASE_DIR);
		cacheDir.mkdirs();
		File cacheFile = new File(cacheDir, name);

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

	public static String getCache(String name) {
		File cacheFile = new File(Constants.BASE_DIR, name);
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
	
	public static String getAppFilePath() {
		String rootPath = Constants.BASE_DIR;
		File f = new File(rootPath + "appfile");
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.getAbsolutePath() + "/";
	}
}
