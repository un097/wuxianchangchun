package cn.ffcs.wisdom.city.modular.query;

import android.widget.TextView;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.modular.query.entity.QueryInfo;
import cn.ffcs.wisdom.city.modular.query.widget.QueryLayout;
import cn.ffcs.wisdom.city.v6.R;

/**
 * <p>Title:  查询类</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-10-24             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class QueryInfoActivity extends WisdomCityActivity {

	private TextView topTitle; //

	private QueryLayout mQueryLayout;

	private QueryInfo mQueryInfo;

//	private View mUserCenter;

	@Override
	protected void onResume() {
		super.onResume();

		isLogin();
	}

	@Override
	protected void initComponents() {
		mQueryLayout = (QueryLayout) findViewById(R.id.query_layout);
		mQueryLayout.setQueryInfoActivity(this);
//		mUserCenter = findViewById(R.id.top_right);

		topTitle = (TextView) findViewById(R.id.top_title);

		// mUserCenter.setBackgroundResource(R.drawable.btn_user_center);
		// mUserCenter.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// boolean mIsLogin =
		// Boolean.parseBoolean(SharedPreferencesUtil.getValue(mContext,
		// K.K_IS_LOGIN));
		// if (mIsLogin) {
		// Intent intent = new Intent(mContext, MyRelevanceActivity.class);
		// String paId = SharedPreferencesUtil.getValue(mContext, "paId");
		// intent.putExtra("paId", paId);
		// startActivity(intent);
		// QueryInfoActivity.this.finish();
		// } else {
		// Intent intent = new Intent(mContext, LoginActivity.class);
		// intent.putExtra("isPerson", true);
		// startActivity(intent);
		// }
		// }
		// });
	}

	@Override
	protected void initData() {

		String itemId = getIntent().getStringExtra("queryinfo_itemid");
		String cityCode = getIntent().getStringExtra("queryinfo_citycode");
		int keyGroupId = getIntent().getIntExtra("queryinfo_keyGroupId", -1);
		mQueryInfo = QueryInfoDataMgr.getInstance().getQueryInfo(cityCode, itemId);
		while (mQueryInfo == null) {
			try {
				Thread.sleep(500);
				mQueryInfo = QueryInfoDataMgr.getInstance().getQueryInfo(cityCode, itemId);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(mQueryInfo != null)
			topTitle.setText(mQueryInfo.getQueryTitle());
		mQueryLayout.init(mQueryInfo, keyGroupId);
	}

	// 判断是否登录
	public void isLogin() {
		// boolean mIsLogin =
		// Boolean.parseBoolean(SharedPreferencesUtil.getValue(mContext,
		// K.K_IS_LOGIN));
		// if (mIsLogin) {
		// mUserCenter.setBackgroundResource(R.drawable.person_center_head_p);
		// } else {
		// Drawable userCenterBg = SkinManager.getDrawable(mContext,
		// R.drawable.btn_user_center);
		// mUserCenter.setBackgroundDrawable(userCenterBg);
		// }
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_queryinfo;
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

}
