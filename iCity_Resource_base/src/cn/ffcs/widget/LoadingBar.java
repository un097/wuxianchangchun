package cn.ffcs.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.ffcs.config.R;

/**
 * <p>Title: 自定义加载进度条效果                                 </p>
 * <p>Description:                     </p>
 * <p>@author: liaodl                  </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-4-15           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class LoadingBar extends LinearLayout {

	private AnimationDrawable animation;
	private ImageView imageView;
	private TextView textView;
	
	private ProgressBar progress_loading;

	public LoadingBar(Context context) {
		super(context);
	}

	public LoadingBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.widget_loading_bar, this, true);
		imageView = (ImageView) findViewById(R.id.loading_bar_img);
		imageView.setVisibility(View.GONE);
		imageView.setBackgroundResource(R.anim.loading_bar_anim);
		animation = (AnimationDrawable) imageView.getBackground();
		imageView.post(new Runnable() {

			@Override
			public void run() {
				animation.setOneShot(false);
				animation.run();
			}
		});
		textView = (TextView) findViewById(R.id.loading_bar_tip);
		textView.setText(context.getString(R.string.common_loading));
		textView.setVisibility(View.GONE);
		
		progress_loading = (ProgressBar) findViewById(R.id.progress_loading);
	}

	/**
	 * 自定义提示信息
	 * @param messageStr
	 */
	public void setMessage(String messageStr) {
		textView.setText(messageStr);
	}

	/**
	 * 自定义字体颜色
	 * @param colors
	 */
	public void setTextColor(ColorStateList colors) {
		textView.setTextColor(colors);
	}

	/**
	 * 自定义字体颜色
	 * @param colors
	 */
	public void setTextColor(int color) {
		textView.setTextColor(color);
	}

	/**
	 * 自定义字体大小
	 * @param colors
	 */
	public void setTextSize(float size) {
		textView.setTextSize(size);
	}

	/**
	 * 隐藏旋转图片
	 */
	public void hideLoadingPic() {
//		imageView.setVisibility(View.GONE);
		progress_loading.setVisibility(View.GONE);
	}

	/**
	 * 显示旋转图片
	 */
	public void showLoadingPic() {
//		imageView.setVisibility(View.VISIBLE);
		progress_loading.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏加载提示信息文字
	 */
	public void hideLoadingTip() {
		textView.setVisibility(View.GONE);
	}

	/**
	 * 显示加载提示信息文字
	 */
	public void showLoadingTip() {
		textView.setVisibility(View.VISIBLE);
	}

	/**
	 * 自定义动画效果，覆盖默认的效果
	 * @param id
	 */
	public void setImageAnim(int id) {
		imageView.setBackgroundResource(id);
		animation = (AnimationDrawable) imageView.getBackground();
		animation.setOneShot(false);
		animation.run();
	}

}
