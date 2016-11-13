package com.ctbri.wxcc.media;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctbri.comm.util.DialogUtils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.entity.CommonHolder;
import com.ctbri.wxcc.entity.MediaSearchBean;
import com.ctbri.wxcc.entity.MediaSearchBean.VideoSearchEntity;
import com.ctbri.wxcc.travel.CommonList;
import com.ctbri.wxcc.widget.LoadMorePTRListView;
import com.ctbri.wxcc.widget.SelfRounderDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class MediaSearchFragment extends CommonList<MediaSearchBean, MediaSearchBean.VideoSearchEntity> {

	private EditText mKeyWord;
	private TextView mSearchBtn;
	private ImageButton mClearTextBtn;
	private LayoutInflater inflater;
	private View mEmptyView;
	private TextView mTvResultCount;
	private String mKeyWordStr;
	private int mKeyWordColor;
	private DisplayImageOptions mDio;
	// 是否手动触发 搜索
	private boolean isManualSearch;

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);

		int radius = getResources().getDimensionPixelSize(R.dimen.audio_circle_image_radius);
		mKeyWordColor = getResources().getColor(R.color.media_search_key_word_color);
		mDio = new DisplayImageOptions.Builder().showImageForEmptyUri(R.drawable.icon_default_image_place_holder).showImageOnFail(R.drawable.icon_default_image_place_holder)
				.showImageOnLoading(R.drawable.icon_default_image_place_holder).cacheInMemory(false).cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
				// 设置是否圆角
				.displayer(new SelfRounderDisplayer(radius, getResources())).build();
	}

	@Override
	protected String getListUrl() {
		return Constants.METHOD_VIDEO_SEARCH_VOD;
	}

	@Override
	protected Class<MediaSearchBean> getGsonClass() {
		return MediaSearchBean.class;
	}

	@Override
	protected List<VideoSearchEntity> getEntitys(MediaSearchBean bean) {
		if (bean != null && bean.getData() != null)
			return bean.getData().getVideos();

		return null;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.activity_media_search_layout;
	}

	@Override
	protected boolean isInflateActionBar() {
		return false;
	}

	@Override
	protected boolean isAfterActivityCreatedDoLoad() {
		return false;
	}

	@Override
	protected boolean isEnd(MediaSearchBean bean) {
		if (bean.getData() != null)
			return bean.getData().getIs_end() == 0;
		return true;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = super.onCreateView(inflater, container, savedInstanceState);
		// 初始化 listview
		// 隐藏编辑按钮
		v.findViewById(R.id.action_bar_right_tv_btn).setVisibility(View.GONE);
		
		v.findViewById(R.id.action_bar_left_btn).setOnClickListener(new FinishClickListener());
		// 设置标题
		((TextView) v.findViewById(R.id.action_bar_title)).setText(R.string.title_search);

		mEmptyView = (View) v.findViewById(R.id.tv_empty_msg);
		// lv_list.setEmptyView(mEmptyView);

		mTvResultCount = ((TextView) v.findViewById(R.id.tv_result_count));

		mSearchBtn = (TextView) v.findViewById(R.id.tv_go);
		mSearchBtn.setVisibility(View.VISIBLE);
		mSearchBtn.setOnClickListener(mSearchListener);

		mKeyWord = (EditText) v.findViewById(R.id.txt_keyword);

		mClearTextBtn = (ImageButton) v.findViewById(R.id.ibtn_clear);
		// 清空 搜索条件
		mClearTextBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mKeyWord.setText("");
				mTvResultCount.setText("");
				// 清空已经搜索到的数据
				if (common_adapter != null) {
					common_adapter.clearData();
					common_adapter.notifyDataSetChanged();
				}
			}
		});
		mKeyWord.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)) {
					mClearTextBtn.setVisibility(View.GONE);
					mSearchBtn.setEnabled(false);
				} else {
					mClearTextBtn.setVisibility(View.VISIBLE);
					mSearchBtn.setEnabled(true);
				}
			}
		});

		this.inflater = inflater;
		return v;
	}

	@Override
	protected boolean initHeaderDetail(LoadMorePTRListView lv_list, LayoutInflater inflater) {
		lv_list.getRefreshableView().setDivider(getResources().getDrawable(R.drawable.ic_listview_default_divider));
		lv_list.getRefreshableView().setDividerHeight(1);
		return false;
	}

	@Override
	protected ArrayList<BasicNameValuePair> getParams() {
		mKeyWordStr = mKeyWord.getText().toString();
		ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("key", mKeyWordStr));
		return params;
	}

	/**
	 * 搜索按钮 事件
	 */
	private OnClickListener mSearchListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			hideSoftInput();
			isManualSearch = true;
			reload();
			DialogUtils.showLoading(getChildFragmentManager());
		}
	};

	private void hideSoftInput() {
		// change by wuchen at 2015-2-26 fix bug
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(mKeyWord.getWindowToken(), 0);
	}

	protected void onDataLoaded(MediaSearchBean bean) {
		int count = 0;
		List<VideoSearchEntity> list = getEntitys(bean);
		if (list != null)
			count = list.size();
		if (isManualSearch) {
			isManualSearch = false;
			// 如果为手动触发搜索，且搜索结果为 0.显示 emptyView,并隐藏控件
			if (count == 0) {
				lv_list.setVisibility(View.GONE);
				mEmptyView.setVisibility(View.VISIBLE);
			} else {
				mEmptyView.setVisibility(View.GONE);
				lv_list.setVisibility(View.VISIBLE);
			}
		}

		String str = getString(R.string.msg_search_result_count, count);
		mTvResultCount.setText(Html.fromHtml(str));

		DialogUtils.hideLoading(getChildFragmentManager());
	};

	@Override
	protected View getListItemView(int position, View convertView, ViewGroup parent, VideoSearchEntity data, ImageLoader imgloader, DisplayImageOptions dio) {

		CommonHolder hold = null;
		if (convertView == null) {
			hold = new CommonHolder();
			convertView = inflater.inflate(R.layout.media_video_list_item, parent, false);
			convertView.setTag(hold);
			hold.iv = (ImageView) convertView.findViewById(R.id.iv_video_img);
			// 视频名称
			hold.tv = (TextView) convertView.findViewById(R.id.tv_title);
			// 视频长度
			hold.tv1 = (TextView) convertView.findViewById(R.id.tv_time);
			// 播放次数
			hold.tv2 = (TextView) convertView.findViewById(R.id.tv_play_count);
			// 点赞数量
			hold.tv3 = (TextView) convertView.findViewById(R.id.tv_zan_count);
			// 评论数量
			hold.tv4 = (TextView) convertView.findViewById(R.id.tv_comment_count);
		} else {
			hold = (CommonHolder) convertView.getTag();
		}
		imgloader.displayImage(data.getPic(), hold.iv, mDio);

		hold.tv.setText(createKeyWordSpan(data.getName()));
		hold.tv1.setText(data.getTime());
		hold.tv2.setText(data.getPlays());
		hold.tv3.setText(data.getLikes());
		hold.tv4.setText(data.getComments());

		return convertView;
	}

	private SpannableString createKeyWordSpan(String msg) {
		if (msg == null)
			msg = "";
		SpannableString ss = new SpannableString(msg);
		int start = msg.indexOf(mKeyWordStr);
		if (start > -1)
			ss.setSpan(new ForegroundColorSpan(mKeyWordColor), start, start + mKeyWordStr.length(), SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);

		return ss;
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, VideoSearchEntity entity) {
		Intent toDetail = new Intent(activity, MediaPlayerActivity.class);
		toDetail.putExtra("type_id", VodVideoListFragmet.TYPE_VOD);
		toDetail.putExtra("vod_id", entity.getId());
		startActivity(toDetail);
	}

	@Override
	protected String getAnalyticsTitle() {
		// TODO Auto-generated method stub
		return null;
	}

}
