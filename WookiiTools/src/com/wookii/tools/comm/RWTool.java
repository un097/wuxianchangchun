package com.wookii.tools.comm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
/**
 * 关于Android系统文件从各种介质中的转移工具类，提供了，从本地到SDCard的复制（这里的本地是指，软件中数据库文件，共享文件
 * 等数据文件存储的地方，一般这个本地地址，为 <b>data\data\软件包名\....,但是要注意，如果要访问这个路径，必须要要有系统最高的权限Root</b>
 * 
 * @author Chen Wu
 * @since 2011.12.10
 *
 */
public class RWTool{
	
	
	
	private RWTool(){};
	
	private static RWTool instance = null;
	
	public synchronized static RWTool getInstance(){
		if(instance == null){
			instance = new RWTool();
		}
		return instance;
	}
	
	/**
	 * 将sdcard中的文件存成本地文件(data\data\软件包名\...)
	 * @see android.content.Context.openFileOutput
	 * @param fos
	 * @param FileName
	 * @throws FileNotFoundException 
	 */
	public static boolean writeFile4SDCard2Local(Context context,String FileName) throws FileNotFoundException{
		
		try {
			byte[] bytes = toByte(FileName);
			if(bytes != null){
				Log.i("xxxxx",bytes+"");
				FileOutputStream fos = context.openFileOutput(FileName, 3);
				fos.write(bytes);
				return true;
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 
	 * 将本地apk文件写入到sdcard中
	 * @param fis
	 * @param FileName
	 */
	public static void writeFile4Local2SDCard(FileInputStream fis, String FileName){
		byte[] bytes = readLocalAPK2Bytes(fis);
		try {
			
			//判断sd卡是否存在，并且可读
			if(Environment.getExternalStorageState()
						.equals(Environment.MEDIA_MOUNTED)){
					
				File sdCardDir = 
						Environment.getExternalStorageDirectory();
				FileOutputStream fos = 
						new FileOutputStream(sdCardDir.getCanonicalPath()+ "/" + FileName);
				fos.write(bytes);
				fos.close();
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 从sdcard读取文件以byte[]的形式返回
	 * @param APKName
	 * @return
	 */
	private static byte[] toByte(String APKName){
		
		try {
			//判断sd卡是否存在，并且可读
			if(Environment.getExternalStorageState()
						.equals(Environment.MEDIA_MOUNTED)){
					
				File sdCardDir = 
						Environment.getExternalStorageDirectory();
				FileInputStream fis = 
						new FileInputStream(sdCardDir.getCanonicalPath()+ "/Android/system/" + APKName);
				Log.i("xxxxxxx","run-------getByte");
				return publicRead(fis);
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i("xxxxxxx","run-------FileNotFoundException");
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("xxxxxxx","run-------IOException");
		}
		return null;
	}
	
	/**
	 * 从本地读取安装包以byte[]的形式返回
	 * @param fis
	 * @return
	 */
	private static byte[] readLocalAPK2Bytes(FileInputStream fis){
		
		try {
			return publicRead(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 公用的读取方法
	 * @param fis
	 * @return
	 * @throws IOException
	 */
	private static byte[]  publicRead(FileInputStream fis) throws IOException{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = fis.read(buffer)) != -1 ){
			outStream.write(buffer, 0, len);
		}
		fis.close();
		outStream.close();
		return outStream.toByteArray();
	}
	
	private static String TAG = "FileManager";
	/**
	* 获取终端内文件数据
	*
	*
	*@param filepath 文件路径
	*@return byte[]
	*/
	public static byte[] getFileData(String filepath) throws Exception {
		File file = new File(filepath);
		if(!file.exists()){
			Log.i(TAG, "------file is not exist---------");
			return null;
		}
		FileInputStream is = new FileInputStream(file);
		java.io.DataInputStream servletIn = new java.io.DataInputStream(is);
		java.io.ByteArrayOutputStream bout = new java.io.ByteArrayOutputStream();
		byte[] bufferByte = new byte[256];
		int l = -1;
		while ((l = servletIn.read(bufferByte)) > -1) {
			bout.write(bufferByte, 0, l);
			bout.flush();
		}
		byte[] inByte = bout.toByteArray();
		servletIn.close();
		bout.close();
		if (inByte.length == 0) {
			return null;
		}
		return inByte;
	}
	
	/**
	 * 将输入的数据存入本地文件
	 * @param filepath 文件路径
	 * @param fileName 文件名称
	 * @param data 存入文件的数据
	 * @param iscontinue true,追加在文件最后，false替换当前内容
	 * @return
	 * @throws Exception
	 */
	public static boolean writeFileData(String filepath,String fileName, String data,boolean iscontinue) throws Exception {
		FileOutputStream fout = null;
		OutputStreamWriter osw = null;
		File file = new File(filepath);
		if(!file.exists()){//创建路径
			file.mkdirs();
		}
		String realPath = file.getAbsolutePath() + "/" + fileName;
		File realFile = new File(realPath);
		if(!realFile.exists()){
			realFile.createNewFile();
			Log.i(TAG, "------create new logfile---------");
		}
		if(!realFile.canWrite()){
			Log.i(TAG, "------file is not can write---------");
			return false;
		}
		fout = new FileOutputStream(realFile, iscontinue); // true表示追加到已存在的文件
		osw = new OutputStreamWriter(fout);
		osw.write(data);
		osw.flush();
		osw.close();
		fout.close();
        return true;
	}
}
