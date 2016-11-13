package cn.ffcs.wisdom.city.common;

import java.io.File;

import android.content.Context;
import cn.ffcs.config.BaseConfig;
import cn.ffcs.wisdom.city.entity.ConfigParams;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.StringUtil;

public class Config extends BaseConfig {

	public static String UMENG_KEY; // 友盟key
	public static String UMENG_CHANNEL; // 渠道
	public static String REPORT_ACTION; // 渠道上报-广播动作 add by caijj 2013-7-17
	public static String UMENG_CHANNEL_KEY = "UMENG_CHANNEL";// 友盟渠道标识符
	public static String TEL_DATA_KEY = "APP_CHANNEL_ID";// 电信189Data标识符
	public static String PUSH_APPLICATION_ID; // 推送应用id
	public static String REFRESH_MSG_COUNT_ACTION; // 更新推送消息条数广播

	public static String SDCARD_CITY_ROOT; // 爱城市sd卡根路径
	public static String SDCARD_SPLASHS; // 地市特色多图闪屏图
	public static String SDCARD_APK;// 应用汇apk下载位置
	public static String SDCARD_WEB;// 浏览器模块图片
	public static String SDCARD_HEADPHOTO;// 用户头像
	public static String SDCARD_HOME_BG;// 首页背景图
	public static String SDCARD_IMAAGECACHE;// 图片缓存
	public static String SDCARD_CITY_TMP;// 爱城市临时目录
	public static String BASE_URL;// BASE url

	// splash background image
	public static final String SPLASH_BG_NAME = "splash_bg"; //

	// 百度追踪key
	public static final String BAIDU_TRADEID = "1000180a";
	// 更新下载界面
	public static final String ACTION_REFRESH_APK_LIST = "update_apk_list";
	// 热词自动更换时间间隔(单位：分钟)
	public static final int TIME_SPACE = 10;
	// 消息中心
	public static final String MSG_CENTER_ACTION = "com.ffcs.android.mc.servce.APP_START";
	// 全球眼分类ID｛注：风景为2｝
	public static final String GLOBAL_EYE_TOUR_ID = "2";
	// 全球眼类型
	public static final String GLOBAL_GEYE_TYPE = "1001";

	/**
	 * 消息类型 
	 * -1. 表示其他类型
	 * 1.  表示系统类型    	---  通知提醒 
	 * 2.  表示个人类型   	---  个人中心消息
	 * 3.  表示意见反馈    
	 * 4.  表示天气类型 
	 * 5.  表示违章类型
	 * 6.  表示新闻详情
	 */
	public static final String OTHER_MSG_TYPE = "-1";
	public static final String SYSTEM_MSG_TYPE = "1";
	public static final String PERSON_MSG_TYPE = "2";
	public static final String FEED_MSG_TYPE = "3";
	public static final String WEATHER_MSG_TYPE = "4";
	public static final String WZ_MSG_TYPE = "5";
	public static final String NEWS_DETAIL_TYPE = "6";

	/**
	 * 消息回执类型 --前3条爱动漫已经写死，定义好了
	 * 1.消息到达回执
	 * 2.用户点击消息（通知栏点击、弹出点击确定按钮）
	 * 3.用户点击通知栏清除或者弹出框取消（可选）
	 * 4.用户点击系统通知消息
	 * 5.用户点击个人中心消息
	 * 6.用户点击天气消息 
	 * 7.用户点击违章消息
	 * 8.用户点击意见反馈消息
	 * 9.用户点击WAP浏览器类型消息
	 */
	public static final String REBACK_MSG_TYPE = "1";
	public static final String REBACK_USER_CLICK_MSG_TYPE = "2";
	public static final String REBACK_USER_CANCLE_MSG_TYPE = "3";
	public static final String REBACK_SYSTEM_MSG_TYPE = "4";
	public static final String REBACK_PERSON_CENTER_MSG_TYPE = "5";
	public static final String REBACK_WEATHER_MSG_TYPE = "6";
	public static final String REBACK_WZ_MSG_TYPE = "7";
	public static final String REBACK_FEED_BACK_MSG_TYPE = "8";
	public static final String REBACK_WAP_MSG_TYPE = "9";

	private static boolean initSuccess = false;

