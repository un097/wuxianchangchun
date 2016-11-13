package cn.ffcs.wisdom.city.home.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:     自动移动背景              </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-4-2           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MoveSurfaceView extends SurfaceView implements Callback, Runnable {
	private SurfaceHolder surfaceHolder;
	private boolean flag = false;// 线程标识
	private Bitmap bitmap_bg;// 背景图

	private float mSurfaceWidth;// 屏幕宽
	private float mSurfaceHeight;// 屏幕高

	private int mBitposX;// 图片的位置

	private Canvas mCanvas;

	private Thread thread;

	private Paint mPaint;

	// 背景移动状态
	private enum State {
		LEFT, RINGHT
	}

	// 默认为向左
	private State state = State.LEFT;

	private final int BITMAP_STEP = 1;// 背景画布移动步伐.

	public MoveSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSurfaceWidth = AppHelper.getScreenWidth(context);
		mSurfaceHeight = AppHelper.getScreenHeight(context);
		mPaint = new Paint();
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		initBitmap(context);
	}

	/***
	 * 进行绘制.
	 */
	public void draw() {
		try {
			drawBG();
			updateBG();
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
		} finally {
			if (mCanvas != null) {
				surfaceHolder.unlockCanvasAndPost(mCanvas);
			}
		}
	}

	/***
	 * 更新背景.
	 */
	public void updateBG() {
		/** 图片滚动效果 **/
		switch (state) {
		case LEFT:
			mBitposX -= BITMAP_STEP;// 画布左移
			break;
		case RINGHT:
			mBitposX += BITMAP_STEP;// 画布右移
			break;
		default:
			break;
		}
		if (mBitposX <= -mSurfaceWidth / 2) {
			state = State.RINGHT;
		}
		if (mBitposX >= 0) {
			state = State.LEFT;
		}
	}

	/***
	 * 绘制背景
	 */
	public void drawBG() {
		mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);// 清屏幕.
		mCanvas.drawBitmap(bitmap_bg, mBitposX, 0, mPaint);// 绘制当前屏幕背景
	}

	@Override
	public void run() {
		while (flag) {
			mCanvas = surfaceHolder.lockCanvas();
			if (mCanvas != null) {
				draw();
			}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				Log.e(e.getMessage(), e);
			}
		}
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.i("surfaceCreated");
		flag = true;
		thread = new Thread(this);
		thread.start();
	}

	/**
	 * 初始化Bitmap
	 */
	public void initBitmap(Context context) {
		try {
			String bgPath = SharedPreferencesUtil.getValue(context, Key.K_HOME_BG);
			if (!StringUtil.isEmpty(bgPath)) {
				Bitmap bitmap = BitmapUtil.readBitMapByLowMemory(bgPath);
				changeBitmap(bitmap);
			} else {
				changeBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.home_background));
			}
		} catch (Exception e) {
			changeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.home_background));
			Log.e("Exception: " + e.getMessage());
		}
	}

	/**
	 * 改变bitmap大小
	 * @param bitmap
	 */
	private void changeBitmap(Bitmap bitmap) {
		bitmap_bg = BitmapUtil.changeBitmap(bitmap, mSurfaceWidth * 3 / 2, mSurfaceHeight);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		Log.i("surfaceChanged: " + format + "," + width + "," + height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		Log.i("surfaceDestroyed");
		flag = false;
	}
}
