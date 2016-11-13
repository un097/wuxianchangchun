package cn.ffcs.wisdom.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;

import android.content.Context;

/**
 * <p>Title: 对象工具类 </p>
 * <p>Description: 
 * 1、序列化对象到硬盘
 * 2、读取硬盘序列化对象</p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-29             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public final class FileUtils {

	/**
	 * Serialize the object
	 */
	public static void write(Context context, String fileName, Object obj) {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(context.openFileOutput(fileName, Context.MODE_PRIVATE));
			out.writeObject(obj);
			out.flush();
		} catch (FileNotFoundException e) {
			Log.e("FileNotFoundException error, message:" + e.getMessage(), e);
			// e.printStackTrace();
		} catch (IOException e) {
			Log.e("IOException error, message:" + e.getMessage(), e);
			// e.printStackTrace();
		} finally {
			StreamUtil.closeSilently(out);
		}
	}

	/**
	 * Deserialize the Object
	 */
	public static Object read(Context context, String fileName) {
		ObjectInputStream input = null;
		try {
			input = new ObjectInputStream(context.openFileInput(fileName));
			return input.readObject();
		} catch (Exception e) {
			Log.e(" error message:" + e.getMessage(), e);
		}finally {
			StreamUtil.closeSilently(input);
		}

		return null;
	}

	/**
	 * delete cache file
	 */
	public static boolean deleteFile(Context context, String fileName) {
		return context.deleteFile(fileName);
	}

	/**
	 * 把文件转为byte[]
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		// 获取文件大小
		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// 文件太大，无法读取
			is.close();
			throw new IOException("File is to large " + file.getName());
		}
		// 创建一个数据来保存文件数据
		byte[] bytes = new byte[(int) length];
		// 读取数据到byte数组中
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length
				&& (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		// 确保所有数据均被读取
		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file " + file.getName());
		}
		// Close the input stream and return bytes
		is.close();
		return bytes;
	}
}
