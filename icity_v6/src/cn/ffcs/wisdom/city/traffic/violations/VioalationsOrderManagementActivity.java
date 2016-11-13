package cn.ffcs.wisdom.city.traffic.violations;

import android.view.View;
import android.widget.ListView;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.v6.R;

/**
 * <p>Title:  违章订单管理       </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-9-24           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class VioalationsOrderManagementActivity extends WisdomCityActivity {

	private ListView orderList;
	private View noData;

	@Override
	protected void initComponents() {
		orderList = (ListView) findViewById(R.id.order_list);
		noData = findViewById(R.id.no_data);
		orderList.setEmptyView(noData);
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.violation_order_mgr);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_vioalations_order_management;
	}
}
