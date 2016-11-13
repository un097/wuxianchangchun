package cn.ffcs.wisdom.surfingscene;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.surfingscene.advert.AdvertBo;
import cn.ffcs.surfingscene.advert.AdvertMgr;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.road.RoadMainActivity;

import com.ffcs.android.api.internal.util.StringUtils;

/**
 * <p>Title: 天翼景象视频通用类  </p>
 * <p>Description: 
 *  1. 可在后台配置视频类型
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-7-5             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class GlobalEyeMain extends Activity {

	private String cityName;
	private String areaCode;
	private String typeCode; // 类型编码
	private String title;// 标题
	private String phone;// 手机号

	@Override
	public void onCreate(Bundle data) {
		super.onCreate(data);
		Intent intent = getIntent();
		cityName = intent.getStringExtra(ExternalKey.K_CITY_NAME);
		areaCode = intent.getStringExtra(ExternalKey.K_AREA_CODE);
		String cityCode = intent.getStringExtra(ExternalKey.K_CITYCODE);
		typeCode = intent.getStringExtra(ExternalKey.K_GLO_TYPE);
		phone = intent.getStringExtra(ExternalKey.K_PHONENUMBER);
		title = intent.getStringExtra(ExternalKey.K_MENUNAME);		
		// add by caijj 天翼景象广告
		AdvertMgr.getInstance().saveTyjxCode(this, areaCode);
		AdvertMgr.getInstance().saveCityCode(this, cityCode);
		String adverImage = AdvertMgr.getInstance().getAdvertImg(this, areaCode);
		AdvertBo bo = new AdvertBo();
		bo.loadAdvertImage(this, adverImage);

		intent.setClass(GlobalEyeMain.this, RoadMainActivity.class);
		Bundle params = new Bundle();
		if (!StringUtils.isEmpty(typeCode)) {
			params.putString(Key.K_GLO_TYPE, typeCode); // 视频类型
		}
		params.putString(Key.K_AREA_CODE, areaCode); // "所在城市区域编码"
		params.putString(Key.K_AREA_NAME, cityName);
		params.putString(Key.K_TITLE_NAME, title);
		params.putString(Key.K_PHONE_NUMBER, phone);
		intent.putExtras(params);
		startActivity(intent);
		finish();
	}
}
