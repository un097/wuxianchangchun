package cn.ffcs.wisdom.city.home.fragment;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ffcs.wisdom.base.BaseFragment;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.LoginActivity;
import cn.ffcs.wisdom.city.personcenter.PersonCenterActivity;
import cn.ffcs.wisdom.city.personcenter.PersonCenterActivityNew;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account.AccountData;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:   用户信息fragment          </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-11-1           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class UserInfoFragment extends BaseFragment {

	private ImageView userHead;
	private TextView nickName;
	private TextView level;
	private LinearLayout userInfoLayout;
	private CityImageLoader loader;

	@Override
	public void initComponents(View view) {
		userInfoLayout = (LinearLayout) view.findViewById(R.id.login_user_info);
		userHead = (ImageView) view.findViewById(R.id.user_head);
		nickName = (TextView) view.findViewById(R.id.nick_name);
		level = (TextView) view.findViewById(R.id.level);
	}

	@Override
	public void initData() {
		refreshData();
	}
	
	/**
	 * 显示登录中
	 */
	public void showLogining(){
		userHead.setImageResource(R.drawable.home_userphoto);
		nickName.setText(R.string.home_logining);
		level.setText("");
		level.setVisibility(View.GONE);
	}
	
	/**
	 * 显示自动登录失败
	 */
	public void showErrorData(){
		unLogin();
	}

	/**
	 * 刷新数据
	 */
	public void refreshData() {
		loader = new CityImageLoader(mContext);
		boolean isLogin = AccountMgr.getInstance().isLogin(mContext);
		if (isLogin) {
			AccountData data = AccountMgr.getInstance().getAccount(mContext).getData();
			if (data != null) {
				String iconUrl = data.getIconUrl();
				if (!StringUtil.isEmpty(iconUrl)) {
					loader.setDefaultFailImage(R.drawable.home_userphoto);
					loader.setIsRealTimeShowImage(true);
					loader.loadUrl(userHead, iconUrl);
				} else {
					userHead.setImageResource(R.drawable.home_userphoto);
				}
				nickName.setText(data.getUserName());
				level.setText(data.getLevelName());
				level.setVisibility(View.VISIBLE);
				userInfoLayout.setOnClickListener(new ToPersonCenterClickListener());
			}
		} else {
			unLogin();
		}
	}

	/**
	 * 没登陆
	 */
	private void unLogin() {
		userHead.setImageResource(R.drawable.home_userphoto);
		nickName.setText(R.string.home_please_login);
		level.setText("");
		level.setVisibility(View.GONE);
		userInfoLayout.setOnClickListener(new ToLoginClickListener());
	}

	class ToPersonCenterClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// 福建省内用户跳转到新的个人中心界面
			Intent intent = new Intent(mContext, PersonCenterActivity.class);
			
			String provinceCode = MenuMgr.getInstance().getProvinceCode(mContext);
			if ("3500".equals(provinceCode)) {
				intent = new Intent(mContext, PersonCenterActivityNew.class);
			}
			
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.home_name));
			startActivity(intent);
		}
	}

	class ToLoginClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, LoginActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.home_name));
			startActivity(intent);
		}
	}

	@Override
	public int getMainContentViewId() {
		return R.layout.fragment_user_info;
	}
}
