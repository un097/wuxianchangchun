package cn.ffcs.external.trafficbroadcast.activity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.external.trafficbroadcast.bo.Traffic_Baoliao_Bo;
import cn.ffcs.external.trafficbroadcast.entity.Traffic_Baoliao_Entity;
import cn.ffcs.widget.LoadingDialog;
import cn.ffcs.wisdom.city.changecity.location.LocationUtil;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.common.Key;
import cn.ffcs.wisdom.city.datamgr.MenuMgr;
import cn.ffcs.wisdom.city.personcenter.datamgr.AccountMgr;
import cn.ffcs.wisdom.city.personcenter.entity.Account;
import cn.ffcs.wisdom.city.resp.UpLoadImageResp;
import cn.ffcs.wisdom.city.simico.base.Application;
import cn.ffcs.wisdom.http.BaseResp;
import cn.ffcs.wisdom.http.HttpCallBack;
import cn.ffcs.wisdom.tools.CommonUtils;
import cn.ffcs.wisdom.tools.SdCardTool;
import cn.ffcs.wisdom.tools.SharedPreferencesUtil;
import cn.ffcs.wisdom.tools.SystemCallUtil;

import com.example.external_trafficbroadcast.R;

/**
 * 发布路况页面
 * 
 * @author daizhq
 * 
 * @date 2014.12.02
 * */
public class TrafficPublishActivity extends Activity implements OnClickListener {

	// 返回键
	private LinearLayout ll_back;

	// 道路类型区域
	private LinearLayout ll_type_shunchang;
	private LinearLayout ll_type_huanman;
	private LinearLayout ll_type_yongdu;
	private LinearLayout ll_type_daolufengbi;
	private LinearLayout ll_type_shigu;
	private LinearLayout ll_type_jingchazhifa;

	// 路况类型显示选中图标
	private ImageView iv_type_shunchang;
	private ImageView iv_type_huanman;
	private ImageView iv_type_yongdu;
	private ImageView iv_type_daolufengbi;
	private ImageView iv_type_shigu;
	private ImageView iv_type_jingchazhifa;

	// 朝向区域
	private LinearLayout ll_east;
	private LinearLayout ll_west;
	private LinearLayout ll_south;
	private LinearLayout ll_north;

	// 朝向图标
	private ImageView iv_east;
	private ImageView iv_west;
	private ImageView iv_south;
	private ImageView iv_north;

	// 已选图片列表
	private RelativeLayout rl_p1;
	private RelativeLayout rl_p2;
	private RelativeLayout rl_p3;
	private RelativeLayout rl_p4;
	private RelativeLayout rl_p5;
	private RelativeLayout rl_p6;

	private ImageView iv_p1;
	private ImageView iv_p2;
	private ImageView iv_p3;
	private ImageView iv_p4;
	private ImageView iv_p5;
	private ImageView iv_p6;

	// 小删除图标
	private ImageView iv_d1;
	private ImageView iv_d2;
	private ImageView iv_d3;
	private ImageView iv_d4;
	private ImageView iv_d5;
	private ImageView iv_d6;

	// 显示发布详情
	private EditText tv_detail;

	// 重定位输入框
	private EditText et_input;

	private Button btn_send;// 发布按钮

	private List<String> selectList;// 上一个界面传过来的图片列表
	ArrayList<Bitmap> bmList = new ArrayList<Bitmap>();// 图片上传列表
	ArrayList<String> pic_uris = new ArrayList<String>();// 图片上传列表
	// 图片路径数组
	String[] pic_uri;

	// 选择爆料的类型，默认为顺畅
	// 新1顺畅 2缓慢 3拥堵 4道路封闭 5事故 6警察执法
	// 1顺畅 2缓慢 3道路封闭 4拥堵 5事故 6警察执法
	private static int status = 1;
	private String currentLocationName = "";// 定位地点名称
	private String detail = "";// 发布详情
	private String face = "";// 朝向
	private String inputStr = "";// 重定位框输入的内容
	private Traffic_Baoliao_Bo baoliaoBo = null;

