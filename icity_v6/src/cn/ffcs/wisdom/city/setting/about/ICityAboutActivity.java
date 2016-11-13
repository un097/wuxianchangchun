package cn.ffcs.wisdom.city.setting.about;

import java.util.Random;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.TextView;
import cn.ffcs.wisdom.city.WisdomCityActivity;
import cn.ffcs.wisdom.city.common.Config;
import cn.ffcs.wisdom.city.utils.ConfigUtil;
import cn.ffcs.wisdom.city.v6.R;

public class ICityAboutActivity extends WisdomCityActivity {

	public static final String[] keywords = { "肖志升", "戴志强", "林碧川" };
	private HotWordsFlow hotWordsFlow;
	private TextView umeng;
	private TextView tel;
	private Button backBtn;
	private String umengChannel;
	private String telChannel;

	@Override
	protected void initComponents() {
		umeng = (TextView) findViewById(R.id.umeng);
		tel = (TextView) findViewById(R.id.tel);
		backBtn = (Button) findViewById(R.id.back_btn);
		backBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});
		hotWordsFlow = (HotWordsFlow) findViewById(R.id.local_search_container);
		hotWordsFlow.setDuration(800l);
		// 添加
		feedKeywordsFlow(hotWordsFlow, keywords);
		hotWordsFlow.go2Show(HotWordsFlow.ANIMATION_IN);
		hotWordsFlow.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hotWordsFlow.rubKeywords();
				feedKeywordsFlow(hotWordsFlow, keywords);
				hotWordsFlow.go2Show(HotWordsFlow.ANIMATION_OUT);
				return false;
			}
		});
	}

	private void feedKeywordsFlow(HotWordsFlow keywordsFlow, String[] arr) {
		int[] sequence = getRandomSequence(arr.length);
		clearWords();

		int count = HotWordsFlow.MAX;
		int keyCount = arr.length;
		String keyword = "";
		for (int i = 0; i < count; ++i) {
			if (keyCount > i) {
				keyword = arr[sequence[i]];
				hotWordsFlow.feedKeyword(keyword);
			}
		}
	}

	/**
	 * 不重复随机数列生成算法
	 */
	public int[] getRandomSequence(int total) {

		int[] sequence = new int[total];
		int[] output = new int[total];

		for (int i = 0; i < total; i++) {
			sequence[i] = i;
		}

		Random random = new Random();

		int end = total - 1;

		for (int i = 0; i < total; i++) {
			int num = random.nextInt(end + 1);
			output[i] = sequence[num];
			sequence[num] = sequence[end];
			end--;
		}

		return output;
	}

	/**
	 * 清除热词
	 */
	public void clearWords() {
		if (hotWordsFlow == null) {
			return;
		}
		hotWordsFlow.rubAllViews();
		hotWordsFlow.rubKeywords();
	}

	@Override
	protected void initData() {
		umengChannel = ConfigUtil.readChannelName(mContext, Config.UMENG_CHANNEL_KEY);
		telChannel = ConfigUtil.readChannelName(mContext, Config.TEL_DATA_KEY);
		umeng.setText("友盟渠道:" + umengChannel);
		tel.setText("189渠道:" + telChannel);
	}

	@Override
	protected int getMainContentViewId() {
		return R.layout.act_about_company;
	}

}
