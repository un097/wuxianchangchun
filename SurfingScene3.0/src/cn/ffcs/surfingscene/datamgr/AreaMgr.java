package cn.ffcs.surfingscene.datamgr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import cn.ffcs.surfingscene.sqlite.AreaList;
import cn.ffcs.surfingscene.sqlite.AreaService;

import com.ffcs.surfingscene.entity.AreaEntity;

public class AreaMgr {

	private static AreaMgr mInstance = new AreaMgr();

	private List<AreaList> proList = null;
	private Map<String, List<AreaList>> cityList = new HashMap<String, List<AreaList>>(); // 二级城市
	private Map<String, List<AreaList>> countyList = new HashMap<String, List<AreaList>>(); // 三级区域

	public static AreaMgr getInstance() {
		if (mInstance == null) {
			mInstance = new AreaMgr();
		}
		return mInstance;
	}

	public void saveAreaList(Context context, List<AreaEntity> list) {
		AreaService.getInstance(context).deleteAll();
		AreaService.getInstance(context).SaveCityList(list);
	}

	/**
	 * 刷缓存城市数据
	 * @param context
	 * @param areaCode 需要的省份areacode
	 */
	public void refresh(Context context, String areaCode) {
		proList = AreaService.getInstance(context).getProList();
		refreshSecond(context, areaCode);
	}

	/**
	 * 刷新2级
	 * @param context
	 * @param areaCode
	 */
	public void refreshSecond(Context context, String areaCode) {
		List<AreaList> secondCityList = AreaService.getInstance(context).getCityList(areaCode);
		cityList.put(areaCode, secondCityList);
	}

	/**
	 * 刷新3级
	 * @param context
	 * @param areaCode
	 */
	public void refreshThrid(Context context, String areaCode) {
		List<AreaList> thridCityList = AreaService.getInstance(context).getCityList(areaCode);
		countyList.put(areaCode, thridCityList);
	}

	/**
	 * 获取1级
	 * @param context
	 */
	public List<AreaList> getFirstAreaList(Context context) {
		if (proList == null) {
			 proList = AreaService.getInstance(context).getProList();
		}
		return proList;
	}
	
	/**
	 * 获取单独一个城市信息
	 * @param context
	 * @param areaCode
	 * @return
	 */
	public AreaList getAreaList(Context context,String areaCode){
		return AreaService.getInstance(context).getAreaListEntity(areaCode);
	}

	/**
	 * 获取2级
	 * @param context
	 * @param areaCode
	 * @return
	 */
	public List<AreaList> getSecondAreaList(Context context, String areaCode) {
		if (cityList.get(areaCode) == null) {
			refreshSecond(context, areaCode);
		}
		return cityList.get(areaCode);
	}

	/**
	 * 获取3级
	 * @param context
	 * @param areaCode
	 * @return
	 */
	public List<AreaList> getThridAreaList(Context context, String areaCode) {
		if (countyList.get(areaCode) == null) {
			refreshThrid(context, areaCode);
		}
		return countyList.get(areaCode);
	}
	
	/**
	 * 根据城市获取省份
	 * @return
	 */
	public AreaList getProByCity(Context context,String areaCode){
		return AreaService.getInstance(context).getParentAreaEntity(areaCode);
	}
}
