package cn.ffcs.external.trafficbroadcast.activity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.external.trafficbroadcast.adapter.DetailPicAdapter;
import cn.ffcs.external.trafficbroadcast.adapter.TrafficReviewAdapter;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_Comment_Bo;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_ItemDetail_Bo;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_Praise_Bo;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_ReviewList_Bo;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_SensitiveWord_Bo;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_Comment_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_Detail_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_ItemDetail_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_Praise_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_ReviewItem_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_ReviewList_Entity;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_SensitiveWord_Entity;
import cn.ffcs.external.trafficbroadcast.view.MyGridView;
import cn.ffcs.external.trafficbroadcast.view.MyListView;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

import com.example.external_trafficbroadcast.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 路况详情界面
 * 
 * @author daizhq
 * 
 * @date 2014.12.01
 * */
public class TrafficDetailActivity extends Activity implements OnClickListener {

	private String road_id = null;
	
	private ScrollView sv_detail;

	//标题
	private TextView textview_title;
	
	// 返回键
	private LinearLayout ll_back;
	// 手机号
	private TextView tv_mobile;
	// 发布时间
	private TextView tv_time;
	// 详情
	private TextView tv_detail;
	// 路况背景图
	private ImageView iv_icon_bg;
	// 标题
	private TextView tv_title;
	// 评论数量显示区域
	private LinearLayout ll_comment_num;
	// 评论数
	private TextView tv_review_num;
	// 发布评论区域
	private RelativeLayout rl_comment;
	// 评论输入框
	private EditText et_comment;
	// 发布按钮
	private Button btn_comment;
	// 点赞区域
	private LinearLayout ll_praise;
	// 点赞数
	private TextView tv_praise_num;
	// 分享区域
	private LinearLayout ll_share;
	// 分享数
	private TextView tv_share_num;

	private Traffic_Detail_Entity detailEntity = null;
	private Traffic_ItemDetail_Entity itemDetailEntity = null;

	// 路况图片
	private MyGridView gv_pic;
	String[] pic_url = new String[6];
	private DetailPicAdapter adapter_pic;

	// 评论列表
	private MyListView lv_review;
	private List<Traffic_ReviewItem_Entity> list_review = new LinkedList<Traffic_ReviewItem_Entity>();
	private TrafficReviewAdapter adapter_review;

	// 当前的页数 默认第1页
	private int page_no = 1;
	// 1页的个数 默认10页
	private int page_size = 50;

	private int praiseNum = 0;

	// 此变量用来标记是否需要请求评论列表，（点赞不需要再次请求评论列表）
	private boolean isRequese = true;

	// 评论类型
	// 1：资讯评论 2：路况评论
	String type = "2";

	Account account = null;
	String user_id = "";
	String mobile = "";
	String lat = "";
	String lng = "";
	String sign = "";
	String imei = "";

	private String title = "";

	private Traffic_ItemDetail_Bo detailBo = null;
	private Traffic_ReviewList_Bo reviewListBo = null;
	private Traffic_Comment_Bo commentBo = null;
	private Traffic_Praise_Bo praiseBo = null;
	private Traffic_SensitiveWord_Bo sensitiveWordBo = null;

