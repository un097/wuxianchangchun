package cn.ffcs.changchuntv.activity.personal;

import java.io.Serializable;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.changchuntv.R;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;
import cn.ffcs.external.share.view.CustomSocialShare;
import cn.ffcs.external.share.view.WeiBoSocialShare;
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;

/**
 * 
 * <p>
 * Title: 关于
 * </p>
 * <p>
 * Description: 检查更新 信息版本说明
 * </p>
 * <p>
 * 
 * @author: xzw
 *          </p>
 *          <p>
 *          Copyright: Copyright (c) 2012
 *          </p>
 *          <p>
 *          Company: FFCS Co., Ltd.
 *          </p>
 *          <p>
 *          Create Time: 2013-3-15
 *          </p>
 *          <p>
 *          Update Time:
 *          </p>
 *          <p>
 *          Updater:
 *          </p>
 *          <p>
 *          Update Comments:
 *          </p>
 */

public class CodeSharelActivity extends WisdomCityActivity implements
		OnClickListener {

	private TextView my_share_code;// 版本说明
	private Button share_button;// 版本说明
	CustomSocialShareEntity entity = new CustomSocialShareEntity();
	Bitmap bitmap;

	@Override
	protected void initComponents() {
		my_share_code = (TextView) findViewById(R.id.my_share_code);
		share_button = (Button) findViewById(R.id.share_button);
		share_button.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title,
				getString(R.string.code_share));
		entity.spreadCode = getIntent().getStringExtra("spreadCode");
		if (entity != null) {
			// entity = (CustomSocialShareEntity)serializable;
			my_share_code.setText("我的邀请码:" + entity.spreadCode);
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.code_share;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.share_button) {
//					entity.shareUrl = "http://t.cn/zWk3y6d?&code=" + entity.spreadCode;
			entity.shareUrl = "http://www.ccradio.cn/index/node_829.shtml?&code=" + entity.spreadCode;
//					 entity.shareUrl =
//					 "http://ccgd.153.cn:50081/icity-api-client-web/v7/infoConfCall/toInfoConfDetail?id=263749&clientType=changchuntv_ver";
					entity.shareTitle = "快来下载无线长春吧";
					entity.shareContent = getString(R.string.spread_string, entity.spreadCode);
//					 entity.shareContent = "@无线长春 新闻资讯不间断更新，助您随时随地了解天下事";
					
					bitmap = BitmapFactory.decodeResource(getResources(),
							R.drawable.ic_launcher);
					entity.imageBitmap = bitmap;
					entity.imageUrl = "http://ccgd.153.cn:50000/icity-wap/image/ic_launcher.png";
					CustomSocialShare.shareImagePlatform(CodeSharelActivity.this, entity, false);
					
				}
		}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (WeiBoSocialShare.mSsoHandler != null) {
			WeiBoSocialShare.mSsoHandler.authorizeCallBack(requestCode,
					resultCode, data);
		}
	}
}
