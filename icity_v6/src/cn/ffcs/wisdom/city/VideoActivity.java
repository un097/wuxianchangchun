package cn.ffcs.wisdom.city;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.adapter.VideoAdapter;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.entity.VideoEntity;
import cn.ffcs.wisdom.city.entity.VideoEntity.VideoItem;
import cn.ffcs.wisdom.city.task.GetVideoListTask;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.JsonUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title: 视频栏目	</p>
 * <p>Description: 视频栏目                      	</p>
 * <p>@author: zhangws				</p>
 * <p>Copyright: Copyright (c) 2012	</p>
 * <p>Company: FFCS Co., Ltd.		</p>
 * <p>Create Time: 2012-7-13		</p>
 * <p>Update Time: 2012-7-14		</p>
 * <p>Updater: Eric.wsd				</p>
 * <p>Update Comments: 修复栏目标题，和栏目标题一致</p>
 */
public class VideoActivity extends WisdomCityActivity implements OnItemClickListener {

	private ListView mListView;
	private VideoAdapter adapter;

	private List<VideoItem> videoItemList = new ArrayList<VideoItem>();
	private String videoType = "1"; // 默认为1

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String menuName = getIntent().getStringExtra(ExternalKey.K_MENUNAME);
		TopUtil.updateTitle(this, R.id.top_title, menuName);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshVideoList();
	}

	private void getVideoList() {
		// 显示等待进度条
		showProgressBar();
		// 获取视频列表
		GetVideoListTask task = new GetVideoListTask(new videoCallBack(),
				mContext, videoType);
		task.execute();
	}

	private void refreshVideoList() {
		if (adapter == null) {
			adapter = new VideoAdapter(mContext, videoItemList);
			mListView.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View v, int pos, long arg3) {
		VideoItem item = videoItemList.get(pos);
		// 记录痕迹
		// record(item);
		// 开始播放
		goToPlay(item);
	}

	/**
	 * 播放视频
	 * @param item
	 */
	private void goToPlay(VideoItem item) {
		Bundle params = new Bundle();
		params.putString(Key.U_BROWSER_URL, item.getRtsp());
		Intent intent = new Intent();
		intent.setClass(this, MPlayerActivity.class);
		intent.putExtras(params);
		startActivity(intent);
	}

	class videoCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// 隐藏进度条
			hideProgressBar();
			try {
				if ("0".equals(response.getStatus())) {
					String httpResult = response.getHttpResult();
					VideoEntity entity = JsonUtil.toObject(httpResult, VideoEntity.class);
					videoItemList.clear();
					videoItemList.addAll(entity.getVideos());
					refreshVideoList();
				} else {
					Toast.makeText(mContext, "获取视频列表失败", Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Toast.makeText(mContext, "获取视频列表失败", Toast.LENGTH_SHORT).show();
				Log.e("Exception" + e);
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
	public int getMainContentViewId() {
		return R.layout.act_video_list;
	}

	@Override
	public Class<?> getResouceClass() {
		return R.class;
	}

	@Override
	protected void initComponents() {
		mListView = (ListView) findViewById(R.id.video_listview);
		mListView.setOnItemClickListener(this);
	}

	@Override
	protected void initData() {
		videoType = getIntent().getStringExtra("videotypes");
		if (StringUtil.isEmpty(videoType)) {
			videoType = "1";
		}
		getVideoList();
	}
}