	// 日志点击区域
	public static final String LOG_AREA_USER_CENTER = "user_center";// 个人中心
	public static final String LOG_AREA_SEARCH = "search";// 搜索
	public static final String LOG_AREA_SETTING = "setting";// 设置
	public static final String LOG_AREA_INDEX = "index";// 首页
	public static final String LOG_AREA_RECOMMEND = "recommend";// 推荐
	public static final String LOG_AREA_COMMON = "common";// 常用

	// 日志点击类型
	public static final String LOG_ITEM_BANNER = "1";
	public static final String LOG_ITEM_MENU = "2";
	public static final String LOG_ITEM_WIDGET = "3";

	public static void init(Context context) {
		UMENG_KEY = context.getString(R.string.umeng_key);
		PUSH_APPLICATION_ID = context.getString(R.string.push_application_id);

		SDCARD_CITY_ROOT = SdCardTool.getSdcardDir() + context.getString(R.string.sdcard_city_root);
		SDCARD_SPLASHS = SDCARD_CITY_ROOT + context.getString(R.string.sdcard_splashs);
		SDCARD_WEB = SDCARD_CITY_ROOT + context.getString(R.string.sdcrad_web);
		SDCARD_APK = SDCARD_CITY_ROOT + context.getString(R.string.sdcard_apk);
		SDCARD_HEADPHOTO = SDCARD_CITY_ROOT + context.getString(R.string.sdcard_user_headphoto);
		SDCARD_HOME_BG = SDCARD_CITY_ROOT + context.getString(R.string.sdcard_home_bg);
		SDCARD_IMAAGECACHE = SDCARD_CITY_ROOT + context.getString(R.string.sdcard_city_image_cache);
		SDCARD_CITY_TMP = SDCARD_CITY_ROOT + context.getString(R.string.sdcard_city_tmp_dir);

		REPORT_ACTION = context.getString(R.string.report_action); // add by
																	// caijj
																	// 2013-7-17
		REFRESH_MSG_COUNT_ACTION = context.getString(R.string.refresh_msg_count_action); // add
																							// by
																							// liaodl
																							// 2013-9-24

		ConfigParams params = ConfigUtil.readConfigParams(context);
		if (params != null) {
			BASE_URL = params.getBASE_URL();
		}
		initSuccess = true;
	}

	public static boolean isSuccess() {
		return initSuccess;
	}

	public static void clear() {
		initSuccess = false;
	}

	/**
	 * 获取图片根目录URL
	 */
	public static String GET_REAL_TIME_IMAGE_URL() {
		if (URL_SERVER_IP.equals("ccgd.153.cn") || URL_SERVER_IP.equals("prodics.153.cn")) {
			return "http://" + URL_SERVER_IP + ":" + URL_RESOURCE_SERVER_PORT + File.separator;
		} else {
			return GET_SERVER_ROOT_URL();
		}
	}

	/**
	 * 获取图片根目录URL
	 */
	public static String GET_IMAGE_ROOT_URL() {
		if (StringUtil.isEmpty(BASE_URL)) {
			if (URL_SERVER_IP.equals("ccgd.153.cn") || URL_SERVER_IP.equals("prodics.153.cn")) {
				return "http://" + URL_IMAGE_SERVER_ROOT + ":" + URL_RESOURCE_SERVER_PORT + File.separator;
			} else {
				return GET_SERVER_ROOT_URL();
			}
		} else {
			return BASE_URL + File.separator;
		}
	}

	public static class UrlConfig {

		/**
		 *  请求类型
		 */
		public static String REQUEST_MENU_SERVICETYPE = "1001";
		public static String REQUEST_GET_VERIFYCODE_SERVICETYPE = "1002";
		public static String REQUEST_REGISTER_SERVICETYPE = "1003";
		public static String REQUEST_LOGINCHECK_SERVICETYPE = "1004";
		public static String REQUEST_LOCATION_SERVICETYPE = "1005";
		public static String REQUEST_ADVERTISEMENT_SERVICETYPE = "1007";
		public static String REQUEST_FORGET_PSW_SERVICETYPE = "1014";
		public static String REQUEST_SEND_ADVICE_SERVICETYPE = "1006";
		public static String REQUEST_NOTIFICATION = "1008";
		public static String REQUEST_UPLOAD_RECORD_SERVICETYPE = "1009";
		public static String REQUEST_UPDATE_SERVICETYPE = "1010";
		public static String REQUEST_CHANGE_CITY_SERVICETYPE = "1011";
		public static String REQUEST_COMMUNITY_SERVICETYPE = "1012";
		public static String REQUEST_CHANGE_MOBILE_SERVICETYPE = "1013";
		public static String REQUEST_COMMUNITY_SETTING_SERVICETYPE = "1015";
		public static String REQUEST_PUBLICITY_SERVICETYPE = "1016";

