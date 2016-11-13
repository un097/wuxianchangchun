package cn.ffcs.wisdom.http.entity;

import android.graphics.Bitmap;

/**
 * <p>Title: WisdomCity</p>
 * <p>Description: 图片下载对象
 *  用于sd卡不存在时</p>
 * <p>Author: tianya</p>
 * <p>CreateTime: 2012-4-23 下午04:41:57 </p>
 * <p>CopyRight: 4.0.2 </p>
 */
public class BitmapProgress extends Progress {

	public BitmapProgress() {
	}

	public BitmapProgress(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	private Bitmap bitmap;
	private String urlStr;//此bitmap下载地址，用于回调验证下载完整标记--add by liaodl

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getUrlStr() {
		return urlStr;
	}

	public void setUrlStr(String urlStr) {
		this.urlStr = urlStr;
	}

}
