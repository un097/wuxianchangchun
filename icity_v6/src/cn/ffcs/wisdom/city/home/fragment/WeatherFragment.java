package cn.ffcs.wisdom.city.home.fragment;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.base.BaseFragment;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.home.bo.WeatherBo;
import cn.ffcs.wisdom.city.home.datamgr.WeatherMgr;
import cn.ffcs.wisdom.city.home.entity.WeatherEntity;
import cn.ffcs.wisdom.city.home.entity.WeatherEntity.WeatherData;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:    天气Fragment        </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-11-1           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class WeatherFragment extends BaseFragment {

	private TextView temperature;// 温度
	private TextView weather;// 天气情况
	private TextView weatherLoadBar;// 天气加载
	private TextView yesterdayInfo;// 与昨日对比信息
	private ImageView weatherIcon;// 天气图标
	private String weatherUrl;// 天气wap地址
	private CityImageLoader loader;
	private View weatherLayout;// 整个天气布局
	private View weatherReload;// 天气重新加载
	private View weatherInfoLayout;// 天气信息布局
	private View aqiLayout;// 空气质量布局
	private TextView aqi;// 污染描述
	private ImageView yesterdayIcon;// 温差图标
	private Typeface typeface;
	private String font = "font/helveticathn.ttf";// 温度字体
	private OnWeatherListener onWeatherListener = new OnWeatherListener();

	private int[] aqiColor = new int[] { 0xff01cc34, 0xffffd553, 0xffffa509, 0xffff0000, 0xff8e0c4f, 0xff6b2d03 };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loader = new CityImageLoader(mContext);
		typeface = Typeface.createFromAsset(getActivity().getAssets(), font);
	}

	@Override
	public void initComponents(View view) {
		weatherLayout = view.findViewById(R.id.weather_layout);
		weatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
		temperature = (TextView) view.findViewById(R.id.temperature);
		temperature.setTypeface(typeface);
		weather = (TextView) view.findViewById(R.id.weather);
		weatherLoadBar = (TextView) view.findViewById(R.id.loading_text);
		yesterdayInfo = (TextView) view.findViewById(R.id.yesterday_info);
		weatherReload = view.findViewById(R.id.reload);
		weatherInfoLayout = view.findViewById(R.id.weather_condition);
		aqi = (TextView) view.findViewById(R.id.aqi);
		aqiLayout = view.findViewById(R.id.aqi_layout);
		yesterdayIcon=(ImageView) view.findViewById(R.id.yesterday_info_icon);
	}

	@Override
	public void initData() {
		refreshWeather();
	}

	/**
	 * 刷新天气
	 */
	public void refreshWeather() {
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		WeatherEntity entity = WeatherMgr.getInstance().getWeatherEntity(cityCode);
		if (entity == null) {
			showLoading();
			WeatherBo.getInstance(mContext).acquireWeather(new WeatherCallBack());
		} else {
			showWeather(entity);
		}
	}

	/**
	 * 天气点击
	 */
	class OnWeatherListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (!StringUtil.isEmpty(weatherUrl)) {
				String mTitle = "天气";
				String cityName = MenuMgr.getInstance().getCityName(mContext);
				if (!StringUtil.isEmpty(cityName)) {
					mTitle = cityName + mTitle;
				}
				Intent intent = new Intent(getActivity(), BrowserActivity.class);
				intent.putExtra(Key.U_BROWSER_URL, weatherUrl);
				intent.putExtra(Key.U_BROWSER_TITLE, mTitle);
				intent.putExtra(Key.K_RETURN_TITLE, mContext.getString(R.string.home_name));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getActivity().startActivity(intent);
			}
		}
	}

	/**
	 * 获取天气回调
	 */
	class WeatherCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			try {
				if (response.isSuccess()) {
					showWeatherInfoView();
					weatherLayout.setOnClickListener(onWeatherListener);
					WeatherEntity weatherEntity = (WeatherEntity) response.getObj();
					showWeather(weatherEntity);
				} else {
					showError();
				}
			} catch (Exception e) {
				showError();
				Log.e(e.getMessage(), e);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
			showError();
		}
	}

	/**
	 * 显示天气信息View
	 */
	private void showWeatherInfoView() {
		weatherLoadBar.setVisibility(View.GONE);
		weatherReload.setVisibility(View.GONE);
		weatherInfoLayout.setVisibility(View.VISIBLE);
		aqiLayout.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示加载View
	 */
	private void showLoading() {
		weatherLoadBar.setVisibility(View.VISIBLE);
		weatherReload.setVisibility(View.GONE);
		weatherInfoLayout.setVisibility(View.GONE);
	}

	/**
	 * 显示失败View
	 */
	private void showError() {
		weatherLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showLoading();
				WeatherBo.getInstance(mContext).acquireWeather(new WeatherCallBack());
			}
		});
		weatherLoadBar.setVisibility(View.GONE);
		weatherReload.setVisibility(View.VISIBLE);
		weatherInfoLayout.setVisibility(View.GONE);
	}

	/**
	 * 显示天气
	 * @param weatherEntity
	 */
	private void showWeather(WeatherEntity weatherEntity) {
		if (weatherEntity != null) {
			WeatherData data = weatherEntity.getData();
			if (data != null) {
				String weatcherIconUrl = data.getWd_icon();
				loader.setDefaultFailImage(R.drawable.defalut_weather);
				loader.loadUrl(weatherIcon, weatcherIconUrl);
				if (!StringUtil.isEmpty(data.getLowt()) && !StringUtil.isEmpty(data.getHigt())) {
					temperature.setText(data.getLowt() + "°/" + data.getHigt() + "°");
					changeTextViewImage(data.getTemperatureContrast(), data.getHightOrLow());
					changeAqi(data.getAqiLevel(), data.getAqi());
					weather.setText(data.getWd());
					weatherUrl = data.getUrl();
					return;
				}
			}
		}
		showError();
	}

	/**
	 * 根据污染等级设置aqi背景颜色
	 * @param level
	 * @param des
	 */
	private void changeAqi(int level, String des) {
		switch (level) {
		case 0:
			aqiLayout.setVisibility(View.GONE);
			break;
		default:
			if (level > 0 && level <= aqiColor.length) {
				aqi.setBackgroundColor(aqiColor[level - 1]);
			} else {
				aqiLayout.setVisibility(View.GONE);
			}
			break;
		}
		aqi.setText(des);
	}

	/**
	 * 改变天气的文字和旁边的图标
	 */
	private void changeTextViewImage(int temperature, int heightOrLow) {
		switch (heightOrLow) {
		case 1:
			yesterdayIcon.setImageResource(R.drawable.weather_down_icon);
			yesterdayInfo.setText(getString(R.string.home_weather_yesterday, "低", temperature));
			break;
		case 2:
			yesterdayIcon.setImageResource(R.drawable.weather_up_icon);
			yesterdayInfo.setText(getString(R.string.home_weather_yesterday, "高", temperature));
			break;
		case 3:
			yesterdayIcon.setImageDrawable(null);
			yesterdayInfo.setText(getString(R.string.home_weather_yesterday_no_change));
			break;
		default:
			yesterdayIcon.setImageDrawable(null);
			break;
		}
	}

	@Override
	public int getMainContentViewId() {
		return R.layout.fragment_weather;
	}

	@Override
	public void onDestroy() {
		WeatherBo.getInstance(mContext).cancelQueryWeather();
		super.onDestroy();
	}
}
