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
 * <p>Title:    交通诱导屏                  </p>
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
public class TrafficsMain130 extends Activity {

	@Override
	public void onCreate(Bundle data) {
		super.onCreate(data);
		Intent i = getIntent();
		String areaCode = i.getStringExtra(ExternalKey.K_AREA_CODE);
		String cityCode = i.getStringExtra(ExternalKey.K_CITYCODE);
		String areaName = i.getStringExtra(ExternalKey.K_CITY_NAME);
		String title = getIntent().getStringExtra(ExternalKey.K_MENUNAME);
		String phone = i.getStringExtra(ExternalKey.K_PHONENUMBER);		
		// add by caijj 天翼景象广告
		AdvertMgr.getInstance().saveTyjxCode(this, areaCode);
		AdvertMgr.getInstance().saveCityCode(this, cityCode);
		String adverImage = AdvertMgr.getInstance().getAdvertImg(this, areaCode);
		AdvertBo bo = new AdvertBo();
		bo.loadAdvertImage(this, adverImage);

		i.setClass(TrafficsMain130.this, RoadMainActivity.class);
		i.putExtra(Key.K_AREA_CODE, areaCode);
		i.putExtra(Key.K_AREA_NAME, areaName);
		i.putExtra(Key.K_GLO_TYPE, "1002");// 1000 路况, 1001景点, 1002 诱导屏
		i.putExtra(Key.K_TITLE_NAME, title);
		i.putExtra(Key.K_PHONE_NUMBER, phone);
		startActivity(i);
		this.finish();
	}
}
