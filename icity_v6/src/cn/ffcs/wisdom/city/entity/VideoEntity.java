package cn.ffcs.wisdom.city.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 视频实体类
 * @author Eric.wsd
 * @since 2012-4-10
 */
public class VideoEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1140678237340990534L;

	private String returnCode;
	private String apiMethod;
	private String subMsg;
	private String subCode;
	private String errorCode;
	private String msg;
	private List<VideoEntity.VideoItem> videos = new ArrayList<VideoEntity.VideoItem>();

	public String getReturnCode() {
		return returnCode;
	}

	public void setReturnCode(String returnCode) {
		this.returnCode = returnCode;
	}

	public String getApiMethod() {
		return apiMethod;
	}

	public void setApiMethod(String apiMethod) {
		this.apiMethod = apiMethod;
	}

	public String getSubMsg() {
		return subMsg;
	}

	public void setSubMsg(String subMsg) {
		this.subMsg = subMsg;
	}

	public String getSubCode() {
		return subCode;
	}

	public void setSubCode(String subCode) {
		this.subCode = subCode;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<VideoItem> getVideos() {
		return videos;
	}

	public void setVideos(List<VideoItem> videos) {
		this.videos = videos;
	}

	public class VideoItem implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -4232193570687858992L;

		private Integer videoId;
		private String videoName;
		private String rtsp;
		private Long videoSize;
		private String intro;
		private String lable;
		private String countryId;
		private String imgUrl;
		private String thumUrl;
		private Long addTime;
		private String provinceId;
		private String cityId;
		private Integer videoTypeId;

		public Integer getVideoId() {
			return videoId;
		}

		public void setVideoId(Integer videoId) {
			this.videoId = videoId;
		}

		public String getVideoName() {
			return videoName;
		}

		public void setVideoName(String videoName) {
			this.videoName = videoName;
		}

		public String getRtsp() {
			return rtsp;
		}

		public void setRtsp(String rtsp) {
			this.rtsp = rtsp;
		}

		public Long getVideoSize() {
			return videoSize;
		}

		public void setVideoSize(Long videoSize) {
			this.videoSize = videoSize;
		}

		public String getIntro() {
			return intro;
		}

		public void setIntro(String intro) {
			this.intro = intro;
		}

		public String getImgUrl() {
			return imgUrl;
		}

		public void setImgUrl(String imgUrl) {
			this.imgUrl = imgUrl;
		}

		public String getThumUrl() {
			return thumUrl;
		}

		public void setThumUrl(String thumUrl) {
			this.thumUrl = thumUrl;
		}

		public Long getAddTime() {
			return addTime;
		}

		public void setAddTime(Long addTime) {
			this.addTime = addTime;
		}

		public String getProvinceId() {
			return provinceId;
		}

		public void setProvinceId(String provinceId) {
			this.provinceId = provinceId;
		}

		public String getCityId() {
			return cityId;
		}

		public void setCityId(String cityId) {
			this.cityId = cityId;
		}

		public Integer getVideoTypeId() {
			return videoTypeId;
		}

		public void setVideoTypeId(Integer videoTypeId) {
			this.videoTypeId = videoTypeId;
		}

		public String getLable() {
			return lable;
		}

		public void setLable(String lable) {
			this.lable = lable;
		}

		public String getCountryId() {
			return countryId;
		}

		public void setCountryId(String countryId) {
			this.countryId = countryId;
		}
	}
}
