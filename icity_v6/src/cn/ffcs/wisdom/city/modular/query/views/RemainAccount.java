package cn.ffcs.wisdom.city.modular.query.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import cn.ffcs.wisdom.city.v6.R;

/**
 * <p>Title:  记住密码，和记住帐号一起控件</p>
 * <p>Description: </p>
 * <p>@author: caijj                </p>
 * <p>Copyright: Copyright (c) 2012    </p>
 * <p>Company: FFCS Co., Ltd.          </p>
 * <p>Create Time: 2012-12-12             </p>
 * <p>Update Time:                     </p>
 * <p>Updater:                         </p>
 * <p>Update Comments:                 </p>
 */
public class RemainAccount extends LinearLayout {

	private CheckBox remainpwd;
	private CheckBox remainAccount;

	private View remainPwsLayout;
	private View remainAccountLayout;

	public RemainAccount(Context context) {
		this(context, null);
	}

	public RemainAccount(Context context, AttributeSet attrs) {
		super(context, attrs);

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout mMainLayout = (LinearLayout) mInflater.inflate(
				R.layout.widget_query_remain_account, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		addView(mMainLayout, params);

		remainpwd = (CheckBox) mMainLayout.findViewById(R.id.remainpwd);
		remainpwd.setClickable(false);
		remainAccount = (CheckBox) mMainLayout.findViewById(R.id.remainAccount);
		remainAccount.setClickable(false);
		remainAccountLayout = findViewById(R.id.remainAccount_layout);
		remainPwsLayout = findViewById(R.id.remainpwd_layout);
		
		remainAccountLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				remainAccount.setChecked(!remainAccount.isChecked());
				if (!remainAccount.isChecked()) {
					remainpwd.setChecked(false);
				}
			}
		});
		
		remainPwsLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (remainAccount.isChecked()) {
					remainpwd.setChecked(!remainpwd.isChecked());
				}
			}
		});
	}

	public void showRemainPwd() {
		remainPwsLayout.setVisibility(View.VISIBLE);
	}

	public void showRemainAccount() {
		remainAccountLayout.setVisibility(View.VISIBLE);
	}

	public void setPwdChecked(boolean checked) {
		remainpwd.setChecked(checked);
	}

	public void setAccountChecked(boolean checked) {
		remainAccount.setChecked(checked);
	}

	public boolean isPwdChecked() {
		return remainpwd.isChecked();
	}

	public boolean isAccountChecked() {
		return remainAccount.isChecked();
	}

}