	boolean isLogin;// 判断是否登录

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_traffic_detail);

		loadView();
		loadData();
	}

	/**
	 * 加载本地控件
	 * */
	private void loadView() {
		// TODO Auto-generated method stub
		textview_title = (TextView)findViewById(R.id.textview_title);
		
		sv_detail = (ScrollView) findViewById(R.id.sv_detail);
		sv_detail.smoothScrollTo(0, 20);

		ll_back = (LinearLayout) findViewById(R.id.ll_back);

		tv_mobile = (TextView) findViewById(R.id.tv_mobile);
		tv_time = (TextView) findViewById(R.id.tv_time);
		tv_detail = (TextView) findViewById(R.id.tv_detail);

		iv_icon_bg = (ImageView) findViewById(R.id.iv_icon_bg);

		tv_title = (TextView) findViewById(R.id.tv_title);

		tv_review_num = (TextView) findViewById(R.id.tv_review_num);
		tv_praise_num = (TextView) findViewById(R.id.tv_praise_num);
		ll_share = (LinearLayout) findViewById(R.id.ll_share);
		tv_share_num = (TextView) findViewById(R.id.tv_share_num);

		ll_comment_num = (LinearLayout) findViewById(R.id.ll_comment_num);
		rl_comment = (RelativeLayout) findViewById(R.id.rl_comment);
		btn_comment = (Button) findViewById(R.id.btn_comment);

		ll_praise = (LinearLayout) findViewById(R.id.ll_praise);

		gv_pic = (MyGridView) findViewById(R.id.gv_pic);
		lv_review = (MyListView) findViewById(R.id.lv_review);
		lv_review.setFocusable(false);

		et_comment = (EditText) findViewById(R.id.et_comment);

		ll_back.setOnClickListener(this);
		ll_share.setOnClickListener(this);
		ll_comment_num.setOnClickListener(this);
		ll_praise.setOnClickListener(this);
		btn_comment.setOnClickListener(this);
		gv_pic.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				// TODO Auto-generated method stub
				String selectPath = pic_url[position];
				String title = itemDetailEntity.getTitle();
				if (selectPath != null && selectPath.length() > 0) {
					Intent intent = new Intent();
					intent.setClass(TrafficDetailActivity.this, TrafficPhotoPreviewActivity.class);
					intent.putExtra("path", selectPath);
					intent.putExtra("title", title);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 加载页面数据以及监听
	 * */
	private void loadData() {
		// TODO Auto-generated method stub

		showProgressBar("正在加载详情数据...");
		loadDetail();
	}

	/**
	 * 加载详情
	 * */
	private void loadDetail() {
		road_id = getIntent().getStringExtra("road_id");
		System.out.println("接收到的id======>>" + road_id);

		Map<String, Object> paramsDetail = new HashMap<String, Object>(1);

		account = AccountMgr.getInstance().getAccount(TrafficDetailActivity.this);
		user_id = String.valueOf(account.getData().getUserId());
		// user_id = "7623773";
		mobile = account.getData().getMobile();
		imei = AppHelper.getIMEI(TrafficDetailActivity.this);

		lat = LocationUtil.getLatitude(TrafficDetailActivity.this);
		lng = LocationUtil.getLongitude(TrafficDetailActivity.this);
		sign = road_id;

		// String cityCode = MenuMgr.getInstance().getCityCode(mContext);

		if (lat == null || lat.equals("")) {
			lat = "unknown";
		}
		if (lng == null || lng.equals("")) {
			lng = "unknown";
		}
		if (mobile == null || mobile.equals("")) {
			mobile = "unknown";
		}

		paramsDetail.put("city_code", "2201");
		paramsDetail.put("org_code", "2201");
		paramsDetail.put("mobile", mobile);
		paramsDetail.put("longitude", lng);
		paramsDetail.put("latitude", lat);
		paramsDetail.put("sign", sign);
		paramsDetail.put("road_id", Integer.valueOf(road_id));

		detailBo = new Traffic_ItemDetail_Bo(TrafficDetailActivity.this);
		detailBo.startRequestTask(new getDetailCallBack(), TrafficDetailActivity.this,
				paramsDetail,
				"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/lbs/road/getRoadDetail");
	}

	/**
	 * 获取路况详情回调
	 * */
	class getDetailCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("获取详情返回====>>" + response.getHttpResult());
			if (response.isSuccess()) {
				detailEntity = (Traffic_Detail_Entity) response.getObj();
				itemDetailEntity = detailEntity.getData();

				tv_mobile.setText(itemDetailEntity.getMobile());
				tv_time.setText(itemDetailEntity.getUpdate_time());
				tv_detail.setText(itemDetailEntity.getDetail());
				praiseNum = itemDetailEntity.getPraise_num();
				tv_praise_num.setText(String.valueOf(praiseNum));
				title = itemDetailEntity.getTitle();
				tv_title.setText(title);
				textview_title.setText(itemDetailEntity.getTitle());
				if (itemDetailEntity.getStatus() == 1) {// 顺畅
					iv_icon_bg.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.iv_shunchang_bg));
				} else if (itemDetailEntity.getStatus() == 2) {// 缓慢
					iv_icon_bg.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.iv_huanman_bg));
				} else if (itemDetailEntity.getStatus() == 3) {// 道路封闭
					iv_icon_bg.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.iv_daolufengbi_bg));
				} else if (itemDetailEntity.getStatus() == 4) {// 拥堵
					iv_icon_bg.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.iv_yongdu_bg));
				} else if (itemDetailEntity.getStatus() == 5) {// 事故
					iv_icon_bg.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.iv_shigu_bg));
				} else if (itemDetailEntity.getStatus() == 6) {// 警察执法
					iv_icon_bg.setBackgroundDrawable(getResources().getDrawable(
							R.drawable.iv_jingchazhifa_bg));
				}

				pic_url = itemDetailEntity.getPic_uri();
				// 路况详情的图片
				adapter_pic = new DetailPicAdapter(TrafficDetailActivity.this, pic_url);
				gv_pic.setAdapter(adapter_pic);

				if (isRequese == true) {
					// 继续获取评论列表
					getReviewList();
				} else {
					hideProgressBar();
				}
			}
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
		}
	}

	/**
	 * 获取评论列表
	 * */
	private void getReviewList() {
		// TODO Auto-generated method stub

		Map<String, Object> paramsRreview = new HashMap<String, Object>(1);
		// 1-启用，0-禁用/屏蔽，-1-删除 2-被举报
		// 默认:1
		int[] status = new int[1];
		status[0] = 1;
		// 评论类型
		// 1：资讯评论 2：路况评论
		sign = "2";
		paramsRreview.put("city_code", "2201");
		paramsRreview.put("org_code", "2201");
		paramsRreview.put("mobile", mobile);
		paramsRreview.put("longitude", lng);
		paramsRreview.put("latitude", lat);
		paramsRreview.put("sign", sign);
		paramsRreview.put("page_no", page_no);
		paramsRreview.put("page_size", page_size);
		paramsRreview.put("status", status);
		paramsRreview.put("type", 2);
		paramsRreview.put("info_id", road_id);

		reviewListBo = new Traffic_ReviewList_Bo(TrafficDetailActivity.this);
		reviewListBo.startRequestTask(new getReviewListCallBack(), TrafficDetailActivity.this,
				paramsRreview,
				"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/icityComment");

	}

	/**
	 * 获取评论列表回调
	 * */
	class getReviewListCallBack implements HttpCallBack<BaseResp> {
		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("评论列表接口返回===>>" + response.getHttpResult());
			if (response.isSuccess()) {
				Traffic_ReviewList_Entity entityList = (Traffic_ReviewList_Entity) response
						.getObj();

				list_review = entityList.getData();

				if (list_review == null) {
					// 评论数量
					tv_review_num.setText("0");
				} else {
					// 评论数量
					tv_review_num.setText(String.valueOf(list_review.size()));
				}
				// 路况详情的评论列表
				adapter_review = new TrafficReviewAdapter(TrafficDetailActivity.this, list_review);
				lv_review.setAdapter(adapter_review);

			}
			hideProgressBar();
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
		}
	}

	/**
	 * 敏感词接口
	 * */
	private void isSensitiveWord() {

		showProgressBar("正在提交评论...");
		Map<String, Object> paramsSensitiveWord = new HashMap<String, Object>(1);

		sign = imei;
		String content = et_comment.getText().toString().trim();

		if (content == null || content.equals("")) {
			CommonUtils.showToast(TrafficDetailActivity.this, "您还尚未输入评论内容！", Toast.LENGTH_SHORT);
			btn_comment.setEnabled(true);
			hideProgressBar();
			return;
		}
		paramsSensitiveWord.put("city_code", "2201");
		paramsSensitiveWord.put("org_code", "2201");
		paramsSensitiveWord.put("mobile", mobile);
		paramsSensitiveWord.put("longitude", lng);
		paramsSensitiveWord.put("latitude", lat);
		paramsSensitiveWord.put("sign", sign);
		paramsSensitiveWord.put("content", content);

		sensitiveWordBo = new Traffic_SensitiveWord_Bo(TrafficDetailActivity.this);
		sensitiveWordBo.startRequestTask(new sensitiveWordCallBack(), TrafficDetailActivity.this,
				paramsSensitiveWord,
				"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/icitySensitiveWord");
	}

	/**
	 * 敏感词回调
	 * */
	class sensitiveWordCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("敏感词接口回调---->>" + response.getHttpResult());
			if (response.isSuccess()) {
				Traffic_SensitiveWord_Entity sensitiveWordEntity = (Traffic_SensitiveWord_Entity) response
						.getObj();
				if (sensitiveWordEntity.isData()) {
					CommonUtils.showToast(TrafficDetailActivity.this, "您的评论中包含敏感词汇，系统拒绝您的评论请求！",
							Toast.LENGTH_SHORT);
					btn_comment.setEnabled(true);
					hideProgressBar();
				} else {
					hideProgressBar();
					releaseComment();
				}
			}
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * 发布评论
	 * */
	private void releaseComment() {
		// TODO Auto-generated method stub

		Map<String, Object> paramscomment = new HashMap<String, Object>(1);

		sign = imei + "$" + road_id + "$" + user_id;

		paramscomment.put("city_code", "2201");
		paramscomment.put("org_code", "2201");
		paramscomment.put("mobile", mobile);
		paramscomment.put("longitude", lng);
		paramscomment.put("latitude", lat);
		paramscomment.put("sign", sign);
		paramscomment.put("type", 2);
		paramscomment.put("info_id", road_id);
		paramscomment.put("title", title);
		String content = et_comment.getText().toString().trim();
		if (content != null || !content.equals("")) {
			paramscomment.put("content", content);
		} else {
			CommonUtils.showToast(TrafficDetailActivity.this, "您还尚未输入评论内容！", Toast.LENGTH_SHORT);
			return;
		}

		paramscomment.put("user_id", user_id);
		paramscomment.put("mobile", mobile);
		paramscomment.put("status", "1");

		commentBo = new Traffic_Comment_Bo(TrafficDetailActivity.this);
		commentBo
				.startRequestTask(new commentCallBack(), TrafficDetailActivity.this, paramscomment,
						"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/icityReleaseComment");
	}

	/**
	 * 发布评论回调
	 * */
	class commentCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("发布评论接口返回====>>" + response.getHttpResult());
			if (response.isSuccess()) {
				Traffic_Comment_Entity commentEntity = (Traffic_Comment_Entity) response.getObj();
				if ("0".equals(commentEntity.getResult_code())) {
					// CommonUtils.showToast(TrafficDetailActivity.this,
					// "评论发布成功！", Toast.LENGTH_SHORT);
					// tv_review_num
					// .setText(String.valueOf(Integer
					// .valueOf(tv_review_num.getText().toString()
					// .trim()) + 1));
					
					getReviewList();
					et_comment.setText("");
					rl_comment.setVisibility(View.GONE);
				} else {
					CommonUtils
							.showToast(TrafficDetailActivity.this, "评论发布失败！", Toast.LENGTH_SHORT);
				}
			}
			et_comment.setHint("我也要评论...");
			btn_comment.setEnabled(true);
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
		}
	}

	/**
	 * 点赞
	 * */
	private void releasePraise() {
		// TODO Auto-generated method stub
		Map<String, Object> paramsPraise = new HashMap<String, Object>(1);

		String id = road_id;
		sign = id;

		paramsPraise.put("city_code", "2201");
		paramsPraise.put("org_code", "2201");
		paramsPraise.put("mobile", mobile);
		paramsPraise.put("longitude", lng);
		paramsPraise.put("latitude", lat);
		paramsPraise.put("sign", sign);

		paramsPraise.put("type", 2);
		paramsPraise.put("id", Integer.valueOf(road_id));

		praiseBo = new Traffic_Praise_Bo(TrafficDetailActivity.this);
		praiseBo.startRequestTask(new praiseCallBack(), TrafficDetailActivity.this, paramsPraise,
				"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/icityGeneralPraise");
	}

	/**
	 * 点赞接口回调
	 * */
	class praiseCallBack implements HttpCallBack<BaseResp> {
		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("点赞返回===>>" + response.getHttpResult());
			if (response.isSuccess()) {
				Traffic_Praise_Entity praiseEntity = (Traffic_Praise_Entity) response.getObj();
				if (praiseEntity.getResult_code().equals("0")) {
					praiseNum++;
					tv_praise_num.setText(String.valueOf(praiseNum));
					ll_praise.setEnabled(true);
				}
			}
			hideProgressBar();
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_share) {
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("A_shareTraffic_trafficDetail_trafficTitle", itemDetailEntity.getTitle());
			MobclickAgent.onEvent(getApplicationContext(), "E_C_trafficDetail_shareTrafficClick", param);
			Intent intent = new Intent(TrafficDetailActivity.this, TrafficShareActivity.class);
			String statusDiscStr = itemDetailEntity.getStatus_desc();
			String detailStr = itemDetailEntity.getDetail();
			intent.putExtra("statusDiscStr", statusDiscStr);
			intent.putExtra("detailStr", detailStr);
			intent.putExtra("title", title);
			if (pic_url.length > 0) {
				intent.putExtra("bitmap_url", pic_url[0]);
			} else {
				intent.putExtra("bitmap_url", "");
			}
			startActivity(intent);
		} else if (id == R.id.ll_back) {
			finish();
		} else if (id == R.id.ll_comment_num) {
			rl_comment.setVisibility(View.VISIBLE);
		} else if (id == R.id.btn_comment) {
			isLogin = Boolean.parseBoolean(SharedPreferencesUtil.getValue(
					TrafficDetailActivity.this, Key.K_IS_LOGIN));
			if (!isLogin) {
				Intent intent = new Intent();
				intent.setClassName(TrafficDetailActivity.this,
						"cn.ffcs.changchuntv.activity.login.LoginActivity");
				startActivity(intent);
			} else {
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("A_commentTraffic_trafficDetail_trafficTitle", itemDetailEntity.getTitle());
				MobclickAgent.onEvent(getApplicationContext(), "E_C_trafficDetail_commentTrafficClick", param);
				// 只能点击一次
				btn_comment.setEnabled(false);
				// 敏感词验证
				isSensitiveWord();
				// releaseComment();
			}

		} else if (id == R.id.ll_praise) {
			isLogin = Boolean.parseBoolean(SharedPreferencesUtil.getValue(
					TrafficDetailActivity.this, Key.K_IS_LOGIN));
			if (!isLogin) {
				Intent intent = new Intent();
				intent.setClassName(TrafficDetailActivity.this,
						"cn.ffcs.changchuntv.activity.login.LoginActivity");
				startActivity(intent);
				finish();
			} else {
				HashMap<String, String> param = new HashMap<String, String>();
				param.put("A_praiseTraffic_trafficDetail_trafficTitle", itemDetailEntity.getTitle());
				MobclickAgent.onEvent(getApplicationContext(), "E_C_trafficDetail_praiseTrafficClick", param);
				showProgressBar("正在提交，请稍等...");
				ll_praise.setEnabled(false);
				isRequese = false;
				releasePraise();
			}
		}
	}

	/**
	 * 读取进度条
	 * */
	public void showProgressBar(String message) {
		LoadingDialog.getDialog(TrafficDetailActivity.this).setMessage(message).show();
	}

	/**
	 * 隐藏进度条
	 * */
	public void hideProgressBar() {
		LoadingDialog.getDialog(TrafficDetailActivity.this).cancel();
	}

}
