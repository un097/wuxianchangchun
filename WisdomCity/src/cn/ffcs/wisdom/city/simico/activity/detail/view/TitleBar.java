package cn.ffcs.wisdom.city.simico.activity.detail.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.city.R;

public class TitleBar extends RelativeLayout {

	private MenuDelegate mListener;
	private Button mBtnBack;
	private TextView mTvtitle;

	public interface MenuDelegate {
		public void onBackClick(View v);
		public void onUrlClick(View v);
	}

	public TitleBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public TitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TitleBar(Context context) {
		super(context);
		init(context);
	}

	private void init(Context context) {
		inflate(context, R.layout.simico_titlebar_detail, this);
		mBtnBack = (Button) findViewById(R.id.btn_back);
		mBtnBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onBackClick(v);
				}
			}
		});
		mTvtitle = (TextView) findViewById(R.id.tv_title);
		mTvtitle.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mListener != null) {
					mListener.onUrlClick(v);
				}
			}
		});
	}

	public void setDelegate(MenuDelegate lis) {
		mListener = lis;
	}

	public void setTitle(String title) {
		mTvtitle.setText(title);
	}

	public void setTitleVisiable(int visibility) {
		mTvtitle.setVisibility(visibility);
	}
}
