package cn.ffcs.changchuntv.activity.home.weather;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.ffcs.changchun_base.activity.BaseFragment;
import cn.ffcs.changchuntv.R;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.home.bo.WeatherBo;
import cn.ffcs.wisdom.city.home.datamgr.WeatherMgr;
import cn.ffcs.wisdom.city.home.entity.WeatherEntity;
import cn.ffcs.wisdom.city.home.entity.WeatherEntity.WeatherData;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

public class WeatherFragment extends BaseFragment {

	private ImageView weatherIcon;// 天气图标
	private TextView weather;// 天气情况
	private TextView temperature;// 温度
	private View aqi_color;
	private TextView aqi;// 污染描述
	private CityImageLoader loader;
	private Typeface typeface;
	private String font = "font/helveticathn.ttf";// 温度字体
	private View loadview;
	private TextView loadinfo;

	private int[] aqiColor = new int[] { 0xff01cc34, 0xffffd553, 0xffffa509,
			0xffff0000, 0xff8e0c4f, 0xff6b2d03 };

	RelativeLayout weatherview;

	@Override
	protected void init(Bundle savedInstanceState) {
		loader = new CityImageLoader(mContext);
		typeface = Typeface.createFromAsset(getActivity().getAssets(), font);
		super.init(savedInstanceState);
	}

	@Override
	public int getLayoutId() {
		return R.layout.fragment_weather;
	}

	@Override
	protected void initview(View view) {
		weatherview = (RelativeLayout) view.findViewById(R.id.weatherview);
		weatherIcon = (ImageView) view.findViewById(R.id.weather_icon);
		weather = (TextView) view.findViewById(R.id.weather);
		temperature = (TextView) view.findViewById(R.id.temperature);
		temperature.setTypeface(typeface);
		aqi_color = view.findViewById(R.id.aqi_color);
		aqi = (TextView) view.findViewById(R.id.aqi);

		loadview = view.findViewById(R.id.weather_loadview);
		loadinfo = (TextView) view.findViewById(R.id.weather_loadinfo);
		weatherview.setOnClickListener(new WeatherviewClick());
		refreshWeather();
		super.initview(view);
	}

	class WeatherviewClick implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
//			android.util.Log.e("sb", "click");
		}
	}
	
	
	/**
	 * 刷新天气
	 */
	public void refreshWeather() {
		String cityCode = MenuMgr.getInstance().getCityCode(mContext);
		WeatherEntity entity = WeatherMgr.getInstance().getWeatherEntity(
				cityCode);
		if (entity == null) {
			showLoading();
			WeatherBo.getInstance(mContext).acquireWeather(
					new WeatherCallBack());
		} else {
			showWeather(entity);
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
					WeatherEntity weatherEntity = (WeatherEntity) response
							.getObj();
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
	 * 显示加载View
	 */
	private void showLoading() {
		loadinfo.setText("天气加载中");
		loadview.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示失败View
	 */
	private void showError() {
		loadinfo.setText("天气加载失败");
		loadview.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示天气
	 * 
	 * @param weatherEntity
	 */
	private void showWeather(WeatherEntity weatherEntity) {
		loadview.setVisibility(View.GONE);
		if (weatherEntity != null) {
			WeatherData data = weatherEntity.getData();
			if (data != null) {
				String weatcherIconUrl = data.getWd_icon();
				loader.setDefaultFailImage(R.drawable.defalut_weather);
				loader.loadUrl(weatherIcon, weatcherIconUrl);
				weather.setText(data.getWd());
				String temperatureString = "";
				if (!StringUtil.isEmpty(data.getLowt())) {
					temperatureString = data.getLowt() + "°/";
				} else {
					temperatureString = "~°/";
				}
				if (!StringUtil.isEmpty(data.getHigt())) {
					temperatureString += data.getHigt() + "°";
				} else {
					temperatureString += "~°";
				}
				temperature.setText(temperatureString);
				changeAqi(data.getAqiLevel(), data.getAqi());
			} else {
				showError();
			}
		} else {
			showError();
		}
	}

	/**
	 * 根据污染等级设置aqi背景颜色
	 * 
	 * @param level
	 * @param des
	 */
	private void changeAqi(int level, String des) {
		if (level > 0 && level <= aqiColor.length) {
			aqi_color.setBackgroundColor(aqiColor[level - 1]);
		} else {
			aqi_color.setBackgroundColor(0xffeeeeee);
		}
		aqi.setText(des);
	}

	@Override
	public void onDestroy() {
		WeatherBo.getInstance(mContext).cancelQueryWeather();
		super.onDestroy();
	}

}
