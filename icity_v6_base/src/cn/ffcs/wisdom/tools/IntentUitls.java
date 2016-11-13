package cn.ffcs.wisdom.tools;

import cn.ffcs.wisdom.base.BaseFragmentActicity;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class IntentUitls {
	public static void intentToActivity(Context mContext, Class className) {
		Intent intent = new Intent();
		intent.setClass(mContext, className);
		// 刚直接把这个栈整体移动到前台，并保持栈中的状态不变，即栈中的activity顺序不变，如果没有，则新建一个栈来存放被启动的activity
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	public static void intentToActivity(Context mContext, Class className,
			Bundle bundle) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setClass(mContext, className);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		mContext.startActivity(intent);
	}

	public static void intentToActivity(
			BaseFragmentActicity BaseFragmentActicity, Class className) {
		Intent intent = new Intent();
		intent.setClass(BaseFragmentActicity, className);
		BaseFragmentActicity.startActivity(intent);
		// BaseFragmentActicity.overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public static void intentToActivity(
			BaseFragmentActicity BaseFragmentActicity, Class className,
			Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(BaseFragmentActicity, className);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		BaseFragmentActicity.startActivity(intent);
		// BaseFragmentActicity.overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public static void intentToActivityForResult(Activity BaseFragmentActicity,
			Class className, Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(BaseFragmentActicity, className);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		BaseFragmentActicity.startActivity(intent);
		// BaseFragmentActicity.overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public static void intentToActivityForResult(
			BaseFragmentActicity BaseFragmentActicity, Class className) {
		Intent intent = new Intent();
		intent.setClass(BaseFragmentActicity, className);
		BaseFragmentActicity.startActivityForResult(intent, 0);
		// BaseFragmentActicity.overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public static void intentToActivityForResult(Activity BaseFragmentActicity,
			Class className) {
		Intent intent = new Intent();
		intent.setClass(BaseFragmentActicity, className);
		BaseFragmentActicity.startActivityForResult(intent, 0);
		// BaseFragmentActicity.overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public static void intentToActivityForResult(
			BaseFragmentActicity BaseFragmentActicity, Class className,
			Bundle bundle) {
		Intent intent = new Intent();
		intent.setClass(BaseFragmentActicity, className);
		if (bundle != null) {
			intent.putExtras(bundle);
		}
		BaseFragmentActicity.startActivityForResult(intent, 0);
		// BaseFragmentActicity.overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public static void intentToActivityAddAction(
			BaseFragmentActicity BaseFragmentActicity, Class className,
			String action) {
		Intent intent = new Intent();
		if (action != null) {
			intent.setAction(action);
		}
		intent.setClass(BaseFragmentActicity, className);
		BaseFragmentActicity.startActivity(intent);
		// BaseFragmentActicity.overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public static void intentToActivityAddAction(
			BaseFragmentActicity BaseFragmentActicity, Class className,
			String action, int requestCode) {
		Intent intent = new Intent();
		if (action != null) {
			intent.setAction(action);
		}
		intent.setClass(BaseFragmentActicity, className);
		BaseFragmentActicity.startActivityForResult(intent, requestCode);
		// BaseFragmentActicity.overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public static void intentToActivityAddAction(Activity activity,
			Class className, String action) {
		Intent intent = new Intent();
		if (action != null) {
			intent.setAction(action);
		}
		intent.setClass(activity, className);
		activity.startActivity(intent);
		// activity.overridePendingTransition(R.anim.push_right_in,
		// R.anim.push_right_out);
	}

	public static void finishActivity(FragmentActivity fragmentActivity) {
		fragmentActivity.finish();
		// fragmentActivity.overridePendingTransition(R.anim.push_left_in,
		// R.anim.push_left_out);
	}

	public static void intentActivityOfString(
			Activity activity, String packgename) {
		Intent intent = new Intent();
		intent.setClassName(activity, packgename);
		activity.startActivity(intent);
	}

}
