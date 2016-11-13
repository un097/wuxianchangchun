package cn.ffcs.wisdom.city.simico.activity.home.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.city.R;

public class TitleBar extends RelativeLayout {

	private TextView weather, location;

	private MenuDelegate mDelegate;

	public interface MenuDelegate {
		public void onMenuLeft();

		public void onRefreshClick();

		public void onLocationClick();
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
		inflate(context, R.layout.simico_titlebar_home, this);

//		weather = (TextView) findViewById(R.id.tv_weather);
		location = (TextView) findViewById(R.id.tv_location);
		findViewById(R.id.iv_avatar).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDelegate != null) {
					mDelegate.onMenuLeft();
				}
			}
		});
		findViewById(R.id.ly_location).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDelegate != null) {
					mDelegate.onLocationClick();
				}
			}
		});
		findViewById(R.id.tv_name).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mDelegate != null) {
					mDelegate.onRefreshClick();
				}
			}
		});
	}

	public void setMenuDelegate(MenuDelegate delegate) {
		mDelegate = delegate;
	}

	public void setLocation(String loc) {
		location.setText(loc);
	}

	public void setWeather(String mWeather) {
		weather.setText(mWeather);
	}
}
