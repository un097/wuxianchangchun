package cn.ffcs.wisdom.tools;

/**
 * <p>Title:  频率控制器</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2013-3-29             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class Frequency {

	private long sectionStart;
	private long sectionEnd;
	private long interval; // 间隔时间 毫秒

	public Frequency(int interval) {
		sectionStart = System.currentTimeMillis();
		sectionEnd = System.currentTimeMillis();
		this.interval = interval;
	}

	public boolean toRun() {
		sectionEnd = System.currentTimeMillis();
		long result = sectionEnd - sectionStart;
		if (result >= interval) {
			sectionStart = System.currentTimeMillis();
			return true;
		}
		return false;
	}

}
