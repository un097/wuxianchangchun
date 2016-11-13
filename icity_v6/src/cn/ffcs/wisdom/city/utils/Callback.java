package cn.ffcs.wisdom.city.utils;

/**
 * Created by echo on 2015/12/24.
 */

public interface Callback<T> {

    /**
     * 处理回调
     *
     * @param data 返回数据
     * @return 如果返回true，表示后续可以不再处理，中止传播，如果返回false，表示回调链条还要处理
     */
    boolean onData(T data);
}
