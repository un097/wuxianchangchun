package cn.ffcs.wisdom.city.simico.kit.application;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.TrafficApplication;
import cn.ffcs.wisdom.city.R;
import cn.ffcs.wisdom.city.simico.kit.log.TLog;

public class BaseApplication extends TrafficApplication {
	private static String PREF_NAME = "changchuntv_simico.pref";
	static Context _context;
	static Resources _resource;
	private static String lastToast = "";
	private static long lastToastTime;

	private static boolean sIsAtLeastGB;

	static {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			sIsAtLeastGB = true;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		_context = getApplicationContext();
		_resource = _context.getResources();
		init();
	}

	protected void init() {
//		JPushInterface.setDebugMode(true);
//	    JPushInterface.init(this);
//	    JPushInterface.resumePush(BaseApplication.this);	
	}

	public static synchronized BaseApplication context() {
		return (BaseApplication) _context;
	}

	public static Resources resources() {
		return _resource;
	}

	public static SharedPreferences getPersistPreferences() {
		return context().getSharedPreferences(PREF_NAME, 0);
	}

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	public static void apply(SharedPreferences.Editor editor) {
		if (sIsAtLeastGB) {
			editor.apply();
		} else {
			editor.commit();
		}
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static SharedPreferences getPreferences() {
		SharedPreferences pre = context().getSharedPreferences(PREF_NAME,
				Context.MODE_MULTI_PROCESS);
		return pre;
	}

	public static int[] getDisplaySize() {
		return new int[] { getPreferences().getInt("screen_width", 480),
				getPreferences().getInt("screen_height", 854) };
	}

	public static void saveDisplaySize(Activity activity) {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		SharedPreferences.Editor editor = getPreferences().edit();
		editor.putInt("screen_width", displaymetrics.widthPixels);
		editor.putInt("screen_height", displaymetrics.heightPixels);
		editor.putFloat("density", displaymetrics.density);
		editor.commit();
		TLog.log("", "分辨率:" + displaymetrics.widthPixels + "x"
				+ displaymetrics.heightPixels + " 密度:" + displaymetrics.density
				+ " " + displaymetrics.densityDpi);
	}

	public static String string(int id) {
		return _resource.getString(id);
	}

	public static String string(int id, Object... args) {
		return _resource.getString(id, args);
	}

	public static void showToast(int message) {
		showToast(message, Toast.LENGTH_LONG, 0);
	}

	public static void showToast(String message) {
		showToast(message, Toast.LENGTH_LONG, 0, 17);
	}

	public static void showToast(int message, int icon) {
		showToast(message, Toast.LENGTH_LONG, icon);
	}

	public static void showToast(String message, int icon) {
		showToast(message, Toast.LENGTH_LONG, icon, 17);
	}

	public static void showToastShort(int message) {
		showToast(message, Toast.LENGTH_SHORT, 0);
	}

	public static void showToastShort(String message) {
		showToast(message, Toast.LENGTH_SHORT, 0, 17);
	}

	public static void showToastShort(int message, Object... args) {
		showToast(message, Toast.LENGTH_SHORT, 0, 17, args);
	}

	public static void showToast(int message, int duration, int icon) {
		showToast(message, duration, icon, 17);
	}

	public static void showToast(int message, int duration, int icon,
			int gravity) {
		showToast(context().getString(message), duration, icon, gravity);
	}

	public static void showToast(int message, int duration, int icon,
			int gravity, Object... args) {
		showToast(context().getString(message, args), duration, icon, gravity);
	}

	public static void showToast(String message, int duration, int icon,
			int gravity) {
		if (message != null && !message.equalsIgnoreCase("")) {
			long time = System.currentTimeMillis();
			if (!message.equalsIgnoreCase(lastToast)
					|| Math.abs(time - lastToastTime) > 2000) {
				View view = LayoutInflater.from(context()).inflate(
						R.layout.simico_view_toast, null);
				((TextView) view.findViewById(R.id.title_tv)).setText(message);
				if (icon != 0) {
					((ImageView) view.findViewById(R.id.icon_iv))
							.setImageResource(icon);
					((ImageView) view.findViewById(R.id.icon_iv))
							.setVisibility(View.VISIBLE);
				}
				Toast toast = new Toast(context());
				toast.setView(view);
				toast.setGravity(gravity, 0, 0);
				// toast.setGravity(Gravity.TOP|Gravity.LEFT,0 ,0);
				toast.setDuration(duration);
				toast.show();
				lastToast = message;
				lastToastTime = System.currentTimeMillis();
			}
		}
	}
}
