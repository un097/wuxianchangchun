package com.ctbri.wxcc.media;

import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_NEXT_PLAY_CONTROL;
import static com.ctbri.wxcc.audio.AudioLiveFragment.ACTION_AUDIO_VOD_PLAYPOSITION_PLAY;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ctbri.comm.util._Utils;
import com.ctbri.comm.widget.ComplaintDialog;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.ObjectAdapter;
import com.ctbri.wxcc.entity.AudioVodDetail;
import com.ctbri.wxcc.entity.CommonHolder;
import com.ctbri.wxcc.entity.CommunityCommentBean;
import com.ctbri.wxcc.entity.CommunityCommentBean.CommunityComment;
import com.ctbri.wxcc.entity.MediaLiveDetail;
import com.ctbri.wxcc.entity.MediaLiveDetail.Program;
import com.ctbri.wxcc.entity.PraiseBean;
import com.ctbri.wxcc.entity.VodDetailBean;
import com.ctbri.wxcc.entity.VodDetailBean.VodDetailItem;
import com.ctbri.wxcc.media.MediaUtils.ILikeSuccess;
import com.ctbri.wxcc.widget.CouponPopupWindow;
import com.ctbri.wxcc.widget.MenuGridView;
import com.ctbri.wxcc.widget.SelfRounderDisplayer;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.utils.LogUtil;
import com.wookii.widget.ryg.utils.Utils;

public class MediaInfoFragment extends BaseFragment {

	/**
	 * 视频类别为合集
	 */
	public static final int VIDEO_TYPE_PIECE = 2;
	/**
	 * 音频类别为合集
	 */
	public static final int AUDIO_TYPE_PIECE = 2;
	
	private String checkedVod;

	/**
	 * 剧集分组时，每组的最大集数
	 */
	private static final int GROUP_COUNT = 10;
	private AbsListView mRealView;
	private ListView mListView;
	private TextView tv_video_info, tv_title, tv_zan, tv_play_count, tv_video_desc;
	private ObjectAdapter<?> mAdapter;
	private Resources res;
	private Button btn_show_desc;
	private boolean showDesc = false;
	private View mView, mInfoView;
	private String group_id;
	private RadioGroup rdo_group;
	private View hsv_chooice_items;
	private CommentAdapter commentsAdapter;
	private LayoutInflater inflater;
	private View mBtn_show_coupon;

	private String mCouponTitle, mCouponDate, mCouponImage, mCouponId, mTypeId;

	private String mMediaId;

	private int vedioPlayPosition = 0;//视频播放的集数
	private int mVods = 0;//视频播放总集数
	private int playPosition;
	
