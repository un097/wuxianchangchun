package cn.ffcs.wisdom.city.splashs;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.tsz.afinal.http.AjaxCallBack;
import android.app.Activity;
import cn.ffcs.wisdom.base.BaseBo;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.splashs.SplashsEntity.Date.Url;
import cn.ffcs.wisdom.city.utils.CityDownloader;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.MD5;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>
 * Title: 特色地市闪屏轮播Bo
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * @author: liaodl
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * <p>
 * Company: ffcs Co., Ltd.
 * </p>
 * <p>
 * Create Time: 2013-5-19
 * </p>
 * <p>
 * @author:
 * </p>
 * <p>
 * Update Time:
 * </p>
 * <p>
 * Updater:
 * </p>
 * <p>
 * Update Comments:
 * </p>
 */
public class SplashBo extends BaseBo {

	public static final String MENU_COMPLETE_ACTION = "cn.ffcs.wisdom.city.MENU_COMPLETE_ACTION";
	public static final String KEY_SPLASH_TIME = "key_splash_time"; // 闪屏播放时间

	private AjaxCallBack<File> listener;

	public SplashBo(Activity activity) {
		super(activity);
		listener = new SplashListener();
	}

	public SplashBo(Activity act, HttpCallBack<BaseResp> icall) {
		super(act, icall);
		listener = new SplashListener();
	}

	/**
	 * 请求闪屏图片地址
	 */
	public void reqSplashUrlTask() {
		Map<String, String> params = new HashMap<String, String>();
		int width = AppHelper.getScreenWidth(mActivity);
		int height = AppHelper.getScreenHeight(mActivity);
		String screenResolution = width + "*" + height;
		String cityCode = MenuMgr.getInstance().getCityCode(mActivity);
		params.put("screenResolution", screenResolution);
		params.put("city_code", cityCode);
		params.put("os_type", AppHelper.getOSType());
		String url = Config.UrlConfig.URL_SPLASHS;
		CommonTask task = CommonTask.newInstance(new SplashsUrlCallBack(),
				mActivity, SplashsEntity.class);
		task.setParams(params, url);
		task.execute();
	}

