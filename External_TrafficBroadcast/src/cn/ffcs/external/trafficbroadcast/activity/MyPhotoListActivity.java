package cn.ffcs.external.trafficbroadcast.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.external_trafficbroadcast.R;

import cn.ffcs.external.trafficbroadcast.adapter.PhotoAibumAdapter;
import cn.ffcs.external.trafficbroadcast.entity.PhotoAibum;
import cn.ffcs.external.trafficbroadcast.entity.PhotoItem;
import cn.ffcs.external.trafficbroadcast.tool.DPIUtil;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/** 
 * 类描述： 相册管理类
 * 
 * @author: daizhq
 * 
 * @time: 2014-12-25 
 **/
public class MyPhotoListActivity extends Activity implements OnClickListener {
	
	// 返回键
	private LinearLayout ll_back;
	// 取消键
	private TextView tv_cancel;
	
	private int status = 1;
	private String currentLocationName = "";
	private String detail = "";
	private String face = "";
	
	private GridView aibumGV;
	private List<PhotoAibum> aibumList;
	
	//发布界面返回的已经选定的图片
	private List<String> alumList = new ArrayList<String>();

	// 设置获取图片的字段信息
	private static final String[] STORE_IMAGES = {
			MediaStore.Images.Media.DISPLAY_NAME, // 显示的名�?
			MediaStore.Images.Media.DATA,
			MediaStore.Images.Media.LONGITUDE, // 经度
			MediaStore.Images.Media._ID, // id
			MediaStore.Images.Media.BUCKET_ID, // dir id 目录
			MediaStore.Images.Media.BUCKET_DISPLAY_NAME // dir name 目录名字
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		DPIUtil.getScreenMetrics(this);		
		setContentView(R.layout.act_traffic_photo_list);
		
		ll_back = (LinearLayout) findViewById(R.id.ll_back);
		tv_cancel = (TextView) findViewById(R.id.tv_cancel);
		
		ll_back.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
		
		alumList = (List<String>) getIntent().getExtras().getSerializable("alumList");
		status = getIntent().getExtras().getInt("status");
		currentLocationName = getIntent().getExtras().getString("currentLocationName");
		detail = getIntent().getExtras().getString("detail");
		face = getIntent().getExtras().getString("face");
		
		aibumGV = (GridView) findViewById(R.id.album_gridview);
		aibumList = getPhotoAlbum();
		aibumGV.setAdapter(new PhotoAibumAdapter(aibumList, this));
		aibumGV.setOnItemClickListener(aibumClickListener);
	}

	/**
	 * 相册点击事件
	 */
	OnItemClickListener aibumClickListener =  new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				int position, long id) {
			Intent intent = new Intent(MyPhotoListActivity.this,
					TrafficPhotoAlumActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("alumList", (Serializable) alumList);
			intent.putExtras(bundle);
			intent.putExtra("aibum", aibumList.get(position));
			intent.putExtra("status", status);
			intent.putExtra("currentLocationName", currentLocationName);
			intent.putExtra("detail", detail);
			intent.putExtra("face", face);
			startActivity(intent);
			finish();
		}
	};
	/**
	 * 方法描述：按相册获取图片信息
	 * 
	 * @author: why
	 * @time: 2013-10-18 下午1:35:24
	 */
	private List<PhotoAibum> getPhotoAlbum() {
		List<PhotoAibum> aibumList = new ArrayList<PhotoAibum>();
		Cursor cursor = MediaStore.Images.Media.query(getContentResolver(),
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STORE_IMAGES);
		Map<String, PhotoAibum> countMap = new HashMap<String, PhotoAibum>();
		PhotoAibum pa = null;
		while (cursor.moveToNext()) {
//			String path=cursor.getString(1);
			String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
			String id = cursor.getString(3);
			String dir_id = cursor.getString(4);
			String dir = cursor.getString(5);
			if (!countMap.containsKey(dir_id)) {
				pa = new PhotoAibum();
				pa.setName(dir);
				pa.setBitmap(Integer.parseInt(id));
				pa.setCount("1");
				pa.getBitList().add(new PhotoItem(Integer.valueOf(id),path));
				countMap.put(dir_id, pa);
				pa.setPath(path);
			} else {
				pa = countMap.get(dir_id);
				pa.setCount(String.valueOf(Integer.parseInt(pa.getCount()) + 1));
				pa.getBitList().add(new PhotoItem(Integer.valueOf(id),path));
				pa.setPath(path);
			}
		}
		cursor.close();
		Iterable<String> it = countMap.keySet();
		for (String key : it) {
			aibumList.add(countMap.get(key));
		}
		return aibumList;
	}
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_back) {
			Intent intent_back = new Intent();
			intent_back.setClass(MyPhotoListActivity.this,
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
		} else if (id == R.id.tv_cancel) {
			Intent intent_back = new Intent();
			intent_back.setClass(MyPhotoListActivity.this,
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
		}
		
	}
	@Override
	public void onBackPressed() {
		Intent intent_back = new Intent();
		intent_back.setClass(MyPhotoListActivity.this,
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
	}
}
