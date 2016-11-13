package cn.ffcs.wisdom.city.home.widget;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.AttributeSet;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.AppHelper;
import cn.ffcs.wisdom.tools.BitmapUtil;
import cn.ffcs.wisdom.tools.Log;
import cn.ffcs.wisdom.tools.StringUtil;

/**
 * <p>Title:     自动移动背景,openGL实现              </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                 </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2014-3-26           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class MoveGLSurfaceView extends GLSurfaceView {
	private Bitmap bitmapBg;// 背景图

	private float mSurfaceWidth;// 屏幕宽
	private float mSurfaceHeight;// 屏幕高
	private boolean bitmapChange = false;

	// 背景移动状态
	private enum State {
		LEFT, RINGHT
	}

	public MoveGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mSurfaceWidth = AppHelper.getScreenWidth(context);
		mSurfaceHeight = AppHelper.getScreenHeight(context);
		initBitmap(context);
		setRenderer(new MoveRenderer());
	}

	/**
	 * 初始化Bitmap
	 */
	public void initBitmap(Context context) {
		try {
			String bgPath = null;
			if (!StringUtil.isEmpty(bgPath)) {
				Bitmap bitmap = BitmapUtil.readBitMapByLowMemory(bgPath);
				changeBitmap(bitmap);
			} else {
				changeBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.home_background));
			}
		} catch (Exception e) {
			changeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.home_background));
			Log.e("Exception: " + e.getMessage());
		}
	}

	/**
	 * 改变bitmap大小
	 * @param bitmap
	 */
	private void changeBitmap(Bitmap bitmap) {
		bitmapBg = BitmapUtil.changeBitmap(bitmap, mSurfaceWidth * 3 / 2, mSurfaceHeight);
		bitmapChange = true;
	}

	class MoveRenderer implements Renderer {

		private float[] mQuadsArray = { 2f, 1f, 0f, -1f, 1f, 0f, -1f, -1f, 0f, 2f, -1f, 0f };// 定义一个1.5倍屏幕的长方形矩阵
		private float[] mTexCoordArray = { 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f };// 定义纹理矩阵
		private FloatBuffer mQuadsBuffer;
		private FloatBuffer mTexCoordBuffer;

		// 默认为向左
		private State state = State.LEFT;

		private float startPostion = 0f;
		private final float BITMAP_STEP = 0.0005f;// 背景画布移动步伐.

		@Override
		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			gl.glShadeModel(GL10.GL_SMOOTH);// 设置平滑着色
			gl.glClearColor(0f, 0f, 0f, 0f);// 设置清屏颜色
			gl.glEnable(GL10.GL_TEXTURE_2D); // 允许2D纹理
//			gl.glClearDepthf(1.0f);// 设置深度缓存值
//			gl.glEnable(GL10.GL_DEPTH_TEST);// 启用深度测试
//			gl.glDepthFunc(GL10.GL_LEQUAL);// 设置深度测试函数
//			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);// 透视修正
			gl.glFrontFace(GL10.GL_CCW);
			mQuadsBuffer = floatToBuffer(mQuadsArray);
			mTexCoordBuffer = floatToBuffer(mTexCoordArray);
			loadCoord(gl, bitmapBg);
		}

		@Override
		public void onSurfaceChanged(GL10 gl, int width, int height) {
//			float ratio = (float) width / height;
			gl.glViewport(0, 0, width, height);//设置OpenGL场景的大小
			gl.glMatrixMode(GL10.GL_PROJECTION);//设置投影矩阵
			gl.glLoadIdentity();//重置投影矩阵
//			gl.glFrustumf(-ratio, ratio, -1, 1, 1, 10);// 设置视口的大小
			gl.glMatrixMode(GL10.GL_MODELVIEW);// 选择模型观察矩阵
			gl.glLoadIdentity();// 重置模型观察矩阵
		}

		@Override
		public void onDrawFrame(GL10 gl) {
			if (bitmapChange) {
				loadCoord(gl, bitmapBg);
			}

			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);// 清屏
			gl.glLoadIdentity();// 初始化当前矩阵
			gl.glTranslatef(startPostion, 0, 0);// x平移
			switch (state) {
			case RINGHT:
				startPostion -= BITMAP_STEP;
				break;
			case LEFT:
				startPostion += BITMAP_STEP;
				break;
			default:
				break;
			}

			if (startPostion <= -1f) {
				state = State.LEFT;
			}
			if (startPostion >= 0f) {
				state = State.RINGHT;
			}
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);// 允许顶点数组
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);// 允许纹理数组
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexCoordBuffer);// 设置纹理顶点
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mQuadsBuffer);// 设置定点数组
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);// 画长方形

			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);// 不允许顶点数组
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);// 不允许纹理数组

		}

		/**
		 * float数组转floatBuffer
		 * @param a
		 * @return
		 */
		public FloatBuffer floatToBuffer(float[] a) {
			FloatBuffer mBuffer;
			//先初始化buffer，数组的长度*4，因为一个float占4个字节  
			ByteBuffer mbb = ByteBuffer.allocateDirect(a.length * 4);
			//数组排序用nativeOrder  
			mbb.order(ByteOrder.nativeOrder());
			mBuffer = mbb.asFloatBuffer();
			mBuffer.put(a);
			mBuffer.position(0);
			return mBuffer;
		}
	}

	/**
	 * 创建纹理
	 * @param gl
	 */
	private void loadCoord(GL10 gl, Bitmap coordBitmap) {
		try {
			IntBuffer intBuffer = IntBuffer.allocate(1);
			gl.glGenTextures(1, intBuffer);// 初始化一个纹理
			int texture = intBuffer.get();
			gl.glBindTexture(GL10.GL_TEXTURE_2D, texture);// 绑定纹理
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);// 缩小过滤，线性过滤
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);// 放大过滤，线性过滤
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_REPEAT);// S方向(水平方向)边缘修正
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);// T方向(垂直方向)边缘修正
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, coordBitmap, 0);// 将图片设置到绑定的纹理上
		} finally {
			if (coordBitmap != null) {
				coordBitmap.recycle();
			}
			bitmapChange = false;
		}
	}
}
