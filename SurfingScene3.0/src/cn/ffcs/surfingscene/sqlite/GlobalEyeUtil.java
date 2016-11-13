package cn.ffcs.surfingscene.sqlite;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;

public class GlobalEyeUtil {

	public static GlobalEye toGloBalEye(GlobalEyeEntity entity) {

		GlobalEye eye = new GlobalEye();

		if (entity != null) {
			eye.setGeyeId(entity.getGeyeId());
			eye.setActionId(entity.getActionId() != null ? entity.getActionId().intValue() : 0);
			eye.setHighflag(entity.getHighflag());
			eye.setImageUrl(entity.getImgUrl());
			eye.setPuName(entity.getPuName());
			eye.setRtspAddr(entity.getRtspAddr());
			eye.setThumUrl(entity.getThumUrl());
			eye.setIntro(entity.getIntro());
			eye.setVlcplayerversion(entity.getVlcplayerversion());
		}

		return eye;
	}
	
	public static GlobalEyeEntity toGloBalEyeEntity(GlobalEye entity) {

		GlobalEyeEntity eye = new GlobalEyeEntity();
		if (entity != null) {
			eye.setGeyeId(entity.getGeyeId());
			eye.setActionId(entity.getActionId() != null ? entity.getActionId().longValue() : 0);
			eye.setHighflag(entity.getHighflag());
			eye.setImgUrl(entity.getImageUrl());
			eye.setPuName(entity.getPuName());
			eye.setRtspAddr(entity.getRtspAddr());
			eye.setThumUrl(entity.getThumUrl());
			eye.setIntro(entity.getIntro());
			eye.setVlcplayerversion(entity.getVlcplayerversion());
		}

		return eye;
	}

}
