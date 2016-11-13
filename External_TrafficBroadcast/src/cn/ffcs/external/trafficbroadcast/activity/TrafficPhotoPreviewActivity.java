package cn.ffcs.external.trafficbroadcast.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.ffcs.wisdom.city.utils.CityImageLoader;

import com.example.external_trafficbroadcast.R;
import com.ffcs.surfingscene.sdk.MainActivity;

/**
 * 路况大图预览界面
 * 
 * @author daizhq
 * 
 * @date 2014.12.03
 * */
public class TrafficPhotoPreviewActivity extends Activity implements OnClickListener{
	
	//返回键
	private LinearLayout ll_back;
	
	//标题
	private TextView tv_title;
	
	//删除图标
	private ImageView iv_del;
	//图片展示区域
	private ImageView iv_preview;
	
	//完成按钮
	private Button btn_count;
	
	//顶部标题
	private String title = "";
	//图片地址
	private String picPath = "";
	//图片加载器
	private CityImageLoader loader;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_traffic_photo);
		
		loadView();
		loadData();
	}

	/**
	 * 加载本页面控件
	 * */
	private void loadView() {
		// TODO Auto-generated method stub
		
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		iv_del = (ImageView) findViewById(R.id.iv_del);
		btn_count = (Button) findViewById(R.id.btn_count);
		iv_preview = (ImageView) findViewById(R.id.iv_preview);
		
		ll_back.setOnClickListener(this);
		btn_count.setOnClickListener(this);
		iv_del.setOnClickListener(this);
	}
	
	/**
	 * 加载页面数据
	 * */
	private void loadData() {
		// TODO Auto-generated method stub
		
		loader = new CityImageLoader(TrafficPhotoPreviewActivity.this);
		
		picPath = getIntent().getExtras().getString("path");
		title = getIntent().getExtras().getString("title");
		System.out.println("path===>>"+picPath+",  title===>>"+title);
		tv_title.setText(title);
		iv_preview.setImageBitmap(BitmapFactory.decodeFile(picPath));
		
		loader.loadUrl(iv_preview, picPath);
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_back) {
			finish();
		} else if (id == R.id.iv_del) {
		} else if (id == R.id.btn_count) {
		} else {
		}
	}

}
