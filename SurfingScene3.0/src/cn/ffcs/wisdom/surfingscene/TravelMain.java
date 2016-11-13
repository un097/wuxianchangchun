package cn.ffcs.wisdom.surfingscene;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.surfingscene.advert.AdvertBo;
import cn.ffcs.surfingscene.advert.AdvertMgr;
import cn.ffcs.surfingscene.common.Key;

/**
 * <p>Title: 旅游视频中转器        </p>
 * <p>Description: 
 *  旅游视频中转器 ，仅限旅游、交通
 * </p>
 * <p>@author: Eric.wsd                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-7-5             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TravelMain extends Activity {

	@Override
	public void onCreate(Bundle data) {
		super.onCreate(data);
		Intent intent = getIntent();
		String cityName = intent.getStringExtra(ExternalKey.K_CITY_NAME);
		String areaCode = intent.getStringExtra(ExternalKey.K_AREA_CODE);
		String cityCode = intent.getStringExtra(ExternalKey.K_CITYCODE);
		String typeCode = intent.getStringExtra(ExternalKey.K_GLO_TYPE);
		String title = intent.getStringExtra(ExternalKey.K_MENUNAME);
		String phone = intent.getStringExtra(ExternalKey.K_PHONENUMBER);
		// add by caijj 天翼景象广告
		AdvertMgr.getInstance().saveTyjxCode(this, areaCode);
		AdvertMgr.getInstance().saveCityCode(this, cityCode);
		String adverImage = AdvertMgr.getInstance().getAdvertImg(this, areaCode);
		AdvertBo bo = new AdvertBo();
		bo.loadAdvertImage(this, adverImage);
		
		intent.setClassName(TravelMain.this, "cn.ffcs.external.tourism.activity.TourismMainActivity");
		intent.putExtra(Key.K_AREA_CODE, areaCode);
		intent.putExtra(Key.K_AREA_NAME, cityName);
		intent.putExtra(Key.K_GLO_TYPE, typeCode);
		intent.putExtra(Key.K_TITLE_NAME, title);
		intent.putExtra(Key.K_PHONE_NUMBER, phone);
		intent.putExtra(Key.K_CITY_CODE, cityCode);
		startActivity(intent);
		finish();
	}
}
