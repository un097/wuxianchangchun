package com.ctbri.wxcc.community;

public class Constants_Community {
	/** 列表每次请求 返回的条数 **/
	public static final int PAGE_SIZE = 20;


	/**
	 * 旅游资讯 四个子模块的 详细打开事件 id
	 */
	public final static String ITEM_EVENT_IDS[] = new String[] { null,
			Constants_Community.E_C_pageName_spotItemClick,
			Constants_Community.E_C_pageName_showItemClick,
			Constants_Community.E_C_pageName_foodItemClick,
			Constants_Community.E_C_pageName_specialityItemClick };

	/**
	 * 旅游资讯 四个子模块的事件 id
	 */
	public final static String EVENT_IDS[] = new String[] { null,
			Constants_Community.E_C_pageName_spotClick,
			Constants_Community.E_C_pageName_showClick,
			Constants_Community.E_C_pageName_foodClick,
			Constants_Community.E_C_pageName_specialityClick };

	public final static String EVENT_PARAMS[] = new String[] {
			null ,
			"A_spot_pageName_spotName",
			"A_show_pageName_showName",
			"A_food_pageName_foodName",
			"A_speciality_pageName_specialityName"
	};

	/****
	 *
	 *
	 * 便民热线功能模块的点击量 计数事件
	 ****/
	public static final String E_C_HotLineFragment_hotLineClick = "E_C_HotLineFragment_hotLineClick";
	/****
	 *
	 *
	 * 餐饮美食子模块的点击量 计数事件
	 ****/
	public static final String E_C_pageName_foodClick = "E_C_pageName_foodClick";
	/****
	 *
	 *
	 * 餐饮美食内各美食店铺的点击量 计数事件
	 ****/
	public static final String E_C_pageName_foodItemClick = "E_C_pageName_foodItemClick";
	/****
	 *
	 * 各热线电话的点击量 计数事件
	 *
	 ****/
	public static final String E_C_pageName_hotLineItemClick = "E_C_pageName_foodItemClick";
	/****
	 *
	 *
	 * 演出活动子模块的点击量 计数事件
	 ****/
	public static final String E_C_pageName_showClick = "E_C_pageName_showClick";
	/****
	 *
	 *
	 * 演出活动子模块内各活动的点击量 计数事件
	 ****/
	public static final String E_C_pageName_showItemClick = "E_C_pageName_showItemClick";
	/****
	 *
	 *
	 * 特产子模块的点击量 计数事件
	 ****/
	public static final String E_C_pageName_specialityClick = "E_C_pageName_specialityClick";
	/****
	 *
	 *
	 * 特产子模块内各特产的点击量 计数事件
	 ****/
	public static final String E_C_pageName_specialityItemClick = "E_C_pageName_specialityItemClick";
	/****
	 *
	 *
	 * 景区子模块的点击量 计数事件
	 ****/
	public static final String E_C_pageName_spotClick = "E_C_pageName_spotClick";
	/****
	 *
	 *
	 * 景区内各景点的点击量 计数事件
	 ****/
	public static final String E_C_pageName_spotItemClick = "E_C_pageName_spotItemClick";
	/****
	 *
	 *
	 * 旅游功能模块的点击量 计数事件
	 ****/
	public static final String E_C_pageName_travelClick = "E_C_pageName_travelClick";
	/****
	 *
	 *
	 * 民意调查模块的点击量 计数事件
	 ****/
	public static final String E_C_pageName_voteClick = "E_C_pageName_voteClick";
	/****
	 *
	 *
	 * 各民意调查题目的点击量
	 ****/
	public static final String E_C_pageName_voteItemClick = "E_C_pageName_voteItemClick";
	/****
	 *
	 *
	 * 音频点播子模块点击量
	 ****/
	public static final String E_C_pageName_aodItemClick = "E_C_pageName_aodItemClick";
	/****
	 *
	 *
	 * 音频点播更多模块点击量
	 ****/
	public static final String E_C_pageName_aodMoreClick = "E_C_pageName_aodMoreClick";
	/****
	 *
	 *
	 * 音频列表模块点击量
	 ****/
	public static final String E_C_pageName_aodListClick = "E_C_pageName_aodListClick";


	/**
	 * 无线长春客户端 下载短地址
	 */
	public static final String APK_DOWNLOAD_URL = "http://t.cn/zWk3y6d";
}
