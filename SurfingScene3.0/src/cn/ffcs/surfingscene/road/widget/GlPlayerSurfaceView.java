package cn.ffcs.surfingscene.road.widget;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GlPlayerSurfaceView extends GLSurfaceView {

	public GlPlayerSurfaceView(Context context) {
		super(context);
	}

	public GlPlayerSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDetachedFromWindow() {
		try {
			super.onDetachedFromWindow();
		} catch (Exception e) {
		}
	}
}
