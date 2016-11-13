package cn.ffcs.surfingscene.sqlite;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_globaleye")
public class GlobalEye {

	@DatabaseField(generatedId = true)
	private Integer id;

	@DatabaseField(columnName = "geye_id", index = true)
	private Integer geyeId; // 视频id

	@DatabaseField(columnName = "pu_name")
	private String puName; // 名称

	@DatabaseField(columnName = "high_flag")
	private Integer highflag; // 高清标识 0：非高清 1:高清

	@DatabaseField(columnName = "rtsp_addr")
	private String rtspAddr; // Rtsp播放地址

	@DatabaseField(columnName = "image_url")
	private String imageUrl; // 原图

	@DatabaseField(columnName = "thum_url")
	private String thumUrl; // 缩略图

	@DatabaseField(columnName = "action_id", index = true)
	private Integer actionId; // 活动id

	@DatabaseField(columnName = "favorite", index = true)
	private Integer favorite; // 收藏 0：收藏 1:未收藏

	@DatabaseField(columnName = "tyjx_code")
	private String tyjxCode; // 天翼景象code

	@DatabaseField(columnName = "intro")
	private String intro;
	
	@DatabaseField(columnName = "vlcplayerversion")
	private String vlcplayerversion;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getGeyeId() {
		return geyeId;
	}

	public void setGeyeId(Integer geyeId) {
		this.geyeId = geyeId;
	}

	public String getPuName() {
		return puName;
	}

	public void setPuName(String puName) {
		this.puName = puName;
	}

	public Integer getHighflag() {
		return highflag;
	}

	public void setHighflag(Integer highflag) {
		this.highflag = highflag;
	}

	public String getRtspAddr() {
		return rtspAddr;
	}

	public void setRtspAddr(String rtspAddr) {
		this.rtspAddr = rtspAddr;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getThumUrl() {
		return thumUrl;
	}

	public void setThumUrl(String thumUrl) {
		this.thumUrl = thumUrl;
	}

	public Integer getActionId() {
		return actionId;
	}

	public void setActionId(Integer actionId) {
		this.actionId = actionId;
	}

	public Integer getFavorite() {
		return favorite;
	}

	public void setFavorite(Integer favorite) {
		this.favorite = favorite;
	}

	public String getTyjxCode() {
		return tyjxCode;
	}

	public void setTyjxCode(String tyjxCode) {
		this.tyjxCode = tyjxCode;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getVlcplayerversion() {
		return vlcplayerversion;
	}

	public void setVlcplayerversion(String vlcplayerversion) {
		this.vlcplayerversion = vlcplayerversion;
	}
	
	
}
