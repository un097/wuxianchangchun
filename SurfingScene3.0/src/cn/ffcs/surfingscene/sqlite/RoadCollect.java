package cn.ffcs.surfingscene.sqlite;

import cn.ffcs.wisdom.tools.StringUtil;

import com.ffcs.surfingscene.entity.GeyeType;
import com.ffcs.surfingscene.entity.GlobalEyeEntity;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "t_road_collect")
public class RoadCollect {

	@DatabaseField(generatedId = true)
	public Integer id;

	@DatabaseField(columnName = "geye_id", index = true)
	public Integer geyeId; // 视频id

	@DatabaseField(columnName = "user_id")
	public String userId; // 用户Id

	@DatabaseField(columnName = "pub_name")
	public String pubName; // 视频名

	@DatabaseField(columnName = "intro")
	public String intro; // 视频描述

	@DatabaseField(columnName = "lat")
	public String lat;// 纬度

	@DatabaseField(columnName = "lng")
	public String lng;// 经度

	@DatabaseField(columnName = "glo_type")
	public String gloType;// 播放类型

	@DatabaseField(columnName = "action_id")
	public String actionId;// 活动id

	@DatabaseField(columnName = "ad_url")
	public String adUrl;// 广告url

	@DatabaseField(columnName = "ad_click_url")
	public String adClickUrl;// 广告点击url

	@DatabaseField(columnName = "rtsp_address")
	public String rtspAddress;// rtsp地址

	@DatabaseField(columnName = "hdVideo")
	public Integer hdVideo;// 高清/标清

	@DatabaseField(columnName = "is_new_video_player")
	public String isNewVideoPlayer;// 新旧播放器

	public static RoadCollect converVideoEntity(String userId, GlobalEyeEntity eyeEntity) {
		RoadCollect collectEntitiy = new RoadCollect();
		collectEntitiy.geyeId = eyeEntity.getGeyeId();
		collectEntitiy.userId = userId;
		collectEntitiy.pubName = eyeEntity.getPuName();
		collectEntitiy.intro = eyeEntity.getIntro();
		collectEntitiy.lat = eyeEntity.getLatitude();
		collectEntitiy.lng = eyeEntity.getLongitude();
		if (eyeEntity.getGeyeType() != null) {
			collectEntitiy.gloType = eyeEntity.getGeyeType().getTypeCode();
		}
		collectEntitiy.actionId = eyeEntity.getActionId() == null ? "" : ""
				+ eyeEntity.getActionId();
		collectEntitiy.adUrl = eyeEntity.getAdvertimgUrl();
		collectEntitiy.adClickUrl = eyeEntity.getAdverlink();
		collectEntitiy.rtspAddress = eyeEntity.getRtspAddr();
		collectEntitiy.hdVideo = eyeEntity.getHighflag();
		collectEntitiy.isNewVideoPlayer = eyeEntity.getVlcplayerversion();
		return collectEntitiy;
	}

	public static GlobalEyeEntity converFromVideoEntity(RoadCollect collectEntity) {
		GlobalEyeEntity entity = new GlobalEyeEntity();
		entity.setGeyeId(collectEntity.geyeId);
		entity.setPuName(collectEntity.pubName);
		entity.setIntro(collectEntity.intro);
		entity.setLatitude(collectEntity.lat);
		entity.setLongitude(collectEntity.lng);
		GeyeType geyeType = new GeyeType();
		geyeType.setTypeCode(collectEntity.gloType);
		entity.setGeyeType(geyeType);
		entity.setActionId((StringUtil.isEmpty(collectEntity.actionId) || collectEntity.actionId
				.equals("null")) ? 0 : Long.parseLong(collectEntity.actionId));
		entity.setAdvertimgUrl(collectEntity.adUrl);
		entity.setAdverlink(collectEntity.adClickUrl);
		entity.setRtspAddr(collectEntity.rtspAddress);
		entity.setHighflag(collectEntity.hdVideo);
		entity.setVlcplayerversion(collectEntity.isNewVideoPlayer);
		return entity;
	}
}
