package cn.ffcs.wisdom.city.personcenter;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.entity.UpLoadImage;
import cn.ffcs.wisdom.city.personcenter.bo.PersonCenterBo;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.ManifestUtil;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.StringUtil;
import cn.ffcs.wisdom.tools.SystemCallUtil;

/**
 * <p>Title: 完善个人资料         </p>
 * <p>Description: 
 * 个人头像
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-26             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class PersonInfoActivity extends WisdomCityActivity {
	private ImageView mHeadImg;// 头像图片
	private Button mSelectPhoto;// 选择图片
	private ImageView mUploadPhoto;// 上传图片
	private Button mGoInto;// 直接进入爱城市
	private TextView mTips;// 头部信息
	private Bitmap mHeadBitMap = null;

	// 拍照保存路径
	public static String FILEPATH;
	public static String FILENAME;
	private PersonCenterBo mPersonCenterBo;
	private int imgSize = 180;
	private Account mAccount;
	private String userName;

	private void showProgress(String msg) {
		LoadingDialog.getDialog(mActivity).setMessage(msg).show();
	}

	private void dismissProgress() {
		LoadingDialog.getDialog(mActivity).cancel();
	}

	@Override
	protected void initComponents() {
		mSelectPhoto = (Button) findViewById(R.id.add_headphoto);
		mSelectPhoto.setOnClickListener(new OnSelectPhotoListener());
		mUploadPhoto = (ImageView) findViewById(R.id.top_right);
		mUploadPhoto.setOnClickListener(new OnUploadPhotoListener());
		mHeadImg = (ImageView) findViewById(R.id.user_headphoto);
		mGoInto = (Button) findViewById(R.id.gointo_icity);
		mGoInto.setOnClickListener(new OnGoIntoICity());
		mTips = (TextView) findViewById(R.id.user_info);
	}

	class OnGoIntoICity implements OnClickListener {

		@Override
		public void onClick(View v) {
			finish();
		}
	}

	class OnSelectPhotoListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 从相册中选择图片，或者直接拍照
			SystemCallUtil.showSelect(mActivity, FILEPATH);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SystemCallUtil.REQUESTCODE_IMAGECUT && resultCode == RESULT_OK) {
			mHeadBitMap = data.getParcelableExtra("data");
			SdCardTool.save(mHeadBitMap, FILEPATH, FILENAME);
			mHeadImg.setImageBitmap(mHeadBitMap);
			
		} else if (requestCode == SystemCallUtil.REQUESTCODE_PHOTOALBUM && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			SystemCallUtil.ImageCut(mActivity, uri, imgSize, imgSize);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_CAMERA && resultCode == RESULT_OK) {
			SystemCallUtil.ImageCut(mActivity, SystemCallUtil.IMAGE_URI, imgSize, imgSize);
		}
	}

	class OnUploadPhotoListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mHeadBitMap == null) {
				CommonUtils.showToast(mActivity, getString(R.string.personinfo_img_empty),
						Toast.LENGTH_SHORT);
				return;
			}
			showProgress(getString(R.string.personinfo_uploding));
//			mPersonCenterBo.imageUpLoad(new FileUpLoadCallBack(), FILEPATH + FILENAME, MenuMgr
//					.getInstance().getCityCode(mContext));
			mPersonCenterBo.imageUpLoad(new FileUpLoadCallBack(), FILEPATH + File.separator + FILENAME, MenuMgr
					.getInstance().getCityCode(mContext));
		}

	}

	class FileUpLoadCallBack implements HttpCallBack<UpLoadImageResp> {

		@Override
		public void call(UpLoadImageResp response) {
			try {
				if (response.isSuccess()) {
					List<UpLoadImage> list = response.getList();
					if (list != null) {
						UpLoadImage image = list.get(0);
						if (image != null) {
							String filePath = image.getFilePath();
							Map<String, String> map = new HashMap<String, String>();
							int userId = AccountMgr.getInstance().getUserId(mContext);
							map.put("paId", String.valueOf(userId));
							map.put("iconUrl", filePath);
							
							String imsi = AppHelper.getMobileIMSI(mContext);
							String imei = AppHelper.getIMEI(mContext);
							map.put("imsi", imsi);
							map.put("imei", imei);
							map.put("client_version", AppHelper.getVersionCode(mContext) + "");
							map.put("client_channel_type", ManifestUtil.readMetaData(mContext, "UMENG_CHANNEL"));
							// 刷新头像地址
							Account account = AccountMgr.getInstance().getAccount(mContext);
							if (account != null) {
								if (account.getData() != null) {
									account.getData().setIconUrl(filePath);
								}
							}
							AccountMgr.getInstance().refresh(mContext, account);
							mPersonCenterBo.updataUserInformation(new UpDataUserInformation(),
									mContext, map);
						}
					}
				} else {
					dismissProgress();
					CommonUtils.showToast(mActivity, R.string.personinfo_upload_fail,
							Toast.LENGTH_SHORT);
				}
			} catch (Exception e) {
				dismissProgress();
				Log.e("Exception: " + e);
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
	 * 修改用户信息回调
	 */
	class UpDataUserInformation implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			dismissProgress();
			if (response.isSuccess()) {
				if ("0".equals(response.getStatus())) {
					Intent i = new Intent();
					setResult(RESULT_OK, i);
					finish();
				} else {
					CommonUtils.showToast(mActivity, R.string.personinfo_update_fail,
							Toast.LENGTH_SHORT);
				}
			} else {
				CommonUtils.showToast(mActivity, R.string.personinfo_update_fail,
						Toast.LENGTH_SHORT);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	@Override
	protected void initData() {
		setReturnBtnEnable(false);
		FILEPATH = Config.SDCARD_CITY_TMP;
		FILENAME = getString(R.string.user_headphoto_name);// 选择图片保存名字
		String mobile = AccountMgr.getInstance().getAccount(mContext).getData().getMobile();
		FILENAME = mobile + ".jpg";
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.personinfo_fill_info);
		TopUtil.updateRight(mActivity, R.id.top_right, R.drawable.user_register_btn);
		mAccount = AccountMgr.getInstance().getAccount(mContext);
		if (mAccount != null) {
			if (mAccount.getData() != null) {
				userName = mAccount.getData().getUserName();
				if (!StringUtil.isEmpty(userName)) {
					userName = userName + ",请您添加头像";
					mTips.setText(userName);
				}
			}
		}
		mPersonCenterBo = new PersonCenterBo();
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_personinfo;
	}
}
