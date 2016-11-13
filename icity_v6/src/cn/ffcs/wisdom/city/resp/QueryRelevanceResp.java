package cn.ffcs.wisdom.city.resp;

import java.util.List;

import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance.MyRelevanceGroup;
import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance.MyRelevanceGroup.MyRelevanceDetail;
import cn.ffcs.wisdom.http.BaseResp;

public class QueryRelevanceResp extends BaseResp {
	private List<MyRelevanceGroup> list;

	public List<MyRelevanceGroup> getList() {
		return list;
	}

	public void setList(List<MyRelevanceGroup> list) {
		this.list = list;
	}

	public String getValue(String key, int keyGroupId) {
		if (list != null) {
			for (int j = 0; j < list.size(); j++) {
				MyRelevanceGroup myRelevanceGroup = list.get(j);
				if (keyGroupId == -1) {
					List<MyRelevanceDetail> detailList = myRelevanceGroup.getKeyList();
					for (int i = 0; i < detailList.size(); i++) {
						String keyName = detailList.get(i).getKeyName();
						if (keyName.equals(key)) {
							return detailList.get(i).getKeyValue();
						}
					}
				} else if (keyGroupId != -1 && myRelevanceGroup.getKeyGroupId() == keyGroupId) {
					List<MyRelevanceDetail> detailList = myRelevanceGroup.getKeyList();
					for (int i = 0; i < detailList.size(); i++) {
						String keyName = detailList.get(i).getKeyName();
						if (keyName.equals(key)) {
							return detailList.get(i).getKeyValue();
						}
					}
				}
			}
		}
		return "";
	}

	public String getIsPrimaryChk() {
		if (list != null && list.size() > 0) {
			return list.get(0).getIsPrimaryChk();
		}
		return "0"; // 返回默认不记住
	}

	public String getIsPrivateChk() {
		if (list != null && list.size() > 0) {
			return list.get(0).getIsPrivateChk();
		}
		return "0"; // 返回默认不记住
	}
}
