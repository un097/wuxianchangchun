package cn.ffcs.surfingscene.road;

import java.util.List;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.activity.GlobaleyeBaseActivity;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.datamgr.RoadVideoListMgr;
import cn.ffcs.surfingscene.road.adapter.OftenBlockedAdapter;
import cn.ffcs.surfingscene.road.bo.RoadBo;
import cn.ffcs.surfingscene.tools.VideoPlayerTool;
import cn.ffcs.widget.LoadingBar;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.ffcs.surfingscene.http.HttpCallBack;
import com.ffcs.surfingscene.response.BaseResponse;

/**
 * <p>Title:                           </p>
 * <p>Description:   
 *  	被MapGroupListActivity取代
 * </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-8-15           </p>
 * <p>@author:   liaodl                </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
@Deprecated
public class MapListActivity extends GlobaleyeBaseActivity {
	private OftenBlockedAdapter adapter;
	private ListView listView;
	private String collectType = "1024";
	private String phone;
	private List<GlobalEyeEntity> list;
	private String gloType = "1000";
	private String areaCode;
	private LoadingBar loadingBar;

	@Override
	protected void initComponents() {
		listView = (ListView) findViewById(R.id.map_list);
		listView.setOnItemClickListener(new MapListItemClick());
		loadingBar = (LoadingBar) findViewById(R.id.loading_bar);
	}

	@Override
	protected void initData() {
		areaCode = getIntent().getStringExtra(Key.K_AREA_CODE);
		phone = getIntent().getStringExtra(Key.K_PHONE_NUMBER);
		list = RoadVideoListMgr.getInstance().getGloList();
		if (list != null) {
			showData();
		} else {
			geteye();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (adapter != null) {
			adapter.notifyDataSetChanged();
		}
	}

	/**
	 * 列表点击
	 */
	class MapListItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			GlobalEyeEntity entity = (GlobalEyeEntity) parent.getAdapter().getItem(position);
			Intent intent = new Intent(mActivity, RoadPlayActivity.class);
			intent.putExtra(Key.K_RETURN_TITLE, getString(R.string.glo_video_list_name));
			VideoPlayerTool.startRoadVideo(mActivity, entity, intent);
		}
	}

	/**
	 * 显示数据
	 */
	private void showData() {
		adapter = new OftenBlockedAdapter(mActivity, phone, collectType);
		adapter.setData(list);
		listView.setAdapter(adapter);
	}

	/**
	 * 获取数据
	 */
	private void geteye() {
		loadingBar.setVisibility(View.VISIBLE);
		RoadBo.getInstance().getVideoList(mContext, areaCode, gloType, new GeteyeCallBack());
	}

	class GeteyeCallBack implements HttpCallBack<BaseResponse> {

		@Override
		public void callBack(BaseResponse resp, String arg1) {
			loadingBar.setVisibility(View.GONE);
			if (resp.getReturnCode().equals("1")) {
				list = resp.getGeyes();
				RoadVideoListMgr.getInstance().setGloList(list);
				showData();
			} else {
				CommonUtils.showToast(mActivity, R.string.glo_error, Toast.LENGTH_SHORT);
			}
		}
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.glo_act_map_list;
	}
}
