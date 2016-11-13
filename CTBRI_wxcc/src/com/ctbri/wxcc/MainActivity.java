package com.ctbri.wxcc;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.ctbri.wxcc.audio.AudioChannelWidget;
import com.ctbri.wxcc.audio.AudioMainActivity;
import com.ctbri.wxcc.audio.AudioStatusBarFragment;
import com.ctbri.wxcc.backplay.server.BackgroundPlayService;
import com.ctbri.wxcc.community.CommunityActivity;
import com.ctbri.wxcc.community.CommunityMyActivity;
import com.ctbri.wxcc.coupon.CouponMainActivity;
import com.ctbri.wxcc.coupon.MyCouponActivity;
import com.ctbri.wxcc.hotline.HotLineActivity;
import com.ctbri.wxcc.media.MediaMainActivity;
import com.ctbri.wxcc.shake.ShakeMainActivity;
import com.ctbri.wxcc.travel.TravelActivity;
import com.ctbri.wxcc.vote.VoteActivity;

public class MainActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		StringBuilder sb = new StringBuilder();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		sb.append("density");
		sb.append(dm.density);

		sb.append(" | ").append("height").append(dm.heightPixels).append(" | ").append("width").append(dm.widthPixels);
		Toast.makeText(this, sb.toString(), Toast.LENGTH_LONG).show();

		findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, HotLineActivity.class);
				startActivity(intent);
			}
		});

		findViewById(R.id.btn_mycommunity).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, CommunityMyActivity.class);
				startActivity(it);
			}
		});

		findViewById(R.id.button3).setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, VoteActivity.class);
				startActivity(it);
			}
		});
		// 爆笑社区
		findViewById(R.id.btn_community).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, CommunityActivity.class);
				startActivity(it);
			}
		});
		// 旅游资讯
		findViewById(R.id.btn_travel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, TravelActivity.class);
				startActivity(it);
			}
		});
		// 优惠券
		findViewById(R.id.btn_coupon).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent it = new Intent(MainActivity.this,
				// MyCouponActivy.class);
				Intent it = new Intent(MainActivity.this, CouponMainActivity.class);
				startActivity(it);
			}
		});
		findViewById(R.id.btn_my_coupon).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, MyCouponActivity.class);
				startActivity(it);
			}
		});

		findViewById(R.id.btn_login).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MessageEditor.initOrUpdateCTBRI(MainActivity.this, "198912110", "programmer1", "http://www.eoeandroid.com/uc_server/data/avatar/000/62/19/12_avatar_middle.jpg",
						"18612186122");
			}
		});
		findViewById(R.id.btn_clear).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("ctbri_file", Context.MODE_PRIVATE);
				Editor edit = sharedPreferences.edit();
				edit.putString("user_id", "");

				edit.putString("user_name", "");

				edit.putString("user_url", "");
				edit.putString("tel", "");
				edit.commit();
			}
		});
		findViewById(R.id.btn_media).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, MediaMainActivity.class);
				startActivity(it);
			}
		});
		findViewById(R.id.btn_audio).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, AudioMainActivity.class);
				startActivity(it);
			}
		});

		findViewById(R.id.btn_shake).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent it = new Intent(MainActivity.this, ShakeMainActivity.class);
				startActivity(it);
			}
		});
		
		getSupportFragmentManager().beginTransaction().replace(R.id.frame_audio_widget, new AudioStatusBarFragment()).commit();
		getSupportFragmentManager().beginTransaction().replace(R.id.frame_coupon, new AudioChannelWidget()).commit();

		TextView tvmain = (TextView) findViewById(R.id.tv_channel_code);

		((TextView) findViewById(R.id.tv_channel_code)).setMovementMethod(LinkMovementMethod.getInstance());
		// ((TextView)findViewById(R.id.textView2)).setMovementMethod(ScrollingMovementMethod.getInstance());
		String str = getString(R.string.hello_world_test);
		tvmain.setText(R.string.share_content_community);
		// tvmain.setText(Html
		// .fromHtml("你好，你是谁。<a href=\"http://www.baidu.com\">跳转到百度</a>。。。       也可以 <a href=\"http://www.suning.com\">跳转到苏宁</a>。。。"));
		
		//开启后台播放音频服务
		Intent intent = new Intent(this,BackgroundPlayService.class);
		startService(intent);
	}
}
