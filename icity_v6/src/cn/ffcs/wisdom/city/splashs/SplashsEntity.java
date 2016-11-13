package cn.ffcs.wisdom.city.splashs;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Title:  闪屏接口实体类                                               </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-5-9            </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class SplashsEntity implements Serializable {

	private static final long serialVersionUID = -5073341436075134792L;

	private String desc;
	private String status;
	private String errorMsg;
	private Date date;

	public class Date implements Serializable {
		private static final long serialVersionUID = -7950364983052986882L;

		private String playTime;
		private List<Url> splash;
		private String cityCode;

		public class Url implements Serializable {
			private static final long serialVersionUID = -3664876936377932629L;

			private String url;

			public String getUrl() {
				return url;
			}

			public void setUrl(String url) {
				this.url = url;
			}
		}

		public String getPlayTime() {
			return playTime;
		}

		public void setPlayTime(String playTime) {
			this.playTime = playTime;
		}

		public String getCityCode() {
			return cityCode;
		}

		public void setCityCode(String cityCode) {
			this.cityCode = cityCode;
		}

		public List<Url> getSplash() {
			return splash;
		}

		public void setSplash(List<Url> splash) {
			this.splash = splash;
		}

	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
