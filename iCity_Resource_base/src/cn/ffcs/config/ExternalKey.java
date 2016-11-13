package cn.ffcs.config;

/**
 * <p>
 * Title: 第三方参数key
 * </p>
 * <p>
 * Description: 规范传递给第三方参数的key
 * </p>
 * <p>
 * @author: Caijj
 * </p>
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * <p>
 * Company: FFCS Co., Ltd.
 * </p>
 * <p>
 * Create Time: 2012-8-20
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
public class ExternalKey {

	// 传递给第三方应用使用的用户名
	public static final String K_USERNAME = "k_icity_username";
	// 传递给第三方应用使用的密码，防止被第三方截获统一进行加密
	public static final String K_PASSWORD = "k_icity_password";
	// 传递给第三方应用使用的电话号码
	public static final String K_PHONENUMBER = "k_icity_phonenumber";
	public static final String K_CITYNAME = "k_icity_cityname";
	public static final String K_CITYCODE = "k_icity_citycode";
	// 传递给第三方应用的菜单ID
	public static final String K_MENUID = "k_icity_menuid";
	public static final String K_MENUNAME = "k_icity_menuname";
	// 传递给第三方的client_type
	public static final String K_CLIENT_TYPE = "changchunTV_ver";
	public static final String K_PRODUCT_ID = "changchunTV";

	// 是否从web页跳到支付商城列表页面
	public static final String K_FROM_WEB = "k_from_web";

	/**
	 * 新闻网
	 */
	public static final String K_ANDROID_URL = "k_android_url";
	public static final String K_URL = "k_url";
	public static final String K_IS_WIDGET = "k_is_widget";
	public static final String K_NEWS_TITLE = "k_news_title";

	/***********************************************************************
	 * 天翼景象 TODO 待天翼景象按照第三方规范修改后，CITY_NAME可删除 注：值不可修改
	 */
	public static final String K_GLOBALEYE_CITYCODE = "k_globaleye_citycode"; // 天翼景象城市编码
																				// add
																				// by
																				// caijj
	public static final String K_AREA_CODE = "AREA_CODE";
	public static final String K_CITY_NAME = "CITY_NAME";
	public static final String K_GLO_TYPE = "GEYE_TYPE_KEY";
	public static final String K_EYE_ID = "k_eye_id";
	public static final String K_EYE_TITLE = "k_eye_title";
	public static final String K_RTSP_ADDRESS = "k_rtsp_address";
	public static final String K_HIGH_FLAG = "high_flag";

	/**********************************************************************
	 * 翼支付 注：值不可修改
	 */
	public static final String K_PRODUCTNO = "PRODUCTNO"; // 手机号
	public static final String K_LOCATION = "LOCATION"; // 区域码

	/**********************************************************************
	 * 翼支付_v2 add by caijj 2013-3-16 注：值不可修改
	 */
	public static final String K_PAY_CITY_CODE = "city_code"; // 城市编码 6位
	public static final String K_PAY_MOBILE = "mobile_num"; // 手机号
	public static final String K_PAY_TIME_TAMP = "timestamp"; // 时间戳 yyyyMMdd
																// hh24:mm:ss
	public static final String K_PAY_SIGN_CODE = "sign_code"; // BASE64(MD5("icity"
																// + city_code +
																// mobile_num +
																// pay_category
																// + timestamp))
	public static final String K_PAY_CATEGORY = "pay_category"; // 业务类型

	/*****************************************************
	 * 随手拍
	 */
	public static final String KEY_APPS_EXT_MENUID = "cn.ffcs.wisdom.city#menuid";
	public static final String K_PHOTO_SHARE_URL = "shareUrl";

	/**
	 * 水电煤
	 */
	public static final String K_USER_ID = "k_user_id"; // 用户账号ID
	public static final String K_WATERCOAL_APP_TYPE = "k_watercoal_app_type"; // 水电煤类型

	// 定位到当前城市的编码、名称、经纬度 - used for road project - add by liaodl 2013-12-19
	public static final String K_ICITY_LOCATION_CITY_CODE = "k_icity_location_city_code";
	public static final String K_ICITY_LOCATION_CITY_NAME = "k_icity_location_city_name";
	public static final String K_LOCATION_LAT = "k_location_lat"; // 纬度
	public static final String K_LOCATION_LNG = "k_location_lng"; // 经度
	/**
	 * 定位到的城市的天翼景象编码
	 */
	public static final String K_LOCATION_AREA_CODE = "k_location_area_code";
	/**
	 * 路况详情的语音播报
	 */
	public static Boolean PLAY_TTS_ONCE = true;

}
