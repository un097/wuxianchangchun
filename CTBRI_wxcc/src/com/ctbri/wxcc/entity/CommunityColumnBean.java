package com.ctbri.wxcc.entity;

import java.util.List;

public class CommunityColumnBean {
	
	private CommunityData data;
	
	public CommunityData getData() {
		return data;
	}
	public void setData(CommunityData data) {
		this.data = data;
	}
	
	public static class CommunityData{
		private List<Column> columns;

		public List<Column> getColumns() {
			return columns;
		}

		public void setColumns(List<Column> columns) {
			this.columns = columns;
		}
	}
	
	public static class Column {
		private String column_id;
		private String column_name;
		public String getColumn_id() {
			return column_id;
		}
		public void setColumn_id(String column_id) {
			this.column_id = column_id;
		}
		public String getColumn_name() {
			return column_name;
		}
		public void setColumn_name(String column_name) {
			this.column_name = column_name;
		}

	}
}
