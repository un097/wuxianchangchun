package cn.ffcs.external.trafficbroadcast.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.external.trafficbroadcast.adapter.AlbumAdapter;
import cn.ffcs.external.trafficbroadcast.adapter.BottomAlbumAdapter;
import cn.ffcs.external.trafficbroadcast.entity.PhotoAibum;
import cn.ffcs.external.trafficbroadcast.entity.PhotoItem;
import cn.ffcs.external.trafficbroadcast.view.MyHGridView;
import cn.ffcs.wisdom.tools.CommonUtils;

import com.example.external_trafficbroadcast.R;

/**
 * 相册图片界面
 * 
 * @author daizhq
 * 
 * @date 2014.12.04
 * */
public class TrafficPhotoAlumActivity extends Activity implements
		OnClickListener {

	// 返回键
	private LinearLayout ll_back;
	// 取消键
	private TextView tv_cancel;

	// 已选取数量
	private Button btn_num;

	// 图片陈列区域
	private GridView gv_photo;
	private AlbumAdapter albumAdapter;

	private ArrayList<PhotoItem> gl_arr = new ArrayList<PhotoItem>();

	// 预览图片陈列区域
	private MyHGridView gv_photo_bottom;
	private LayoutInflater inflater;
	private BottomAlbumAdapter bottomAlbumAdapter;

	// 选中的图片列表信息
	private ArrayList<String> paths = new ArrayList<String>();
	private ArrayList<String> ids = new ArrayList<String>();

	// 已经选中的图片数量
	private int chooseNum = 0;

	// 上个界面传递过来的相册信息
	private PhotoAibum aibum;
	private int status = 1;
	private String currentLocationName = "";
	private String detail = "";
	private String face = "";

	private List<String> alumList = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_photo_alum);

		loadView();
		loadData();
	}

	/**
	 * 加载本页面控件
	 * */
	private void loadView() {
		// TODO Auto-generated method stub

		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);

		btn_num = (Button) findViewById(R.id.btn_count);

		gv_photo = (GridView) findViewById(R.id.gv_photo);
		gv_photo_bottom = (MyHGridView) findViewById(R.id.gv_photo_bottom);

		ll_back.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		btn_num.setOnClickListener(this);
		
		btn_num.setText("确定(0)");
	}

	/**
	 * 加载本页面数据以及控件监听
	 * */
	private void loadData() {
		// TODO Auto-generated method stub

		alumList = (List<String>) getIntent().getExtras().getSerializable("alumList");
		
		if(alumList != null){
			
			paths.addAll(alumList);
		}
		status = getIntent().getExtras().getInt("status");
		currentLocationName = getIntent().getExtras().getString(
				"currentLocationName");
		detail = getIntent().getExtras().getString("detail");
		face = getIntent().getExtras().getString("face");

		aibum = (PhotoAibum) getIntent().getExtras().get("aibum");
		
		//标记已选定的图片
		if(alumList != null){
			for(int j=0; j<alumList.size(); j++){
				for (int i = 0; i < aibum.getBitList().size(); i++) {
					if(aibum.getBitList().get(i).getPath().equals(alumList.get(j))){
						aibum.getBitList().get(i).setSelect(true);
						chooseNum++;
					}
				}
			}
		}
		
		/** 
		 * 获取已经选择的图片
		**/
		for (int i = 0; i < aibum.getBitList().size(); i++) {
			if (aibum.getBitList().get(i).isSelect()) {
//				chooseNum++;
				gl_arr.add(aibum.getBitList().get(i));
			}
		}

		albumAdapter = new AlbumAdapter(TrafficPhotoAlumActivity.this, aibum,
				null);
		gv_photo.setAdapter(albumAdapter);
		gv_photo.setOnItemClickListener(gvItemClickListener);

//		gv_photo_bottom.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view,
//					int position, long id) {
//				String path = bottomAlbumAdapter.getItem(position).getPath();
//				Intent intent = new Intent(TrafficPhotoAlumActivity.this,
//						TrafficPhotoPreviewActivity.class);
//				intent.putExtra("path", path);
//				startActivity(intent);
//			}
//		});
		for (int i = 0; i < gl_arr.size(); i++) {
			inite(gl_arr.get(i), gl_arr.get(i).isSelect());
		}

	}

	/**
	 * 照片列表点击事件
	 * */
	private OnItemClickListener gvItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			PhotoItem gridItem = aibum.getBitList().get(position);
			if (aibum.getBitList().get(position).isSelect()) {
				aibum.getBitList().get(position).setSelect(false);
				paths.remove(aibum.getBitList().get(position).getPath());
				ids.remove(aibum.getBitList().get(position).getPhotoID() + "");
				gl_arr.remove(aibum.getBitList().get(position));
				chooseNum--;
				inite(aibum.getBitList().get(position),
						aibum.getBitList().get(position).isSelect());
			} else {
				if(paths.size() < 6){
					aibum.getBitList().get(position).setSelect(true);
					ids.add(aibum.getBitList().get(position).getPhotoID() + "");
					paths.add(aibum.getBitList().get(position).getPath());
					gl_arr.add(aibum.getBitList().get(position));
					chooseNum++;
					inite(aibum.getBitList().get(position),
							aibum.getBitList().get(position).isSelect());
				}else{
					CommonUtils.showToast((Activity) TrafficPhotoAlumActivity.this, "最多只能上传6张图片！",
							Toast.LENGTH_SHORT);
				}
			}
			albumAdapter.notifyDataSetChanged();
		}
	};

	/**
	 * 初始化被选中的图片的方法 将图片添加或者删除
	 * */
	private void inite(PhotoItem str, boolean isSeclect) {
		bottomAlbumAdapter = new BottomAlbumAdapter(this, aibum, gl_arr);
		if (isSeclect) {
			btn_num.setText("确定(" + gl_arr.size() + ")");
		} else {
			btn_num.setText("确定(" + gl_arr.size() + ")");
		}
		inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		gv_photo_bottom.setAdapter(bottomAlbumAdapter);
		int size = gl_arr.size();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float density = dm.density;
		int allWidth = (int) (110 * size * density);
		int itemWidth = (int) (75 * density);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				allWidth, LinearLayout.LayoutParams.FILL_PARENT);
		gv_photo_bottom.setLayoutParams(params);
		gv_photo_bottom.setColumnWidth(itemWidth);
		gv_photo_bottom.setHorizontalSpacing(10);
		gv_photo_bottom.setStretchMode(GridView.NO_STRETCH);
		gv_photo_bottom.setNumColumns(size);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.btn_count) {
			 Intent intent_back = new Intent();
			 intent_back.setClass(TrafficPhotoAlumActivity.this,
			 TrafficPublishActivity.class);
			 Bundle bundle = new Bundle();
			 bundle.putSerializable("alumList", (Serializable) paths);
			 intent_back.putExtras(bundle);
			 intent_back.putExtra("status", status);
			 intent_back.putExtra("currentLocationName", currentLocationName);
			 intent_back.putExtra("detail", detail);
			 intent_back.putExtra("face", face);
			 startActivity(intent_back);
			 finish();
		} else if (id == R.id.ll_back) {
			Intent intent = new Intent();
			intent.setClass(TrafficPhotoAlumActivity.this,
					MyPhotoListActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("alumList", (Serializable) alumList);
			intent.putExtras(bundle);
			intent.putExtra("status", status);
			intent.putExtra("currentLocationName", currentLocationName);
			intent.putExtra("detail", detail);
			intent.putExtra("face", face);
			startActivity(intent);
			finish();
		} else if (id == R.id.tv_cancel) {
			Intent intent_back = new Intent();
			intent_back.setClass(TrafficPhotoAlumActivity.this,
					TrafficPublishActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("alumList", (Serializable) alumList);
			intent_back.putExtras(bundle);
			intent_back.putExtra("status", status);
			intent_back.putExtra("currentLocationName", currentLocationName);
			intent_back.putExtra("detail", detail);
			intent_back.putExtra("face", face);
			startActivity(intent_back);
			finish();
		} else if (id == R.id.ll_preview) {
			Intent intent_preview = new Intent();
			intent_preview.setClass(TrafficPhotoAlumActivity.this,
					TrafficPhotoPreviewActivity.class);
			startActivity(intent_preview);
		} else {
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setClass(TrafficPhotoAlumActivity.this,
				MyPhotoListActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("alumList", (Serializable) alumList);
		intent.putExtras(bundle);
		intent.putExtra("status", status);
		intent.putExtra("currentLocationName", currentLocationName);
		intent.putExtra("detail", detail);
		intent.putExtra("face", face);
		startActivity(intent);
		finish();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		System.gc();
	}
}