		public static String REQUEST_SNAPSHOT_SERVICETYPE = "1021";
		public static String REQUEST_SEND_SNAPSHOT_SERVICETYPE = "1022";
		public static String REQUEST_SEND_SNAPSHOT_COMMENT_SERVICETYPE = "1023";
		public static String REQUEST_MY_SNAPSHOT_SERVICETYPE = "1024";
		public static String REQUEST_SNAPSHOT_COMMENT_SERVICETYPE = "1025";
		public static String REQUEST_SNAPSHOT_HITS_SERVICETYPE = "1026";

		// 百度热词关键字搜索
		public static String BAIDU_HOT_WORLD_SERVICETYPE = "word";
		public static String BAIDU_HOT_WORLD_TYPE = "type";
		// 热门关键字搜索
		public static String REQUEST_GLOBAL_FLOW = "1017";
		public static String REQUEST_RANK_SERVICETYPE = "1027";
		public static String REQUEST_KEYWORD_SERVICETYPE = "1028";
		public static String REQUEST_SEARCH_SERVICETYPE = "1029";
		public static String REQUEST_WEATHER_DATA = "1031";
		public static String REQUEST_SMS_BIND_ID = "1032";
		public static String REQUEST_CHANNEL_RECORD = "1033";

		public static String REQUEST_PASSWORD_CHANGE = "1035";
		public static String REQUEST_USERNAME_CHANGE = "1036";
		public static String REQUEST_SHARE = "1037";
		public static String REQUEST_GET_CONFIG_PARAMS = "1038";
		public static String REQUEST_BIND_XIAMENNET_ACCOUNT = "1039";
		public static String REQUEST_REGISTER_NEWS_ACCOUNT = "1040";
		public static String REQUEST_AUTO_LOGIN = "1041";

		public static String REQUEST_CHANNEL_REGISTER = "1045"; // 厦门渠道录入
		public static String REQUEST_FEEDBACK_REPLY = "1046"; // 意见反馈回复
		public static String REQUEST_CHONGQING_SDM = "1047"; // 重庆水电煤

		public static String REQUEST_SNAPSHOT_SY_3509_SERVICETYPE = "1042"; // 宁德网摄影
		public static String REQUEST_SNAPSHOT_SSP_3509_SERVICETYPE = "1043"; // 宁德随手
		public static String REQUEST_WZ_SERVICETYPE = "1044"; // 车辆违章查询
		public static String REQUEST_WZ_DELETE_SERVICETYPE = "1062"; // 违章删除和保存
		public static String REQUEST_GET_UER_INFORMATION_SERVICETYPE = "1065";// 个人中心获取用户信息
		public static String REQUEST_SIGN_IN_SERVICETYPE = "1066";// 签到
		public static String REQUEST_GET_RELEVANCE_SERVICETYPE = "1067";// 我的关联
		public static String REQUEST_SPLSAHS_SERVICETYPE = "1070";// 闪屏功能添加

		public static String REQUEST_LOG_SERVICETYPE = "";// 日志点击上报

		/**
		 * 获取百度热词URL
		 */
		public static final String GET_BAIDU_HOTWORLD_URL() {
			return "http://api.m.baidu.com/?" + BAIDU_HOT_WORLD_TYPE + "=hot" + "&from="
					+ BAIDU_TRADEID;
		}

		// 爱城市2期基地址
		public final static String ICITY_BASE_URL = GET_SERVER_ROOT_URL() + "icity/service/";

		// 个人中心baseUrl
		public final static String PERSONCENTERBASE = ICITY_BASE_URL + "personalcenter/";

		/**
		 * 百度wap根URL
		 */
		public static final String GET_BAIDU_URL() {
			return "http://wap.baidu.com";
		}

		// 请求首页和菜单
		public final static String URL_HOME_MENU = ICITY_BASE_URL
				+ "menu/levelonemenu/query?baseLine=400";
		

		public final static String URL_MENU = ICITY_BASE_URL + "menu/menuquery/query?baseLine=400";

