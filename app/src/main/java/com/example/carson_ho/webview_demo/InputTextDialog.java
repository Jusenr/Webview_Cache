package com.example.carson_ho.webview_demo;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Description:
 * Copyright  : Copyright (c) 2019
 * Email      : jusenr@163.com
 * Author     : Jusenr
 * Date       : 2019/04/12
 * Time       : 18:21
 * Project    ：ShePaiXueYuan_EN_1.1.6.
 */
public class InputTextDialog {

    private Context context;
    private Dialog dialog;
    private RelativeLayout layout_bg;
    private TextView btn_neg, btn_pos;
    private EditText edit_text;
    private Display display;

    private boolean showPosBtn = false;
    private boolean showNegBtn = false;

    public InputTextDialog(Context context) {
        this.context = context;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        this.display = windowManager.getDefaultDisplay();
    }

    public InputTextDialog builder() {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_input_text_dialog, (ViewGroup) null);
        layout_bg = (RelativeLayout) view.findViewById(R.id.lLayout_bg);
        btn_neg = (TextView) view.findViewById(R.id.tv_cancel);
        btn_pos = (TextView) view.findViewById(R.id.tv_send);
        edit_text = (EditText) view.findViewById(R.id.edit_text);
        dialog = new Dialog(context, R.style.AlertDialogStyle);
        dialog.setContentView(view);
        layout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) ((double) display.getWidth() * 0.85D), -2));
        return this;
    }

    public InputTextDialog setCancelable(boolean cancel) {
        dialog.setCancelable(cancel);
        return this;
    }

    public InputTextDialog setCanceledOnTouchOutside(boolean cancel) {
        dialog.setCanceledOnTouchOutside(cancel);
        return this;
    }

    public InputTextDialog setInputType(int type) {
        edit_text.setInputType(type);
        return this;
    }

    public InputTextDialog setPositiveButton(String text, final View.OnClickListener listener) {
        showPosBtn = true;
        if (TextUtils.isEmpty(text)) {
            btn_pos.setText(context.getString(R.string.que_ren));
        } else {
            btn_pos.setText(text);
        }

        btn_pos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }

    public InputTextDialog setNegativeButton(String text, final View.OnClickListener listener) {
        showNegBtn = true;
        if (TextUtils.isEmpty(text)) {
            btn_neg.setText(context.getString(R.string.cancel));
        } else {
            btn_neg.setText(text);
        }

        btn_neg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                listener.onClick(v);
                dialog.dismiss();
            }
        });
        return this;
    }


    public String getText() {
        return edit_text.getText().toString().trim();
    }

    private void setLayout() {
        if (showPosBtn && showNegBtn) {
            btn_pos.setVisibility(View.VISIBLE);
            btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
            btn_neg.setVisibility(View.VISIBLE);
            btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
        }
    }

    public void show() {
        setLayout();
        dialog.show();

        showSoftInputFromWindow(edit_text);

    }

    public void showSoftInputFromWindow(final EditText editText) {
        //监听软键盘是否显示或隐藏
        layout_bg.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        Rect r = new Rect();
                        layout_bg.getWindowVisibleDisplayFrame(r);
                        int screenHeight = layout_bg.getRootView().getHeight();
                        int heightDifference = screenHeight - (r.bottom);
                        if (heightDifference > 200) {
                            //软键盘显示
// changeKeyboardHeight(heightDifference);
                        } else {
                            //软键盘隐藏

                        }
                    }

                });
        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                editText.setFocusable(true);
                editText.setFocusableInTouchMode(true);
                return false;
            }

        });

        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        editText.findFocus();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);// 显示输入法
    }


}
