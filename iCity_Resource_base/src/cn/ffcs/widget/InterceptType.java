package cn.ffcs.widget;

/**
 * <p>Title:   拦截类型        </p>
 * <p>Description:                     </p>
 * <p>@author: zhangwsh                </p>
 * <p>Copyright: Copyright (c) 2013    </p>
 * <p>Company: ffcs Co., Ltd.          </p>
 * <p>Create Time: 2013-9-30           </p>
 * <p>@author:                         </p> 
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public enum InterceptType {
	
	/**
	 * 由系统决定
	 */
	INTERCEPT_DEFAULT(0) {
		@Override
		String getDes() {
			return "由系统决定";
		}
	},

	/**
	 * 全部不拦截
	 */
	INTERCEPT_NULL(1) {
		@Override
		String getDes() {
			return "全部不拦截";
		}
	},

	/**
	 * 拦截横向
	 */
	INTERCEPT_TRANSVERSE(2) {
		@Override
		String getDes() {
			return "拦截横向";
		}
	},

	/**
	 * 拦截竖向
	 */
	INTERCEPT_VERTICAL(3) {
		@Override
		String getDes() {
			return "拦截竖向";
		}
	},

	/**
	 * 全部拦截
	 */
	INTERCEPT_ALL(4) {
		@Override
		String getDes() {
			return "全部拦截";
		}
	},

	/**
	 * 拦截左边横向
	 */
	INTERCEPT_LEFT_TRANSVERSE(5) {
		@Override
		String getDes() {
			return "拦截左边横向";
		}
	},

	/**
	 * 拦截右边横向
	 */
	INTERCEPT_RIGHT_TRANSVERSE(6) {
		@Override
		String getDes() {
			return "拦截右边横向";
		}
	};

	private int value;

	abstract String getDes();
	
	InterceptType(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}
}
