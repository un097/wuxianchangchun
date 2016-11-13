package cn.ffcs.wisdom.city.xg;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.ffcs.wisdom.city.utils.Callback;
import cn.ffcs.wisdom.city.v6.R;

/**
 * Created by echo on 2016/1/28.
 */
public class MsgDialog extends Dialog implements View.OnClickListener {

    private EditText editText = null;

    private Button ok = null;
    private Button cancel = null;

    private Context context;

    private String title;
    private String msg;
    private String oldValue;

    Callback<String> callback;

    private String okButtonText;
    private String cancelButtonText;

    private String editHit;

    private int maxInputLen = -1;

    public MsgDialog(Context context) {
        this(context, null, null);
    }

    public MsgDialog(Context context, String title, String msg) {
        super(context, R.style.tipDialog);
        this.context = context;
        this.title = title;
        this.msg = msg;
    }


    public MsgDialog setEditHit(String editHit) {
        this.editHit = editHit;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public MsgDialog setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public MsgDialog setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getMaxInputLen() {
        return maxInputLen;
    }

    public MsgDialog setMaxInputLen(int maxInputLen) {
        this.maxInputLen = maxInputLen;
        return this;
    }

    public String getOldValue() {
        return oldValue;
    }

    public MsgDialog setOldValue(String oldValue) {
        this.oldValue = oldValue;
        return this;
    }

    public Callback<String> getCallback() {
        return callback;
    }

    public MsgDialog setCallback(Callback<String> callback) {
        this.callback = callback;
        return this;
    }

    public String getOkButtonText() {
        return okButtonText;
    }

    public MsgDialog setOkButtonText(String okButtonText) {
        this.okButtonText = okButtonText;
        return this;
    }

    public String getCancelButtonText() {
        return cancelButtonText;
    }

    public MsgDialog setCancelButtonText(String cancelButtonText) {
        this.cancelButtonText = cancelButtonText;
        return this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_confirm);

        ok = (Button) findViewById(R.id.btnOk);
        ok.setOnClickListener(this);
        cancel = (Button) findViewById(R.id.btnCancel);
        cancel.setOnClickListener(this);


        editText = (EditText) findViewById(R.id.editTv);

        if (oldValue == null)
            editText.setVisibility(View.GONE);
        else
            editText.setText(oldValue);

        if (editHit != null)
            editText.setHint(editHit);

        if (maxInputLen > 0)
            editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxInputLen)});

        TextView tvMsg = (TextView) findViewById(R.id.tvMsg);

        if (msg == null || msg.trim().length() == 0)
            tvMsg.setVisibility(View.GONE);
        else
            tvMsg.setText(msg);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(title);

        if (okButtonText != null)
            ok.setText(okButtonText);

        if (cancelButtonText != null)
            cancel.setText(cancelButtonText);

        setCancelable(true);
        setCanceledOnTouchOutside(false);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnCancel){
            dismiss();
        }else if(v.getId() == R.id.btnOk){
            if (oldValue != null) {
                if (TextUtils.isEmpty(editText.getText().toString())) {
                    Toast.makeText(context, "请输入", Toast.LENGTH_LONG).show();
                }
                //如果值有变化才通知
                if (!editText.getText().toString().equals(oldValue)
                        && callback != null) {
                    callback.onData(editText.getText().toString());
                }
            } else {
                callback.onData(ok.getText().toString());
            }
            dismiss();
        }
    }
}