package cn.ffcs.wisdom.city.home.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ViewFlipper;
import cn.ffcs.wisdom.city.home.widget.interfaces.OnEditClickListener;
import cn.ffcs.wisdom.city.v6.R;

public class TrafficWidget extends BaseHomeWidget {

	private ViewFlipper mViewFlipper;
	private Button wzLine;
	private Button roadLine;
	private int mLastPosition = 0;
	private WzCarWidget wzWidget;
	private RoadVideoWidget roadWidget;

	public TrafficWidget(Context context) {
		super(context);
		initComponents();
	}
	
	public TrafficWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		initComponents();
	}

	public void initComponents() {
		mViewFlipper = (ViewFlipper) findViewById(R.id.ViewFlippermain);
		wzLine = (Button) findViewById(R.id.wz_line);
		wzLine.setOnClickListener(new MyOnclick());
		roadLine = (Button) findViewById(R.id.road_line);
		roadLine.setOnClickListener(new MyOnclick());
		wzWidget = (WzCarWidget) findViewById(R.id.wz_content);
		roadWidget = (RoadVideoWidget) findViewById(R.id.road_video_content);
	}

	@Override
	public void refresh() {

	}

	@Override
	public int setContentView() {
		return R.layout.widget_traffic;
	}

	class MyOnclick implements OnClickListener {
		@Override
		public void onClick(View v) {
			int id = v.getId();
			if (id == R.id.wz_line) {
				wzLine.setBackgroundResource(0);
				roadLine.setBackgroundResource(R.drawable.widget_tra_tag);
				wzWidget.setVisibility(View.VISIBLE);
				if (mLastPosition != 0) {
					mViewFlipper.setInAnimation(mContext, R.anim.push_right_in);
					mViewFlipper.getChildAt(mLastPosition).setVisibility(View.VISIBLE);
					mViewFlipper.setOutAnimation(mContext, R.anim.push_right_out);
					mViewFlipper.setDisplayedChild(0);
				}
				mLastPosition = 0;
			} else if (id == R.id.road_line) {
				wzLine.setBackgroundResource(R.drawable.widget_tra_tag);
				roadLine.setBackgroundResource(0);
				roadWidget.setVisibility(View.VISIBLE);
				if (mLastPosition != 1) {
					mViewFlipper.setInAnimation(mContext, R.anim.push_left_in);
					mViewFlipper.getChildAt(mLastPosition).setVisibility(View.VISIBLE);
					mViewFlipper.setOutAnimation(mContext, R.anim.push_left_out);
					mViewFlipper.setDisplayedChild(1);
				}
				mLastPosition = 1;
			}

		}
	}
	
    public void setOnEditClickListener(OnEditClickListener listener){
		wzWidget.setOnEidtClickListener(listener);
		roadWidget.setOnEditClickListener(listener);
    }
}
