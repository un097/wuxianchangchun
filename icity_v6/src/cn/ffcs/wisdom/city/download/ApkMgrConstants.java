package cn.ffcs.wisdom.city.download;

public class ApkMgrConstants {

	/**
	 * 发送广播通知Activity更改apk列表 <br/>
	 * 通过control值 判断具体动作 <br/>
	 * RECEIVER_DELETE_APK:更新已下载栏目,且 "正在下载"界面有应用下载完成时,自动跳到"已下载"界面<br/>
	 * RECEIVER_UPDATE:删除任务时刷新"正在下载"界面 <br/>
	 * RECEIVER_PACKAGE_REFRESH:刷新"已下载"界面,相比1，没有自动跳转界面 <br/>
	 */
	public static final int RECEIVER_DELETE_APK = 0x1;// 从apk列表界面删除，在已下载界面显示
	public static final int RECEIVER_UPDATE = 0x2;// 删除任务时刷新"下载中"界面
	public static final int RECEIVER_PACKAGE_REFRESH = 0x3;// 安装包不存在，被删除情况 或 程序被卸载情况 下 更新已下载界面
	public static final int RECEIVER_RE_DOWN_TIP = 0x4;// 下载异常（超时或网络异常等）提示用户

	public static final int DOWNLOAD_PADDING = 0x10;// 未下载
	public static final int DOWNLOAD_RUNNING = 0x11;// 下载中
	public static final int DOWNLOAD_PAUSE = 0x12;// 暂停下载
	public static final int DOWNLOAD_SUCCESS = 0x13;// 下载完成

	public static final int INSTALL_SUCCESS = 0x20;// 已安装
	public static final int INSTALL_FAIL = 0x21;// 未安装

	public static final String POSTFIX = ".apk";

	public static final String INTENT_CONTROL_APK = "intent_control_apk";
	public static final String INTENT_APK_ENTITY = "intent_apk_entity";

	public static final String COMPLETE_DOWNLOAD_FLAG = "complete_download_flag";
	public static final String COMPLETE_DOWNLOAD_YES = "complete_download_yes";
	public static final String COMPLETE_DOWNLOAD_NO = "complete_download_no";

	// 扩展字段
	public static final int SUPPORT_YES = 1;
	public static final int SUPPORT_NO = 0;

}
