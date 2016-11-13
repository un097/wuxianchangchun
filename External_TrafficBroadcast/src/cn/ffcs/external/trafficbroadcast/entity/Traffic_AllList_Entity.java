package cn.ffcs.external.trafficbroadcast.entity;

import java.util.List;

/**
 * 全部路况列表详情类
 * 
 * @author daizhq
 * 
 * @date 2014.12.10
 * */
public class Traffic_AllList_Entity {

	// 结果码
	private String result_code;
	// 结果描述
	private String result_desc;
	// 时间戳，格式为yyyy-MM-dd HH:mm:ss，例如：2008-01-25 20:23:30
	private String timestamp;
	// 地图页面上的打点列表单项
	private List<Traffic_AllItem_Entity> data;

	public String getResult_code() {
		return result_code;
	}

	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}

	public String getResult_desc() {
		return result_desc;
	}

	public void setResult_desc(String result_desc) {
		this.result_desc = result_desc;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public List<Traffic_AllItem_Entity> getData() {
		return data;
	}

	public void setData(List<Traffic_AllItem_Entity> data) {
		this.data = data;
	}

}
