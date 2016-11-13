package com.wookii.widget;

import com.wookii.wookiiwidget.R;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.view.animation.LinearInterpolator;
import android.webkit.WebView.FindListener;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomLoadingDialog extends Dialog{

	private Context context = null;  
    private static CustomLoadingDialog customProgressDialog = null;
	public static CustomLoadingDialog mDialog;
	private static ObjectAnimator loadingAnimator;  
      
    public CustomLoadingDialog(Context context){  
        super(context);  
        this.context = context;  
    }  
      
    public CustomLoadingDialog(Context context, int theme) {  
        super(context, theme);  
    }  
      
    @SuppressLint("NewApi")
	public static CustomLoadingDialog createDialog(Context context){  
        customProgressDialog = new CustomLoadingDialog(context,R.style.dialog);  
        customProgressDialog.setContentView(R.layout.custom_loading_dialog);  
        customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;  
        ImageView imageView = (ImageView) customProgressDialog.findViewById(R.id.loadingImageView);
        loadingAnimator = ObjectAnimator.ofFloat(imageView, "rotation", 0, 360);
        loadingAnimator.setInterpolator(new LinearInterpolator());
        loadingAnimator.setDuration(1000);
		loadingAnimator.setRepeatMode(ObjectAnimator.RESTART);
		loadingAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        return customProgressDialog;  
    }  
   
    public void onWindowFocusChanged(boolean hasFocus){  
          
        if (customProgressDialog == null){  
            return;  
        }  
    }  
   
    /** 
     *  
     * [Summary] 
     *       setTitile 标题 
     * @param strTitle 
     * @return 
     * 
     */  
    public CustomLoadingDialog setTitile(String strTitle){  
        return customProgressDialog;  
    }  
      
    /** 
     *  
     * [Summary] 
     *       setMessage 提示内容 
     * @param strMessage 
     * @return 
     * 
     */  
    public CustomLoadingDialog setMessage(String strMessage){  
        TextView tvMsg = (TextView)customProgressDialog.findViewById(R.id.id_tv_loadingmsg);  
          
        if (tvMsg != null){  
            tvMsg.setText(strMessage);  
        }  
          
        return customProgressDialog;  
    }  
    
    @SuppressLint("NewApi")
	public static void showProgress(Context context,
			CharSequence title, CharSequence message, boolean indeterminate,
			boolean cancelable) {
		CustomLoadingDialog dialog = CustomLoadingDialog.createDialog(context);
		loadingAnimator.start();  
		mDialog = dialog;
		dialog.setTitle(title);
		dialog.setMessage(message.toString()).setCancelable(cancelable);
		dialog.setCanceledOnTouchOutside(cancelable);
		dialog.show();
	}

	
	@SuppressLint("NewApi")
	public static void dismissDialog(){
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		if(loadingAnimator != null){
			loadingAnimator.cancel();
		}
	}
	
	
}
