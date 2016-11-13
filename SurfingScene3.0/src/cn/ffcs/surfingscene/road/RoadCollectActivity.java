package cn.ffcs.surfingscene.road;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.activity.GlobaleyeBaseActivity;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.road.adapter.RoadCollectAdapter;
import cn.ffcs.surfingscene.sqlite.RoadCollect;
import cn.ffcs.surfingscene.sqlite.RoadCollectService;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;

public class RoadCollectActivity extends GlobaleyeBaseActivity {

	private ListView collectList;
	private RoadCollectAdapter adapter;
	private String phone;
	private List<RoadCollect> list;
	private LinearLayout noCollect;

	@Override
	protected void initComponents() {
		noCollect = (LinearLayout) findViewById(R.id.no_collect);
		collectList = (ListView) findViewById(R.id.collect_list);
		collectList.setFooterDividersEnabled(true);
		collectList.setOnItemClickListener(new CollectItemClick());
	}

	class CollectItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			RoadCollect entity = (RoadCollect) parent.getAdapter().getItem(position);
			Intent intent = new Intent(mActivity, RoadPlayActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.glo_road_title));
			VideoPlayerTool.startRoadVideo(mActivity, RoadCollect.converFromVideoEntity(entity), intent);
		}
	}

	@Override
	protected void initData() {
		phone = getIntent().getStringExtra(Key.K_PHONE_NUMBER);
	}

	@Override
	protected void onResume() {
		super.onResume();
		list = RoadCollectService.getInstance(mContext).getCollect(phone);
		if (list != null && list.size() > 0) {
			if (adapter == null) {
				adapter = new RoadCollectAdapter(mContext, phone);
				adapter.setData(list);
				collectList.setAdapter(adapter);
			} else {
				adapter.setData(list);
				adapter.notifyDataSetChanged();
			}
			noCollect.setVisibility(View.GONE);
		} else {
			noCollect.setVisibility(View.VISIBLE);
			if (adapter != null) {
				adapter.setData(list);
				adapter.notifyDataSetChanged();
			}
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_act_road_collect;
	}
}
