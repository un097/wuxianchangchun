package cn.ffcs.wisdom.surfingscene;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.surfingscene.advert.AdvertBo;
import cn.ffcs.surfingscene.advert.AdvertMgr;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.road.RoadMainActivity;

/**
 * <p>Title: 路况兼容跳转类                          </p>
 * <p>Description:                     </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-6-26           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class TrafficsMain extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i = getIntent();
		String areaCode = i.getStringExtra(ExternalKey.K_AREA_CODE);
		String cityCode = i.getStringExtra(ExternalKey.K_CITYCODE);
		String areaName = i.getStringExtra(ExternalKey.K_CITY_NAME);
		String gloType = i.getStringExtra(ExternalKey.K_GLO_TYPE);
		String phone = i.getStringExtra(ExternalKey.K_PHONENUMBER);
		String title = getIntent().getStringExtra(ExternalKey.K_MENUNAME);
		// add by caijj 天翼景象广告
		AdvertMgr.getInstance().saveTyjxCode(this, areaCode);
		AdvertMgr.getInstance().saveCityCode(this, cityCode);
		String adverImage = AdvertMgr.getInstance().getAdvertImg(this, areaCode);
		AdvertBo bo = new AdvertBo();
		bo.loadAdvertImage(this, adverImage);

		i.setClass(TrafficsMain.this, RoadMainActivity.class);
		i.putExtra(Key.K_AREA_CODE, areaCode);
		i.putExtra(Key.K_AREA_NAME, areaName);
		i.putExtra(Key.K_GLO_TYPE, gloType);
		i.putExtra(Key.K_TITLE_NAME, title);
		i.putExtra(Key.K_PHONE_NUMBER, phone);
		startActivity(i);
		finish();
	}
}