		// 城市切换
		public final static String URL_CHANGE_CITY = GET_HOME_URL() + "serviceType="
				+ REQUEST_CHANGE_CITY_SERVICETYPE;

		// 获取定位
		public final static String URL_LOCATION = GET_HOME_URL() + "serviceType="
				+ REQUEST_LOCATION_SERVICETYPE;

		// 登录验证
		public final static String URL_LOGIN_CHECK = PERSONCENTERBASE + "mobilelogin/login";

		//登录接口改造，密码使用3des加密
//		public final static String URL_LOGIN_CHECK_NEW = GET_SERVER_ROOT_URL()
//				+ "icity-api-client-other/icity/service/personalcenter/mobilelogin/encryptlogin";
		public final static String URL_LOGIN_CHECK_NEW = GET_SERVER_ROOT_URL()
				+ "icity-api-client-other/hwyz/icity/service/personalcenter/mobilelogin/encryptlogin";
		
		// 自动登录，改成加密的地址
		public final static String URL_AUTO_LOGIN = GET_SERVER_ROOT_URL()
				+ "service/encrypt/login.action?serviceType=" + REQUEST_AUTO_LOGIN;
		
		// 自动登录新接口,密码使用3des加密
		public final static String URL_AUTO_LOGIN_NEW = GET_SERVER_ROOT_URL()
				+ "icity-api-client-other/hwyz/icity/service/encrypt/login.action";

		// 忘记密码
		public final static String URL_FORGET_PSW = GET_HOME_URL() + "serviceType="
				+ REQUEST_FORGET_PSW_SERVICETYPE;

		// 注册
		public final static String URL_REGISTER = GET_HOME_URL() + "serviceType="
				+ REQUEST_REGISTER_SERVICETYPE;
		
		// 注册新接口
		public final static String URL_REGISTER_NEW = GET_SERVER_ROOT_URL()
				+ "icity-api-client-other/hwyz/icity/service/user/mobileregister/encryptmobileregister";

		// 获取手机验证码
		public final static String URL_GET_VERIFYCODE = GET_HOME_URL() + "serviceType="
				+ REQUEST_GET_VERIFYCODE_SERVICETYPE;
		
		// 长春是否首次使用第三方账号登陆接口
		public final static String URL_IS_FIRST_LOGIN = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/firstLogin";
				
		// 长春验证手机号是否注册接口
		public final static String URL_IS_REGISTERED_MOBILE = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/isRegisterMobile";
				
		// 长春用户名判重接口
		public final static String URL_IS_REPEAT_NAME = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/isRepeatingName";
				
		// 长春验证手机号是否已绑定接口
		public final static String URL_IS_BOUND_MOBILE = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/isBandingMobile";
				
		// 长春发送验证码接口
		public final static String URL_SEND_CAPTCHA = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/sendCaptcha";
				
		// 长春验证码校验接口
		public final static String URL_VERIFY_CAPTCHA = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/verifyCaptcha";
				
		// 长春第三方账号绑定接口
		public final static String URL_SAVE_BINDING = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/thirdAccountSave";
				
		// 长春更新个人信息接口
		public final static String URL_UPDATE_PERINFO = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/updatePerInfo";
		
		// 长春填写邀请码接口
		public final static String URL_INPUT_INVITATION = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/updateinvitationcode";
				
		// 长春获取个人信息接口
		public final static String URL_GET_PERINFO = GET_SERVER_ROOT_URL() + "icity-api-client-major/icity/service/user/getPerInfo";
		
		// 长春获取个人信息接口
		public final static String URL_GET_POINTS = GET_SERVER_ROOT_URL() + "icity-api-client-other/icity/service/points/getPoints";

		// 首页广告
		public final static String URL_ADVERTISEMENT = GET_HOME_URL() + "serviceType="
				+ REQUEST_ADVERTISEMENT_SERVICETYPE;

		// 我的应用
		public final static String URL_MYAPP_QUERY = ICITY_BASE_URL + "menu/myAppQuery/query";

		// 添加应用
		public final static String URL_MYAPP_ADD = ICITY_BASE_URL + "menu/myAppQuery/add";

		// 删除应用
		public final static String URL_MYAPP_DEL = ICITY_BASE_URL + "menu/myAppQuery/delete";

