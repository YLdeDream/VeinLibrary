package com.shangzuo.veindemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MEditText extends RelativeLayout {
    private EditText editText;
    private ImageView iv_search;
    public MEditText(Context context) {
        super(context);
    }
    public MEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.mine_edit_text, this, true);
        editText = findViewById(R.id.edit_query);
        iv_search=findViewById(R.id.iv_search);
        ImageView iv_clea = findViewById(R.id.iv_clea);
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.MEditText);
            String inputType = a.getString(R.styleable.MEditText_inputType);
            String hint = a.getString(R.styleable.MEditText_hint);
            boolean show = a.getBoolean(R.styleable.MEditText_showSearch,true);
            if (inputType != null)
                if ("num".equals(inputType)) {
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else {
                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            if (hint!=null){
                editText.setHint(hint);
            }
            iv_search.setVisibility(show ? VISIBLE:GONE);
        }


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    iv_clea.setVisibility(VISIBLE);
                } else {
                    iv_clea.setVisibility(GONE);
                }
            }
        });

        iv_clea.setOnClickListener(view -> {
            editText.setText("");
        });


    }

    public String getText(){
        return editText.getText().toString();
    }

    public void setText(String str){
         editText.setText(str);
    }
    public void addSearchListener(OnClickListener listener){
        iv_search.setOnClickListener(listener);
    }
}
