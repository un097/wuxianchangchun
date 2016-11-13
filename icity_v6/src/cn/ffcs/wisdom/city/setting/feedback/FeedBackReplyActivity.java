package cn.ffcs.wisdom.city.setting.feedback;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.entity.FeedbackEntity;
import cn.ffcs.wisdom.city.entity.FeedbackEntity.FeedbackItem;
import cn.ffcs.wisdom.city.push.PushMsgBo;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
/**
 * 
 * <p>Title:反馈历史信息        </p>
 * <p>Description: 
 * 显示反馈的历史信息列表
 * </p>
 * <p>@author: xzw               </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-4             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class FeedBackReplyActivity extends WisdomCityActivity implements
		HttpCallBack<BaseResp> {
    private TextView topTitle;
	private List<FeedbackItem> list = new ArrayList<FeedbackItem>();
	private ListView mFeedbackList;
	private LinearLayout mNoReply;
	private LinearLayout mFeedbackLayout;
	
	private LinearLayout loading_bar = null;// 进度条

	private FeedbackAdapter adapter;

	@Override
	protected void initComponents() {
		topTitle=(TextView)findViewById(R.id.top_title);
		mFeedbackList = (ListView) findViewById(R.id.feedback_listview);
		mNoReply = (LinearLayout) findViewById(R.id.no_data);
		mFeedbackLayout = (LinearLayout) findViewById(R.id.feedback_result_layout);
		loading_bar = (LinearLayout) findViewById(R.id.loading_bar);
	}

	@Override
	protected void initData() {
		showProgressBar();
		topTitle.setText(getResources().getString(R.string.feedback_conversations));
		FeedBackBo bo=new FeedBackBo(mContext, this);
		loading_bar.setVisibility(View.VISIBLE);// 显示加载控件
		bo.feedBackReply();
		//消息回执 --liaodl
		new PushMsgBo(mActivity).pushFeedbackAndClearMsg(Config.REBACK_FEED_BACK_MSG_TYPE);
	}

	/**
	 * 渲染ListView
	 */
	private void updateListView() {
		if (adapter == null) {
			adapter = new FeedbackAdapter(mContext, list);
			mFeedbackList.setAdapter(adapter);
		} else {
			adapter.notifyDataSetChanged();
		}
	}

	@Override
	protected Class<?> getResouceClass() {
		return R.class;
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_feedback_conversations;
	}

	@Override
	public void call(BaseResp response) {
		hideProgressBar();
		if (response.isSuccess()) {
			list.clear();
			FeedbackEntity entity = (FeedbackEntity) response.getObj();
			if (entity != null) {
				List<FeedbackItem> itemList = entity.getList();
				if (itemList != null && itemList.size() > 0) {
					mFeedbackList.setVisibility(View.VISIBLE);
					mNoReply.setVisibility(View.GONE);
					list.addAll(itemList);
					updateListView();
				} else {
					mFeedbackList.setVisibility(View.GONE);
					mNoReply.setVisibility(View.VISIBLE);
				}

				mFeedbackLayout.requestLayout();
			}
		} else {
			Toast.makeText(mContext, "请求失败", Toast.LENGTH_SHORT).show();
		}
		loading_bar.setVisibility(View.GONE);// 隐藏加载控件
	}

	@Override
	public void progress(Object... obj) {

	}

	@Override
	public void onNetWorkError() {

	}

}
