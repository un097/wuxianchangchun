package cn.ffcs.wisdom.base;

import java.util.HashMap;
import java.util.Map;

/**
 * <h3>类映射关系表      </h3>
 */
public class RouterType {
	private static Map<String, String> routerMap = new HashMap<String, String>();

	/**
	 * 登陆
	 */
	public static final String K_INNER_LOGIN = "inner_login"; //
	/**
	 * 分享有礼
	 */
	public static final String K_INNER_SHARE = "inner_share_friends"; //
	/**
	 * 通用请求
	 */
	public static final String K_COMMON = "k_common"; //

	/**
	 * 初始化
	 */
	static {
		routerMap.put(K_INNER_LOGIN, "cn.ffcs.wisdom.city.LoginActivity");
		routerMap.put(K_INNER_SHARE, "cn.ffcs.wisdom.city.SMSShareActivity");
	}

	/**
	 * 返回对应key值的value
	 * @param key	
	 * 		数据key
	 * @return String 
	 * 		对应key值
	 */
	public static String getValue(String key) {
		return routerMap.get(key);
	}
}