		// 自定义控件
		public final static String URL_WIDGET_QUERY = ICITY_BASE_URL + "menu/widget/query";

		// 更新用户信息接口
		public final static String URL_UPDATA_USER_INFORMATION = PERSONCENTERBASE
				+ "pcupdate/update";

		// 绑定短信，换成加密接口
		public final static String URL_SMS_BIND_ID = GET_SERVER_ROOT_URL()
				+ "service/encrypt/share.action";

		// 反馈
		public final static String URL_SEND_ADVICE = GET_HOME_URL() + "serviceType="
				+ REQUEST_SEND_ADVICE_SERVICETYPE;
		// 意见反馈回复
		public final static String URL_FEEDBACK_REPLY = GET_HOME_URL() + "serviceType="
				+ REQUEST_FEEDBACK_REPLY;
		// 签到
		public final static String SIGN_IN = PERSONCENTERBASE + "pcsign/sign";

		// 获取关键字
		public final static String URL_KEYWORD = GET_HOME_URL() + "serviceType="
				+ REQUEST_KEYWORD_SERVICETYPE;

		// 获取天气预报数据
		public final static String URL_WEATHER_DATA = "http://info-source.153.cn:8081/icity-infosource-ztq/service/icity/ztq/weather.jhtml";
		// 长春天气
//		public final static String URL_WEATHER_DATA = "http://ccgd-info-source.153.cn:52281/icity-infosource-ztq/service/icity/ztq/weather.jhtml";

		// 搜索关键字
		public final static String URL_KEYWORD_SEARCH = GET_SERVER_ROOT_URL() + "icity-api-client-v7/icity/service/v7/queryApp";

		// 搜索百度热词 http://wap.baidu.com/s?word=
		public final static String URL_BAIDU_HOTWORLD = GET_BAIDU_URL() + File.separator + "s?"
				+ BAIDU_HOT_WORLD_SERVICETYPE + "=";

		public final static String URL_GET_CONFIG_PARAMS = GET_HOME_URL() + "serviceType="
				+ REQUEST_GET_CONFIG_PARAMS;

		// 上报记录
		public final static String URL_UPLOAD_RECORD = GET_HOME_URL() + "serviceType="
				+ REQUEST_UPLOAD_RECORD_SERVICETYPE;

		// 上报栏目点击记录 by caijj 2013-9-10
		public final static String URL_UPLOAD_RECORD_BATCH = GET_SERVER_ROOT_URL()
				+ "icity-api-jms/service/icity/user-behavior/menu/batch/click";

		// 获取全局流量
		public final static String URL_GLOBAL_FLOW = GET_HOME_URL() + "serviceType="
				+ REQUEST_GLOBAL_FLOW;

		// 获取常用应用
		public final static String URL_COMMONLY_USE = GET_SERVER_ROOT_URL()
				+ "service/v6/queryCommonApp.action";
		// 修改密码
		public final static String URL_PASSWORD_CHANGE = GET_HOME_URL() + "serviceType="
				+ REQUEST_PASSWORD_CHANGE;
		
		// 修改密码新接口
		public final static String URL_PASSWORD_CHANGE_NEW = GET_SERVER_ROOT_URL()
				+ "icity-api-client-other/hwyz/icity/service/user/userinfomodify/encryptuserinfomodify";

		// 修改昵称
		public final static String URL_USERNAME_CHANGE = GET_HOME_URL() + "serviceType="
				+ REQUEST_USERNAME_CHANGE;
		// 话费和流量查询
		public final static String URL_PHONE_BILL_QUERY = ICITY_BASE_URL
				+ "pc/queryFlowAndCharge/query";

		// add by 蔡杰杰 查询类
		public final static String URL_QUERY_INFO = GET_SERVER_ROOT_URL() + "query.shtml"; // 查询类配置信息接口

		// add by 许振伟 客户端升级
		public final static String URL_UPDATE = GET_HOME_URL() + "serviceType="
				+ REQUEST_UPDATE_SERVICETYPE + "&os_type=1";

		// 记录渠道
		public final static String URL_CHANNEL_RECORD = GET_HOME_URL() + "serviceType="
				+ REQUEST_CHANNEL_RECORD + "&ffcs_icity=start";

		// 向平台注册由消息中心推送分配的DeviceToken
		public final static String URL_REGISTER_TOKEN = ICITY_BASE_URL
				+ "msgsend/msgInfoOper/registerToken";

