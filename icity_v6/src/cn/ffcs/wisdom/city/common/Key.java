package cn.ffcs.wisdom.city.common;

public final class Key {

	// ===========================================
	// SharedPreferences key
	// ===========================================
	// the cityCode key
	public static final String K_LOCAL_CITY_CODE = "k_local_city_code";

	// the cityName key
	public static final String K_LOCAL_CITY_NAME = "k_local_city_name";

	// the menu version key
	public static final String K_LOCAL_MENU_VERSION = "k_local_menu_version";

	public static final String K_IS_LOGIN = "k_is_login";

	public static final String K_PHONE_NUMBER = "k_phone_number";

	// add by caijj 是否首次安装， false:第一次启动 
	public static final String K_FIRST_INSTALL = "k_first_install";
	// add by caijj 上一次版本号
	public static final String K_LAST_VERSION = "k_last_version";
	// add by liaodl 是否首次注册消息中心Token
	public static final String K_FIRST_REGISTER = "k_first_register";
	// add by liaodl 消息中心服务是否启动成功
	public static final String K_START_PUSH_SERVER = "k_start_push_server_flag";
	// 向平台注册Token状态标志
	public static final String K_REG_MEG_TOKEN = "k_reg_meg_token";
	// 消息中心分配的token号
	public static final String K_DEVICE_TOKEN = "k_device_token";

	public static final String K_FIRST_IN_CATEGORY = "k_first_in_category";
	/**
	 * 绑定厦门网账户
	 */
	public static final String K_BIND_NEWS_ACCOUNT = "k_bind_news_account";

	// ===========================================
	// URL key
	// ===========================================
	public static final String U_BROWSER_URL = "url";
	public static final String U_BROWSER_TITLE = "title";
	public static final String U_BROWSER_CITYCODE = "citycode";
	public static final String U_BROWSER_ITEMID = "item_id";
	public static final String U_BROWSER_IMSI = "imsi";
	public static final String U_BROWSER_ITEM = "sub_menu_item";
	public static final String U_BROESER_MOBILE = "mobile";
	public static final String U_BROWSER_QUERY = "isQuery";
	public static final String U_BROWSER_COOKIES = "cookies";
	public static final String U_FORCE_HIDE_T_B = "force_hide_t_b";
	public static final String U_BROWSER_IS_PUSH = "u_browser_is_push";	//是否推送
	/**************************************************************************/
	// 显示TopBar
	public static final String K_SHOW_TOPBAR = "k_show_top_bar";
	/**************************************************************************/
	// 显示定位提示
	public static final String K_SHOW_LOCATION_TIPS = "k_show_location_tips";
	// 定位Location
	public static final String K_LOCATION = "k_location";
	public static final String K_LOCATION_LAT = "k_location_lat"; // 纬度
	public static final String K_LOCATION_LNG = "k_location_lng"; // 经度
	public static final String K_LOCATION_CITY = "k_location_city"; // 城市名称
	public static final String K_LOCATION_DISTRICT = "k_location_district"; // 纬度
	public static final String K_LOCATION_ADDR = "k_location_addr"; // 详细地址
	public static final String K_LOCATION_SUCCESS = "k_location_success"; // 定位成功标志
	public static final String K_SELECT_CITY = "k_select_city"; // 用户选择的城市
	/***********************************************************************/
	// 宣传活动URL
	public static final String K_PUBLICITY_URL = "k_publicity_url";
	// 宣传活动是否显示
	public static final String K_PUBLICITY_ISSHOW = "k_publicity_ishow";
	// 宣传活动ID
	public static final String K_PUBLICITY_ID = "k_publicity_id";
	// 宣传活动实体类
	public static final String K_PUBLICITY_PRO = "k_publicity_pro";

	/*********************************************************************/
	public static final String K_NOTIFICATION_FLAG = "k_notification_flag";

	public static final String K_NOTIFICATION_TIME = "k_notification_time"; // 定时器间隔时间
																			// 默认2分钟

	public static final String K_ALARM_PARAM_CLS = "k_alarm_param_cls"; // 定时器CLS参数

	public static final String K_NOTIFICATION_PERMANENT = "k_notification_permanent"; // 常驻通知定时器

	/*********************************************************************/
	/**
	 * 天翼景象
	 */
	public static final String K_GLOBALEYE_CITYCODE = "k_globaleye_citycode";

	public static final String K_GLOBALEYE_TYPE_CODE = "k_globaleye_type_code"; // 天翼景象类型编码

	/*********************************************************************/
	/**
	 * 切换城市
	 */
	public static final String K_CITY_CHANGE_FOR_NEWGUID = "k_city_change_for_newguid"; // 新手引导页后的城市切换

	public static final String K_CITY_CHANGE_NO_TIPS = "k_city_change_no_tips"; // 定位提示城市切换，是否提示

	/*********************************************************************/
	/**
	 * 退出应用
	 */
	public static final String K_EXIT_ICITY = "k_exit_icity"; // 退出应用，保存点击时的时间
	/**********************************************************************/
	/**
	 * 中转站
	 */
	public static final String K_ROUTER_PROCESSOR_TYPE = "k_router_processor_type"; // 中转处理器类型

