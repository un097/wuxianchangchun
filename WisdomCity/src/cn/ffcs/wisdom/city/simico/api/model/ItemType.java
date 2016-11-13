package cn.ffcs.wisdom.city.simico.api.model;

import java.util.ArrayList;
import java.util.List;

public class ItemType {

	@SuppressWarnings("rawtypes")
	protected List list = new ArrayList();
	private String typeName;
	private String typeName2;
	private boolean isTop;

	public ItemType() {
	}

	@SuppressWarnings("rawtypes")
	public List getList() {
		return list;
	}

	@SuppressWarnings("rawtypes")
	public void setList(List list) {
		this.list = list;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public boolean isTop() {
		return isTop;
	}

	public void setTop(boolean isTop) {
		this.isTop = isTop;
	}

	public String getTypeName2() {
		return typeName2;
	}

	public void setTypeName2(String typeName2) {
		this.typeName2 = typeName2;
	}
}
