package cn.ffcs.wisdom.city.setting;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import cn.ffcs.ui.tools.TopUtil;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.setting.service.ListenNetStateService;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;

public class Flow3gSettingActivity extends WisdomCityActivity {
	private ImageView flow3g_switch;

	@Override
	protected void initComponents() {
		flow3g_switch = (ImageView) findViewById(R.id.flow3g_switch);
		if (SharedPreferencesUtil.getBoolean(mContext, Key.K_3G_SWITCH) == false) {
			flow3g_switch.setImageResource(R.drawable.setting_flow3g_switch_on);
		} else {
			flow3g_switch.setImageResource(R.drawable.setting_flow3g_switch_off);
		}
		flow3g_switch.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (SharedPreferencesUtil.getBoolean(mContext, Key.K_3G_SWITCH) == true) {
					SharedPreferencesUtil.setBoolean(mContext, Key.K_3G_SWITCH, false);
					flow3g_switch.setImageResource(R.drawable.setting_flow3g_switch_on);
					Intent service = new Intent(mContext, ListenNetStateService.class);
					startService(service);
				} else {
					SharedPreferencesUtil.setBoolean(mContext, Key.K_3G_SWITCH, true);
					flow3g_switch.setImageResource(R.drawable.setting_flow3g_switch_off);
					Intent service = new Intent(mContext, ListenNetStateService.class);
					stopService(service);
				}
			}
		});
	}

	@Override
	protected void initData() {
		TopUtil.updateTitle(mActivity, R.id.top_title, R.string.setting_3g);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_flow3g_setting;
	}

}
