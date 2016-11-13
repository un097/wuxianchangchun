package com.ctbri.comm.widget;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.LinearInterpolator;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.ctbri.comm.util._Utils;
//import com.baidu.mapapi.map.MyLocationData;
import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.CurrentBean;
import com.ctbri.wxcc.JsonWookiiHttpContent;
import com.ctbri.wxcc.MessageEditor;
import com.ctbri.wxcc.MyBaseProtocol;
import com.ctbri.wxcc.R;
import com.google.gson.Gson;
import com.wookii.sendpics.Bimp;
import com.wookii.sendpics.FileUtils;
import com.wookii.sendpics.PicGridViewFragmnet;
import com.wookii.tools.comm.DeviceTool;
import com.wookii.tools.comm.LogS;
import com.wookii.tools.net.WookiiHttpPost;
import com.wookii.widget.CustomLoadingDialog;
import com.wookii.widget.IconButton;

public class PublishActivity extends FragmentActivity implements
BDLocationListener{

	private static final int EDIT_TEXT_MAX_LENGTH = 1000;
	private static final int EDIT_TEXT_MIN_LENGTH = 5;
	protected Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			CustomLoadingDialog.dismissDialog();
			if(msg.what != 0) {
				Toast.makeText(PublishActivity.this, (String)msg.obj, Toast.LENGTH_SHORT).show();
			}
		};
	}; 
	private LocationClient mLocClient;
	//private MyLocationData locData;
	private EditText content;
	private String column_id;
	private IconButton locationTextView;
	private String[] communityNames;
	private String[] communityIds;
	private View spinner;
	private ImageView rightButton;
	private TextView communityText;
	private MyDialog dialog;
	private BDLocation location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_community_send);
		PicGridViewFragmnet picGridViewFragmnet = new PicGridViewFragmnet();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_community_send, picGridViewFragmnet).commit();
		
		((TextView)findViewById(R.id.action_bar_title)).setText("我要爆料");;
		findViewById(R.id.action_bar_left_btn).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		content = (EditText)findViewById(R.id.community_send_text);
		content.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if(s.toString().length() >= EDIT_TEXT_MIN_LENGTH && s.toString().length() <= EDIT_TEXT_MAX_LENGTH){
					rightButton.setImageResource(R.drawable.button_selector_confirm);
					rightButton.setEnabled(true);
				} else {
					rightButton.setImageResource(R.drawable.common_icon_confirm_unpressed);
					rightButton.setEnabled(false);
				}
			}
		});
		
		locationTextView = (IconButton)findViewById(R.id.community_send_location);
		locationTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getLocation();
			}
		});
		rightButton = (ImageView)findViewById(R.id.action_bar_right_btn);
		rightButton.setImageResource(R.drawable.common_icon_confirm_unpressed);
		rightButton.setEnabled(false);
		rightButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(TextUtils.isEmpty(column_id)){
					Toast.makeText(PublishActivity.this, "请选择栏目", Toast.LENGTH_SHORT).show();
					return;
				}
				send();
			}
		});
		spinner = (View)findViewById(R.id.community_type_id);
		spinner.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!DeviceTool.isNetworkAvailable(PublishActivity.this)) {
					Toast.makeText(PublishActivity.this, "网络不可用！", Toast.LENGTH_SHORT).show();
					return;
				}
				requestCommunityId();
			}
		});
		communityText = (TextView)findViewById(R.id.community_type_text);
		
	}

	private void getLocation() {
		if(location != null) {
			closeLoationToggle();
			location = null;
			return;
		}
		//百度定位
		mLocClient = new LocationClient(this);
		mLocClient.registerLocationListener(this);
		LocationClientOption option = new LocationClientOption();
		option.setAddrType ("all");  
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocClient.setLocOption(option);
		mLocClient.start();
	}
	
	private void closeLoationToggle() {
		ObjectAnimator ofFloat = ObjectAnimator.ofFloat(locationTextView.getChildAt(0), "scaleX", 1f,0f);
		ofFloat.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				locationTextView.setText("添加位置");
				locationTextView.setIcon(R.drawable.common_icon_location_normal);
				ObjectAnimator ofFloat = ObjectAnimator.ofFloat(locationTextView.getChildAt(0), "scaleX", 0f,1f);
				ofFloat.setDuration(500);
				ofFloat.setInterpolator(new LinearInterpolator());
				ofFloat.start();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		ofFloat.setDuration(500);
		ofFloat.setInterpolator(new LinearInterpolator());
		ofFloat.start();
	}

	private void requestCommunityId() {
		// TODO Auto-generated method stub
		if(MyDialog.ids != null && MyDialog.ids.length != 0 && dialog != null) {
			dialog.show(getSupportFragmentManager(), "communityTypeDialog");
			return;
		}
		ArrayList<BasicNameValuePair> pairs = new ArrayList<BasicNameValuePair>();
		new MyBaseProtocol(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String json = (String) msg.obj;
				Gson gson = new Gson();
				CurrentBean currentMessage = gson.fromJson(json,
						CurrentBean.class);
				if (currentMessage.getRet() == 0) {
					try {
						JSONObject jsoO = new JSONObject(json);
						JSONArray jsonArray = jsoO.getJSONObject("data").getJSONArray("columns");
						communityNames = new String[jsonArray.length()];
						communityIds = new String[jsonArray.length()];
						for (int i = 0; i < jsonArray.length(); i ++) {
							communityNames[i] = jsonArray.getJSONObject(i).getString("column_name");
							communityIds[i] = jsonArray.getJSONObject(i).getString("column_id");
						}
						dialog = MyDialog.newInstance(communityNames, communityIds);
						dialog.setOnClickListener(new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int arg) {
									MyDialog.chooiceWhich  = MyDialog.which;
									if(MyDialog.ids != null && MyDialog.ids.length != 0) {
										column_id = MyDialog.ids[MyDialog.chooiceWhich];
									}
									if(MyDialog.data != null && MyDialog.data.length != 0) {
										communityText.setText(MyDialog.data[MyDialog.chooiceWhich]);
									}
									
								}
							});
						
						dialog.show(getSupportFragmentManager(), "communityTypeDialog");
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}

			}
		}, new WookiiHttpPost(), Constants.METHOD_COMMUNITY_COLUMN_LIST).startInvoke(
				new JsonWookiiHttpContent(pairs), 0);
	}

	
	
	/**
	 * 发送的时候可以调用这个方法
	 */
	private void send() {
		CustomLoadingDialog.showProgress(PublishActivity.this, null,
				"正在发布爆料", false, true);
		// 高清的压缩图片全部就在 list 路径里面了
		// 高清的压缩过的 bmp 对象 都在 Bimp.bmp里面
		new Thread() {
			@Override
			public void run() {
				List<String> list = new ArrayList<String>();
				for (int i = 0; i < Bimp.drr.size(); i++) {
					String Str = Bimp.drr.get(i).substring(
							Bimp.drr.get(i).lastIndexOf("/") + 1,
							Bimp.drr.get(i).lastIndexOf("."));
					String fPath = FileUtils.SDPATH + Str + ".JPEG"; 
					list.add(fPath);
				}
				try {
					post(list, Constants.METHOD_COMMUNITY_SEND_CONTENT, handler);
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	private void post(List<String> pathToOurFile, String urlServer,
			Handler handler) throws ClientProtocolException, IOException,
			JSONException {
		HttpClient httpclient = new DefaultHttpClient();
		// 设置通信协议版本
		httpclient.getParams().setParameter(
				CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

		String requestJson = buildJson();
		LogS.i("config json", requestJson);
		MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
		HttpPost httppost = new HttpPost(urlServer);
		for (String files : pathToOurFile) {
			String path = createFilePath(files);
			String fileName = getFileName(files);
			File file = new File(path, fileName);
			ContentBody cbFile = new FileBody(file);
			mpEntity.addPart("filedata", cbFile);
		}
		mpEntity.addPart("json", new StringBody(requestJson, Charset.forName(HTTP.UTF_8)));
		httppost.setEntity(mpEntity);
		LogS.i("executing request " , httppost.getRequestLine().toString());

		HttpResponse response = httpclient.execute(httppost);
		HttpEntity resEntity = response.getEntity();

		String json = "";
		int code = -1;
		if (resEntity != null) {
			try {
				json = EntityUtils.toString(resEntity, "utf-8");
				JSONObject p = null;
				p = new JSONObject(json);
				code = p.getInt("ret");
				Message msg = Message.obtain();
				msg.what = code;
				if(code != 0) {
					msg.obj = (String) p.get("desp");
					handler.sendMessage(msg);
				} else {
					// 完成上传服务器后 .........
					FileUtils.deleteDir();
					handler.sendMessage(msg);
					finish();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (resEntity != null) {
			resEntity.consumeContent();
		}

		httpclient.getConnectionManager().shutdown();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mLocClient != null) {
			mLocClient.unRegisterLocationListener(this);
		}
		if(mLocClient != null && mLocClient.isStarted()) {
			mLocClient.stop();
		}
	}
	
	private String buildJson() {
		double[] gps = new double[2];
		if(location != null){
			gps[0] = location.getLatitude();
			gps[1] = location.getLongitude();
		}
		PostData data = new PostData(MessageEditor.getUserId(this), 
				MessageEditor.getUserName(this),MessageEditor.getUserUrl(this),content.getText().toString(), 
				TextUtils.isEmpty(column_id) ? "1" : column_id, gps, MessageEditor.getTel(this));
		Gson gson = new Gson();
		String json = gson.toJson(data);
		return json;
	}

	private String getFileName(String files) {
		return files.substring(files.lastIndexOf
				("/") + 1, files.length());
	}

	private String createFilePath(String files) {
		String path = files.substring(0, files.lastIndexOf("/"));
		try {
			File file = new File(path);
			if (!file.exists()) {
				file.mkdir();
			}
		} catch (Exception e) {

		}
		return path;
	}

	
	class PostData{
		private String user_id;
		private String user_name;
		private String user_url;
		public String getUser_name() {
			return user_name;
		}
		public void setUser_name(String user_name) {
			this.user_name = user_name;
		}
		public String getUser_url() {
			return user_url;
		}
		public void setUser_url(String user_url) {
			this.user_url = user_url;
		}
		private String content;
		private String column_id;
		private String mobile;
		
		
		public String getMobile() {
			return mobile;
		}
		public void setMobile(String mobile) {
			this.mobile = mobile;
		}
		private double[] gps;
		public String getUser_id() {
			return user_id;
		}
		public void setUser_id(String user_id) {
			this.user_id = user_id;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getColumn_id() {
			return column_id;
		}
		public void setColumn_id(String column_id) {
			this.column_id = column_id;
		}
		public double[] getGps() {
			return gps;
		}
		public void setGps(double[] gps) {
			this.gps = gps;
		}
		public PostData(String user_id, String user_name, String user_url,
				String content, String column_id, double[] gps, String mNumber) {
			super();
			this.user_id = user_id;
			this.user_name = user_name;
			this.user_url = user_url;
			this.content = content;
			this.column_id = column_id;
			this.gps = gps;
			this.mobile = mNumber;
		}
		
		
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public static class MyDialog extends DialogFragment {
		
		protected static int chooiceWhich = 0;
		public static MyDialog newInstance(String[] data, String[] ids){
			Bundle b = new Bundle();
			b.putStringArray("data", data);
			b.putStringArray("ids", ids);
			MyDialog f = new MyDialog();
			f.setArguments(b);
			return f;
		}
		public static int which = 0;
		public static String[] data;
		public static String[] ids;
		private android.content.DialogInterface.OnClickListener listenenr;
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			data = getArguments().getStringArray("data");
			ids = getArguments().getStringArray("ids");
			builder.setTitle("选择栏目名称")
					.setSingleChoiceItems(data, chooiceWhich,
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							MyDialog.which = which;
						}
					})
					.setPositiveButton("确定",
							listenenr)
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									which = chooiceWhich;
									dialog.dismiss();
								}
							});
			AlertDialog dialog = builder.create();
			dialog.setOnShowListener(new OnShowListener() {
				
				@Override
				public void onShow(DialogInterface dialog) {
				}
			});
			return dialog;

		}
		public void setOnClickListener(DialogInterface.OnClickListener listenenr){
			this.listenenr = listenenr;
		}
	}

//	@Override
//	public void onReceivePoi(final BDLocation location) {
//		
//	}

	@Override
	public void onReceiveLocation(final BDLocation location) {
		// TODO Auto-generated method stub
		if (location == null)
			return;
		if(mLocClient != null && mLocClient.isStarted()) {
			mLocClient.stop();
		}
		this.location = location;
		
		ObjectAnimator ofFloat = ObjectAnimator.ofFloat(locationTextView.getChildAt(0), "scaleX", 1f,0f);
		ofFloat.addListener(new AnimatorListener() {
			
			@Override
			public void onAnimationStart(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animator animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animator animation) {
				locationTextView.setText(location.getAddrStr());
				locationTextView.setIcon(R.drawable.common_icon_location_selected);
				ObjectAnimator ofFloat = ObjectAnimator.ofFloat(locationTextView.getChildAt(0), "scaleX", 0f,1f);
				ofFloat.setDuration(500);
				ofFloat.setInterpolator(new LinearInterpolator());
				ofFloat.start();
			}
			
			@Override
			public void onAnimationCancel(Animator animation) {
				// TODO Auto-generated method stub
				
			}
		});
		ofFloat.setDuration(500);
		ofFloat.setInterpolator(new LinearInterpolator());
		ofFloat.start();
	}
}
