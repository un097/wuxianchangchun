package cn.ffcs.wisdom.base;

import cn.ffcs.wisdom.http.BaseResp;

/**
 * <h3>处理器接口类   </h3>
 * <p>作为其他处理器的父类</p>
 */
public interface IProcessor {

	/**
	 * 处理器
	 * @return BaseResp
	 */
	public BaseResp process();

}