		// add by 许振伟 违章查询
		// 车辆违章查询
		// public final static String URL_WZ = GET_HOME_URL() + "serviceType="
		// + REQUEST_WZ_SERVICETYPE;

		// 违章查询：11月27修改
//		public final static String URL_WZ ="http://www.153.cn:8888/exter.shtml?serviceType=1044";
//		public final static String URL_WZ = GET_HOME_URL() + "serviceType=1044";
		// 长春
//		public final static String URL_WZ ="http://ccgd.153.cn:50888/exter.shtml?serviceType=1044";
		public final static String URL_WZ ="http://ccgd-info-source.153.cn:52281/car_illegal/mobile/illegal/queryInfo.jhtml";

		// 违章查询 查看是否绑定了车辆
		public final static String URL_WZ_SELECT_BIND = PERSONCENTERBASE +
				 "pcrelevance/queryKeyGroupListInPbt";
		
		// 违章删除关联
		public final static String DELETE_WZ_RELEVANCE = PERSONCENTERBASE
				+ "pcrelevance/autoAddKeyGroup";

		// 违章缴费总额
		public final static String ALL_VIOLATIONS_PAY = GET_SERVER_ROOT_URL() + "violate/pay.shtml";

		// 视频基地址
		public final static String URL_VIDEO_BASE = "http://tyjx.fjii.com:8081/geyeapi";

		// 获取视频列表地址
		public final static String URL_VIDEO_LIST = URL_VIDEO_BASE + "/video/list";

		// widget上报
		public final static String URL_WIDGET_LOG = GET_SERVER_ROOT_URL()
				+ "icity/service/misc/widgetlog/add";

		// 我的关联
		public final static String URL_GET_RELEVANCE = PERSONCENTERBASE
				+ "pcrelevance/queryReleData";

		// 删除关联
		public final static String URL_DELETE_RELEVANCE = PERSONCENTERBASE
				+ "pcrelevance/disableKeyGroup";

		// 获取当前城市下可关联栏目
		public final static String URL_RELEVANCE_ADD = PERSONCENTERBASE
				+ "pcrelevance/queryReleAppMenuListInCity";

		public final static String URL_RELEVANCE_ADD_NEW = PERSONCENTERBASE
				+ "pcrelevance/queryReleAppMenuList2InCity";

		// 请求闪屏图片
		public final static String URL_SPLASHS = GET_HOME_URL() + "serviceType="
				+ REQUEST_SPLSAHS_SERVICETYPE;

		// add by 许振伟 积分规则
		// public final static String URL_CREDIT_RULE =
		// "http://www.153.cn/personalcenter/jfdjgz/";// 积分规则
		public final static String URL_CREDIT_RULE = "http://ccgd.153.cn/icity-wap/personalCenter/jfdjgz/index.jsp";// 积分规则

		// 获取查询类关联数据
		public final static String GET_QUERY_RELEVANCE = PERSONCENTERBASE
				+ "pcrelevance/queryKeyGroupListInPbt";

		// 获取分享提示
		public final static String GET_SHARE_NOTICE = ICITY_BASE_URL
				+ "misc/msgkindlyreminder/query";

		// FAQ
//		public final static String URL_CREDIT_FAQ = "http://www.153.cn/icity-wap/faq/index.jsp";
		// 长春FAQ
		public final static String URL_CREDIT_FAQ = "http://ccgd.153.cn:50000/icity-wap/faq/index.jsp";

		// 日志记录
		// public final static String URL_LOG_REPORT =
		// "http://218.85.135.162:38080/icity-api-jms/service/icity/user-behavior/click";
		public final static String URL_LOG_REPORT = "http://ccgd.153.cn:50081/icity-api-jms/service/icity/user-behavior/click";

		// 应用汇下载成功上报
		public final static String URL_APK_LOG = GET_SERVER_ROOT_URL()
				+ "icity-api-jms/service/icity/user-behavior/app/download";

		// 天翼积分 add by caijj 20131113
		public final static String URL_TIANYI_BILL = GET_SERVER_ROOT_URL()
				+ "icity-api-3rd/service/icity/ctIntegral/getCurrIntegral";
		
		public final static String URL_ONE_MENU = GET_SERVER_ROOT_URL()
				+ "icity/service/menu/levelonemenu/query";

	}
}
