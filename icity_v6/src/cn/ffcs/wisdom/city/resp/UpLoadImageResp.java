package cn.ffcs.wisdom.city.resp;

import java.util.List;

import cn.ffcs.wisdom.city.entity.UpLoadImage;
import cn.ffcs.wisdom.http.BaseResp;

public class UpLoadImageResp extends BaseResp {
	private List<UpLoadImage> list;

	public List<UpLoadImage> getList() {
		return list;
	}

	public void setList(List<UpLoadImage> list) {
		this.list = list;
	}
}
