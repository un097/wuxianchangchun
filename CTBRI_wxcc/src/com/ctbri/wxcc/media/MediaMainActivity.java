package com.ctbri.wxcc.media;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import cn.ffcs.wisdom.city.common.MenuType;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.home.bo.AdvertisingBo;
import cn.ffcs.wisdom.city.home.datamgr.AdvertisingMgr;
import cn.ffcs.wisdom.city.home.entity.AdvertisingEntity;
import cn.ffcs.wisdom.city.home.entity.AdvertisingEntity.Advertising;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem.MenuTypeMedia;
import cn.ffcs.wisdom.city.utils.AppMgrUtils;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.Log;

import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.widget.ImageLooperFragment;
import com.ctbri.wxcc.widget.ImageLooperFragment.OnItemClickListener;
import com.ctbri.wxcc.widget.ImageLooperFragment.Pic;
import com.ctbri.wxcc.widget.ImageLooperFragment_new;
import com.ctbri.wxcc.widget.PlayConformDialog;

/**
 * 视频
 * 
 * @author Administrator
 *
 */
public class MediaMainActivity extends BaseActivity {

	private static final int INIT_BROADCAST = 1, INIT_VOD = 2,
			INIT_SHORT_VIDEO = 3, INIT_VIDEO_NAVIGATOR = 4;

	private int[] init_index = { INIT_BROADCAST, INIT_VOD, INIT_SHORT_VIDEO,
			INIT_VIDEO_NAVIGATOR };
	private int start_index = 0;
	private Activity mActivity = MediaMainActivity.this;

	@Override
	protected void onCreate(Bundle arg0) {
		// 解决加载 surfaceview时闪屏问题
		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		super.onCreate(arg0);

		setContentView(R.layout.activity_media_main);
		if (_Utils.isWifiTriggerOpen(this)) {
			new PlayConformDialog().show(getSupportFragmentManager(),
					"play_conform");
		}
		initWidget();
	}

	private Runnable mRunner = new Runnable() {
		@Override
		public void run() {
			if (start_index >= init_index.length)
				return;
			switch (init_index[start_index]) {
			case INIT_BROADCAST:
				initBroadcast();
				break;
			case INIT_SHORT_VIDEO:
				initShortVideo();
				break;
			case INIT_VIDEO_NAVIGATOR:
				initVideoCategory();
				break;
			case INIT_VOD:
				initVideoVod();
				break;
			}

			start_index++;
			mHandler.post(this);
		}
	};
	private Handler mHandler = new InitHandler();

