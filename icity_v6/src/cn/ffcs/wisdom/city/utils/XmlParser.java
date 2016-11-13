package cn.ffcs.wisdom.city.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.XmlResourceParser;
import cn.ffcs.wisdom.city.entity.CityEntity;
import cn.ffcs.wisdom.city.entity.WzCarTypeEntry;
import cn.ffcs.wisdom.city.v6.R;
import cn.ffcs.wisdom.tools.Log;

public class XmlParser {
	private static List<WzCarTypeEntry> carTypeList = new ArrayList<WzCarTypeEntry>();
	private static Map<String, CityEntity> cityMap;
	private static Map<String, String> provinceMap;

	/**
	 * 转换城市编码
	 * @return
	 */
	public static Map<String, CityEntity> cityCodeParser(Context ctx) {
		try {
			XmlResourceParser parser = ctx.getResources().getXml(R.xml.citycode_map);
			int eventType = parser.getEventType();
			CityEntity city = null;
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlResourceParser.START_DOCUMENT:
					if (cityMap == null) {
						cityMap = new HashMap<String, CityEntity>();
					}
					break;
				case XmlResourceParser.START_TAG:
					if ("city".equals(parser.getName())) {
						city = new CityEntity();
					}

					if (city != null) {
						if ("name".equals(parser.getName())) {
							city.setCity_name(parser.nextText());
						}
						if ("code".equals(parser.getName())) {
							city.setCity_code(parser.nextText());
						}
						if ("zip".equals(parser.getName())) {
							city.setZip(parser.nextText());
						}
						if ("cartype".equals(parser.getName())) {
							city.setCarType(parser.nextText());
						}
					}
					break;
				case XmlResourceParser.END_TAG:
					if ("city".equals(parser.getName())) {
						cityMap.put(city.getCity_code(), city);
						city = null;
					}
					break;
				default:
					break;
				}

				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(e.getMessage(), e);
		} catch (IOException e) {
			Log.e(e.getMessage(), e);
		}

		return cityMap;
	}

	/**
	 * 根据citycode获取车牌码
	 * 
	 * @param ctx
	 * @param cityCode
	 * @return
	 */
	public static String getCarTypeByCitycode(Context ctx, String cityCode) {
		Map<String, CityEntity> map = XmlParser.cityCodeParser(ctx);

		if (map.containsKey(cityCode)) {
			CityEntity entity = map.get(cityCode);
			return entity.getCarType();
		}

		return null;
	}

	/**
	 * 获取车辆类型
	 * 
	 * @param cityCode
	 * @return
	 */
	public static List<WzCarTypeEntry> carTypeParser(Context ctx) {
		try {
			XmlResourceParser parser = ctx.getResources().getXml(R.xml.car_type_map);
			int eventType = parser.getEventType();
			WzCarTypeEntry entry = null;
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlResourceParser.START_DOCUMENT:
					// if (carTypeList == null) {
					// carTypeList = new ArrayList<WzCarTypeEntry>();
					// }
					break;
				case XmlResourceParser.START_TAG:
					if ("car_type".equals(parser.getName())) {
						entry = new WzCarTypeEntry();
					}
					if ("type_name".equals(parser.getName())) {
						entry.setTypeName(parser.nextText());
					}
					if ("type_coe".equals(parser.getName())) {
						entry.setTypeCode(parser.nextText());
					}
					break;
				case XmlResourceParser.END_TAG:
					if ("car_type".equals(parser.getName())) {
						carTypeList.add(entry);
						entry = null;
					}
					break;
				}

				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(e.getMessage(), e);
		} catch (IOException e) {
			Log.e(e.getMessage(), e);
		}

		return carTypeList;
	}

	public static List<WzCarTypeEntry> getWzCarTypeData(Context context) {
		if (carTypeList == null) {
			carTypeList = new ArrayList<WzCarTypeEntry>();
		} else if (carTypeList.size() <= 0) {
			return carTypeParser(context);
		}
		return carTypeList;
	}
	
	/**
	 * 转换省份编码
	 * @return
	 */
	public static Map<String/** province name**/, String /** province code**/> provinceCodeParser(Context ctx) {
		try {
			XmlResourceParser parser = ctx.getResources().getXml(R.xml.province_code_map);
			int eventType = parser.getEventType();
			String provinceName = "";
			String provinceCode = "";
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlResourceParser.START_DOCUMENT:
					if (provinceMap == null) {
						provinceMap = new HashMap<String, String>();
					}
					break;
				case XmlResourceParser.START_TAG:
					if ("key".equals(parser.getName())) {
						provinceName = parser.nextText();
					}
					if ("code".equals(parser.getName())) {
						provinceCode = parser.nextText();
					}
					break;
				case XmlResourceParser.END_TAG:
					if ("province".equals(parser.getName())) {
						provinceMap.put(provinceName, provinceCode);
					}
					break;
				default:
					break;
				}

				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			Log.e("Xml解析异常", e);
		} catch (IOException e) {
			Log.e("IO异常", e);
		}

		return provinceMap;
	}

	/**
	 * 通过省份名称获取编码
	 * @param ctx
	 * @param proviceCode
	 * @return
	 */
	public static String getProviceCodeByCode(Context ctx, String proviceCode) {
		Map<String, String> map = XmlParser.provinceCodeParser(ctx);
		
		if(map.containsKey(proviceCode)) {
			return map.get(proviceCode);
		}
		
		return null;
	}

	/**
	 * 获取车辆所在城市列表
	 * @param ctx
	 * @return
	 */
	public static List<String> carCityCodeParser(Context ctx) {
		List<String> cityCodeList = new ArrayList<String>(0);
		try {
			XmlResourceParser parser = ctx.getResources().getXml(R.xml.car_citycode);
			int eventType = parser.getEventType();
			while (eventType != XmlResourceParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlResourceParser.START_DOCUMENT:

					break;
				case XmlResourceParser.START_TAG:
					if ("city_code".equals(parser.getName())) {
						cityCodeList.add(parser.nextText());
					}
					break;
				case XmlResourceParser.END_TAG:
					break;
				}

				eventType = parser.next();
			}
		} catch (XmlPullParserException e) {
			Log.e(e.getMessage(), e);
		} catch (IOException e) {
			Log.e(e.getMessage(), e);
		}

		return cityCodeList;
	}
}
