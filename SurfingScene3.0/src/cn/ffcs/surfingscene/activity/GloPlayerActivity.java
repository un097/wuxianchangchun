//package cn.ffcs.surfingscene.activity;
//
//import java.io.Serializable;
//
//import android.view.GestureDetector;
//import android.view.GestureDetector.SimpleOnGestureListener;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//import cn.ffcs.surfingscene.R;
//import cn.ffcs.surfingscene.common.Key;
//import cn.ffcs.surfingscene.tools.GloSurfaceView;
//import cn.ffcs.wisdom.base.BaseActivity;
//import cn.ffcs.wisdom.tools.AppHelper;
//import cn.ffcs.wisdom.tools.CommonUtils;
//import cn.ffcs.wisdom.tools.Log;
//import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
//import cn.ffcs.wisdom.tools.StringUtil;
//
//import com.ffcs.surfingscene.entity.GlobalEyeEntity;
//import com.ffcs.surfingscene.function.SurfingScenePlayer;
//
///**
// * <p>Title:  开始播放        </p>
// * <p>Description: 
// * </p>
// * <p>@author: Leo                </p>
// * <p>Copyright: Copyright (c) 2012    </p>
// * <p>Company: FFCS Co., Ltd.          </p>
// * <p>Create Time: 2013-6-25             </p>
// * <p>Update Time:                     </p>
// * <p>Updater:                         </p>
// * <p>Update Comments:                 </p>
// */
//public class GloPlayerActivity extends BaseActivity {
//
//	private LinearLayout mGloPlayerLayout;
//	private int screenwidth;
//	private GloSurfaceView mGloPlayerSurfaceView;
//	private String rtspAddress;// rtsp地址
//	private int netType = 1;// 网络类型
//	private int linkType = 0;// 连接类型
//	private SurfingScenePlayer player;
//	private GestureDetector gestureDetector; // 手势判断
//	private TextView mTextProgress;
//	private int eyeId = 0;// 摄像头id
//	private int plid = 2;// 爱城市使用代码
//	private int playStatus;
//	private int playSubStatus;
//	private Long startTime;
//	private GlobalEyeEntity entitiy;
//	private String actionId = "712";
//	private String gloType;
//	private ImageView loadingImage;
//
//	@Override
//	protected void initComponents() {
//		mGloPlayerLayout = (LinearLayout) findViewById(R.id.glo_player_layout);
//		mGloPlayerSurfaceView = (GloSurfaceView) findViewById(R.id.glo_player_surfaceview);
//		mTextProgress = (TextView) findViewById(R.id.glo_player_progress);
//		loadingImage = (ImageView) findViewById(R.id.loading_img);
//	}
//
//	@Override
//	protected void initData() {
//		try {
//			screenwidth = AppHelper.getScreenWidth(mContext);
//			gestureDetector = new GestureDetector(new MyGestureDetector());
//			mGloPlayerLayout.getLayoutParams().height = screenwidth;
//			mGloPlayerLayout.invalidate();
//			player = new SurfingScenePlayer(mActivity);
//			player.init(mGloPlayerSurfaceView);
//			player.setprogress(mTextProgress, loadingImage);
//			Serializable serializable = getIntent().getSerializableExtra(Key.K_GLO_ENTITY);
//			if (serializable != null) {
//				entitiy = (GlobalEyeEntity) serializable;
//				rtspAddress = entitiy.getRtspAddr();
//				netType = entitiy.getNetType();
//				linkType = entitiy.getLinkType();
//				eyeId = entitiy.getGeyeId();
//				actionId = entitiy.getActionId() == null ? "" : "" + entitiy.getActionId();
//				if (StringUtil.isEmpty(actionId)) {
//					actionId = "712";
//				}
//				if (entitiy.getGeyeType() != null) {
//					gloType = entitiy.getGeyeType().getTypeCode();
//				}
//				if (StringUtil.isEmpty(gloType)) {
//					gloType = "1002";
//				}
//			}
//
//			if (!StringUtil.isEmpty(rtspAddress)) {// 调用rtsp播放
//				startTime = System.currentTimeMillis();
//				player.playVideo(rtspAddress, netType, linkType);
//			} else if (eyeId != 0) {// 调用摄像头id播放
//				startTime = System.currentTimeMillis();
//				player.playerVideo(eyeId, 2, plid, gloType, actionId);
//			}
//			showToast();
//		} catch (Exception e) {
//			Log.d("调用摄像头播放失败");
//		}
//	}
//
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
////		if(player!=null){
////			player.unReceiver();
////		}
//	}
//
//	@Override
//	public void finish() {
//		super.finish();
//		if (player != null) {
//			player.unReceiver();
//		}
//		Long endtime = System.currentTimeMillis();
//		playStatus = player.playStatus;
//		if (playStatus == 0) {
//			playSubStatus = 3;
//		} else {
//			playSubStatus = 1;
//		}
//		long playLongTime = endtime - startTime;
//		player.playStatistics(eyeId, null, "/geye/playCallback", playStatus, playSubStatus,
//				playLongTime);
//
//	}
//
//	private void showToast() {
//		boolean isShow = SharedPreferencesUtil.getBoolean(mContext, Key.K_SHOW_TOAST_KEY);
//		if (!isShow) {
//			SharedPreferencesUtil.setBoolean(mContext, Key.K_SHOW_TOAST_KEY, true);
//			CommonUtils.showToast(mActivity, R.string.glo_right_exit, Toast.LENGTH_SHORT);
//		}
//	}
//
//	// 手势判断滑动
//	public class MyGestureDetector extends SimpleOnGestureListener {
//
//		@Override
//		public boolean onFling(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//			float disX = Math.abs(e1.getX() - e2.getX());
//			float disY = Math.abs(e1.getY() - e2.getY());
//			if (disY < disX && disX - disY > 80) {
//				stopPlay();
//			}
//			return super.onFling(e1, e2, distanceX, distanceY);
//		}
//	}
//
//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		if (gestureDetector.onTouchEvent(event)) {
//			return true;
//		}
//		return false;
//	}
//
//	/**
//	 * 停止播放
//	 */
//	public void stopPlay() {
//		if (player != null) {
//			player.playEnd();// 关闭视频
//			finish();
//		}
//	}
//
//	@Override
//	protected int getMainContentViewId() {
//		return R.layout.glo_player;
//	}
//
//	@Override
//	protected Class<?> getResouceClass() {
//		return null;
//	}
//
//	/**
//	 * 监听键盘返回键
//	 */
//	@Override
//	public boolean dispatchKeyEvent(KeyEvent event) {
//		if (event.getAction() == KeyEvent.ACTION_DOWN
//				&& event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//			stopPlay();
//			return true;
//		}
//		return super.dispatchKeyEvent(event);
//	}
//
//	@Override
//	public void onBackPressed() {
//		super.onBackPressed();
//		stopPlay();
//	}
//}
