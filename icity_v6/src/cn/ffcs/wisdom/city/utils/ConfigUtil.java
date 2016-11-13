package cn.ffcs.wisdom.city.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.entity.ConfigParams;
import cn.ffcs.wisdom.city.entity.ConfigParamsEntity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpRequest;
import cn.ffcs.wisdom.tools.FileUtils;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

public class ConfigUtil {

	private static final String CONFIG_FILE_NAME = "config_params";
	private static ConfigParams mConfigParams;

	/**
	 * 请求服务器，获取配置数据
	 */
	public static void getConfigParamsAsync(final Context context) {
		new Thread() {
			public void run() {
				getConfigParams(context);
			}
		}.start();
	}

	public static void getConfigParams(final Context context) {
		HttpRequest request = new HttpRequest(BaseResp.class);
		try {
			String cityCode = MenuUtil.getCityCode(context);
			String code = MenuUtil.getProvinceCode(context, cityCode);
			request.addParameter("province", code);
			request.addParameter("client_type",
					context.getResources().getString(R.string.version_name_update));
			BaseResp resp = request.execute(Config.UrlConfig.URL_GET_CONFIG_PARAMS);
			if (resp.isSuccess()) {
				String result = resp.getHttpResult();
				ConfigParamsEntity entity = JsonUtil.toObject(result, ConfigParamsEntity.class);
				saveCofigParams(context, entity);
			}
		} catch (Exception e) {
			Log.e(e.getMessage(), e);
			// e.printStackTrace();
		} finally {
			request.release();
		}
	}

	/**
	 * 保存配置信息
	 * @param context
	 * @param entity
	 */
	public static void saveCofigParams(Context context, ConfigParamsEntity entity) {
		deleteConfigParams(context);
		FileUtils.write(context, CONFIG_FILE_NAME, entity);

		// 刷新
		if (entity != null) {
			List<ConfigParams> params = entity.getCodelist();
			if (params.size() > 0) {
				mConfigParams = params.get(0);
			}
		}
	}

	/**
	 * 获取配置信息
	 * @param context
	 * @param filename
	 * @return
	 */
	private static ConfigParams readConfigParams(Context context, String filename) {
		ConfigParamsEntity entity = (ConfigParamsEntity) FileUtils.read(context, filename);
		if (entity != null) {
			List<ConfigParams> params = entity.getCodelist();
			if (params.size() > 0) {
				return params.get(0);
			}
		}
		return null;
	}

	/**
	 * 获取配置文件
	 * @param context
	 * @return
	 */
	public static ConfigParams readConfigParams(Context context) {
		if (mConfigParams == null) {
			mConfigParams = readConfigParams(context, CONFIG_FILE_NAME);
		}
		if (mConfigParams == null) {
			return new ConfigParams();
		}
		return mConfigParams;
	}

	public static boolean deleteConfigParams(Context context) {
		return FileUtils.deleteFile(context, CONFIG_FILE_NAME);
	}

	/**
	 * 分解菜单栏目Item中的map对象，作为栏目参数
	 * @param params
	 * @return
	 */
	public static Map<String, String> params2Map(String params) {
		if (StringUtil.isEmpty(params)) {
			return Collections.emptyMap();
		}

		Map<String, String> paramsMap = new HashMap<String, String>();
		String[] paramsArr = params.split(",");
		for (int i = 0; i < paramsArr.length; i++) {
			String[] paramArr = paramsArr[i].split(":");
			if (paramArr.length == 2) {
				paramsMap.put(paramArr[0], paramArr[1]);
			}
		}

		return paramsMap;
	}

	/**
	 * 读取友盟渠道编号
	 * @param activity
	 * @return
	 */
	public static String readChannelName(Context context, String channelKey) {
		ApplicationInfo info;
		String channelName = "";
		try {
			info = context.getPackageManager().getApplicationInfo(context.getPackageName(),
					PackageManager.GET_META_DATA);
			channelName = info.metaData.getString(channelKey);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return channelName;
	}

	public static String readVersionName(Context context) {
		String versionName = "";
		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			versionName = pinfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	public static String readVersionCode(Context context) {
		String versionName = "";
		try {
			PackageInfo pinfo = context.getPackageManager().getPackageInfo(
					context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			versionName = pinfo.versionCode + "";
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}
}
