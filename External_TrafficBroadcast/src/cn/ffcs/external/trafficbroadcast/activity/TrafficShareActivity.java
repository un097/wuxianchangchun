package cn.ffcs.external.trafficbroadcast.activity;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.ffcs.external.share.entity.CustomSocialShareEntity;
import cn.ffcs.external.share.view.CustomSocialShare;
import cn.ffcs.external.share.view.WeiBoSocialShare;
import cn.ffcs.wisdom.city.utils.CityImageLoader;
import cn.ffcs.wisdom.tools.StringUtil;

import com.example.external_trafficbroadcast.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 路况分享页面
 * 
 * @author daizhq
 * 
 * @date 2014.12.03
 * */
public class TrafficShareActivity extends Activity implements OnClickListener {

	private LinearLayout ll_back;

	private TextView tv_detail;
	private TextView tv_share_url;

	private ImageView iv_bitmap;

	private Button btn_share;

	private CityImageLoader loader;

	private String detailStr = "";// 路况详情
	private String statusDiscStr = "";// 路况类型
	private String content = "";// 分享内容
	private String title = "";// 分享标题
	private String pic_url = "";// 分享图片地址
	private Bitmap bitmap = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_traffic_share);

		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		tv_detail = (TextView) findViewById(R.id.tv_detail);
		tv_share_url = (TextView) findViewById(R.id.tv_share_url);
		iv_bitmap = (ImageView) findViewById(R.id.iv_bitmap);
		btn_share = (Button) findViewById(R.id.btn_share);

		ll_back.setOnClickListener(this);
		btn_share.setOnClickListener(this);

		loader = new CityImageLoader(TrafficShareActivity.this);

		// 接收上一个界面传过来的详情信息
		detailStr = getIntent().getExtras().getString("detailStr");
		statusDiscStr = getIntent().getExtras().getString("statusDiscStr");
		title = getIntent().getExtras().getString("title");
		pic_url = getIntent().getExtras().getString("bitmap_url");

		if (pic_url != null && pic_url.length() > 0) {
			bitmap = loader.getBitmapFormCache(pic_url);
			loader.loadUrl(iv_bitmap, pic_url);
		}

		content = "  " + "【" + statusDiscStr + "】  " + title + " 当前" + detailStr;
		tv_detail.setText(content);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_back) {
			finish();
		} else if (id == R.id.btn_share) {
			HashMap<String, String> param = new HashMap<String, String>();
			param.put("A_share_trafficShare_trafficTitle", title);
			MobclickAgent.onEvent(getApplicationContext(),
					"E_C_trafficShare_shareClick", param);
			CustomSocialShareEntity entity = new CustomSocialShareEntity();

			// ConfigParams params = ConfigUtil
			// .readConfigParams(TrafficShareActivity.this);
			// String shareUrl = params.getMenuShareUrl();
			if (bitmap == null) {
				bitmap = BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_launcher);
			}
			String shareUrl = "http://d.****.cn";
			shareUrl = getString(R.string.category_default_url);
			entity.shareUrl = shareUrl;
			entity.shareTitle = title;
			entity.shareTitle = content;
			entity.shareContent = content;
			entity.shareContent = "@无线长春 实时路况分享给您。大家帮大家，方便你我他";
			entity.imageBitmap = bitmap;
			if (!StringUtil.isEmpty(pic_url)) {
				entity.imageUrl = pic_url;// 分享中图片的地址
			} else {
				entity.imageUrl = "";// 分享中图片的地址
			}
			CustomSocialShare.shareImagePlatform(TrafficShareActivity.this,
					entity, false);
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
