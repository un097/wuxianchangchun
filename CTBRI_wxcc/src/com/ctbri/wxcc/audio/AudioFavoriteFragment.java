package com.ctbri.wxcc.audio;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

import com.ctbri.wxcc.Constants;
import com.ctbri.wxcc.R;
import com.ctbri.wxcc.community.BaseActivity;
import com.ctbri.wxcc.entity.AudioFavortieBean;
import com.ctbri.wxcc.entity.AudioFavortieBean.AudioFavoriteEntity;
import com.ctbri.wxcc.media.MediaPlayerActivity;
import com.ctbri.wxcc.media.MediaUtils;
import com.ctbri.wxcc.media.MyFavoriteFragment;
import com.ctbri.wxcc.widget.ColorRounderDrawable;

public class AudioFavoriteFragment extends MyFavoriteFragment<AudioFavortieBean, AudioFavortieBean.AudioFavoriteEntity> {

	private int mRadius;

	@Override
	protected void checkItem(AudioFavoriteEntity e) {
		e.checked = true;
	}

	@Override
	protected String getListUrl() {
		return Constants.METHOD_AUDIO_FAVORITES;
	}

	@Override
	protected boolean itemIsChecked(AudioFavoriteEntity e) {
		return e.checked;
	}

	@Override
	public String getItemId(AudioFavoriteEntity e) {
		return e.getId();
	}

	@Override
	public String getItemName(AudioFavoriteEntity e) {
		return e.getName();
	}

	@Override
	public String getItemPic(AudioFavoriteEntity e) {
		return e.getPic();
	}

	@Override
	public int getItemGroupLayout() {
		return R.layout.audio_favorite_group_item;
	}

	@Override
	public int getItemLayout() {
		return R.layout.audio_favorite_item;
	}

	@Override
	protected int getLayoutResId() {
		return R.layout.audio_favorite_gridview;
	}

	@Override
	public void unFavroiteGroup(String ids) {
		MediaUtils.unAudioFavorite(ids, MediaUtils.FLAG_GROUP, (BaseActivity) activity);
	}

	@Override
	public void unFavorite(String ids) {
		MediaUtils.unAudioFavorite(ids, MediaUtils.FLAG_SINGLE_VIDEO, (BaseActivity) activity);
	}

	@Override
	protected Class<AudioFavortieBean> getGsonClass() {
		return AudioFavortieBean.class;
	}

	@Override
	protected List<AudioFavoriteEntity> getEntitys(AudioFavortieBean bean) {
		if (bean != null && bean.getData() != null)
			return bean.getData().getAudios();
		return null;
	}

	@Override
	protected boolean isEnd(AudioFavortieBean bean) {
		return bean.getData().getIs_end() == 1;
	}

	@Override
	protected boolean itemIsChecked(AudioFavoriteEntity e, boolean checked) {
		e.checked = checked;
		return checked;
	}

	@Override
	protected int getViewItemType(int postion, AudioFavoriteEntity data) {
		if (data.getType() == AudioFavoriteEntity.TYPE_GROUP)
			return ITEM_TYPE_GROUP;
		return ITEM_TYPE_ITEM;
	}

	@Override
	public void onAttach(Activity activity_) {
		super.onAttach(activity_);
		mRadius = getResources().getDimensionPixelSize(R.dimen.audio_favorite_item_radius);
	}

	/**
	 * 更改 FrameView 的背影图片
	 * 
	 * @param frame
	 * @param isChecked
	 */
	protected void changeFrameBg(View frame, boolean isChecked) {
		if (!isChecked) {
			frame.setBackgroundDrawable(new ColorRounderDrawable(checked_color, mRadius));
		} else {
			frame.setBackgroundDrawable(null);
		}
	}

	@Override
	protected void onItemClick(AdapterView<?> parent, View view, int position, long id, AudioFavoriteEntity entity) {
		if (AudioFavoriteEntity.TYPE_GROUP == entity.getType()) {
			Intent toListIntent = new Intent(activity, AudioListActivity.class);
			toListIntent.putExtra("group_id", entity.getId());
			toListIntent.putExtra("title", entity.getName());
			startActivity(toListIntent);
		} else {
			Intent toDetail = new Intent(activity, MediaPlayerActivity.class);
			toDetail.putExtra("type_id", MediaPlayerActivity.TYPE_AUDIO_VOD);
			toDetail.putExtra("vod_id", entity.getId());
			startActivity(toDetail);
		}
	}

	@Override
	protected String getActionBarTitle() {
		return getString(R.string.title_myaod_favorite);
	}

}
