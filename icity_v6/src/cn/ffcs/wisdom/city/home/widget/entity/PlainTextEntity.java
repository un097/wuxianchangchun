package cn.ffcs.wisdom.city.home.widget.entity;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title: 纯文字信息         </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-4-10             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class PlainTextEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private List<PlainTextData> data;

	public List<PlainTextData> getData() {
		return data;
	}

	public void setData(List<PlainTextData> data) {
		this.data = data;
	}

	public class PlainTextData {
		private String title;// 标题
		private String link;// 链接地址
		private String urlType;// url类型
		private String requestDetailUrl; // 请求原生新闻内容地址

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getRequestDetailUrl() {
			return requestDetailUrl;
		}

		public void setRequestDetailUrl(String requestDetailUrl) {
			this.requestDetailUrl = requestDetailUrl;
		}

		public String getUrlType() {
			return urlType;
		}

		public void setUrlType(String urlType) {
			this.urlType = urlType;
		}
	}
}
