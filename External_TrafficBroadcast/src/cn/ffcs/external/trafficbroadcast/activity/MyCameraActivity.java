package cn.ffcs.external.trafficbroadcast.activity;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.tools.SdCardTool;

import com.example.external_trafficbroadcast.R;

/**
 * 拍照界面
 * 
 * @author daizhq
 * 
 * @date 2014.12.04
 * */
public class MyCameraActivity extends Activity implements OnClickListener {

	// 拍照视野控件
	private SurfaceView surfaceView;
	// 返回键
	private ImageView iv_back;
	// 拍照键
	private RelativeLayout rl_camera;

	private Camera camera;
	private Camera.Parameters parameters = null;
	// 声明一个Bundle对象，用来存储数据
	Bundle bundle = null;
	
	private List<String> alumList = new ArrayList<String>();
	private int status = 1;
	private String currentLocationName = "";
	private String detail = "";
	private String face = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_take_photo);
		
		alumList = (List<String>) getIntent().getExtras().getSerializable("alumList");
		status = getIntent().getExtras().getInt("status");
		currentLocationName = getIntent().getExtras().getString("currentLocationName");
		detail = getIntent().getExtras().getString("detail");
		face = getIntent().getExtras().getString("face");

		loadView();
	}

	/**
	 * 加载本页面控件
	 * */
	private void loadView() {
		// TODO Auto-generated method stub
		surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		rl_camera = (RelativeLayout) findViewById(R.id.rl_camera);

		iv_back.setOnClickListener(this);
		rl_camera.setOnClickListener(this);

		surfaceView.getHolder()
				.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// 设置Surface分辨率
		surfaceView.getHolder().setFixedSize(176, 144);
		// 屏幕常亮
		surfaceView.getHolder().setKeepScreenOn(true);
		// 为SurfaceView的句柄添加一个回调函数
		surfaceView.getHolder().addCallback(new SurfaceCallback());
	}

	private final class SurfaceCallback implements Callback {

		// 拍照状态变化时调用该方法
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			parameters = camera.getParameters(); // 获取各项参数
			parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
			parameters.setPreviewSize(width, height); // 设置预览大小
			parameters.setPreviewFrameRate(5); // 设置每秒显示4帧
			parameters.setPictureSize(width, height); // 设置保存的图片尺寸
			parameters.setJpegQuality(80); // 设置照片质量
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		}

		// 开始拍照时调用该方法
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				// 打开摄像头
				camera = Camera.open();
				// 设置用于显示拍照影像的SurfaceHolder对象
				camera.setPreviewDisplay(holder);
				camera.setDisplayOrientation(getPreviewDegree(MyCameraActivity.this));
				// 开始预览
				camera.startPreview();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		// 停止拍照时调用该方法
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				// 释放照相机
				camera.release();
				camera = null;
			}
		}
	}

	// 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
	public static int getPreviewDegree(Activity activity) {
		// 获得手机的方向
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degree = 0;
		// 根据手机的方向计算相机预览画面应该选择的角度
		switch (rotation) {
		case Surface.ROTATION_0:
			degree = 90;
			break;
		case Surface.ROTATION_90:
			degree = 0;
			break;
		case Surface.ROTATION_180:
			degree = 270;
			break;
		case Surface.ROTATION_270:
			degree = 180;
			break;
		}
		return degree;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.iv_back) {
			finish();
		} else if (id == R.id.rl_camera) {
			if (camera != null) {
				camera.autoFocus(new AutoFocusListener());
			}
		} else {
		}
	}
	
	private class AutoFocusListener implements AutoFocusCallback {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			if (success) {
				camera.takePicture(null, null, new MyPictureCallback());
			}

		}

	}

	private final class MyPictureCallback implements PictureCallback {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			try {
				Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
				// 根据拍摄的方向旋转图像（纵向拍摄时要需要将图像选择90度)
				Matrix matrix = new Matrix();
				matrix.setRotate(MyCameraActivity.getPreviewDegree(MyCameraActivity.this));
				Bitmap cameraBitmap = Bitmap
						.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
								bitmap.getHeight(), matrix, true);
				Date date = new Date();
				SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss"); // 格式化时间
				String filename = format.format(date) + ".jpg";
				Config config = new Config();
				config.init(MyCameraActivity.this);
				File mf = SdCardTool.save(cameraBitmap, config.SDCARD_CITY_TMP, File.separator
						+ filename + ".jpg");
				String load = config.SDCARD_CITY_TMP + File.separator
						+ filename + ".jpg";
				if(alumList == null){
					alumList = new ArrayList<String>();
				}
				alumList.add(load);
				
				Intent intent_back = new Intent();
				intent_back.setClass(MyCameraActivity.this,
						TrafficPublishActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("alumList", (Serializable) alumList);
				intent_back.putExtras(bundle);
				intent_back.putExtra("status", status);
				intent_back.putExtra("currentLocationName", currentLocationName);
				intent_back.putExtra("detail", detail);
				intent_back.putExtra("face", face);
				startActivity(intent_back);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			finish();
		}
	}
	
}
