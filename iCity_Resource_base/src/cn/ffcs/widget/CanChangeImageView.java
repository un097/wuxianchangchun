package cn.ffcs.widget;

import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.BitmapUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CanChangeImageView extends ImageView {
	private int screenWidth;

	public CanChangeImageView(Context context) {
		super(context);
		init();
	}

	public CanChangeImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void init() {
		screenWidth = AppHelper.getScreenWidth(getContext());
	}

	public void setWidth(int width){
		this.screenWidth = width;
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		int width = getWidth();
		if (width == 0) {
			width = screenWidth;
		}
		if(bm!=null){
			bm = BitmapUtil.zoom(bm, width);
		}
		super.setImageBitmap(bm);
	}
	
}
