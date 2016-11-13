package cn.ffcs.config;

import java.io.File;

public class BaseConfig {
//	public static final String URL_SERVER_IP = "www.153.cn"; // 正式
//	public static final String URL_SERVER_PORT = "8081"; // 正式
//	public static final boolean SURFINGSCENE_ISOFFICIAL = true;// 天翼景象正式环境
//	public static final String URL_IMAGE_SERVER_ROOT = "pic.153.cn";// 图片服务器根地址
//	public static final String URL_RESOURCE_SERVER_PORT = "8081";// 图片资源服务器端口
//	public static final String URL_UP_LOAD_IMAGE_PORT = "8082";// 图片上传端口
	
	// 长春
	public static final String URL_SERVER_IP = "ccgd.153.cn"; // 正式
	public static final String URL_SERVER_PORT = "50081"; // 正式
	public static final boolean SURFINGSCENE_ISOFFICIAL = true;// 天翼景象正式环境
	public static final String URL_IMAGE_SERVER_ROOT = "ccgd-pic.153.cn";// 图片服务器根地址
	public static final String URL_RESOURCE_SERVER_PORT = "51181";// 图片资源服务器端口
	public static final String URL_UP_LOAD_IMAGE_PORT = "50082";// 图片上传端口
	public static final String URL_IMAGE_DOWNLOAD = "http://ccgd-pic.153.cn:51181/";
//	public static final String URL_SERVER_IP = "218.5.99.35"; // 测试
//	public static final String URL_SERVER_PORT = "8083"; // 测试
//	public static final boolean SURFINGSCENE_ISOFFICIAL = false;// 天翼景象测试环境
//	public static final String URL_RESOURCE_SERVER_PORT = URL_SERVER_PORT;// 图片资源服务器端口

//	public static final String URL_SERVER_IP = "prodics.153.cn"; // 外部地址映射
//	public static final String URL_SERVER_PORT = "8903"; // 外部地址映射
//	public static final String URL_IMAGE_SERVER_ROOT = URL_SERVER_IP;// 图片服务器根地址
//	public static final boolean SURFINGSCENE_ISOFFICIAL = false;// 天翼景象测试环境
//	public static final String URL_RESOURCE_SERVER_PORT = "8908";// 图片资源服务器端口
//	public static final String URL_UP_LOAD_IMAGE_PORT = "8904";// 图片上传端口

	public static String URL_PROJECT_DIR = "exter.shtml?";
	public static String URL_BASELINE = "400"; // 基线版本号

	/**
	 * <!-- 百度地图2.3新key -->
	 * 百度地图测试用到的key，用debug.keystore打包的key,
	 * 每个人开发工具的debug.keystore不一样，自己要用时到http://lbsyun.baidu.com/apiconsole/key申请测试
	 */
//	public static String baidu_map_new_key_icity = "C6765b75c2ca52f1cf631154fd1ecdae";
	
	
	/**
	 * <!-- 百度地图2.3新key -->
	 * 正式发布打包时用到的key，用ffcs_public.keystore打包的key
	 */
	public static String baidu_map_new_key_icity = "XIG5ilLShMFnByQBlnnBUlpu";
////	public static String baidu_map_new_key_icity = "zQCyqeYVyz3NQnqyE0DCLOnl";
	
	
	//fmj
//	public static String baidu_map_new_key_icity = "kYT4MdETA8bGVHtR0rx45jQQ";
	
	

	/**
	 * 获取服务器根目录URL
	 */
	public static final String GET_SERVER_ROOT_URL() {
		return "http://" + URL_SERVER_IP + ":" + URL_SERVER_PORT + "/";
	}

	/**
	 * 获取图片根目录URL
	 */
	public static String GET_IMAGE_ROOT_URL() {
		return "http://" + URL_IMAGE_SERVER_ROOT + ":" + URL_RESOURCE_SERVER_PORT + File.separator;
	}

	/**
	 * 上传图片接口根目录
	 * @return
	 */
	public static String GET_UP_LOAD_IMAGE_ROOT_URL() {
		return "http://" + URL_SERVER_IP + ":" + URL_UP_LOAD_IMAGE_PORT + File.separator;
	}

	/**
	 * 旧版父接口
	 */
	public static final String GET_HOME_URL() {
		return GET_SERVER_ROOT_URL() + URL_PROJECT_DIR + "baseLine=" + URL_BASELINE + "&";
	}
}
