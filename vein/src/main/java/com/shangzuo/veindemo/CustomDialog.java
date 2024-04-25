package com.shangzuo.veindemo;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class CustomDialog extends Dialog implements View.OnClickListener {

    private TextView closeButton;
    private OnBack onListen; // 用于接收参数的变量

    public CustomDialog(Context context,OnBack onBack) {
        super(context);
        onListen =onBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_vein);

        closeButton = findViewById(R.id.btnCancel);
        closeButton.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onListen.onShow();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TAG", "onStop: onDismiss" );
        onListen.onDismiss();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnCancel) {
            dismiss(); // 点击关闭按钮时关闭对话框
        }
    }

    public interface OnBack {
        void onDismiss();

        void onShow();
    }
}