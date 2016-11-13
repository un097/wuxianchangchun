package cn.ffcs.surfingscene.common;

import android.content.Context;
import cn.ffcs.config.BaseConfig;
import cn.ffcs.surfingscene.R;
import cn.ffcs.wisdom.tools.SdCardTool;

public class Config extends BaseConfig{

	public static String TYPE_COMPE_RECOMMEND = "1022";// 精品四条数据
	public static String TYPE_COMP_LIST = "1021";// 精品列表
	public static String TYPE_CITY = "1001";// 当前城市
	public static String TYPE_GROUP_LIST_VIDEO = "1031";// 全部视频分组列表

	public static String METHOD_BANNER_ACTION_LIST = "/action/listBest";// 广告列表
	public static String METHOD_CITY_EYELIST = "/action/listNormalAndGeyes";// 景点列表方法
	public static String METHOD_CITY_HOT = "/area/listOfInterface";// 热门城市
	public static String METHOD_CITY_PROVINCE = "/area/listOfProvince";// 省份列表
	public static String METHOD_CITY_PRO_CITY = "/area/listOfCity";// 省份下的城市列表
	public static String UPLOAD_IMAGE="uploadimageandsave.action";// 图片上传

	public static String PHOTO_SHARE_ACTIVITY_SUBJECT="activitySubject";//专题随手拍的编号
	/**
	 * 获取图片缓存地址
	 * @param context
	 * @return
	 */
	public static String getSdcardImageRoot(Context context) {
		String sdcardRoot = SdCardTool.getSdcardDir() + context.getString(R.string.glo_sdcard_root);
		return sdcardRoot + context.getString(R.string.glo_img_cache);
	}
}