	public static final String K_ROUTER_RESPONSE_RESULT = "k_router_response_result"; // 中转路由返回给调用者的数据

	/*************************Receiver Action Filter*****************************/
	// 路由接收器
	public static final String K_ROUTER_RESPONSE_ACTION_TYPE = "k_router_response_action_type"; // 第三方应用广播接收ACTION_TYPE

	public static final String K_ROUTER_ACTION_TYPE = "k_router_action_type"; // 中转路由ActionType。

	/*******************************************************************/
	/**
	 * 新浪微博
	 */
	public static final String K_SINA_ACCESS_TOKEN = "sina_access_token";
	public static final String K_SINA_EXPIRES_IN = "sina_expires_in";
	public static final String K_SINA_SECRET = "sina_secret";

	/**************************************************************************/
	/**
	 * 新手引导页标识
	 */
	public static final String K_NEWGUID_FOR_NEWUSER = "k_newguid_for_newuser";

	/**************************************************************************/

	/**
	 * 我的关联
	 */
	public static final String K_RELEVANCE = "k_relevance";

	/**
	 * webview浏览器小图标位置
	 */
	public static final String K_WEB_ICO_RIGHT = "k_web_ico_right";
	public static final String K_WEB_ICO_BOTTOM = "k_web_ico_bottom";

	/**
	 *违章查询
	 */
	public static final String ADD_NEW_CAR = "ADD_NEW_CAR";
	public static final String K_WZCAR_ENTRY = "k_wzcar_entry";
	public static final String REFRESH_UPDATE_TIME = "Refresh_update_time";

	/**
	 * 搜索类型
	 */
	public static final String K_SEARCH_TYPE = "search_type";

	public static final String K_HOME_CONFIG = "home_config";

	/**
	 * 获取系统推送配置
	 */
	public static final String K_SYSTEM_INFO = "k_system_info";

	public static final String K_RETURN_TITLE = "k_return_title";

	public static final String K_RESULT_TITLE = "k_result_title";

	// 传给分享的标题
	public static final String K_SHATE_TITLE = "k_icity_share_title";
	// 传给分享的内容
	public static final String K_SHARE_CONTENT = "k_icity_share_content";

	/**
	 * 获取百度热词间隔时间
	 */
	public static final String K_GET_BAIDU_HOTWORD_TIME = "k_get_baidu_hotword_time";
	/**
	 * 获取本地热门应用
	 */
	public static final String K_GET_HOT_APP = "k_get_hot_app";
	/**
	 * 保存共享启动第三方应用需要的一些其他参数
	 */
	public static final String K_OTHER_APP_KEY_PARAMS = "share_other_app_key_params";

	/**
	 *首页帮助 
	 */
	public static final String K_HOME_HELP = "k_home_help";

	/**
	 * 首页背景图
	 */
	public static final String K_HOME_BG = "k_home_bg";
	/**
	 * 是否从网络请求城市列表
	 */
	public static final String K_REQ_NET_CITY = "k_request_net_city";

	/**
	 * 是否成功获取城市
	 */
	public static final String K_GET_CITY_IS_SEND = "k_get_city_is_send";

	/**
	 * 单张闪屏图片下载成功标志
	 */
	public static final String K_SPLASHS_PIC_COMPLETE = "k_splashs_pic_complete";

	/**
	 * 视频跳转输出地址
	 */
	public static final String K_OUT_URL = "k_out_url";

	/**
	 * 能力开放视频。wap，url
	 */
	public static final String K_WEB_URL = "k_web_url";

	/**
	 * 是不是从登陆进来
	 */
	public static final String K_IS_FROM_LOGIN = "k_is_from_login";

	/**
	 * 记住的用户名
	 */
	public static final String K_REMEMBER_USERNAME = "k_remember_username";

	/**
	 * 记住的密码
	 */
	public static final String K_REMEMBER_PASSWORD = "k_remember_password";
	/**
	 * 3G提示开关,false是打开，true是关闭
	 */
	public static final String K_3G_SWITCH = "k_3g_switch";
	
	/**
	 * 网络类型,0是3G
	 */
	public static final String K_NETWORK_TYPE = "k_network_type";

	/**
	 * 车牌号
	 */
	public static final String K_CAR_NO = "k_car_no";

	/**
	 * 车架位
	 */
	public static final String K_CAR_LAST_CODES = "k_car_last_codes";

	public static final String K_IS_GET_VALUE = "k_is_get_value";

	public static final String LAST_REFRESH_TIME = "10001"; //最后更新时间key
	
	/////////////////////信鸽推送的//////////////////////////////
	public static final String K_XG_NOTIFICATION_WAP = "k_xg_notification_wap"; //推送消息 用来保存
	public static final String K_XG_NOTIFICATION_URL = "k_xg_notification_url"; //推送消息 用来保存
	public static final String K_XG_NOTIFICATION_IS_LOGIN = "k_xg_notification_is_login"; //推送消息 用来保存
	public static final String K_XG_NOTIFICATION_TITLE = "k_xg_notification_title"; //推送消息 用来保存
	public static final String K_XG_NOTIFICATION_CONTENT = "k_xg_notification_content"; //推送消息 用来保存
}
