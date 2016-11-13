package cn.ffcs.wisdom.city.setting.about;

import android.content.Intent;
import android.net.Uri;
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
import cn.ffcs.ui.tools.AlertBaseHelper;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.city.web.BrowserActivity;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;

/**
 * 
 * <p>Title: 关于        </p>
 * <p>Description: 
 * 检查更新
 * 信息版本说明
 * </p>
 * <p>@author: xzw               </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-15             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */

public class FFAboutActivity extends WisdomCityActivity implements OnClickListener {

	private TextView version;// 版本说明
	private Button versionExplainBtn;// 版本说明
	private Button checkVersion;// 检查更新
	private Button free_statement;

	// 版本升级对话框控件
	private ProgressBar mBtnCheckVerStatusProgress; // 搜索状态提示
	private String downLoadUrl;// 下载地址
	private VersionCheckBo mVersionCheckBo;// 检查更新业务类
	
	private LinearLayout ffcsAbout;
	
	private TextView about;

	@Override
	protected void initComponents() {

		version = (TextView) findViewById(R.id.icity_version);

		versionExplainBtn = (Button) findViewById(R.id.version_dis);
		versionExplainBtn.setOnClickListener(this);
		versionExplainBtn.setOnLongClickListener(new MyOnLongClickListener());

		checkVersion = (Button) findViewById(R.id.check_version);
		checkVersion.setOnClickListener(this);
		checkVersion.setOnLongClickListener(new MyOnLongClickListener());
		
		ffcsAbout=(LinearLayout)findViewById(R.id.company_line);
		ffcsAbout.setOnLongClickListener(new MyOnLongClickListener());
		free_statement = (Button)findViewById(R.id.free_statement);
		free_statement.setOnClickListener(this);
		mBtnCheckVerStatusProgress = (ProgressBar) findViewById(R.id.check_version_status); // 查询过程进度条
//		Spanned sp = Html.fromHtml(
//				"<strong>交通路况：</strong>长春市各大交通路口实施路况视频随你看，高速公路实时路况播报，智能导航（妈妈再也不用担心我迷路了），个性化路线定制服务，违章查询方便实用；<br />"
//				+ "<strong>广播电视：</strong>长春广播电视台6套频率，5套频道实时收听收看，更多精彩不怕错过，往期回放一点就看；<br />"
//				+ "<strong>新闻资讯：</strong>每日新鲜资讯，网罗天下大事；<br />"
//				+ "<strong>便民热线：</strong>海量便民热线一键拨打<br />"
//				+ "<strong>爆料社区：</strong>发布您身边的新鲜事，还可以和大家一起讨论本地热点<br />"
//				+ "<strong>民意调查：</strong>长春大事您来定，官方发布调查信息，让城市建设听到您的意见<br />"
//				+ "<strong>天气预报：</strong>长春天气早知道，每日提供贴心天气服务<br />"
//				+ "<strong>商旅预定：</strong>出行一键搞定，车票、机票、酒店轻松预定<br />"
//				+ "<strong>美食旅游：</strong>最全面的长春旅游信息，美景、美食随你挑<br />"
//				+ "<strong>优惠团购：</strong>会员专享购物优惠，现金优惠券免费领");
		Spanned sp = Html.fromHtml(
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;无线长春由长春广播电视台、长春公安交警支队、中科院、中国电信等单位联合打造，由长春视听传媒有限公司负责运营。无线长春力争建设成为长春市信息发布的主平台、舆论宣传的主阵地、惠民服务的主力军,努力打造成本地乃至与全省移动互联网品牌媒体。<br />"
				+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;无线长春充分利用了本地的政府资源和媒体资源,可以提供：新闻资讯、政务发布、民意调查、路况详情、路况视频、交通违章查询、广播电视直播点播、旅游资讯、商旅预订、便民热线、天气、广告与电子优惠券等服务。在设计理念上,无线长春借鉴了国际发达国家和国内发达地区的先进经验,充分体现了“以人为本”的服务理念。无线长春的UI界面突出了当今最流行的“清新、简洁、美观”的特点。在用户体验上，强调“有用、易用”。<br />"
						+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;无线长春的“新闻资讯”,将提供24小时滚动新闻,重要信息实时推送;“路况详情”功能会通过红、绿、黄三种颜色显示全市的交通状况;“路况视频”可以通过交警的监控摄像头查看部分路口的车流变化;“违章查询”功能里,不仅可以查询车辆违法信息,还可以把信息主动推送给车主;“旅游资讯”里,有长春市最热门的景区景点介绍,还有本地特产、美食展示,将来还可以实现线上消费。<br />" +
				"&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;为了让APP功能更全，为了让用户的体验更好，我们将不断努力……<br />" + "联系电话：0431—88462250;13843000307<br />期待与您合作！");
		about = (TextView) findViewById(R.id.about);
		about.setText(sp);
	}
	
