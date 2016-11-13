package cn.ffcs.wisdom.city.simico.api.model;

import org.json.JSONException;
import org.json.JSONObject;

import cn.ffcs.wisdom.city.simico.kit.util.DateUtil;

public class EventLog {
	private String areaId;
	private String itemType;
	private String itemId;
	private String subItemId;
	private String desc;
	private String timestamp;
	private String field1;
	private String field2;

	private int status;
	private String user;
	private String cityCode;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getItemType() {
		return itemType;
	}

	public void setItemType(String itemType) {
		this.itemType = itemType;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getSubItemId() {
		return subItemId;
	}

	public void setSubItemId(String subItemId) {
		this.subItemId = subItemId;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	public String getField1() {
		return field1;
	}

	public void setField1(String field1) {
		this.field1 = field1;
	}

	public String getField2() {
		return field2;
	}

	public void setField2(String field2) {
		this.field2 = field2;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public JSONObject toJSONObject() throws JSONException {
		JSONObject json = new JSONObject();
		// ---------------------------------//

		// ----------------------------------//
		json.put("area_id", areaId);
		json.put("item_type", itemType);
		json.put("item_id", itemId);
		json.put("sub_item_id", subItemId);
		json.put("desc", desc);
		json.put("timestamp", DateUtil.getDateStr(Long.valueOf(timestamp),"yyyy-MM-dd HH:mm:ss"));
		json.put("field1", field1);
		json.put("field2", field2);
		json.put("city_code", cityCode);
		return json;
	}
}
