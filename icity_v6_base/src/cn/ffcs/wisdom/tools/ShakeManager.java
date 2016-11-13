package cn.ffcs.wisdom.tools;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import cn.ffcs.wisdom.interfaces.IShakeCallBack;

/**<p>Title:  摇动监听   </p>
 * <p>Description:   回调摇动的次数在result  </p>
 * <p>@author: zhangws                 </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2012-9-11           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class ShakeManager {
	//检测摇动相关变量
	private SensorManager sm = null;// 摇动管理器
	private SensorEventListener acceleromererListener = null;//摇动监听器	
	private Context mContext;
	private IShakeCallBack iCall;// 回调
	private Sensor acceleromererSensor = null;// 加速度感应器 
	private int sensitivity = 11;// 灵敏度
	private static ShakeManager shakeManager;
	private static Vibrator vibrator;

	public static ShakeManager getShakeManger(Context context) {
		if (shakeManager == null) {
			shakeManager = new ShakeManager(context);
		}
		if (vibrator == null) {
			vibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);
		}
		return shakeManager;
	}

	private ShakeManager(Context context) {
		this.mContext = context;
	}

	/**
	 * 设置摇动回调
	 * @param iCall
	 */
	public void setShakeCallBack(IShakeCallBack iCall) {
		this.iCall = iCall;
	}

	/**
	 * 设置灵敏度
	 * @param sensitivity
	 */
	public void setSensitivity(int sensitivity) {
		this.sensitivity = sensitivity;
	}

	/**
	 * 注册摇动监听
	 */
	public void register() {
		// 获取传感器管理器
		if (sm == null) {
			sm = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
		}
		// 获取加速度传感器
		if (acceleromererSensor == null) {
			acceleromererSensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		// 定义传感器事件监听器
		if (acceleromererListener == null) {
			acceleromererListener = new SensorEventListener() {
				float mLastX = 0;
				float mLastY = 0;
				float mLastZ = 0;
				long mLastUpdateTime = 0;
				long mShakeTime = 0;
				int i = 0;
				boolean curShake = false;

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
				}

				//传感器数据变动事件
				@Override
				public void onSensorChanged(SensorEvent event) {
					long currentTime = System.currentTimeMillis();
					long diffTime = currentTime - mLastUpdateTime;
					if (diffTime < 100)
						return;
					mLastUpdateTime = currentTime;
					float x = event.values[0];
					float y = event.values[1];
					float z = event.values[2];
					float deltaX = x - mLastX;
					float deltaY = y - mLastY;
					float deltaZ = z - mLastZ;
					mLastX = x;
					mLastY = y;
					mLastZ = z;
//					double delta = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ)
//							/ diffTime * 10000;
					boolean isShake=(Math.abs(deltaX) > sensitivity||Math.abs(deltaY) > sensitivity||Math.abs(deltaZ) > sensitivity);
					if (isShake) { // 当加速度的差值大于指定的值，认为这是一个摇晃  
						curShake = true;
						i++;
						mShakeTime = System.currentTimeMillis();
					}
					long diffShareTime = currentTime - mShakeTime;
					if (diffShareTime > 500 && curShake) {
						int shakeSuc = i;
						vibrator.vibrate(500);
						iCall.call(shakeSuc);
						i = 0;
						curShake = false;
					}
				}
			};
		}
		sm.registerListener(acceleromererListener, acceleromererSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}

	/**
	 * 取消摇动监听
	 */
	public void unRegister() {
		if (sm != null && acceleromererListener != null) {
			Log.i("sm and acceleromererListener is not null");
			sm.unregisterListener(acceleromererListener);
		}
	}
}
