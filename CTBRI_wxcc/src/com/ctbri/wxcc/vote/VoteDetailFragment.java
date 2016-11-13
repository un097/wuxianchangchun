package com.ctbri.wxcc.vote;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctbri.comm.util.DialogUtils;
import com.ctbri.comm.util._Utils;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseFragment;
import com.ctbri.wxcc.community.Constants_Community;
import com.ctbri.wxcc.community.Watcher;
import com.ctbri.wxcc.community.WatcherManager;
import com.ctbri.wxcc.community.WatcherManagerFactory;
import com.ctbri.wxcc.entity.AnimatorBean;
import com.ctbri.wxcc.entity.VoteDetailBean;
import com.ctbri.wxcc.entity.VoteDetailBean.Item;
import com.ctbri.wxcc.entity.VoteDetailBean.ResItem;
import com.ctbri.wxcc.entity.VoteDetailBean.VoteDetailContainer;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wookii.tools.comm.LogS;

public class VoteDetailFragment extends BaseFragment {

	public static final String KEY_INVESTIGATION_ID = "investigation_id";
	private VoteHolder holder;
	private VoteRatioBgFactory ratioBgFactory;
	private LayoutInflater infalter;
	private ViewGroup voteContainer;
	private String investigation_id;
	private int voteFullWidth = -1;
	private List<RatioHolder> ratios;
	private ImageLoader imgloader;
	private DisplayImageOptions dio;
	private WatcherManager watchManager;
	private VoteDetailContainer voteDetailBean;
	/** 是否是单选 **/
	private boolean isSingle;
	/** 此次请求的民意调查详情，是否有投票结果 **/
	private boolean hasRes;
	@SuppressWarnings("unused")
	private Rect unselectPaddingRect, selectedRect;
	/** 移动投票组件后的，执行的后续操作回调 **/
	private RemoveCallback removedCallback;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.vote_detail_fragment, container,
				false);
		holder = new VoteHolder();
		holder.iv_img = (ImageView) view.findViewById(R.id.iv_vote_bg);
		holder.tv_num = (TextView) view.findViewById(R.id.tv_vote_number);
		holder.tv_source = (TextView) view.findViewById(R.id.tv_source);
		holder.tv_status = (TextView) view.findViewById(R.id.tv_vote_state);
		holder.tv_title = (TextView) view.findViewById(R.id.tv_vote_title);
		holder.tv_content = (TextView) view.findViewById(R.id.tv_vote_info);
		holder.btn_voting = (Button) view.findViewById(R.id.btn_voting);
		holder.tv_comment_line = (TextView) view
				.findViewById(R.id.tv_comment_line);
		voteContainer = (ViewGroup) view.findViewById(R.id.vote_item_container);
		return view;
	}

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		if (activity_ instanceof WatcherManagerFactory) {
			watchManager = ((WatcherManagerFactory) activity_).getManager();
			watchManager.addWatcher(new ShareWatcher());
			watchManager.addWatcher(new PullToRefreshWatcher());
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		imgloader = ImageLoader.getInstance();
		dio = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565).build();
		infalter = activity.getLayoutInflater();
		ratioBgFactory = new VoteRatioBgFactory();
		ratios = new ArrayList<VoteDetailFragment.RatioHolder>();

		getData();
	}

	private void getData() {

		DialogUtils.showLoading(getFragmentManager());
		investigation_id = activity.getIntent().getStringExtra(
				KEY_INVESTIGATION_ID);
		String withParamsUrl = Constants.METHOD_INVESTIGATION_DETAILE
				+ "?investigation_id=" + investigation_id;
		request(withParamsUrl, new RequestCallback() {
			@Override
			public void requestSucc(String json) {
				fillData(json);
				DialogUtils.hideLoading(getFragmentManager());
			}

			@Override
			public void requestFailed(int errorCode) {
				DialogUtils.hideLoading(getFragmentManager());
			}
		});
	}

	/**
	 * 填充 评论数量统计
	 * 
	 * @param count
	 */
	private void setCommentCount(String count) {
		View v_count = activity.findViewById(R.id.send_comments_count);
		if (v_count != null)
			((TextView) v_count).setText(count);
	}

	/**
	 * 填充 民意调查的 详细描述信息（背景图，来源，标题，投票人数）
	 * 
	 * @param json
	 */
	private void fillData(String json) {
		Gson gson = new Gson();
		// 标示是否已经结束
		boolean expired = true;
		VoteDetailBean voteDetail = gson.fromJson(json, VoteDetailBean.class);
		voteDetailBean = voteDetail.getData();

		holder.tv_num.setText(getString(R.string.vote_number,
				voteDetailBean.getVote_num()));
		holder.tv_source.setText(getString(R.string.vote_source,
				voteDetailBean.getVote_res()));
		String[] mVoteStatus = activity.getResources().getStringArray(R.array.vote_status);
		
		if(voteDetailBean.getStatus() < mVoteStatus.length && voteDetailBean.getStatus() > -1)
		holder.tv_status.setText(mVoteStatus[voteDetailBean.getStatus()]);
		if (voteDetailBean.getStatus() == 1) {
			holder.tv_status.setBackgroundColor(getResources().getColor(
					R.color.vote_status_starting));
			expired = false;
		} else
			holder.tv_status.setBackgroundColor(getResources().getColor(
					R.color.vote_status_ended));

		holder.tv_title.setText(voteDetailBean.getTitle());
		holder.tv_content.setText(voteDetailBean.getContent());
		imgloader.displayImage(voteDetailBean.getImg_rel().trim(),
				holder.iv_img, dio);

		isSingle = voteDetailBean.getType() == 0;
		hasRes = voteDetailBean.getHas_res() == 0;
		/** 便于测试， 正式发布后，删除该条件 **/
		// hasRes = count++ % 2 ==0;
		View view = getView();
		// 显示单选 多选
		if (isSingle) {
			view.findViewById(R.id.tv_chooice_mode_single).setVisibility(
					View.VISIBLE);
		} else {
			view.findViewById(R.id.tv_chooice_mode_multiple).setVisibility(
					View.VISIBLE);
		}
		// 显示或隐藏 投票按钮
		holder.btn_voting.setVisibility(expired ? View.GONE
				: hasRes ? View.GONE : View.VISIBLE);
		holder.btn_voting.setOnClickListener(new VotingListener());

		setCommentCount(voteDetailBean.getComment_num());

		fillVoteRatios(voteDetailBean);
		initCommentDesc(voteDetailBean);
	}

	private void initCommentDesc(VoteDetailContainer vote) {
		if (watchManager != null) {
			HashMap<String, String> data = new HashMap<String, String>();
			data.put("title", vote.getTitle());
			data.put("source",
					getString(R.string.vote_source, vote.getVote_res()));
			watchManager.trigger(Watcher.TYPE_UPDATE_CONMENT_DESC, data);
		}
	}

	// private void initSelectedLayoutParams(){
	// Resources res = getResources();
	// selectedRect = new Rect();
	// selectedRect.top =
	// res.getDimensionPixelSize(R.dimen.vote_selected_ratio_margin_top);
	// selectedRect.bottom =
	// res.getDimensionPixelSize(R.dimen.vote_selected_ratio_padding_bottom);
	// }
	// private void initUnSelectedLayoutParams(){
	// Resources res = getResources();
	// unselectPaddingRect = new Rect();
	// unselectPaddingRect.right = unselectPaddingRect.bottom =
	// unselectPaddingRect.top =
	// res.getDimensionPixelSize(R.dimen.vote_unselect_ratio_padding_bottom);
	//
	// }

	private LayoutAnimationController getLayoutAnimation() {
		Animation anim = AnimationUtils.loadAnimation(activity,
				R.anim.vote_ratio_layout_anim);
		LayoutAnimationController lac = new LayoutAnimationController(anim);
		return lac;
	}

	private void startRemoveAnimation() {
		voteContainer.getLayoutParams().height = voteContainer.getHeight();
		voteContainer.setLayoutParams(voteContainer.getLayoutParams());
		final int childCount = voteContainer.getChildCount();
		for (int i = 0; i < childCount; i++) {
			Animation anim = AnimationUtils.loadAnimation(activity,
					R.anim.vote_ratio_alpha_remove);
			View v = voteContainer.getChildAt(i);
			anim.setAnimationListener(new RemoveRatioViewAnimListener(v));
			v.startAnimation(anim);
		}
	}

	/**
	 * 在投票结果返回后，移除原来的投票附件。并显示投票百分比，此时动画没能执行结束，投票组件就已经被移除， <BR />
	 * 为了解决这一问题，在投票组件完全移除后，再显示投票百分比组件。<br />
	 * 该类即移除动画结束后，执行的操作，保存了投票结果数据，在动画结束后，回填投票百分比数据。
	 * 
	 * @author yanyadi
	 * 
	 */
	class RemoveCallback implements Runnable {
		private VoteDetailContainer data;

		public RemoveCallback(VoteDetailContainer vote) {
			this.data = vote;
		}

		@Override
		public void run() {
			// 填充数据
			fillVoteRatios(data);
			// 重置 voteContainer layoutparams
			voteContainer.getLayoutParams().height = LayoutParams.WRAP_CONTENT;
			voteContainer.setLayoutParams(voteContainer.getLayoutParams());
		}

	}

	private void fillVoteRatios(VoteDetailContainer vote) {
		if (ratios.size() > 0) {
			ratios.clear();
			removedCallback = new RemoveCallback(vote);
			startRemoveAnimation();
			// 清空保存的列表
			return;
		}
		if (hasRes)
			voteContainer.setLayoutAnimation(getLayoutAnimation());
		// 清空容器
		// voteContainer.removeAllViews();

		final int mode_id = isSingle ? R.id.rdo_item : R.id.chk_item;
		final int layout_id = hasRes ? R.layout.vote_single_select_view
				: R.layout.vote_single_unselect_view;
		boolean isRegisterLayoutListener = false;
		for (int i = 0; i < vote.getItem().size(); i++) {
			Item item = vote.getItem().get(i);
			View itemView = infalter.inflate(layout_id, voteContainer, false);

			RatioHolder ratioHolder = new RatioHolder();
			itemView.setTag(ratioHolder);
			ratioHolder.button = (CompoundButton) itemView
					.findViewById(mode_id);
			// 显示多选 或者 单选按钮
			ratioHolder.button.setVisibility(View.VISIBLE);

			ratioHolder.item_id = item.getItem_id();
			// 选项描述信息
			ratioHolder.tv_item_desc = (TextView) itemView
					.findViewById(R.id.tv_vote_desc);
			ratioHolder.tv_item_desc.setText(item.getItem_content());

			// 如果是第一个，则注册 onLayout 事件，获取当前组件投票比率的最大宽度
			if (i == 0 && voteFullWidth == -1) {
				isRegisterLayoutListener = true;
				ratioHolder.tv_item_desc.getViewTreeObserver()
						.addOnGlobalLayoutListener(
								new LayoutChangeListener(
										ratioHolder.tv_item_desc));
			}

			// 如果有答案，填充投票百分比数据
			if (hasRes) {
				// 选项百分比描述
				ratioHolder.tv_ratio_desc = (TextView) itemView
						.findViewById(R.id.tv_ratio_desc);
				// 设置选项比率的颜色
				ratioHolder.ratio = itemView.findViewById(R.id.ratio_item);
				ratioHolder.ratio.setBackgroundColor(ratioBgFactory
						.getColorByPosition(i));
				// ====================================================
				ResItem resItem = vote.getRes_item().get(i);
				ratioHolder.tv_ratio_desc.setText(resItem.getRes_o());
				ratioHolder.ratioValue = getRealRatioValue(resItem.getRes_o());

				ratioHolder.button.setEnabled(false);
				ratioHolder.button.setFocusable(false);
				ratioHolder.button.setChecked(item.getIsChecked() == 1);
			} else {
				itemView.setOnClickListener(new RatioItemClickListener());
				ratioHolder.button
						.setOnCheckedChangeListener(new CheckedChangeListener());
				ratioHolder.button.setTag(itemView);
			}
			ratios.add(ratioHolder);
			voteContainer.addView(itemView);
		}

		// 如果有投票结果，并且未注册 layout listener ,则立即更新 投票比率
		if (hasRes && !isRegisterLayoutListener)
			updateRatioWidth();
	}

	/***
	 * 
	 * @author yanyadi
	 * 
	 * 
	 *         // private void fillVoteRatio(VoteDetailContainer vote) { // //
	 *         // 如果是更新数据 // if (ratios.size() > 0) { // // if (hasRes) { ////
	 *         initSelectedLayoutParams(); // // for (int i = 0; i <
	 *         vote.getItem().size(); i++) { // RatioHolder ratioHolder =
	 *         ratios.get(i); // ResItem resItem = vote.getRes_item().get(i); //
	 *         // ratioHolder.tv_ratio_desc.setText(resItem.getRes_o()); //
	 *         ratioHolder.ratioValue = getRealRatioValue(resItem //
	 *         .getRes_o()); // ////
	 *         ratioHolder.ratio.setVisibility(View.VISIBLE); ////
	 *         LinearLayout.LayoutParams lp =
	 *         (LinearLayout.LayoutParams)ratioHolder
	 *         .ratio_container.getLayoutParams(); //// lp.topMargin =
	 *         selectedRect.top; //// ratioHolder.ratio_container.setPadding(0,
	 *         0, 0, selectedRect.bottom); ////
	 *         ratioHolder.ratio_container.setLayoutParams(lp); // } // } //
	 *         updateRatioWidth(); // } else { // final int mode_id = isSingle ?
	 *         R.id.rdo_item : R.id.chk_item; // final int layout_id = hasRes ?
	 *         R.layout.vote_single_select_view // :
	 *         R.layout.vote_single_unselect_view; //// int layout_id =
	 *         R.layout.vote_single_select_view; // for (int i = 0; i <
	 *         vote.getItem().size(); i++) { // Item item =
	 *         vote.getItem().get(i); // // View itemView =
	 *         infalter.inflate(layout_id, voteContainer, // false); // //
	 *         RatioHolder ratioHolder = new RatioHolder(); //
	 *         itemView.setTag(ratioHolder); // ratioHolder.button =
	 *         (CompoundButton) itemView // .findViewById(mode_id); //
	 *         ratioHolder.button // .setOnCheckedChangeListener(new
	 *         CheckedChangeListener()); // // 显示多选 或者 单选按钮 //
	 *         ratioHolder.button.setVisibility(View.VISIBLE); // //
	 *         ratioHolder.item_id = item.getItem_id(); // // 选项描述信息 //
	 *         ratioHolder.tv_item_desc = (TextView) itemView //
	 *         .findViewById(R.id.tv_vote_desc); //
	 *         ratioHolder.tv_item_desc.setText(item.getItem_content()); // //
	 *         ratioHolder.ratio_container = (RelativeLayout) itemView //
	 *         .findViewById(R.id.ll_ratio_container); // // // 如果是第一个，则注册
	 *         onLayout 事件，获取当前组件投票比率的最大宽度 // if (i == 0 && voteFullWidth == -1)
	 *         { // ratioHolder.tv_item_desc.getViewTreeObserver() //
	 *         .addOnGlobalLayoutListener( // new LayoutChangeListener( //
	 *         ratioHolder.tv_item_desc)); // } // // // 如果有答案，填充投票百分比数据 // if
	 *         (hasRes) { // // 选项百分比描述 // ratioHolder.tv_ratio_desc =
	 *         (TextView) itemView // .findViewById(R.id.tv_ratio_desc); // //
	 *         设置选项比率的颜色 // ratioHolder.ratio =
	 *         itemView.findViewById(R.id.ratio_item); //
	 *         ratioHolder.ratio.setBackgroundColor(ratioBgFactory //
	 *         .getColorByPosition(i)); //
	 *         //==================================================== // ResItem
	 *         resItem = vote.getRes_item().get(i); //
	 *         ratioHolder.tv_ratio_desc.setText(resItem.getRes_o()); //
	 *         ratioHolder.ratioValue = getRealRatioValue(resItem //
	 *         .getRes_o()); // ratioHolder.button.setEnabled(false); //
	 *         ratioHolder.button.setFocusable(false); // } //
	 *         voteContainer.addView(itemView); // ratios.add(ratioHolder); // }
	 *         // } // }
	 * 
	 * @author yanyadi
	 * 
	 */

	class RemoveRatioViewAnimListener implements AnimationListener {
		private View v;

		public RemoveRatioViewAnimListener(View v1) {
			v = v1;
		}

		@Override
		public void onAnimationStart(Animation animation) {
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// 移动当前组件
			((ViewGroup) v.getParent()).removeView(v);
			// 如果回调参数不为空，并且父组件为空，则执行回填操作
			if (removedCallback != null && voteContainer.getChildCount() == 0) {
				voteContainer.post(removedCallback);
				removedCallback = null;
			}
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
		}

	}

	class RatioItemClickListener implements OnClickListener {
		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			RatioHolder holder = (RatioHolder) v.getTag();
			if (holder.button.isChecked())
				v.setBackgroundDrawable(null);
			else
				v.setBackgroundColor(getResources().getColor(
						R.color.vote_select_item_bg));

			holder.button.setChecked(!holder.button.isChecked());
		}

	}

	private void updateRatioWidth() {
		int defaultWidth = activity.getResources().getDimensionPixelOffset(
				R.dimen.vote_detail_ratio_default_width);
		for (RatioHolder holder : ratios) {
			int newX = Math.round(voteFullWidth * holder.ratioValue)
					+ defaultWidth;
			Animator anim = ObjectAnimator.ofInt(
					new AnimatorBean(holder.ratio), "width", newX);
			anim.setDuration(1000L);
			anim.setStartDelay(300L);
			anim.start();
		}

	}

	class RatioHolder {
		CompoundButton button;
		RelativeLayout ratio_container;
		int type;
		TextView tv_item_desc;
		TextView tv_ratio_desc;
		View ratio;
		String item_id;
		float ratioValue;
	}

	class CheckedChangeListener implements OnCheckedChangeListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (buttonView instanceof RadioButton)
				onCheckedChanged((RadioButton) buttonView, isChecked);

			if (isChecked)
				((View) buttonView.getTag()).setBackgroundColor(getResources()
						.getColor(R.color.vote_select_item_bg));
			else
				((View) buttonView.getTag()).setBackgroundDrawable(null);

		}

		@SuppressWarnings("deprecation")
		private void onCheckedChanged(RadioButton buttonView, boolean isChecked) {
			if (isChecked)
				for (RatioHolder holder : ratios) {
					if (holder.button != buttonView) {
						holder.button.setChecked(false);
						((View) holder.button.getTag())
								.setBackgroundDrawable(null);
					}
				}
		}

	}

	class VoteRatioBgFactory {
		public VoteRatioBgFactory() {
			colors = new int[] { R.color.ratio_color0, R.color.ratio_color1,
					R.color.ratio_color2, R.color.ratio_color3,
					R.color.ratio_color4, R.color.ratio_color5,
					R.color.ratio_color6, R.color.ratio_color7 };
			res = activity.getResources();
		}

		private int[] colors;
		private Resources res;

		public int getColorByPosition(int position) {
			int realIndex = position % colors.length;
			return res.getColor(colors[realIndex]);
		}
	}

	class VoteHolder {
		ImageView iv_img;
		TextView tv_status, tv_source, tv_num, tv_title, tv_content,
				tv_mode_single, tv_mode_multiple, tv_comment_line;
		Button btn_voting;
	}

	/**
	 * 投票按钮 事件监听
	 * 
	 * @author yanyadi
	 * 
	 */
	class VotingListener implements OnClickListener {

		@Override
		public void onClick(View v) {

			if (_Utils.checkLoginAndLogin(activity)) {
				String chooiced_ids = getVoteData(isSingle);
				if(TextUtils.isEmpty(chooiced_ids))
				{
					toast("请选择赞成的选项");
					return;
				}
				// 禁用该按钮
				v.setEnabled(false);

				ArrayList<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
				params.add(new BasicNameValuePair(KEY_INVESTIGATION_ID,
						investigation_id));
				/** 添加 user_name 参数，提交投票后显示用户名. by yyd  2015-03-26 **/
				params.add(new BasicNameValuePair("user_name", MessageEditor.getUserName(activity)));
				/** 增加用户手机号参数，by 闫亚迪 2015-05-25 **/
				params.add(new BasicNameValuePair("mobile", MessageEditor.getTel(activity)));
				
				params.add(new BasicNameValuePair("options_id", chooiced_ids));
				request(Constants.METHOD_INVESTIGATION_VOTE,
						new RequestCallback() {
							@Override
							public void requestSucc(String json) {
								getData();
							}
							@Override
							public void requestFailed(int errorCode) {
							}
						}, params);
			}
		}

		private String getVoteData(boolean isSingle) {
			if (isSingle) {
				for (RatioHolder holder : ratios)
					if (holder.button.isChecked())
						return holder.item_id;

				return "";
			} else {
				StringBuilder sb = new StringBuilder();
				for (RatioHolder holder : ratios)
					if (holder.button.isChecked()) {
						sb.append(holder.item_id);
						sb.append(",");
					}
				return sb.toString();
			}

		}
	}

	class LayoutChangeListener implements OnGlobalLayoutListener {
		public LayoutChangeListener(View v_) {
			this.v = v_;
		}

		private View v;

		@SuppressWarnings("deprecation")
		@Override
		public void onGlobalLayout() {
			voteFullWidth = this.v.getWidth()
					- activity.getResources().getDimensionPixelOffset(
							R.dimen.vote_detail_ratio_desc_width);
			;
			this.v.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			LogS.i("onGlobalLayout", " voteFullWidth  =" + voteFullWidth);

			// 如果有结果，则立即更新投票百分比
			if (hasRes)
				updateRatioWidth();
		}
	}

	class ShareWatcher implements Watcher {

		@Override
		public void trigger(Object obj) {
			if (voteDetailBean != null)
				_Utils.shareAndCheckLogin(activity, voteDetailBean.getTitle(),
						Constants_Community.APK_DOWNLOAD_URL,
						getString(R.string.share_content_vote),
						_Utils.getDefaultAppIcon(activity));
		}

		@Override
		public int getType() {
			return TYPE_SHARE;
		}
	}

	class PullToRefreshWatcher implements Watcher {

		@Override
		public void trigger(Object obj) {
			getData();
		}

		@Override
		public int getType() {
			return TYPE_PULLTOREFRESH;
		}

	}

	private float getRealRatioValue(String ratio) {
		DecimalFormat df = new DecimalFormat("##.00%");
		float f = 0.0f;
		try {
			f = df.parse(ratio).floatValue();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return f;
	}

	@Override
	protected String getAnalyticsTitle() {
		return "vote_detail";
	}
}
