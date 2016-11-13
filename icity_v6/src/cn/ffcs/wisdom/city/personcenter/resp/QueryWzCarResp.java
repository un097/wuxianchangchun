package cn.ffcs.wisdom.city.personcenter.resp;

import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.notify.WzCarEntity;

public class QueryWzCarResp extends BaseResp {
	
	private WzCarEntity entity;

	public WzCarEntity getEntity() {
		return entity;
	}

	public void setEntity(WzCarEntity entity) {
		this.entity = entity;
	}

}
