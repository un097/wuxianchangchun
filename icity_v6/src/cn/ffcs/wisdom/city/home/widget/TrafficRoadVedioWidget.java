package cn.ffcs.wisdom.city.home.widget;

import android.content.Context;
import android.util.AttributeSet;
import cn.ffcs.wisdom.city.home.widget.interfaces.OnEditClickListener;
import cn.ffcs.wisdom.city.v6.R;

public class TrafficRoadVedioWidget extends BaseHomeWidget {


	private RoadVideoWidget roadWidget;
	public TrafficRoadVedioWidget(Context context) {
		super(context);
	}
	
	public TrafficRoadVedioWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		roadWidget = (RoadVideoWidget) findViewById(R.id.road_video_content);
	}


	@Override
	public void refresh() {

	}

	@Override
	public int setContentView() {
		return R.layout.widget_traffic_road_video;
	}
	
    public void setOnEditClickListener(OnEditClickListener listener){
		roadWidget.setOnEditClickListener(listener);
    }
}