	private static class InitHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
		}
	}

	private OnClickListener mFavoriteBtnListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(MediaMainActivity.this,
					MyFavoriteActivity.class);
			startActivity(intent);
		}
	};

	private void initWidget() {
		findViewById(R.id.action_bar_left_btn).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		// 收藏列表
		ImageView iv_rgiht_btn = ((ImageView) findViewById(R.id.action_bar_right_btn));
		iv_rgiht_btn
				.setImageResource(R.drawable.video_favorite_button_selector);
		iv_rgiht_btn.setOnClickListener(mFavoriteBtnListener);

		// 加载 首页图片轮播
		initImageLooper();

		// 设置标题
		((TextView) findViewById(R.id.action_bar_title))
				.setText(R.string.title_video);

		// 搜索框事件
		findViewById(R.id.tv_search).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent toSearch = new Intent(MediaMainActivity.this,
						com.ctbri.wxcc.media.MediaSearchActivity.class);
				startActivity(toSearch);
			}
		});

		mHandler.postDelayed(mRunner, 500);

	}

	private void initImageLooper() {

		ArrayList<Pic> pics = new ArrayList<Pic>();
		// pics.add(new Pic("天上再没有这样的可人儿",
		// "http://img31.mtime.cn/mg/2015/02/01/094117.53566720.jpg"));
		// pics.add(new Pic("大胖子是个什么",
		// "http://img31.mtime.cn/mg/2015/02/02/093822.40293236.jpg"));
		// pics.add(new Pic("何以笙箫默",
		// "http://img31.mtime.cn/mg/2015/01/31/092710.37057010.jpg"));
		// pics.add(new Pic("从新婚蜜月到导演片场",
		// "http://img31.mtime.cn/mg/2015/01/30/085343.82401041.jpg"));

//		ImageLooperFragment fragment = ImageLooperFragment.newInstance(pics);
//		fragment.setItemClickListener(imgItemClick);
//		getSupportFragmentManager().beginTransaction()
//				.replace(R.id.frame_video_looper, fragment).commit();

//		String cityCode = MenuMgr.getInstance().getCityCode(mActivity);
//		AdvertisingEntity entity = AdvertisingMgr.getInstance()
//				.getAdvertisingEntity(cityCode);
		
		AdvertisingEntity entity = AdvertisingMgr.getInstance()
				.getAdvertisingEntity_withCode("media");
		if (entity == null) {
			AdvertisingBo.getInstance().getAdvertising_new(mActivity,
					new AdvertisingCallBack(), "3");
		} else {
			showAdvertising(entity);
		}

		// ArrayList<Pic> pics = new ArrayList<Pic>();
		// pics.add(new Pic("天上再没有这样的可人儿",
		// "http://img31.mtime.cn/mg/2015/02/01/094117.53566720.jpg"));
		// pics.add(new Pic("大胖子是个什么",
		// "http://img31.mtime.cn/mg/2015/02/02/093822.40293236.jpg"));
		// pics.add(new Pic("何以笙箫默",
		// "http://img31.mtime.cn/mg/2015/01/31/092710.37057010.jpg"));
		// pics.add(new Pic("从新婚蜜月到导演片场",
		// "http://img31.mtime.cn/mg/2015/01/30/085343.82401041.jpg"));

	}

	private void showAdvertising(AdvertisingEntity adEntity) {
		List<Advertising> list = adEntity.getAdvs();
		android.util.Log.e("sb", list.size()
				+ "                            视频");
		if (list.size() > 0) {
			ImageLooperFragment_new fragment = ImageLooperFragment_new
					.newInstance((ArrayList<AdvertisingEntity.Advertising>) list);
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.frame_video_looper, fragment).commit();
			fragment.setItemClickListener(new ImageLooperFragment_new.OnItemClickListener() {
				@Override
				public void onClick(View v, Object data) {
					// TODO Auto-generated method stub
					Advertising advertising = (Advertising) data;
					if (!advertising.getItem_type().equals("imgAdv")) {
						MenuItem item = new MenuItem();
						item.setMenuType(MenuType.WAP);
						item.setMenuName(advertising.getTitle());
						item.setUrl(advertising.getOfficial_link());
						item.media = MenuTypeMedia.bannerWapShare;
						// 首页Banner点击跳转到wap，右上角显示分享按钮
						AppMgrUtils.launchAPP(mActivity, item, R.string.home_name);
					}
				}
			});
		}
	}

	class AdvertisingCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			if (response.isSuccess()) {
				AdvertisingEntity adEntity = (AdvertisingEntity) response
						.getObj();
				AdvertisingMgr.getInstance().setAdvertisingEntity_withCode("media",
						adEntity);
				showAdvertising(adEntity);
			}
		}

		@Override
		public void progress(Object... obj) {
		}

		@Override
		public void onNetWorkError() {
		}
	}

	private OnItemClickListener imgItemClick = new OnItemClickListener() {

		@Override
		public void onClick(View v, Object data) {
		}
	};

	private void initBroadcast() {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.frame_video_broadcast,
						new BroadcastGridFragment()).commit();
	}

	private void initVideoVod() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_video_vod, new VideoVodGridFragment())
				.commit();
	}

	private void initVideoCategory() {
		getSupportFragmentManager()
				.beginTransaction()
				.replace(
						R.id.frame_video_category,
						VideoNavigatorGridFragment.newInstance(false,
								Constants.METHOD_VIDEO_CATEGORY)).commit();
	}

	private void initShortVideo() {
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.frame_video_short, new VideoShortFragment())
				.commit();
	}

}
