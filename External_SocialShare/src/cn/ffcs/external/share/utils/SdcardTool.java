package cn.ffcs.external.share.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.graphics.Bitmap;
import android.os.Environment;

public class SdcardTool {
	
	/**
	 *  sdcard root directory
	 */
	public static String getSdcardDir() {
		return Environment.getExternalStorageDirectory().toString();
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

		File f = new File(absolutePath);
		if (!f.exists()) {
			if (!f.mkdirs()) {
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
		} catch (IOException e) {
		} finally {
			closeSilently(outputStream);
		}
		return mf;
	}

	public static void closeSilently(Closeable c) {
		if (c == null)
			return;
		try {
			c.close();
		} catch (Throwable t) {
		}
	}
}
