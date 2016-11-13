package cn.ffcs.wisdom.city.test;

import java.util.HashMap;
import java.util.Map;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.ffcs.wisdom.base.CommonTask;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.entity.FeedbackEntity;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;

public class TestBaseTaskActivity extends WisdomCityActivity implements OnClickListener, HttpCallBack<BaseResp>{

	private String url = "http://ccgd.153.cn:50081/exter.shtml?baseLine=400&serviceType=1046";

	private Button mBtn;
	
	private Map<String, String> params = new HashMap<String, String>(1);
	
	@Override
	protected void initComponents() {
		mBtn = (Button) findViewById(R.id.test);
		mBtn.setOnClickListener(this);
	}

	@Override
	protected void initData() {
		// TODO Auto-generated method stub

	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_task_test;
	}

	@Override
	public void onClick(View v) {
		CommonTask task = CommonTask.newInstance(this, mContext, FeedbackEntity.class);
		params.put("imsi", "460030409013985");
		params.put("mobile", "18060477549");
		params.put("os_type", "1");
		task.setParams(params, url);
		task.execute();
	}

	@Override
	public void call(BaseResp response) {
		if(response.isSuccess()) {
//			String result = response.getHttpResult();
//			FeedbackEntity entity = (FeedbackEntity) response.getObj();
//			String status = entity.getStatus();
		}
		
	}

	@Override
	public void progress(Object... obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onNetWorkError() {
		// TODO Auto-generated method stub
		
	}

}
