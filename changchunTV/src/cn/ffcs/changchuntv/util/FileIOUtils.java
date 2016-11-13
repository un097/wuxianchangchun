package cn.ffcs.changchuntv.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.ffcs.wisdom.city.simico.base.Application;
import org.apache.http.util.EncodingUtils;

import android.content.Context;
import android.os.Environment;

import com.ctbri.wxcc.BaseApplication;


/**
 * IO流存储数据到本地的工具类
 * @author Zane
 *
 */
public class FileIOUtils {
	private static String SDFilePath = Environment.getExternalStorageDirectory()+ "/audioStatu.txt";
	
	/**
	 * 在应用程序包中创建文件
	 */
	public static File createFile() {
		//判断是否挂载SD卡
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			File file = new File(SDFilePath);//Application.filePath
			try {
				if(!file.exists())
					file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return file;
		}
		return null;
	}
	
	/**
	 * 写入数据到文件
	 * 
	 * @param write_str
	 */
	public static void writeFileSdcardFile(String write_str) {
		try {
			FileOutputStream fout = new FileOutputStream(SDFilePath);
			byte[] bytes = write_str.getBytes();
			fout.write(bytes);
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 从文件中读取数据
	 * 
	 * @return
	 */
	public static String readFileSdcardFile() {
		String res = "";
		try {
			FileInputStream fin = new FileInputStream(SDFilePath);//BaseApplication.filePath
			int length = fin.available();
			byte[] buffer = new byte[length];
			fin.read(buffer);
			res = EncodingUtils.getString(buffer, "UTF-8");
			fin.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
