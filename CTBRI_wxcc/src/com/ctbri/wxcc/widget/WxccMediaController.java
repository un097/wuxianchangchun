package com.ctbri.wxcc.widget;

import tv.danmaku.ijk.media.widget.MediaController;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.ctbri.wxcc.R;

public class WxccMediaController extends MediaController {

	private Context mContext;
	public WxccMediaController(Context context) {
		super(context);
		this.mContext = context;
	}

	public WxccMediaController(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
	}

	@Override
	protected View makeControllerView() {
		return ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.widget_mediaplayer_controller, this);
	}
}
