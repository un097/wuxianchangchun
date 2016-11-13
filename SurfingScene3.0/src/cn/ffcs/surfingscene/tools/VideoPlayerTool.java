package cn.ffcs.surfingscene.tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import cn.ffcs.config.ExternalKey;
import cn.ffcs.surfingscene.R;
import cn.ffcs.surfingscene.common.Key;
import cn.ffcs.surfingscene.road.RoadPlayActivity;

import com.ffcs.surfingscene.entity.GlobalEyeEntity;

/**
 * <p>Title:视频播放工具          </p>
 * <p>Description: 
 * </p>
 * <p>@author: Leo                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-6-25             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class VideoPlayerTool {

	/**
	 * 跳转到播放器
	 * @param activity
	 * @param entity
	 */
	public static void playVideo(Activity activity, GlobalEyeEntity entity) {
		if (entity == null) {
			return;
		}
		initParam(activity, entity);
	}

	/**
	 * 初始化参数
	 * @param activity
	 * @param entity
	 */
	public static void initParam(Activity activity, GlobalEyeEntity entity) {
		Intent intent = new Intent(activity, RoadPlayActivity.class);
		intent.putExtra(Key.K_RETURN_TITLE, activity.getString(R.string.glo_app_title));
		intent.putExtra(Key.K_IS_COLLECT, false);
		intent.putExtra(Key.K_PLAYER_TYPE, "2");//1代表路况播放器，2代表景点播放器
		startRoadVideo(activity, entity, intent);
	}

	/**
	 * 路况播放
	 * @param activity
	 * @param entity
	 * @param intent
	 */
	public static void startRoadVideo(Activity activity, GlobalEyeEntity entity, Intent intent) {
		Bundle bundle = new Bundle();
		bundle.putInt(ExternalKey.K_EYE_ID, entity.getGeyeId());
		bundle.putString(ExternalKey.K_EYE_TITLE, entity.getPuName());
		bundle.putInt(ExternalKey.K_HIGH_FLAG, entity.getHighflag());
		bundle.putString(Key.K_EYE_NAME, entity.getPuName());
		bundle.putString(Key.K_EYE_INTRO, entity.getIntro());
		bundle.putString(Key.K_GLO_TYPE, entity.getGeyeType() == null ? "" : entity.getGeyeType()
				.getTypeCode());
		bundle.putString(Key.K_ACTION_ID,
				entity.getActionId() == null ? "" : "" + entity.getActionId());
		bundle.putString(Key.K_AD_IMAGE, entity.getAdvertimgUrl());
		bundle.putString(Key.K_AD_CLICK, entity.getAdverlink());
		bundle.putString(Key.K_RTSP_ADDRESS, entity.getRtspAddr());
		bundle.putSerializable(Key.K_GLO_ENTITY, entity);
		bundle.putString(Key.K_VIDEO_TYPE, entity.getVlcplayerversion());
		intent.putExtras(bundle);
		activity.startActivity(intent);
//		activity.startActivityForResult(intent, 1);
	}
	
	
	/**
	 * 路况播放 带requestCode参数
	 * @param activity
	 * @param entity
	 * @param intent
	 */
	public static void startRoadVideo(Activity activity, GlobalEyeEntity entity, Intent intent,int requestCode) {
		Bundle bundle = new Bundle();
		bundle.putInt(ExternalKey.K_EYE_ID, entity.getGeyeId());
		bundle.putString(ExternalKey.K_EYE_TITLE, entity.getPuName());
		bundle.putInt(ExternalKey.K_HIGH_FLAG, entity.getHighflag());
		bundle.putString(Key.K_EYE_NAME, entity.getPuName());
		bundle.putString(Key.K_EYE_INTRO, entity.getIntro());
		bundle.putString(Key.K_GLO_TYPE, entity.getGeyeType() == null ? "" : entity.getGeyeType()
				.getTypeCode());
		bundle.putString(Key.K_ACTION_ID,
				entity.getActionId() == null ? "" : "" + entity.getActionId());
		bundle.putString(Key.K_AD_IMAGE, entity.getAdvertimgUrl());
		bundle.putString(Key.K_AD_CLICK, entity.getAdverlink());
		bundle.putString(Key.K_RTSP_ADDRESS, entity.getRtspAddr());
		bundle.putSerializable(Key.K_GLO_ENTITY, entity);
		bundle.putString(Key.K_VIDEO_TYPE, entity.getVlcplayerversion());
		intent.putExtras(bundle);
		activity.startActivityForResult(intent,requestCode);
	}
}
