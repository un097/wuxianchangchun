package com.wookii.sendpics;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

public class Bimp {
	public static final int MAX_SIZE = 3;
	public static int max = 0;
	public static boolean act_bool = true;
	public static List<Bitmap> bmp = new ArrayList<Bitmap>();

	// 图片sd地址 上传服务器时把图片调用下面方法压缩后 保存到临时文件夹 图片压缩后小于100KB，失真度不明显
	public static List<String> drr = new ArrayList<String>();
	/**
	 * 压缩图片，并还原图片至拍摄时的角度。
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public static Bitmap revitionImageSize(String path) throws IOException {
		//获取图片的拍摄角度，在压缩完成后旋转。如果直接旋转原图，太耗时。
		int degress = getPictureDegress(path);
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(
				new File(path)));
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		int i = 0;
		Bitmap bitmap = null;
		while (true) {
			if ((options.outWidth >> i <= 1000)
					&& (options.outHeight >> i <= 1000)) {
				in = new BufferedInputStream(
						new FileInputStream(new File(path)));
				options.inSampleSize = (int) Math.pow(2.0D, i);
				options.inJustDecodeBounds = false;
				bitmap = BitmapFactory.decodeStream(in, null, options);
				break;
			}
			i += 1;
		}
		//如果拍摄角度能够获取到，则旋转压缩完成后的图片。
		if(degress!=-1)
			return resetBitmapDegress(bitmap, degress); 
		
		return bitmap;
	}
	/**
	 * 获取图片的角度
	 * @param path
	 * @return
	 */
	public static int getPictureDegress(String path) {
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int degress = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);

			switch (degress) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degress = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degress = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degress = 270;
				break;
			default:
				degress = -1;
				break;

			}
			return degress;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public static Bitmap resetBitmapDegress(Bitmap bitmap, int angle) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}
}