	class SplashsUrlCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				SplashsEntity entity = (SplashsEntity) response.getObj();
				if (entity != null) {
					downPicByUrl(entity);
				}

			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}

	}

	public int getPlayTime() {
		return SharedPreferencesUtil.getInteger(mActivity, KEY_SPLASH_TIME,
				2000);
	}

	/**
	 * 通过URL下载闪屏图片
	 * 
	 * @param entity
	 */
	public void downPicByUrl(SplashsEntity entity) {
		StringBuffer urlsBuf = new StringBuffer();
		List<Url> urls = null;
		try {
			urls = entity.getDate().getSplash();
			if (urls == null || urls.size() <= 0) {
				return;
			}
		} catch (Exception e) {
			// TODO: handle exception
//			android.util.Log.e("sb", e.getMessage());
			return;
		}
		

		try {
			// 单位秒
			if (!StringUtil.isEmpty(entity.getDate().getPlayTime())) {
				try {
					int playTime = Integer.parseInt(entity.getDate()
							.getPlayTime());
					if (playTime > 0) {
						// 存储，转化为毫秒
						SharedPreferencesUtil.setInteger(mActivity,
								KEY_SPLASH_TIME, playTime * 1000);
					}
				} catch (NumberFormatException e) {
					Log.e("多图闪屏播放时间异常");
				}
			}
		} catch (NullPointerException e) {
			Log.e("多图闪屏数据异常");
		}

		CityDownloader loader = new CityDownloader(mActivity);
		loader.setCategory(CityDownloader.CATEGORY_SPLASHS);
		loader.setResume(false);
		for (Url url : urls) {
			String urlStr = patternUrl(url.getUrl());
			Log.i("--闪屏图片url:--" + urlStr);
			if (StringUtil.isEmpty(urlStr)) {
				return;
			}
			urlsBuf.append(urlStr).append(",");
			loader.down(urlStr, listener);
		}
		int location = urlsBuf.length() - 1;
		cacheSplashUrls(urlsBuf.deleteCharAt(location).toString());
	}

	class SplashListener extends AjaxCallBack<File> {

		@Override
		public void onSuccess(File t) {
			super.onSuccess(t);
			setSplashPicFlag(t.getName(), true);
		}
	}

	/**
	 * 设置闪屏图标下载成功标志
	 */
	public void setSplashPicFlag(String url, Boolean flag) {
		if (StringUtil.isEmpty(url)) {
			return;
		}
		SharedPreferencesUtil.setBoolean(mActivity, url, flag);
	}

	public Boolean getSplashPicFlag(String url) {
		if (StringUtil.isEmpty(url)) {
			return false;
		}
		String urlKey = MD5.getMD5Str(url);
		return SharedPreferencesUtil.getBoolean(mActivity, urlKey);
	}

	/**
	 * 判断闪屏图片是否下载完整
	 * 
	 * @return
	 */
	public boolean isCompleteDown() {
		String urls = getSplashUrls();
		if (!StringUtil.isEmpty(urls)) {
			final String[] urlArray = urls.split(",");
			int len = urlArray.length;
			if (len <= 0) {
				return false;
			}
			for (int i = 0; i < len; i++) {
				final int index = i;
				if (!isFileExist(urlArray[i])) {// 文件不存在，直接返回false
					clearSplashUrls();
					return false;
				}
				boolean flag = getSplashPicFlag(urlArray[i]);
				if (!flag) {
					doDelFiles(getSplashPath(urlArray[index]));
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private void doDelFiles(final String path) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				delFiles(path);
			}
		}).start();
	}

	public boolean isFileExist(String url) {
		String path = getSplashPath(url);
		File file = new File(path);
		if (file.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * 计算闪屏图片数
	 * 
	 * @return
	 */
	public int getSplashsNum() {
		String urls = getSplashUrls();
		if (!StringUtil.isEmpty(urls)) {
			String[] urlArray = urls.split(",");
			if (urlArray != null && urlArray.length > 0) {
				return urlArray.length;
			}
		}
		return 0;
	}

	/**
	 * 闪屏图片根目录
	 * 
	 * @return
	 */
	public String getSplashRootPath() {
		return Config.SDCARD_SPLASHS;
	}

	/**
	 * 闪屏图片目录
	 * 
	 * @param url
	 * @return
	 */
	public String getSplashPath(String url) {
		String path = "";
		if (!StringUtil.isEmpty(url)) {
			String urlKey = MD5.getMD5Str(url);
			String splashRoot = Config.SDCARD_SPLASHS;
			path = splashRoot + File.separator + urlKey;
		}
		return path;
	}

	/**
	 * 缓存地市下所有闪屏图片的urls
	 * 
	 * @param urls
	 */
	public void cacheSplashUrls(String urls) {
		String cityCode = MenuMgr.getInstance().getCityCode(mActivity);
		SharedPreferencesUtil.setValue(mActivity, Key.K_SPLASHS_PIC_COMPLETE
				+ cityCode, urls);
	}

	/**
	 * 获取缓存的闪屏图片真实的urls
	 * 
	 * @return
	 */
	public String getSplashUrls() {
		String cityCode = MenuMgr.getInstance().getCityCode(mActivity);
		return SharedPreferencesUtil.getValue(mActivity,
				Key.K_SPLASHS_PIC_COMPLETE + cityCode);
	}

	public void clearSplashUrls() {
		String cityCode = MenuMgr.getInstance().getCityCode(mActivity);
		SharedPreferencesUtil.setValue(mActivity, Key.K_SPLASHS_PIC_COMPLETE
				+ cityCode, "");
	}

	public String patternUrl(String url) {
		if (StringUtil.isEmpty(url)) {
			return "";
		} else {
			if (!StringUtil.isEmpty(url) && url.indexOf("http://") < 0) {
				url = Config.GET_IMAGE_ROOT_URL() + url;
			}
			Log.i("splashs url:" + url);
			return url;
		}
	}

	public void delFiles(String path) {
		if (StringUtil.isEmpty(path)) {
			return;
		}
		try {
			File file = new File(path);
			if (!file.exists()) {
				return;
			}
			if (file.isDirectory()) {
				File[] delFile = file.listFiles();
				if (delFile == null) {
					return;
				}
				if (delFile.length == 0) {
					file.delete();
				} else {
					int i = delFile.length;
					for (int j = 0; j < i; j++) {
						// 每遍历一个文件对象，判断其如果是目录，则
						// 调用自身方法进行递归操作，不是目录则直接删除
						if (delFile[j].isDirectory()) {
							delFiles(delFile[j].getAbsolutePath());
						}
						delFile[j].delete();
					}
				}
			}
			// else {
			// delFiles(file.getParent());
			// }
		} catch (Exception e) {
			Log.e(e.getMessage());
		}
	}

}