	public void calltelephone(String telephoneNumber) {
		Uri phoneUri = Uri.parse("tel:" + telephoneNumber);
		Intent phoneintent = new Intent(Intent.ACTION_DIAL, phoneUri);
		phoneintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(phoneintent);
	}
	
	class MyOnLongClickListener implements OnLongClickListener{
		@Override
		public boolean onLongClick(View v) {
			Intent intent = new Intent(mActivity, ICityAboutActivity.class);
			startActivity(intent);
			return true;
		}
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, getString(R.string.setting_about));
		String versNo = AppHelper.getVersionName(mContext);
		version.setText(mContext.getString(R.string.about_version, versNo));

		mVersionCheckBo = new VersionCheckBo(mContext, new checkVersionCall());
		
		
	}

	class checkVersionCall implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp resp) {
			hideCheckProgress();
			if (resp.isSuccess()) {
				VersionResp verResp = (VersionResp) resp.getObj();
				boolean needUpdate = verResp.getNeedupdate();

				if (!resp.isSuccess() || !needUpdate) { // 无需升级
					String newest = mActivity.getString(R.string.version_newest);
					CommonUtils.showToast(mActivity, newest, Toast.LENGTH_SHORT);
					return;
				}
				AlertBaseHelper.showConfirm(mActivity, getString(R.string.version_update),
						verResp.getUpdate_desc(), new UpdateVersion());
				downLoadUrl = verResp.getDownload_url();

			} else {
				CommonUtils.showToast(mActivity, getString(R.string.version_newest),
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
	protected int getMainContentViewId() {
		return R.layout.act_about1;
	}


	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.version_dis) {
			AlertBaseHelper.showMessage(mActivity, getString(R.string.icity_version_disp),
					getString(R.string.version_explan), new CancleClick());
		} else if (id == R.id.check_version) {
			showSearchProgress();
			mVersionCheckBo.check();
		}else if (id == R.id.free_statement) {
			Intent intent = new Intent(mActivity, BrowserActivity.class);
			intent.putExtra(Key.U_BROWSER_URL, "http://ccgd.153.cn:50000/icity-wap/disclaimer/index.html");
			intent.putExtra(Key.U_BROWSER_TITLE, mContext.getString(R.string.free_statement));
			intent.putExtra(Key.K_RETURN_TITLE, mContext.getString(R.string.home_name));
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mActivity.startActivity(intent);
		}

	}

	private class UpdateVersion implements OnClickListener {

		@Override
		public void onClick(View v) {
			Uri uri = Uri.parse(downLoadUrl);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
			AlertBaseHelper.dismissAlert(mActivity);
		}

	}

	private class CancleClick implements OnClickListener {

		@Override
		public void onClick(View v) {
			AlertBaseHelper.dismissAlert(mActivity);
		}

	}
	
	// 显示查询状态提示
	private void showSearchProgress() {
		mBtnCheckVerStatusProgress.setVisibility(View.VISIBLE);
		checkVersion.setText("");
		checkVersion.setClickable(false);
	}

	// 隐藏查询状态提示
	private void hideCheckProgress() {
		mBtnCheckVerStatusProgress.setVisibility(View.GONE);
		checkVersion.setText(getString(R.string.check_update));
		checkVersion.setClickable(true);
	}
}
