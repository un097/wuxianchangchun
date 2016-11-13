package cn.ffcs.wisdom.city.simico.ui.progressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class ProgressSpinnerView extends View {

	private boolean _loading;
	private ProgressSpinner _progress;

	public ProgressSpinnerView(Context context) {
		super(context);
		_progress = new ProgressSpinner();
		_loading = true;
	}

	public ProgressSpinnerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		_progress = new ProgressSpinner();
		_loading = true;
	}

	public ProgressSpinnerView(Context context, AttributeSet attrs, int i) {
		super(context, attrs, i);
		_progress = new ProgressSpinner();
		_loading = true;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (_loading && getVisibility() == 0)
			_progress.drawScaled(this, canvas, getMeasuredWidth(),
					getMeasuredHeight());
		super.onDraw(canvas);
	}

	public void setLoading(boolean flag) {
		_loading = flag;
		invalidate();
	}
}
