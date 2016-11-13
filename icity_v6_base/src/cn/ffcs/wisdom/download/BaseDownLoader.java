package cn.ffcs.wisdom.download;

import java.io.File;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.HttpHandler;
import android.content.Context;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.MD5;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:   Base下载器                                                 </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-23           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public abstract class BaseDownLoader {

	private FinalHttp finalHttp;
	private HttpHandler<File> httpHandler;
	private boolean isResume;// 断点续传标志
	private boolean isRunning;// 是否正在下载
	public Context mContext;

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	/**
	 * 存放目录
	 */
	public abstract String getDownDir();

	/**
	 * 格式化下载地址
	 */
	public abstract String patternUrl(String url);

	public BaseDownLoader(Context context) {
		init(context);
	}

	private void init(Context context) {
		mContext = context;
		finalHttp = new FinalHttp();
		finalHttp.configRequestExecutionRetryCount(8);
		isResume = true;// 默认支持断点续传
	}

	/**
	 * 开始下载，默认保存名称为reqUrl的MD5字符串
	 * @param reqUrl
	 * @param callBack
	 */
	public void down(String reqUrl, AjaxCallBack<File> callBack) {
		down(reqUrl, "", callBack);
	}

	/**
	 * 开始下载
	 * @param reqUrl
	 * @param saveName
	 * @param callBack
	 */
	public void down(String reqUrl, String saveName, AjaxCallBack<File> callBack) {
		if (StringUtil.isEmpty(reqUrl)) {
			return;
		}
		try {
			reqUrl = patternUrl(reqUrl);
			if (StringUtil.isEmpty(saveName)) {
				saveName = getFileName(reqUrl);
			}
			String pathName = getDownDir() + File.separator + saveName;
			File file = new File(pathName);
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
//			if (!file.exists()) {
//				file.createNewFile();
//			}
			//调用download方法开始下载
			httpHandler = finalHttp.download(reqUrl, //下载路径
					pathName,//保存到本地的路径,包含文件名称
					isResume, //true:断点续传  false:不断点续传（全新下载）
					callBack);
			setRunning(true);
		} catch (Exception e) {
			setRunning(false);
			Log.e(e.getMessage(), e);
		}
	}

	/**
	 * 停止下载
	 */
	public void stopDownload() {
		if (httpHandler != null) {
			httpHandler.stop();
			setRunning(false);
		}
	}

	/**
	 * 获取文件名
	 * @param conn
	 * @return
	 */
	public String getFileName(String url) {
		return MD5.getMD5Str(url);
	}

	/**
	 * 设置断点续传 true：支持 false:重新下载
	 * 默认为断点续传
	 * @param isResume
	 */
	public void setResume(boolean isResume) {
		this.isResume = isResume;
	}

}
