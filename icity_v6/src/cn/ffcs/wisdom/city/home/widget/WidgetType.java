package cn.ffcs.wisdom.city.home.widget;

/**
 * <p>Title:   widget类型     </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2014-3-24           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public enum WidgetType {

	TEXT_WIDGET("0", "文字widget"), IMAGE_WIDGET("1", "图片widget"), TRAFFIC_WIDGET("2", "交通widget"), TRAVEL_WIDGET(
			"3", "旅游widget"), ROAD_WIDGET("4", "单独路况视频widget"), NEWS_WIDGET("5", "原生图片新闻widget");

	private String value;
	private String des;

	WidgetType(String value, String des) {
		this.value = value;
		this.des = des;
	}

	public String getValue() {
		return value;
	}

	public String getDes() {
		return des;
	}

	/**
	 * 重写equals方法
	 * @param data
	 * @return
	 */
	public boolean equals(String data) {
		return this.value.equals(data);
	}
}
