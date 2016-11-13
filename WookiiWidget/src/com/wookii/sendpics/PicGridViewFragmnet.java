package com.wookii.sendpics;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;

import com.wookii.wookiiwidget.R;

public class PicGridViewFragmnet extends Fragment{

	private Activity context;
	private GridView gridView;
	private static final int TAKE_PICTURE = 0x000000;
	public static final String TAG = "PicGridViewFragmnet";
	private String path = "";
	private View parentView;
	private GridViewAdapter adapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	@Override
	public void onAttach(Activity activity) {
		this.context = activity;
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		parentView = inflater.inflate(R.layout.pic_grid_view_fragment, null);
		gridView = (GridView)parentView.findViewById(R.id.pic_grid_view);
		gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		adapter = new GridViewAdapter(context);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == Bimp.bmp.size()) {
					new PopupWindows(context, gridView);
					//强行隐藏软键盘
					InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
					inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				} else {
					Intent intent = new Intent(context,
							PhotoActivity.class);
					intent.putExtra("ID", position);
					startActivity(intent);
				}
				  
			}
		});
		gridView.setAdapter(adapter);
		return parentView;
	}
	
	public class PopupWindows extends PopupWindow {

		public PopupWindows(Context mContext, View parent) {

			View view = View
					.inflate(mContext, R.layout.chooice_way, null);
			view.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.fade_ins));
			LinearLayout ll_popup = (LinearLayout) view
					.findViewById(R.id.ll_popup);
			ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in_2));
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setBackgroundDrawable(new BitmapDrawable(getResources(), (Bitmap) null));
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(view);
			showAtLocation(parent, Gravity.BOTTOM, 0, 0);
			update();

			Button bt1 = (Button) view
					.findViewById(R.id.item_popupwindows_camera);
			Button bt2 = (Button) view
					.findViewById(R.id.item_popupwindows_Photo);
			Button bt3 = (Button) view
					.findViewById(R.id.item_popupwindows_cancel);
			bt1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					photo();
					dismiss();
				}
			});
			bt2.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent intent = new Intent(context,
							LocalPicActivity.class);
					startActivityForResult(intent, 0x124);
					dismiss();
				}
			});
			bt3.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					dismiss();
				}
			});

		}
	}

	public void photo() {
		Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		String string = Environment.getExternalStorageDirectory() + "/myimage/";
		File f = new File(string);
		f.mkdirs();
		File file = new File(f, String.valueOf(System.currentTimeMillis())
				+ ".jpg");
		path = file.getPath();
		Uri imageUri = Uri.fromFile(file);
		openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		startActivityForResult(openCameraIntent, TAKE_PICTURE);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case TAKE_PICTURE:
			if (Bimp.drr.size() < Bimp.MAX_SIZE && resultCode == -1) {
				Bimp.drr.add(path);
			}
			break;
		}
	}
	
	@Override
	public void onResume() {
		adapter.update();
		super.onResume();
	}
	class GridViewAdapter extends BaseAdapter {
		private LayoutInflater inflater; // 视图容器
		private int selectedPosition = -1;// 选中的位置
		private boolean shape;
		public GridViewAdapter(Activity context) {
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return Bimp.bmp.size() + 1;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final int coord = position;
			ViewHolder holder = null;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.send_pic_grid,
						parent, false);
				holder = new ViewHolder();
				holder.image = (ImageView) convertView
						.findViewById(R.id.item_grida_image);
				holder.deletez_image = (ImageView) convertView
						.findViewById(R.id.item_grida_image_del);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.deletez_image.setImageResource(getResources()
					.getIdentifier("button_selector_delete", "drawable" , context.getPackageName()));
			holder.deletez_image.setVisibility(View.VISIBLE);
			if (position == Bimp.bmp.size()) {
				holder.image.setImageResource(getResources()
						.getIdentifier("button_selector_add", "drawable" , context.getPackageName()));
				if (position == Bimp.MAX_SIZE) {
					holder.image.setVisibility(View.GONE);
				} else {
					holder.image.setVisibility(View.VISIBLE);
				}
				holder.deletez_image.setVisibility(View.GONE);
			} else {
				holder.image.setImageBitmap(Bimp.bmp.get(position));
			}
			holder.deletez_image.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (Bimp.bmp.size() == 1) {
						Bimp.bmp.clear();
						Bimp.drr.clear();
						Bimp.max = 0;
						FileUtils.deleteDir();
					} else {
						String newStr = Bimp.drr.get(position).substring( 
								Bimp.drr.get(position).lastIndexOf("/") + 1,
								Bimp.drr.get(position).lastIndexOf("."));
						Bimp.bmp.remove(position);
						Bimp.drr.remove(position);
						Bimp.max--;
						FileUtils.delFile(newStr + ".JPEG"); 
					}
					update();
				}
			});
			return convertView;
		}
		
		public class ViewHolder {
			public ImageView deletez_image;
			public ImageView image;
		}
		Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					notifyDataSetInvalidated();
					break;
				}
			}
		};
		public void update() {
			loading();
		}
		public void loading() {
			new Thread(new Runnable() {
				public void run() {
					while (true) {
						if (Bimp.max == Bimp.drr.size()) {
							Message message = Message.obtain();
							message.what = 1;
							handler.sendMessage(message);
							break;
						} else {
							try {
								String path = Bimp.drr.get(Bimp.max);
								System.out.println(path);
								Bitmap bm = Bimp.revitionImageSize(path);
								Bimp.bmp.add(bm);
								String newStr = path.substring(
										path.lastIndexOf("/") + 1,
										path.lastIndexOf("."));
								FileUtils.saveBitmap(bm, "" + newStr);
								Bimp.max += 1;
							} catch (IOException e) {

								e.printStackTrace();
							}
						}
						Message message = Message.obtain();
						message.what = 1;
						handler.sendMessage(message);
					}
				}
			}).start();
		}
	}
	
	@Override
	public void onDestroy() {
		Bimp.drr.clear();
		Bimp.max = 0;
		Bimp.bmp.clear();
		super.onDestroy();
	}
}
