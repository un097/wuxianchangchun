package cn.ffcs.wisdom.tools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.os.Environment;

/**
 * <p>Title:  SD卡工具类</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-29             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class SdCardTool {

	private SdCardTool() {
	}

	/**
	 *  sdcard is exists
	 *  call this method before working on the sdcard
	 */
	@Deprecated
	public static boolean exists() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 *  sdcard is exists
	 *  call this method before working on the sdcard
	 */
	public static boolean isMounted() {
		return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	}

	/**
	 * copy file
	 */
	public static File copy(File file, String dir, String fileName) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			return write(in, dir, fileName);
		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException error message:" + e.getMessage(), e);
			StreamUtil.closeSilently(in);
		}
		return null;
	}

	public static File write(final InputStream in, final String dir, final String fileName) {
		if (in == null)
			return null;

		String absolutePath = dir;

		File f = new File(absolutePath);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				Log.e("mkdirs error:" + absolutePath);
			}
		}

		File mf = new File(absolutePath + "/" + fileName);
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(mf);

			byte bt[] = new byte[512];
			int n = -1;
			while (true) {
				n = in.read(bt);
				if (n <= 0)
					break;
				out.write(bt, 0, n);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException error message:" + e.getMessage(), e);
			// e.printStackTrace();
		} catch (IOException e) {
			Log.e("IOException error desc:" + e.getMessage(), e);
			// e.printStackTrace();
		} finally {
			absolutePath = null;
			StreamUtil.closeSilently(out);
			StreamUtil.closeSilently(in);
		}
		return mf;
	}

	/**
	 * save bitmap to the sdcard
	 * dir "/mnt/sdcard/temp/"
	 * fileName "20111020163433.jpg"
	 */
	public static File save(final Bitmap bitmap, String dir, String fileName) {
		if (bitmap == null)
			return null;

		String absolutePath = dir;
		Log.d("absolutePath " + absolutePath);

		File f = new File(absolutePath);
		if (!f.exists()) {
			if (!f.mkdirs()) {
				Log.e("mkdirs error:" + absolutePath);
			}
		}

		File mf = new File(absolutePath + "/" + fileName);
		OutputStream outputStream = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out);
		byte[] jpegData = out.toByteArray();

		try {
			outputStream = new FileOutputStream(mf);
			outputStream.write(jpegData);
		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException error message:" + e.getMessage(), e);
			// e.printStackTrace();
		} catch (IOException e) {
			Log.e("IOException error desc:" + e.getMessage(), e);
			// e.printStackTrace();
		} finally {
			StreamUtil.closeSilently(outputStream);
		}
		return mf;
	}

	/**
	 *  sdcard root directory
	 */
	public static String getSdcardDir() {
		return Environment.getExternalStorageDirectory().toString();
	}

	/**
	 * delete all files on the path
	 * @param path
	 */
	public static void deleteAll(String path) {
		File file = new File(path);
		if (file.exists()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles();
				for (File f : files) {
					f.delete();
				}
			} else {
				file.delete();
			}
		}
	}

}
