package cn.ffcs.wisdom.city.entity;

import java.util.List;

import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity;
import cn.ffcs.wisdom.city.traffic.violations.entity.TrafficViolationsEntity.TrafficViolationsInfo;

public class WZconfig {
	public String getIsPrimaryChk() {
		return isPrimaryChk;
	}

	public void setIsPrimaryChk(String isPrimaryChk) {
		this.isPrimaryChk = isPrimaryChk;
	}

	public String getIsPrivateChk() {
		return isPrivateChk;
	}

	public void setIsPrivateChk(String isPrivateChk) {
		this.isPrivateChk = isPrivateChk;
	}

	public String getKeyGroupId() {
		return keyGroupId;
	}

	public void setKeyGroupId(String keyGroupId) {
		this.keyGroupId = keyGroupId;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}


	String isPrimaryChk;
	String isPrivateChk;
	String keyGroupId;
	String itemId;
	List<Wzlist> keyList;
	List<TrafficViolationsInfo> detaillist;
	
	public List<TrafficViolationsInfo> getDetaillist() {
		return detaillist;	
	}

	public void setDetaillist(List<TrafficViolationsInfo> detaillist) {
		this.detaillist = detaillist;
	}

	public List<Wzlist> getKeyList() {
		return keyList;
	}

	public void setKeyList(List<Wzlist> keyList) {
		this.keyList = keyList;
	}
}
