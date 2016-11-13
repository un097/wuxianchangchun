package cn.ffcs.wisdom.city.simico.kit.activity;

import cn.ffcs.wisdom.city.simico.ui.notify.WaitDialog;

public interface DialogControl {

	public abstract void hideWaitDialog();

	public abstract WaitDialog showWaitDialog();

	public abstract WaitDialog showWaitDialog(int resid);

	public abstract WaitDialog showWaitDialog(String text);
}