	private int currentUpload = 0;// 当前正在上传的图片序列号

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_traffic_publish);

		loadView();

		loadDate();
	}

	/**
	 * 加载本页面控件
	 * */
	private void loadView() {
		// TODO Auto-generated method stub

		ll_back = (LinearLayout) findViewById(R.id.ll_back);

		ll_type_shunchang = (LinearLayout) findViewById(R.id.ll_type_shunchang);
		ll_type_huanman = (LinearLayout) findViewById(R.id.ll_type_huanman);
		ll_type_yongdu = (LinearLayout) findViewById(R.id.ll_type_yongdu);
		ll_type_daolufengbi = (LinearLayout) findViewById(R.id.ll_type_daolufengbi);
		ll_type_shigu = (LinearLayout) findViewById(R.id.ll_type_shigu);
		ll_type_jingchazhifa = (LinearLayout) findViewById(R.id.ll_type_jingchazhifa);

		iv_type_shunchang = (ImageView) findViewById(R.id.iv_type_shunchang);
		iv_type_huanman = (ImageView) findViewById(R.id.iv_type_huanman);
		iv_type_yongdu = (ImageView) findViewById(R.id.iv_type_yongdu);
		iv_type_daolufengbi = (ImageView) findViewById(R.id.iv_type_daolufengbi);
		iv_type_shigu = (ImageView) findViewById(R.id.iv_type_shigu);
		iv_type_jingchazhifa = (ImageView) findViewById(R.id.iv_type_jingchazhifa);

		ll_east = (LinearLayout) findViewById(R.id.ll_east);
		ll_west = (LinearLayout) findViewById(R.id.ll_west);
		ll_south = (LinearLayout) findViewById(R.id.ll_south);
		ll_north = (LinearLayout) findViewById(R.id.ll_north);

		iv_east = (ImageView) findViewById(R.id.iv_east);
		iv_west = (ImageView) findViewById(R.id.iv_west);
		iv_south = (ImageView) findViewById(R.id.iv_south);
		iv_north = (ImageView) findViewById(R.id.iv_north);

		tv_detail = (EditText) findViewById(R.id.tv_detail);
		et_input = (EditText) findViewById(R.id.et_input);

		rl_p1 = (RelativeLayout) findViewById(R.id.rl_p1);
		rl_p2 = (RelativeLayout) findViewById(R.id.rl_p2);
		rl_p3 = (RelativeLayout) findViewById(R.id.rl_p3);
		rl_p4 = (RelativeLayout) findViewById(R.id.rl_p4);
		rl_p5 = (RelativeLayout) findViewById(R.id.rl_p5);
		rl_p6 = (RelativeLayout) findViewById(R.id.rl_p6);

		iv_p1 = (ImageView) findViewById(R.id.iv_p1);
		iv_p2 = (ImageView) findViewById(R.id.iv_p2);
		iv_p3 = (ImageView) findViewById(R.id.iv_p3);
		iv_p4 = (ImageView) findViewById(R.id.iv_p4);
		iv_p5 = (ImageView) findViewById(R.id.iv_p5);
		iv_p6 = (ImageView) findViewById(R.id.iv_p6);

		iv_d1 = (ImageView) findViewById(R.id.iv_d1);
		iv_d2 = (ImageView) findViewById(R.id.iv_d2);
		iv_d3 = (ImageView) findViewById(R.id.iv_d3);
		iv_d4 = (ImageView) findViewById(R.id.iv_d4);
		iv_d5 = (ImageView) findViewById(R.id.iv_d5);
		iv_d6 = (ImageView) findViewById(R.id.iv_d6);

		btn_send = (Button) findViewById(R.id.btn_send);

		ll_back.setOnClickListener(this);

	}

	/**
	 * 加载本页面数据
	 * */
	private void loadDate() {
		// TODO Auto-generated method stub

		selectList = new LinkedList<String>();
		selectList = (List<String>) getIntent().getExtras().getSerializable(
				"alumList");
		status = getIntent().getExtras().getInt("status");
		currentLocationName = getIntent().getExtras().getString(
				"currentLocationName");
		detail = getIntent().getExtras().getString("detail");
		// face = getIntent().getExtras().getString("face");

		setType(status);
		// setDetail(currentLocationName, face, status);
		setDetail(currentLocationName, "", status);
		setPhotoList(selectList);
		// tv_detail.setText(detail);
		et_input.setHint(currentLocationName);
		// if ("向东".equals(face)) {
		// setFace(0, 8, 8, 8);
		// } else if ("向西".equals(face)) {
		// setFace(8, 0, 8, 8);
		// } else if ("向南".equals(face)) {
		// setFace(8, 8, 0, 8);
		// } else if ("向北".equals(face)) {
		// setFace(8, 8, 8, 0);
		// }

		ll_type_shunchang.setOnClickListener(this);
		ll_type_huanman.setOnClickListener(this);
		ll_type_yongdu.setOnClickListener(this);
		ll_type_daolufengbi.setOnClickListener(this);
		ll_type_shigu.setOnClickListener(this);
		ll_type_jingchazhifa.setOnClickListener(this);

		ll_east.setOnClickListener(this);
		ll_west.setOnClickListener(this);
		ll_south.setOnClickListener(this);
		ll_north.setOnClickListener(this);

		iv_d1.setOnClickListener(this);
		iv_d2.setOnClickListener(this);
		iv_d3.setOnClickListener(this);
		iv_d4.setOnClickListener(this);
		iv_d5.setOnClickListener(this);
		iv_d6.setOnClickListener(this);

		rl_p1.setOnClickListener(this);
		rl_p2.setOnClickListener(this);
		rl_p3.setOnClickListener(this);
		rl_p4.setOnClickListener(this);
		rl_p5.setOnClickListener(this);
		rl_p6.setOnClickListener(this);

		btn_send.setOnClickListener(this);

		et_input.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				inputStr = et_input.getText().toString().trim();
				if (inputStr.equals("")) {
					setDetail(currentLocationName, face, status);
				} else {
					setDetail(inputStr, face, status);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.ll_back) {
			finish();
		} else if (id == R.id.ll_type_shunchang) {
			status = 1;
			setType(status);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.ll_type_huanman) {
			status = 2;
			setType(status);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.ll_type_yongdu) {
			status = 3;
			setType(status);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.ll_type_daolufengbi) {
			status = 4;
			setType(status);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.ll_type_shigu) {
			status = 5;
			setType(status);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.ll_type_jingchazhifa) {
			status = 6;
			setType(status);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.ll_east) {
			face = "向东  ";
			setFace(0, 8, 8, 9);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.ll_west) {
			face = "向西  ";
			setFace(8, 0, 8, 8);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.ll_north) {
			face = "向北  ";
			setFace(8, 8, 8, 0);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.ll_south) {
			face = "向南  ";
			setFace(8, 8, 0, 8);
			if (inputStr.equals("")) {
				setDetail(currentLocationName, face, status);
			} else {
				setDetail(inputStr, face, status);
			}
		} else if (id == R.id.iv_d1) {
			selectList.remove(0);
			bmList.remove(0);
			setPhotoList(selectList);
		} else if (id == R.id.iv_d2) {
			selectList.remove(1);
			bmList.remove(1);
			setPhotoList(selectList);
		} else if (id == R.id.iv_d3) {
			selectList.remove(2);
			bmList.remove(2);
			setPhotoList(selectList);
		} else if (id == R.id.iv_d4) {
			selectList.remove(3);
			bmList.remove(3);
			setPhotoList(selectList);
		} else if (id == R.id.iv_d5) {
			selectList.remove(4);
			bmList.remove(4);
			setPhotoList(selectList);
		} else if (id == R.id.iv_d6) {
			selectList.remove(5);
			bmList.remove(5);
			setPhotoList(selectList);
		} else if (id == R.id.rl_p1 || id == R.id.rl_p2 || id == R.id.rl_p3
				|| id == R.id.rl_p4 || id == R.id.rl_p5 || id == R.id.rl_p6) {

			final CharSequence[] picItems = { "本地图片", "拍照", "取消" };
			AlertDialog dlg = new AlertDialog.Builder(this).setItems(picItems,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int picItem) {
							if (picItem == 0) {
								Intent intent = new Intent();
								intent.setClass(TrafficPublishActivity.this,
										MyPhotoListActivity.class);
								Bundle bundle = new Bundle();
								bundle.putSerializable("alumList",
										(Serializable) selectList);
								intent.putExtras(bundle);
								intent.putExtra("status", status);
								intent.putExtra("currentLocationName",
										currentLocationName);
								intent.putExtra("detail", tv_detail.getText()
										.toString());
								intent.putExtra("face", face);
								startActivity(intent);
								finish();
								// SystemCallUtil.photoAlbum(TrafficPublishActivity.this);
							} else if (picItem == 1) {
								// try {
								// Intent intent = new Intent();
								// intent.setClass(TrafficPublishActivity.this,
								// MyCameraActivity.class);
								// Bundle bundle = new Bundle();
								// bundle.putSerializable("alumList",
								// (Serializable) selectList);
								// intent.putExtras(bundle);
								// intent.putExtra("status", status);
								// intent.putExtra("currentLocationName",
								// currentLocationName);
								// intent.putExtra("detail",
								// tv_detail.getText().toString());
								// intent.putExtra("face", face);
								// startActivity(intent);
								// finish();
								// } catch (Exception e) {
								// }
								SystemCallUtil.camera(
										TrafficPublishActivity.this,
										Config.SDCARD_CITY_TMP);
							} else {
							}
						}
					}).create();
			dlg.show();

		} else if (id == R.id.btn_send) {
			boolean isLogin = Boolean.parseBoolean(SharedPreferencesUtil
					.getValue(TrafficPublishActivity.this, Key.K_IS_LOGIN));
			if (!isLogin) {
				Intent intent = new Intent();
				intent.setClassName(TrafficPublishActivity.this,
						"cn.ffcs.changchuntv.activity.login.LoginActivity");
				startActivity(intent);
			} else {
				showProgressBar("正在上报...");

				baoliaoBo = new Traffic_Baoliao_Bo(TrafficPublishActivity.this);
				System.out.println("图片列表长度====>>" + bmList.size());
				if (bmList.size() > 0) {
					// 遍历图片列表，逐张上传
					for (int i = 0; i < bmList.size(); i++) {
						Bitmap bitmap = bmList.get(i);
						Config config = new Config();
						config.init(TrafficPublishActivity.this);
						SdCardTool.save(bitmap, config.SDCARD_CITY_TMP,
								File.separator + String.valueOf(i) + ".jpg");
						String load = config.SDCARD_CITY_TMP + File.separator
								+ String.valueOf(i) + ".jpg";
						baoliaoBo.imageUpLoad(new fileUpLoadCallBack(), load);
					}
				} else {
					Map<String, Object> params = new HashMap<String, Object>(1);
					Account account = AccountMgr.getInstance().getAccount(
							TrafficPublishActivity.this);
					String user_id = String.valueOf(account.getData()
							.getUserId());
					// 测试使用用户账号
					// String user_id = "7623773";
					String mobile = account.getData().getMobile();
					String lat = LocationUtil
							.getLatitude(TrafficPublishActivity.this);
					String lng = LocationUtil
							.getLongitude(TrafficPublishActivity.this);
					String sign = user_id;
					detail = tv_detail.getText().toString().trim();

					if (lat == null || lat.equals("")) {
						lat = "unknown";
					}
					if (lng == null || lng.equals("")) {
						lng = "unknown";
					}
					if (mobile == null || mobile.equals("")) {
						mobile = "unknown";
					}
					System.out.println("遍历前列表长度====>>" + pic_uris.size());
					pic_uri = new String[] {};
					params.put("city_code", "2201");
					params.put("org_code", "2201");
					params.put("mobile", mobile);
					params.put("longitude", lng);
					params.put("latitude", lat);
					params.put("sign", sign);
					params.put("user_id", user_id);
					params.put("pic_uri", pic_uri);
					if (!inputStr.equals("")) {
						params.put("title", inputStr);
					} else if (!currentLocationName.equals("")) {
						params.put("title", currentLocationName);
					} else {
						// 没有标题就提示并且不请求爆料接口
						CommonUtils.showToast(TrafficPublishActivity.this,
								"请输入爆料位置信息！", Toast.LENGTH_SHORT);
						hideProgressBar();
						return;
					}
					params.put("detail", detail);
					if (status == 3) {
						params.put("status", String.valueOf(4));
					} else if (status == 4) {
						params.put("status", String.valueOf(3));
					} else {
						params.put("status", String.valueOf(status));
					}
					params.put("pic_uri", pic_uri);
					baoliaoBo
							.startObRequestTask(
									new baoliaoCallBack(),
									TrafficPublishActivity.this,
									params,
									"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/lbs/road/addRoadExpose");
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == SystemCallUtil.REQUESTCODE_CAMERA
				&& resultCode == Activity.RESULT_OK) {
			selectList.add(SystemCallUtil.FILE_FULL_PATH);
			setPhotoList(selectList);
		} else if (requestCode == SystemCallUtil.REQUESTCODE_PHOTOALBUM
				&& resultCode == Activity.RESULT_OK) {
			Uri uri = data.getData();
			selectList.add(uri.getPath());
			setPhotoList(selectList);
		}
	}

	/**
	 * 文件图片文件回调
	 */
	class fileUpLoadCallBack implements HttpCallBack<UpLoadImageResp> {

		@Override
		public void call(UpLoadImageResp response) {
			// TODO Auto-generated method stub
			currentUpload++;
			String filePath = response.getList().get(0).getFilePath();
			System.out.println("上传图片返回====>>" + filePath);
			pic_uris.add(filePath);

			if (currentUpload == bmList.size()) {
				Map<String, Object> params = new HashMap<String, Object>(1);
				Account account = AccountMgr.getInstance().getAccount(
						TrafficPublishActivity.this);
				String user_id = String.valueOf(account.getData().getUserId());
				// 测试使用用户账号
				// String user_id = "7623773";
				String mobile = account.getData().getMobile();
				String lat = LocationUtil
						.getLatitude(TrafficPublishActivity.this);
				String lng = LocationUtil
						.getLongitude(TrafficPublishActivity.this);
				String sign = user_id;
				detail = tv_detail.getText().toString().trim();

				if (lat == null || lat.equals("")) {
					lat = "unknown";
				}
				if (lng == null || lng.equals("")) {
					lng = "unknown";
				}
				if (mobile == null || mobile.equals("")) {
					mobile = "unknown";
				}
				System.out.println("遍历前列表长度====>>" + pic_uris.size());
				pic_uri = new String[pic_uris.size()];
				for (int i = 0; i < pic_uris.size(); i++) {
					pic_uri[i] = pic_uris.get(i);
				}
				params.put("city_code", "2201");
				params.put("org_code", "2201");
				params.put("mobile", mobile);
				params.put("longitude", lng);
				params.put("latitude", lat);
				params.put("sign", sign);
				params.put("user_id", user_id);
				params.put("pic_uri", pic_uri);
				if (!inputStr.equals("")) {
					params.put("title", inputStr);
				} else if (!currentLocationName.equals("")) {
					params.put("title", currentLocationName);
				} else {
					// 没有标题就提示并且不请求爆料接口
					CommonUtils.showToast(TrafficPublishActivity.this,
							"请输入爆料位置信息！", Toast.LENGTH_SHORT);
					hideProgressBar();
					return;
				}
				params.put("detail", detail);
				if (status == 3) {
					params.put("status", String.valueOf(4));
				} else if (status == 4) {
					params.put("status", String.valueOf(3));
				} else {
					params.put("status", String.valueOf(status));
				}
				params.put("pic_uri", pic_uri);
				baoliaoBo
						.startObRequestTask(
								new baoliaoCallBack(),
								TrafficPublishActivity.this,
								params,
								"http://ccgd.153.cn:50081/icity-api-client-other/icity/service/lbs/road/addRoadExpose");
			}
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
		}

	}

	/**
	 * 爆料回调
	 * */
	class baoliaoCallBack implements HttpCallBack<BaseResp> {

		@Override
		public void call(BaseResp response) {
			// TODO Auto-generated method stub
			System.out.println("发布页面爆料请求返回====>>" + response.getHttpResult());
			hideProgressBar();
			if (response.isSuccess()) {
				Traffic_Baoliao_Entity baoliaoEntity = (Traffic_Baoliao_Entity) response
						.getObj();
				if (baoliaoEntity.getResult_code().equals("0")) {
					CommonUtils.showToast(TrafficPublishActivity.this,
							"路况爆料发布成功！", Toast.LENGTH_SHORT);
					finish();
				} else {
					CommonUtils.showToast(TrafficPublishActivity.this,
							"路况爆料发布失败！", Toast.LENGTH_SHORT);
				}
			}
		}

		@Override
		public void progress(Object... obj) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onNetWorkError() {
			// TODO Auto-generated method stub
		}
	}

	/**
	 * 设置选中的路况类型
	 * */
	private void setType(int status) {
		// TODO Auto-generated method stub
		iv_type_shunchang.setVisibility(8);
		iv_type_huanman.setVisibility(8);
		iv_type_yongdu.setVisibility(8);
		iv_type_daolufengbi.setVisibility(8);
		iv_type_shigu.setVisibility(8);
		iv_type_jingchazhifa.setVisibility(8);

		if (status == 1) {
			iv_type_shunchang.setVisibility(0);
		} else if (status == 2) {
			iv_type_huanman.setVisibility(0);
		} else if (status == 3) {
			iv_type_yongdu.setVisibility(0);
		} else if (status == 4) {
			iv_type_daolufengbi.setVisibility(0);
		} else if (status == 5) {
			iv_type_shigu.setVisibility(0);
		} else if (status == 6) {
			iv_type_jingchazhifa.setVisibility(0);
		}
	}

	/**
	 * 设置播报详情
	 * */
	private void setDetail(String currentLocationName, String face, int status) {
		et_input.setHint(currentLocationName);
		if (status == 1) {
			tv_detail.setText(face + "顺畅");
			// tv_detail.setText(currentLocationName + "  " + face + "  " +
			// "顺畅");
		} else if (status == 2) {
			tv_detail.setText(face + "缓慢");
			// tv_detail.setText(currentLocationName + "  " + face + "  " +
			// "缓慢");
		} else if (status == 3) {
			tv_detail.setText(face + "拥堵");
			// tv_detail.setText(currentLocationName + "  " + face + "  " +
			// "拥堵");
		} else if (status == 4) {
			tv_detail.setText(face + "道路封闭");
			// tv_detail.setText(currentLocationName + "  " + face + "  " +
			// "道路封闭");
		} else if (status == 5) {
			tv_detail.setText(face + "事故");
			// tv_detail.setText(currentLocationName + "  " + face + "  " +
			// "事故");
		} else if (status == 6) {
			tv_detail.setText(face + "警察执法");
			// tv_detail.setText(currentLocationName + "  " + face + "  " +
			// "警察执法");
		}
	}

	/**
	 * 设置朝向
	 */
	private void setFace(int east, int west, int south, int north) {
		iv_east.setVisibility(east);
		iv_west.setVisibility(west);
		iv_south.setVisibility(south);
		iv_north.setVisibility(north);
	}

	/**
	 * 设置已选图片
	 */
	private void setPhotoList(List<String> selectList) {
		// TODO Auto-generated method stub

		iv_p1.setImageBitmap(null);
		iv_p2.setImageBitmap(null);
		iv_p3.setImageBitmap(null);
		iv_p4.setImageBitmap(null);
		iv_p5.setImageBitmap(null);
		iv_p6.setImageBitmap(null);
		iv_p1.setBackgroundDrawable(null);
		iv_p2.setBackgroundDrawable(null);
		iv_p3.setBackgroundDrawable(null);
		iv_p4.setBackgroundDrawable(null);
		iv_p5.setBackgroundDrawable(null);
		iv_p6.setBackgroundDrawable(null);

		int size = selectList.size();

		bmList.clear();
		if (size == 0) {
			setPhotoListVisibility(0, -1, -1, -1, -1, -1);
			iv_p1.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.iv_add));
			setClick(true, false, false, false, false, false);
			iv_d1.setVisibility(8);
		} else if (size == 1) {
			setPhotoListVisibility(0, 0, -1, -1, -1, -1);
			iv_p1.setImageBitmap(getImageThumbnail(selectList.get(0), 300, 220));
			iv_p2.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.iv_add));
			setClick(false, true, false, false, false, false);
			iv_d1.setVisibility(0);
			iv_d2.setVisibility(8);
		} else if (size == 2) {
			setPhotoListVisibility(0, 0, 0, 8, 8, 8);
			iv_p1.setImageBitmap(getImageThumbnail(selectList.get(0), 300, 220));
			iv_p2.setImageBitmap(getImageThumbnail(selectList.get(1), 300, 220));
			iv_p3.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.iv_add));
			setClick(false, false, true, false, false, false);
			iv_d1.setVisibility(0);
			iv_d2.setVisibility(0);
			iv_d3.setVisibility(8);
		} else if (size == 3) {
			setPhotoListVisibility(0, 0, 0, 0, 8, 8);
			iv_p1.setImageBitmap(getImageThumbnail(selectList.get(0), 300, 220));
			iv_p2.setImageBitmap(getImageThumbnail(selectList.get(1), 300, 220));
			iv_p3.setImageBitmap(getImageThumbnail(selectList.get(2), 300, 220));
			iv_p4.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.iv_add));
			setClick(false, false, false, true, false, false);
			iv_d1.setVisibility(0);
			iv_d2.setVisibility(0);
			iv_d3.setVisibility(0);
			iv_d4.setVisibility(8);
		} else if (size == 4) {
			setPhotoListVisibility(0, 0, 0, 0, 0, 8);
			iv_p1.setImageBitmap(getImageThumbnail(selectList.get(0), 300, 220));
			iv_p2.setImageBitmap(getImageThumbnail(selectList.get(1), 300, 220));
			iv_p3.setImageBitmap(getImageThumbnail(selectList.get(2), 300, 220));
			iv_p4.setImageBitmap(getImageThumbnail(selectList.get(3), 300, 220));
			iv_p5.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.iv_add));
			setClick(false, false, false, false, true, false);
			iv_d1.setVisibility(0);
			iv_d2.setVisibility(0);
			iv_d3.setVisibility(0);
			iv_d4.setVisibility(0);
			iv_d5.setVisibility(8);
		} else if (size == 5) {
			setPhotoListVisibility(0, 0, 0, 0, 0, 0);
			iv_p1.setImageBitmap(getImageThumbnail(selectList.get(0), 300, 220));
			iv_p2.setImageBitmap(getImageThumbnail(selectList.get(1), 300, 220));
			iv_p3.setImageBitmap(getImageThumbnail(selectList.get(2), 300, 220));
			iv_p4.setImageBitmap(getImageThumbnail(selectList.get(3), 300, 220));
			iv_p5.setImageBitmap(getImageThumbnail(selectList.get(4), 300, 220));
			iv_p6.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.iv_add));
			setClick(false, false, false, false, false, true);
			iv_d1.setVisibility(0);
			iv_d2.setVisibility(0);
			iv_d3.setVisibility(0);
			iv_d4.setVisibility(0);
			iv_d5.setVisibility(0);
			iv_d6.setVisibility(8);
		} else if (size == 6) {
			setPhotoListVisibility(0, 0, 0, 0, 0, 0);
			iv_p1.setImageBitmap(getImageThumbnail(selectList.get(0), 300, 220));
			iv_p2.setImageBitmap(getImageThumbnail(selectList.get(1), 300, 220));
			iv_p3.setImageBitmap(getImageThumbnail(selectList.get(2), 300, 220));
			iv_p4.setImageBitmap(getImageThumbnail(selectList.get(3), 300, 220));
			iv_p5.setImageBitmap(getImageThumbnail(selectList.get(4), 300, 220));
			iv_p6.setImageBitmap(getImageThumbnail(selectList.get(5), 300, 220));
			setClick(false, false, false, false, false, false);
			iv_d1.setVisibility(0);
			iv_d2.setVisibility(0);
			iv_d3.setVisibility(0);
			iv_d4.setVisibility(0);
			iv_d5.setVisibility(0);
			iv_d6.setVisibility(0);
		}
	}

	private void setClick(boolean p1, boolean p2, boolean p3, boolean p4,
			boolean p5, boolean p6) {
		// TODO Auto-generated method stub
		rl_p1.setEnabled(p1);
		rl_p2.setEnabled(p2);
		rl_p3.setEnabled(p3);
		rl_p4.setEnabled(p4);
		rl_p5.setEnabled(p5);
		rl_p6.setEnabled(p6);
	}

	/**
	 * 设置图片是否展示
	 * */
	private void setPhotoListVisibility(int rl_p1, int rl_p2, int rl_p3,
			int rl_p4, int rl_p5, int rl_p6) {
		this.rl_p1.setVisibility(rl_p1);
		this.rl_p2.setVisibility(rl_p2);
		this.rl_p3.setVisibility(rl_p3);
		this.rl_p4.setVisibility(rl_p4);
		this.rl_p5.setVisibility(rl_p5);
		this.rl_p6.setVisibility(rl_p6);
	}

	/**
	 * 第一次获取的bitmap实际上为null，只是为了读取宽度和高度，第二次读取的bitmap是根据比例压缩过的图像，
	 * 第三次读取的bitmap是所要的缩略图。
	 */
	private Bitmap getImageThumbnail(String imagePath, int width, int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		options.inJustDecodeBounds = false;
		options.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		bmList.add(bitmap);
		return bitmap;
	}

	/**
	 * 读取进度条
	 * */
	public void showProgressBar(String message) {
		LoadingDialog.getDialog(TrafficPublishActivity.this)
				.setMessage(message).show();
	}

	/**
	 * 隐藏进度条
	 * */
	public void hideProgressBar() {
		LoadingDialog.getDialog(TrafficPublishActivity.this).cancel();
	}

}
