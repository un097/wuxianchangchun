package cn.ffcs.wisdom.city.simico.ui.progressbar;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.kit.application.BaseApplication;



public class ProgressSpinner {

	private static final int UPDATE_MS = 33;
	private static Paint _blankPaint;
	private static Bitmap _loading;
	private Matrix loadingMatrix;
	private int loadingX;
	private int loadingY;
	private float scale;
	
	public ProgressSpinner() {
		loadingMatrix = null;
		if (_loading == null)
			_loading = ((BitmapDrawable) BaseApplication.context().getResources()
					.getDrawable(R.drawable.simico_ic_loading)).getBitmap();
		if (_blankPaint == null) {
			_blankPaint = new Paint();
			_blankPaint.setAntiAlias(true);
			_blankPaint.setFilterBitmap(true);
		}
	}

	private void draw(View view, Canvas canvas, int i, int j, int k, int l,
			int i1) {
		int j1 = Math.max(i, 2 * loadingX);
		int k1 = Math.max(j, 2 * loadingY);
		if (k <= 0)
			k = _loading.getWidth();
		if (l <= 0)
			_loading.getHeight();
		if (loadingMatrix == null) {
			loadingMatrix = new Matrix();
			loadingX = _loading.getWidth() / 2;
			loadingY = _loading.getHeight() / 2;
			scale = (float) k / (float) _loading.getWidth();
		}
		loadingMatrix.postRotate(12F, loadingX, loadingY);
		canvas.save();
		canvas.scale(scale, scale);
		canvas.translate(j1 / 2 - loadingX, k1 / 2 - loadingY);
		canvas.drawBitmap(_loading, loadingMatrix, _blankPaint);
		canvas.restore();
		if (view != null)
			view.postInvalidateDelayed(i1);
	}

	public final void drawCentered(View view, Canvas canvas, int i, int j) {
		drawCentered(view, canvas, i, j, UPDATE_MS);
	}

	public final void drawCentered(View view, Canvas canvas, int i, int j, int k) {
		draw(view, canvas, i, j, 0, 0, k);
	}

	public final void drawScaled(View view, Canvas canvas, int i, int j) {
		draw(view, canvas, 0, 0, i, j, UPDATE_MS);
	}

	public final Bitmap getBitmap() {
		return _loading;
	}
}
