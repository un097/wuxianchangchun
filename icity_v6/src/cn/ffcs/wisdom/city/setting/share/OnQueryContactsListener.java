package cn.ffcs.wisdom.city.setting.share;

import java.util.List;
import java.util.Map;

public interface OnQueryContactsListener {
	void onQueryStart();
	
	void onQueryFinish(List<Map<String, Object>> list);
}
