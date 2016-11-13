package cn.ffcs.wisdom.city.simico.activity.subscribe.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.IBinder;
import android.view.View;
import android.view.WindowManager;

public class DragView extends View {

	private Bitmap dragBitmap;
	private Paint mPaint;
	private int initX;
	private int initY;
	private WindowManager.LayoutParams mParams;
	private WindowManager mWindowMgr;

	public DragView(Context context, Bitmap bitmap, int x, int y, int bitmapX, int bitmapY,
			int width, int height) {
		super(context);
		mWindowMgr = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		Matrix matrix = new Matrix();
		float sx = (20F + width) / width;
		matrix.setScale(sx, sx);
		dragBitmap = Bitmap.createBitmap(bitmap, bitmapX, bitmapY, width, height, matrix, true);
		initX = x + 10;
		initY = y + 10;
		mPaint = new Paint();
		mPaint.setAlpha(180);
	}

	public void remove() {
		mWindowMgr.removeView(this);
	}

	public void moveTo(int x, int y) {
		mParams.x = x - initX;
		mParams.y = y - initY;
		mWindowMgr.updateViewLayout(this, mParams);
	}

	public void a(IBinder ibinder, int x, int y) {
		WindowManager.LayoutParams params = new WindowManager.LayoutParams(-2,
				-2, x - initX, y - initY, 1002, 0x20300, -3);
		params.gravity = 51;
		params.token = ibinder;
		params.setTitle("DragView");
		mParams = params;
		mWindowMgr.addView(this, params);
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		dragBitmap.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(dragBitmap, 0.0F, 0.0F, mPaint);
	}

	@Override
	protected void onMeasure(int i, int j) {
		setMeasuredDimension(dragBitmap.getWidth(), dragBitmap.getHeight());
	}

	public void setPaint(Paint paint) {
		mPaint = paint;
		invalidate();
	}
}
