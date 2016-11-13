package cn.ffcs.wisdom.city.myapp;

import java.util.ArrayList;
import java.util.List;

import android.widget.GridView;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.myapp.adapter.EditAppGridViewAdapter;
import cn.ffcs.wisdom.city.sqlite.model.MenuItem;
import cn.ffcs.wisdom.city.v6.R;

/**
 * <p>Title: 我的应用         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-2-28           </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class EditMyAppActivity extends WisdomCityActivity {
	private EditAppGridViewAdapter mEditAppGridViewAdapter;
	private GridView mMyAppGridView;
	private List<MenuItem> mMyAppList = new ArrayList<MenuItem>();

	@Override
	protected void initComponents() {
		mMyAppGridView = (GridView) findViewById(R.id.myapp_list);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.myapp_edit);
		mMyAppList = (List<MenuItem>) getIntent().getSerializableExtra("mMyAppList");
		if (mMyAppList.size() > 1) {// 去掉空白的最后一项
			mMyAppList.remove(mMyAppList.size() - 1);
		}
		mEditAppGridViewAdapter = new EditAppGridViewAdapter(mActivity, mMyAppList);
		mMyAppGridView.setAdapter(mEditAppGridViewAdapter);
	}

	@Override
	public void finish() {
		setResult(RESULT_OK);
		super.finish();
	}
	
	@Override
	protected int getMainContentViewId() {
		return R.layout.act_myapp_edit;
	}
}
