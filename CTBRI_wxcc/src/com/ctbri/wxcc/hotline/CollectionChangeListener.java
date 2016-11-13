package com.ctbri.wxcc.hotline;

import com.ctbri.wwcc.greenrobot.HotLine;

interface CollectionChangeListener {

	public abstract void onChange(int status, HotLine hotLine);

}