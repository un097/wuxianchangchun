package cn.ffcs.wisdom.city.setting;

import java.io.File;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.SystemCallUtil;

public class HomeBgSettingActivity extends WisdomCityActivity {

	private RelativeLayout localPic;
	private RelativeLayout camera;
	private RelativeLayout defaultPic;
	private String homeBgFileName = "home_bg";
	private String imageDir = Config.SDCARD_HOME_BG;

	@Override
	protected void initComponents() {
		localPic = (RelativeLayout) findViewById(R.id.local_pic);
		camera = (RelativeLayout) findViewById(R.id.camera);
		defaultPic = (RelativeLayout) findViewById(R.id.default_pic);

		localPic.setOnClickListener(new OnLayoutClickListener());
		camera.setOnClickListener(new OnLayoutClickListener());
		defaultPic.setOnClickListener(new OnLayoutClickListener());
	}

	class OnLayoutClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.local_pic) {
				SystemCallUtil.photoAlbum(mActivity);
			} else if (id == R.id.camera) {
				SystemCallUtil.camera(mActivity, imageDir);
			} else if (id == R.id.default_pic) {
				SharedPreferencesUtil.setValue(mContext, Key.K_HOME_BG, "");
				notifyAndToast();
			}
		}
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.setting_change_bg);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_home_background_setting;
	}

	private void cutImage(Uri cutImage, Uri saveImage) {
		SystemCallUtil.ImageCut(mActivity, cutImage, saveImage,
				AppHelper.getScreenWidth(mContext) * 2 / 3,
				AppHelper.getScreenHeight(mContext) * 2 / 3);
	}

	private void notifyAndToast() {
		notifyBgChange();
		CommonUtils.showToast(mActivity, R.string.setting_change_succes, Toast.LENGTH_SHORT);
	}

	private void notifyBgChange() {
		Intent i = new Intent();
		i.setAction("homeBgChange");
		LocalBroadcastManager.getInstance(mContext).sendBroadcast(i);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
			case SystemCallUtil.REQUESTCODE_CAMERA:
				cutImage(SystemCallUtil.IMAGE_URI, Uri.fromFile(new File(imageDir, homeBgFileName)));
				break;
			case SystemCallUtil.REQUESTCODE_PHOTOALBUM:
				Uri uri = data.getData();
				cutImage(uri, Uri.fromFile(new File(imageDir, homeBgFileName)));
				break;
			case SystemCallUtil.REQUESTCODE_IMAGECUT:
				String path = imageDir + homeBgFileName;
				SharedPreferencesUtil.setValue(mContext, Key.K_HOME_BG, path);
				notifyAndToast();
				break;
			default:
				break;
			}
		}
	}
}
