package cn.ffcs.wisdom.city.entity;

import java.util.List;

import cn.ffcs.wisdom.city.personcenter.entity.MyRelevanceEntity.MyRelevance.MyRelevanceGroup;


public class QueryRelevanceEntity {
	List<MyRelevanceGroup> data;

	public List<MyRelevanceGroup> getData() {
		return data;
	}

	public void setData(List<MyRelevanceGroup> data) {
		this.data = data;
	}
}
