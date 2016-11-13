package com.wookii.tools.comm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
/**
 * 操作图片的简单工具
 * @author wuchen
 * @version
 * 
 */
public class PictureUtils {
	
	
	private static final String TAG = "PictureUtils";

	private PictureUtils(){}
	
	private static PictureUtils instance= null;
	
	public synchronized static PictureUtils getInstance(){
		
		if(instance == null){
			instance =  new PictureUtils();
			
		}
		return instance;
	}
	/**
	 * 通过图片真实路径，获取缩略图
	 * 
	 * @param picPath 图片路径
	 * @param size 缩略图大小
	 * @return 如果通过 picPath没有获取到Bitmap，返回null
	 */
	public static  Bitmap getPreview(Context context, String picPath, int size) {
		
		if(picPath == null){
			return null;
		}
		
		BitmapFactory.Options options = new BitmapFactory.Options();
		// 打开只获取“边界”信息
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap bitmap = BitmapFactory.decodeFile(picPath, options); // 此时返回bm为空
		int height = options.outHeight;
		int width = options.outWidth;
		int useMin;//用相对小的一边来计算缩放比
		if(height > width){
			useMin = width;
		} else{
			useMin = height;
		}
		// 计算缩放比
		int be;
		if(size == -1){//如果传过来的size为-1.就将除数设置为被除数，那么缩放比就会为1，
			be = 1;
		} else{
			be = (int) (useMin / (float) size);
		}
		
		if (be <= 0)be = 1;//如果be<=0,就将be设置为1，原图输出。
		options.inSampleSize = be;
		// 关闭只获取“边界”信息
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(picPath, options);
		//如果没获取到，说明这个path所对应的图片已经不存在了，那么就将这条消息，从数据库中删除掉
		if(bitmap == null){
			return null;
		}
		return bitmap;
	}
	
	/**
	 * 压缩图片
	 * @param context
	 * @param picPath
	 * @param simpleSize
	 * @return
	 */
	public static Bitmap zip(Context context, String picPath, int simpleSize) {
		if(picPath == null){
			return null;
		}
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(picPath, options); // 此时返回bm为空
		options.inSampleSize = simpleSize;
		options.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(picPath, options);
		if(bitmap == null){
			return null;
		}
		return bitmap;
	}
	
	/**
	 * 将bitmap装换为输入流
	 * @param bitmap
	 * @return
	 */
	public static InputStream bitmap2Stream(Bitmap bitmap){
		
		ByteArrayOutputStream byOutS = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byOutS);
		
		ByteArrayInputStream byInS = new ByteArrayInputStream(byOutS.toByteArray());
		return byInS;
	}
	/**
	 * 美化图片-圆角
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		if(bitmap == null) return null;
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}
	
	/**
	 * 获取颜色
	 * @param context
	 * @param source
	 * @return
	 */
	public static ColorStateList getColors(Context context, int source){
		Resources resource = (Resources) context.getResources();
		return (ColorStateList) resource.getColorStateList(source);
	}
}
