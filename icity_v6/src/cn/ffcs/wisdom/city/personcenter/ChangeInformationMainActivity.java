package cn.ffcs.wisdom.city.personcenter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.bo.PersonCenterBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.SystemCallUtil;

/**
 * <p>Title:   修改资料主页面   </p>
 * <p>Description:                     </p>
 * <p>@author: yangchx                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-3-6            </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ChangeInformationMainActivity extends WisdomCityActivity {

	private LinearLayout changeNickName;
	private LinearLayout changePassword;
	private LinearLayout changeHead;
	private TextView username;
	private ProgressDialog progressDialog;
	private PersonCenterBo personCenterBo;
	private String paId;
	private ImageView headphoto;
	private CityImageLoader bitmaploader;
	private String iconurl;
	private int ispws = 40012;
	private int isuername = 40011;
	private int paw = 0;
	private String filePath;
	private String myusername = null;

	@Override
	protected void initComponents() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);// 竖屏
		changeNickName = (LinearLayout) findViewById(R.id.change_nickname);
		changePassword = (LinearLayout) findViewById(R.id.change_password);
		changeHead = (LinearLayout) findViewById(R.id.change_user_head);
		headphoto = (ImageView) this.findViewById(R.id.person_center_head_photo);
		username = (TextView) this.findViewById(R.id.change_username);
	}

	@Override
	protected void initData() {
		paId = String.valueOf(AccountMgr.getInstance().getAccount(mContext).getData().getId());
		TopUtil.updateTitle(this, R.id.top_title, R.string.person_center_change_information);
		iconurl = AccountMgr.getInstance().getAccount(mContext).getData().getIconUrl();
		myusername = AccountMgr.getInstance().getAccount(mContext).getData().getUserName();
		String mobile = AccountMgr.getInstance().getAccount(mContext).getData().getMobile();
		// 设置头像图片
		bitmaploader = new CityImageLoader(mContext);
		bitmaploader.setIsRealTimeShowImage(true);
		bitmaploader.setDefaultFailImage(R.drawable.info_userphoto);
		// 设置头像图片
		Bitmap bm = BitmapUtil.compressBitmapFromFile(Config.SDCARD_CITY_TMP + File.separator + mobile + ".jpg", 78, 78);
		if(bm != null) {
			headphoto.setImageBitmap(bm);
		} else {
			bitmaploader.loadUrl(headphoto, iconurl);
		}
		// 设置用户名
		if (myusername == null) {
			username.setText(mobile);
		} else {
			username.setText(myusername);
		}

		personCenterBo = new PersonCenterBo();
		changeNickName.setOnClickListener(new OnLayoutClickLinear());
		changePassword.setOnClickListener(new OnLayoutClickLinear());
		changeHead.setOnClickListener(new OnLayoutClickLinear());
	}

	class OnLayoutClickLinear implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();

			if (id == R.id.change_nickname) {
				Intent intent = new Intent(ChangeInformationMainActivity.this,
						ChangeUsernameActivity.class);
				intent.putExtra(Key.K_RETURN_TITLE,
						getString(R.string.person_center_change_information));
				startActivityForResult(intent, isuername);
			} else if (id == R.id.change_password) {
				Intent intent = new Intent(ChangeInformationMainActivity.this,
						ChangePasswordActivity.class);
				intent.putExtra(Key.K_RETURN_TITLE,
						getString(R.string.person_center_change_information));
				startActivityForResult(intent, ispws);
			} else if (id == R.id.change_user_head) {
				SystemCallUtil.showSelect(ChangeInformationMainActivity.this, Config.SDCARD_CITY_TMP);
			}

		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_change_information_main;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

	@Override
	public void finish() {
		if (paw != 0) {
			setResult(paw);
		} else {
			setResult(RESULT_OK);
		}
		super.finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SystemCallUtil.REQUESTCODE_PHOTOALBUM && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			SystemCallUtil.ImageCut(this, uri, 200, 200);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_CAMERA && resultCode == RESULT_OK) {
			SystemCallUtil.ImageCut(ChangeInformationMainActivity.this, SystemCallUtil.IMAGE_URI,
					200, 200);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_IMAGECUT && resultCode == RESULT_OK) {
			showProgressBar(getString(R.string.person_center_up_head));
			Bitmap bitmap = data.getParcelableExtra("data");
			String mobile = AccountMgr.getInstance().getAccount(mContext).getData().getMobile();
			SdCardTool.save(bitmap, Config.SDCARD_CITY_TMP, File.separator + mobile + ".jpg");
			String load = Config.SDCARD_CITY_TMP + File.separator + mobile + ".jpg";
			personCenterBo.imageUpLoad(new fileUpLoadCallBack(), load, MenuMgr.getInstance()
					.getCityCode(mContext));
		} else if (requestCode == ispws) {
			paw = resultCode;
		} else if (requestCode == isuername && resultCode == RESULT_OK) {
			myusername = data.getStringExtra("username");
			if (data.getStringExtra("username") != null) {
				username.setText(myusername);
			}
		}
	}

	/**
	 * 文件上传回调
	 */
	class fileUpLoadCallBack implements HttpCallBack<UpLoadImageResp> {

		@Override
		public void call(UpLoadImageResp response) {
			try {
				isnetwork();
				if (response.isSuccess()) {
					filePath = response.getList().get(0).getFilePath();
					Map<String, String> map = new HashMap<String, String>();
					map.put("paId", paId);
					map.put("iconUrl", filePath);
					personCenterBo
							.updataUserInformation(new UpDataUserInformation(), mContext, map);
					hideProgressBar();
				}
			} catch (Exception e) {
				hideProgressBar();
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

		}
	}

	/**
	 * 修改用户头像回调
	 */
	class UpDataUserInformation implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			isnetwork();
			hideProgressBar();
			if (response.isSuccess()) {
				// 设置头像图片
				String mobile = AccountMgr.getInstance().getAccount(mContext).getData().getMobile();
				Bitmap bm = BitmapUtil.compressBitmapFromFile(Config.SDCARD_CITY_TMP + File.separator + mobile + ".jpg", 35, 35);
				if(bm != null) {
					headphoto.setImageBitmap(bm);
				} else {
					bitmaploader.loadUrl(headphoto, filePath);
				}
				
				CommonUtils.showToast(mActivity, R.string.change_susseed, Toast.LENGTH_SHORT);
			} else {
				CommonUtils.showToast(mActivity, R.string.change_fail, Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {

		}

		@Override
		public void onNetWorkError() {

		}
	}
	
	public void showProgressBar(String message) {
		if (null == progressDialog) {
			progressDialog = new ProgressDialog(this);
		}
		progressDialog.setMessage(message);
		progressDialog.show();
	}

	public void hideProgressBar() {
		if (progressDialog != null) {
			progressDialog.dismiss();
			progressDialog = null;
		}
	}

	public void isnetwork() {
		if (!CommonUtils.isNetConnectionAvailable(mContext)) {
			CommonUtils.showToast(mActivity, R.string.net_error, Toast.LENGTH_SHORT);
			hideProgressBar();
			return;
		}
	}
}