	public static MediaInfoFragment newInstance(String group_id) {
		Bundle args = new Bundle();
		args.putString("group_id", group_id);
		MediaInfoFragment fragment = new MediaInfoFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		registerReceiver();
		checkedChanged(playPosition);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_AUDIO_VOD_NEXT_PLAY_CONTROL);
		activity.registerReceiver(mPlayNextReceiver, filter);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		activity.unregisterReceiver(mPlayNextReceiver);
	}
	
	/**
	 * 播放控制
	 */
	private BroadcastReceiver mPlayNextReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String act = intent.getAction();
			if(ACTION_AUDIO_VOD_NEXT_PLAY_CONTROL.equals(act)) {
				playPosition = intent.getIntExtra("playPosition",-1);
				if(playPosition != -1)
					checkedChanged(playPosition);
			}
		}
	};
	
	/**
	 * 选中播放集数显示样式
	 * @param checkedIndex
	 */
	private void checkedChanged(int checkedIndex) {
		if(mRealView != null && mAdapter != null) {
			mRealView.setItemChecked(checkedIndex, true);
			mAdapter.notifyDataSetChanged();
		}
	};
	
	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		group_id = getArgs("group_id");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.media_broadcast_video_info, container, false);
		tv_video_info = (TextView) mView.findViewById(R.id.tv_video_info);
		mInfoView = mView.findViewById(R.id.rl_info);
		tv_title = (TextView) mView.findViewById(R.id.tv_title);
		tv_zan = (TextView) mView.findViewById(R.id.tv_zan_count);
		tv_zan.setOnClickListener(mZanListener);

		tv_play_count = (TextView) mView.findViewById(R.id.tv_play_count);
		tv_video_desc = (TextView) mView.findViewById(R.id.tv_video_desc);

		mListView = (ListView) mView.findViewById(R.id.lv_video_info);
		if (VodVideoListFragmet.TYPE_BROADCAST.equals(group_id)) {
			// 为直播时，显示节目播放信息
			mInfoView.setVisibility(View.VISIBLE);
			mRealView = mListView;
		} else {
			mRealView = (MenuGridView) inflater.inflate(R.layout.widget_media_piece_grid, mListView, false);
			mRealView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
			// 设置剧集 选中事件
			mRealView.setOnItemClickListener(mOnPieceItemClick);
			mListView.addHeaderView(mRealView, null, false);

			// 存储 单选按钮 group
			rdo_group = (RadioGroup) mView.findViewById(R.id.rdo_group_item_stub);
			// 为点播时， 默认显示选集
			hsv_chooice_items = mView.findViewById(R.id.hsv_chooice);
			hsv_chooice_items.setVisibility(View.VISIBLE);

			// 需求变更， 只有详情为 点播时。才有详情信息. (by yyd 2015-03-25)
			btn_show_desc = (Button) mView.findViewById(R.id.btn_show_info);
			btn_show_desc.setVisibility(View.VISIBLE);
			btn_show_desc.setOnClickListener(new ShowDetailListener());
		}

		mRealView.setVisibility(View.VISIBLE);

		mBtn_show_coupon = mView.findViewById(R.id.btn_show_coupon);
		mBtn_show_coupon.setOnClickListener(new ShowCouponListener());

		return mView;
	}

	/**
	 * 点赞事件
	 */
	private OnClickListener mZanListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			playZanCartoon(v);
			MediaUtils.like(mMediaId, mTypeId, mLikeCallback, (BaseActivity) activity);
		}
	};

	/**
	 * 点播之后的回调函数
	 */
	private ILikeSuccess mLikeCallback = new ILikeSuccess() {

		@Override
		public void onSuccess(String total) {
			tv_zan.setText(total);
		}
	};

	/**
	 * 分集点击事件
	 */
	private OnItemClickListener mOnPieceItemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//记录播放集数
			vedioPlayPosition = position;
			
			VodDetailItem item = (VodDetailItem) mAdapter.getItem(position);
			checkedVod = item.getSeq();
			MediaPlayerFragment player = (MediaPlayerFragment) getFragmentManager().findFragmentByTag(MediaLivePlayerFragment.KEY_PLAYER_FRAGMENT);
			if (player != null) {
				if(MediaPlayerActivity.TYPE_AUDIO_VOD.equals(mTypeId)) {
					//将当前集数传递给服务
					Intent intent = new Intent(ACTION_AUDIO_VOD_PLAYPOSITION_PLAY);
					intent.putExtra("playPosition", position);
					intent.putExtra("piece", true);
					activity.sendBroadcast(intent);
				}

				player.update(item.getName(), item.getVod(), mMediaId, mTypeId, null);
			}
		}
	};

	/**
	 * 更新直播视频的相关数据
	 * 
	 * @param bean
	 */
	public void updateInfo(MediaLiveDetail bean) {
		if (bean != null && bean.getData() != null) {
			MediaLiveDetail detail = bean.getData();
			mAdapter = new ProgramAdapter(activity, detail.getProgram_list());
			mRealView.setAdapter(mAdapter);

			MediaLiveDetail data = bean.getData();
			tv_title.setText(null);
			tv_video_desc.setText(data.getChannel_name());
			tv_play_count.setText(data.getPlays());

			Program program = checkedPlayingItem();
			if (program != null) {
				tv_title.setText(program.getName());
			}

			mCouponId = detail.getCoupon_id();
			mTypeId = VodVideoListFragmet.TYPE_BROADCAST;
			mMediaId = detail.getChannel_id();

			if (TextUtils.isEmpty(mCouponId)) {
				mBtn_show_coupon.setVisibility(View.INVISIBLE);
			} else {
				mCouponDate = detail.getCoupon_validity();
				mCouponImage = detail.getCoupon_pic();
				mCouponTitle = detail.getCoupon_name();
			}
		}
	}

	public Program checkedPlayingItem() {
		if (mAdapter != null) {
			int c = mAdapter.getCount();
			for (int i = 0; i < c; i++) {
				Program program = (Program) mAdapter.getItem(i);
				if (program.getStatus() == Program.STATUS_BROADCAST) {
					i = i > 1 ? i - 1 : i;
					mRealView.setSelection(i);
					return program;
				}
			}
		}
		return null;
	}

	/**
	 * 更新点播视频的相关数据
	 * 
	 * @param bean
	 */
	public void updateInfo(VodDetailBean bean) {
		if (bean != null && bean.getData() != null) {
			List<VodDetailItem> vods = bean.getData().getVideos();

			VodDetailBean data = bean.getData();
			tv_title.setText(data.getVideo_name());
			tv_video_desc.setText(data.getGroup_name());
			tv_video_info.setText(data.getVideo_desp());
			tv_play_count.setText(data.getPlays());
			tv_zan.setVisibility(View.VISIBLE);
			tv_zan.setText(data.getLikes());

			mMediaId = bean.getData().getVideo_id();
			// 获取热门评论
			String url = Constants.METHOD_VIDEO_COMMENT_LIST + "?start=0&count=20&type=1&video_id=" + mMediaId;
			// 获取热门评论
			loadComment(url);
			mTypeId = VodVideoListFragmet.TYPE_VOD;
			// 如果当前的视频类别不是合集，则隐藏合集按钮
			if (data.getVideo_type() != VIDEO_TYPE_PIECE) {
				btn_show_desc.setVisibility(View.INVISIBLE);
				showMediaDetail(false);
			}
			
			checkedVod = vods.get(0).getSeq();

			// 进行剧集分组
			initPieceGroupInfo(vods);

			mCouponId = data.getCoupon_id();
			if (TextUtils.isEmpty(mCouponId)) {
				mBtn_show_coupon.setVisibility(View.INVISIBLE);
			} else {
				mCouponDate = data.getCoupon_validity();
				mCouponImage = data.getCoupon_pic();
				mCouponTitle = data.getCoupon_name();
			}
		}
	}

	/**
	 * 更新音频点播视频的相关数据
	 * 
	 * @param bean
	 */
	public void updateInfo(AudioVodDetail bean) {
		if (bean != null && bean.getData() != null) {

			AudioVodDetail data = bean.getData();
			List<VodDetailItem> vods = data.getAudios();
			
			tv_title.setText(data.getAudio_name());
			tv_video_desc.setText(data.getGroup_name());
			tv_video_info.setText(data.getAudio_desp());
			tv_play_count.setText(data.getPlays());
			tv_play_count.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_shoutingrenshu, 0, 0, 0);
			tv_zan.setVisibility(View.VISIBLE);
			tv_zan.setText(data.getLikes());

			mMediaId = data.getAudio_id();
			String url = Constants.METHOD_AUDIO_COMMENT + "?start=0&count=20&type=1&audio_id=" + mMediaId;
			// 获取热门评论
			loadComment(url);
			if (data.getAudio_type() != AUDIO_TYPE_PIECE) {
				btn_show_desc.setVisibility(View.INVISIBLE);
				showMediaDetail(false);
			}

			mTypeId = MediaPlayerActivity.TYPE_AUDIO_VOD;
			// 进行剧集分组
			initPieceGroupInfo(vods);

			mCouponId = data.getCoupon_id();
			// 如果没有优惠券信息，则隐藏
			if (TextUtils.isEmpty(mCouponId)) {
				mBtn_show_coupon.setVisibility(View.INVISIBLE);
			} else {
				mCouponDate = data.getCoupon_validity();
				mCouponImage = data.getCoupon_pic();
				mCouponTitle = data.getCoupon_name();
			}
		}
	}

	/**
	 * 初始化评论列表
	 */
	private void loadComment(String url) {
		request(url, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				fillList(json);
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		});
	}

	private void fillList(String json) {
		Gson gson = new Gson();
		CommunityCommentBean commentBean = gson.fromJson(json, CommunityCommentBean.class);

		commentsAdapter = new CommentAdapter(activity, commentBean.getData().getCommList());
		mListView.setAdapter(commentsAdapter);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		res = getResources();
		inflater = LayoutInflater.from(activity);
	}

	/**
	 * 初始化 座位信息
	 */
	private void initPieceGroupInfo(List<VodDetailItem> vods) {
		if (vods == null)
			return;
		int mCount = vods.size();
		mVods = vods.size();
		int mGroupCount = mCount / GROUP_COUNT;
		int mLastGroupCount = mCount % GROUP_COUNT;

		mAdapter = new GridPieceAdapter(activity, null);
		mRealView.setAdapter(mAdapter);

		//隐藏RadioButton分集栏目  by 张政 2015-7-06
//		if (mGroupCount > 0) {
//			for (int i = 0; i < mGroupCount; i++) {
//
//				int mGroupStart = i * GROUP_COUNT + 1;
//				int mGroupEnd = (i + 1) * GROUP_COUNT;
//				RadioButton mRadio = generateRadioButton(mGroupStart + " - " + mGroupEnd);
//				rdo_group.addView(mRadio);
//				mRadio.setTag(vods.subList(mGroupStart - 1, mGroupEnd));
//			}
//			if (mLastGroupCount > 0) {
//				int mGroupStart = (mGroupCount * GROUP_COUNT + 1);
//				RadioButton mRadio = generateRadioButton((mGroupCount * GROUP_COUNT + 1) + " - " + mCount);
//				rdo_group.addView(mRadio);
//				mRadio.setTag(vods.subList(mGroupStart - 1, mCount));
//			}
//		} else {
			RadioButton mRadio = generateRadioButton("1 - " + mCount);
			mRadio.setTag(vods);
			rdo_group.addView(mRadio);
//		}
		hsv_chooice_items.setVisibility(View.VISIBLE);
		((RadioButton) rdo_group.getChildAt(0)).setChecked(true);
	}

	private RadioButton generateRadioButton(String text) {
		RadioButton mRadio = (RadioButton) inflater.inflate(R.layout.media_broadcast_video_info_chooice_bar, rdo_group, false);
		mRadio.setOnCheckedChangeListener(mCheckedChangeListener);
//		mRadio.setText(text);
		return mRadio;
	}

	private OnCheckedChangeListener mCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

			if (isChecked) {
				int checkedIndex = -1;
				mAdapter.clearData();
				List list = (List<?>) buttonView.getTag();
				mAdapter.addAll(list);

				if (checkedVod != null) {
					System.out.println(checkedVod);
					int mCount = list.size();
					for (int i = 0; i < mCount; i++)
						if (checkedVod.equals(((VodDetailItem) list.get(i)).getSeq())) {
							checkedIndex = i;
							playPosition = checkedIndex;
							break;
						}
				}

				mRealView.setItemChecked(checkedIndex, true);
				mAdapter.notifyDataSetChanged();
			}
		}
	};

	class ShowCouponListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			CouponPopupWindow mCouponWindow = new CouponPopupWindow(activity);
			// mCouponWindow.showAtLocation(mView, Gravity.NO_GRAVITY, 0 ,0);
			int[] pos = new int[2];
			mView.getLocationOnScreen(pos);
			mCouponWindow.setArgs(mCouponTitle, mCouponDate, mCouponImage, mCouponId);
			mCouponWindow.setAnimationStyle(R.style.coupon_animation);
			mCouponWindow.showAtLocation(mView, Gravity.NO_GRAVITY, 0, pos[1]);
			// mCouponWindow.showAsDropDown(v);
		}

	}

	class ShowDetailListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			showMediaDetail(showDesc);
		}
	}

	private void showMediaDetail(boolean showPiece) {
		if (showPiece) {
			tv_video_info.setVisibility(View.GONE);
			mRealView.setVisibility(View.VISIBLE);
			btn_show_desc.setText(R.string.txt_detail);
			mInfoView.setVisibility(View.INVISIBLE);
			// 显示选集
			mListView.setVisibility(View.VISIBLE);
			hsv_chooice_items.setVisibility(View.VISIBLE);
		} else {
			tv_video_info.setVisibility(View.VISIBLE);
			mRealView.setVisibility(View.GONE);
			btn_show_desc.setText(R.string.txt_chooice_piece);
			mInfoView.setVisibility(View.VISIBLE);
			// 隐藏选集
			hsv_chooice_items.setVisibility(View.GONE);
			// 隐藏评论
			mListView.setVisibility(View.GONE);
		}
		showDesc = !showPiece;
	}

	class ProgramAdapter extends ObjectAdapter<Program> {

		public ProgramAdapter(Activity activity, List<Program> data_) {
			super(activity, data_);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CommonHolder holder = null;
			if (convertView == null) {
				holder = new CommonHolder();
				convertView = inflater.inflate(R.layout.media_broadcast_video_info_item, parent, false);
				convertView.setTag(holder);
				// 正在播放、已经播放、未播放 的状态圆环
				holder.iv = (ImageView) convertView.findViewById(R.id.iv_broadcast_circle);
				// 节目时间
				holder.tv1 = (TextView) convertView.findViewById(R.id.tv_time_line);
				// 节目名称
				holder.tv2 = (TextView) convertView.findViewById(R.id.tv_video_name);
			} else {
				holder = (CommonHolder) convertView.getTag();
			}
			Program timeline = getItem(position);
			if (timeline.getStatus() == 1) {
				holder.iv.setImageResource(R.drawable.ic_broadcast_state_circle_broadcast);
				holder.tv1.setTextColor(res.getColor(R.color.media_time_line_broadcast_color));
				holder.tv2.setTextColor(res.getColor(R.color.media_time_line_broadcast_color));
			} else {
				holder.iv.setImageResource(R.drawable.ic_broadcast_state_circle_normal);
				holder.tv1.setTextColor(res.getColor(R.color.media_time_line_normal_color));
				holder.tv2.setTextColor(res.getColor(R.color.media_time_line_normal_color));
			}

			holder.tv1.setText(timeline.getTime());
			holder.tv2.setText(timeline.getName());

			return convertView;
		}
	}

	class GridPieceAdapter extends ObjectAdapter<VodDetailItem> {

		public GridPieceAdapter(Activity activity, List<VodDetailItem> data_) {
			super(activity, data_);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CommonHolder holder = null;
			if (convertView == null) {
				holder = new CommonHolder();
				convertView = inflater.inflate(R.layout.media_vod_piece_item, parent, false);
				convertView.setTag(holder);
				// 正在播放、已经播放、未播放 的状态圆环
				holder.tv = (TextView) convertView.findViewById(R.id.tv_piece);
			} else {
				holder = (CommonHolder) convertView.getTag();
			}
			VodDetailItem timeline = getItem(position);
			holder.tv.setText(timeline.getSeq());
			return convertView;
		}

	}

	class CommentAdapter extends ObjectAdapter<CommunityCommentBean.CommunityComment> {

		private DisplayImageOptions dio;
		ImageLoader imgloader;
		private ZanAndReportListener zanAndReportListener;

		public CommentAdapter(Activity activity, List<CommunityComment> data) {
			super(activity, data);
			zanAndReportListener = new ZanAndReportListener();
			imgloader = ImageLoader.getInstance();
			dio = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.default_user).showImageForEmptyUri(R.drawable.default_user)
					.showImageOnFail(R.drawable.default_user).cacheInMemory(true).cacheOnDisc(true)
					// .considerExifParams(true)
					.bitmapConfig(Bitmap.Config.RGB_565)
					// 设置是否圆角
					.displayer(new SelfRounderDisplayer(Utils.dp2px(activity, 4), getResources())).build();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			CommentHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.common_comment_item2, parent, false);
				holder = new CommentHolder();
				convertView.setTag(holder);

				holder.iv_avtor = (ImageView) convertView.findViewById(R.id.iv_avator);
				holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
				holder.tv_pubDate = (TextView) convertView.findViewById(R.id.tv_pubdate);
				holder.tv_report = (TextView) convertView.findViewById(R.id.tv_report);
				holder.tv_userName = (TextView) convertView.findViewById(R.id.tv_username);
				holder.tv_zan = (TextView) convertView.findViewById(R.id.tv_zan);
				holder.iv_zan = (ImageView) convertView.findViewById(R.id.iv_zan);

				holder.rl_zan_container = convertView.findViewById(R.id.rl_zan_cancontainer);
				holder.rl_zan_container.setOnClickListener(zanAndReportListener);

				holder.tv_report.setOnClickListener(zanAndReportListener);

			} else {
				holder = (CommentHolder) convertView.getTag();
			}
			CommunityComment comment = getItem(position);
			fillItem(holder, comment);
			return convertView;
		}

		private void fillItem(CommentHolder holder, CommunityComment comment) {
			// 显示头像
			imgloader.displayImage(comment.getPic_url(), holder.iv_avtor, dio);

			holder.tv_content.setText(comment.getContent());
			holder.tv_pubDate.setText(comment.getCreate_time());
			holder.tv_userName.setText(comment.getUser_name());
			holder.tv_report.setTag(comment);

			holder.tv_zan.setText(comment.getComment_zan_num());
			// holder.tv_zan.setSelected("0".equals(comment.getStatus()));

			holder.iv_zan.setTag(comment);
			// holder.iv_zan.setSelected("0".equals(comment.getStatus()));

			holder.rl_zan_container.setTag(holder.iv_zan);
		}
	}

	class CommentHolder {
		ImageView iv_avtor, iv_zan;
		View rl_zan_container;
		TextView tv_userName, tv_pubDate, tv_content, tv_zan, tv_report;
	}

	/***
	 * 点赞 和 举报 的回调事件
	 * 
	 * @author yanyadi
	 * 
	 */
	class ZanAndReportListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			int v_id = v.getId();
			CommunityComment comment;
			if (v_id == R.id.tv_report) {
				comment = (CommunityComment) v.getTag();
				report(comment);
				toast("请举报");
			} else if (v_id == R.id.rl_zan_cancontainer) {
				// 判断当前用户是否登录，未登录则跳转至登录窗口 by 闫亚迪 2015-3-10
				if (!_Utils.checkLoginAndLogin(activity)) {
					return;
				}
				v = v_id == R.id.iv_zan ? v : ((View) v.getTag());
				comment = (CommunityComment) v.getTag();
				// 提交点赞数据
				zan(comment);
				// 开始点赞动画
				playZanCartoon(v);

				comment.setStatus("0");
				int zanNumber = Integer.parseInt(comment.getComment_zan_num()) + 1;
				comment.setComment_zan_num(String.valueOf(zanNumber));
				commentsAdapter.notifyDataSetChanged();
			}
		}
	}

	private void playZanCartoon(View v) {
		Animation anim = AnimationUtils.loadAnimation(activity, R.anim.anim_praise_zoom);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				tv_zan.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_list_yizan, 0, 0, 0);
			}
		});
		// v.setAnimation(anim);
		// anim.startNow();
		v.startAnimation(anim);
	}

	private void report(final CommunityComment comment) {
		ComplaintDialog complaint = ComplaintDialog.newInstance(Constants.METHOD_VIDEO_REPORT, comment.getComment_id());
		complaint.show(getChildFragmentManager(), "report_dialog");
	}

	private void zan(final CommunityComment comment) {
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("comment_id", comment.getComment_id()));
		request(Constants.METHOD_VIDEO_PRAISE, new RequestCallback() {
			public void requestSucc(String json) {
				// 此外应该更新 commentsAdapter 中点赞评论的点赞数量
				Gson gson = new Gson();
				PraiseBean totals = gson.fromJson(json, PraiseBean.class);
				comment.setComment_zan_num(totals.getData().getTotls());
				comment.setStatus("0");
				commentsAdapter.notifyDataSetChanged();
			}

			@Override
			public void requestFailed(int errorCode) {

			}
		}, params);
	}

	@Override
	protected String getAnalyticsTitle() {
		return null;
	}

	@Override
	protected boolean isEnabledAnalytics() {
		return false;
	}
	
	/**
	 * 当视频播放完毕之后通知播放下一集
	 * @author 张政  2015/7/6
	 */
	public void playNextVedio(){
		vedioPlayPosition = vedioPlayPosition+1;
		
		//没有下一集了
		if(vedioPlayPosition >= mVods) {
			return;
		}
		
		VodDetailItem item = (VodDetailItem) mAdapter.getItem(vedioPlayPosition);
		if(item != null) {
			checkedVod = item.getSeq();
			MediaPlayerFragment player = (MediaPlayerFragment) getFragmentManager().findFragmentByTag(MediaLivePlayerFragment.KEY_PLAYER_FRAGMENT);
			if (player != null) {
				player.update(item.getName(), item.getVod(), mMediaId, mTypeId, null);
				//by zhangzheng 9-8
				checkedChanged(vedioPlayPosition);
			}
		}
	}
}
