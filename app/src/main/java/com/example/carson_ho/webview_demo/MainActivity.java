package com.example.carson_ho.webview_demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

import static android.view.View.GONE;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "MainActivity";
    /**
     * 填充品论框
     */
    private ViewStub viewStubPinlun;
    /***
     * 是否填充 评论看布局
     */
    private boolean isInflated;
    /**
     * 填充 评论看布局
     */
    private View inflatedStub;
    /**
     * 评论输入框
     */
    private EditText inPutPinglun;

    private Animation mShowAction;
    private Animation mHiddenAction;

    WebView mWebview;
    TextView beginLoading, endLoading, loading, mtitle, btn_cancel, btn_send;

    public Activity mActivity;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mActivity = this;

        viewStubPinlun = (ViewStub) findViewById(R.id.view_stub_ping_lun);

        mWebview = (WebView) findViewById(R.id.webView1);
        beginLoading = (TextView) findViewById(R.id.text_beginLoading);
        endLoading = (TextView) findViewById(R.id.text_endLoading);
        loading = (TextView) findViewById(R.id.text_Loading);
        mtitle = (TextView) findViewById(R.id.title);


        WebSettings webSettings = mWebview.getSettings();

        // 设置与Js交互的权限
        webSettings.setJavaScriptEnabled(true);
        // 设置允许JS弹窗
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);


        // 通过addJavascriptInterface()将Java对象映射到JS对象
        //参数1：Javascript对象名
        //参数2：Java对象名
        mWebview.addJavascriptInterface(new AndroidtoJs(), "test");//AndroidtoJS类对象映射到js的test对象

        // 加载JS代码
        // 格式规定为:file:///android_asset/文件名.html
        mWebview.loadUrl("file:///android_asset/javascript.html");


        //如果JS想要得到Android方法的返回值，只能通过 WebView 的 loadUrl （）去执行 JS 方法把返回值传递回去
        //String result="result";
        //mWebview.loadUrl("javascript:returnResult(" + result + ")");


//        mWebview.loadUrl("http://www.baidu.com/");


        //设置不用系统浏览器打开,直接显示在当前Webview
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        //设置WebChromeClient类
        mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setTitle("Alert");
                b.setMessage(message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }

            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                System.out.println("标题在这里");
                mtitle.setText(title);
            }


            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    String progress = newProgress + "%";
                    loading.setText(progress);
                } else if (newProgress == 100) {
                    String progress = newProgress + "%";
                    loading.setText(progress);
                }
            }
        });


        //设置WebViewClient类
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                // 步骤2：根据协议的参数，判断是否是所需要的url
                // 一般根据scheme（协议格式） & authority（协议名）判断（前两个参数）
                //假定传入进来的 url = "js://webview?arg1=111&arg2=222"（同时也是约定好的需要拦截的）

                Uri uri = Uri.parse(url);
                // 如果url的协议 = 预先约定的 js 协议
                // 就解析往下解析参数
                if (uri.getScheme().equals("js")) {

                    // 如果 authority = 预先约定协议里的 webview，即代表都符合约定的协议
                    // 所以拦截url,下面JS开始调用Android需要的方法
                    if (uri.getAuthority().equals("webview")) {

                        // 步骤3：
                        // 执行JS所需要调用的逻辑
                        System.out.println("js调用了Android的方法");
                        // 可以在协议上带有参数并传递到Android上
                        HashMap<String, String> params = new HashMap<>();
                        Set<String> collection = uri.getQueryParameterNames();

//                        showInputText();

                        mWebview.post(new Runnable() {
                            @Override
                            public void run() {
                                String params = "{\"content\":\"120c83f7606f9a1e58f\",\"deviceType\":\"android\",\"hint\":\"请输入\"}";
                                String js1 = "javascript:returnResult(#result#)";
                                showInputWindow(params, js1);
                            }
                        });
                    }

                    return true;
                }
                return super.shouldOverrideUrlLoading(view, url);
            }

            //设置加载前的函数
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                System.out.println("开始加载了");
                beginLoading.setText("开始加载了");

            }

            //设置结束加载函数
            @Override
            public void onPageFinished(WebView view, String url) {
                endLoading.setText("结束加载了");

            }
        });
    }


    private void showInputText() {
        try {
            String params = "{\"content\":\"120c83f7606f9a1e58f\",\"deviceType\":\"android\"}";
            if (!TextUtils.isEmpty(params)) {
                JSONObject object = new JSONObject(params);
                final String content = object.optString("content");
                String confirm = object.optString("confirm");
                String cancel = object.optString("cancel");
            }

            final JSONObject jsonObject = new JSONObject();
            final InputTextDialog mAlertDialog = new InputTextDialog(this).builder();

            mAlertDialog.setNegativeButton(getString(R.string.cancel), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        jsonObject.put("no", "cancel");
                        jsonObject.put("result", false);
                        String s = jsonObject.toString();
                        mWebview.loadUrl("javascript:returnResult(" + s + ")");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            mAlertDialog.setPositiveButton(getString(R.string.send), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String text = mAlertDialog.getText();
                        jsonObject.put("yes", "yes");
                        jsonObject.put("result", true);
                        jsonObject.put("content", text);
                        String s = jsonObject.toString();
                        Log.i("InputText", "doMethod-text: " + text);
                        mWebview.loadUrl("javascript:returnResult(" + s + ")");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

            mAlertDialog.show();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_1:
                // 通过Handler发送消息
                mWebview.post(new Runnable() {
                    @Override
                    public void run() {
                        // 注意调用的JS方法名要对应上
                        // 调用javascript的callJS()方法
                        mWebview.loadUrl("javascript:callJS()");
                    }
                });
                break;
            case R.id.btn_2:
                showInputText();
                break;
            case R.id.btn_3:
                String params = "{\"content\":\"120c83f7606f9a1e58f\",\"deviceType\":\"android\",\"hint\":\"请输入\"}";
                String js1 = "javascript:returnResult(#result#)";
                showInputWindow(params, js1);
                break;
            case R.id.btn_4:
                startActivity(new Intent(this, PhotoActivity.class));
                break;
            case R.id.btn_5:
                startActivity(new Intent(this, TableActivity.class));
                break;
        }
    }

    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebview.canGoBack()) {
            mWebview.goBack();
            return true;
        }
        // 处理 弹出框输入布局
        if (inPutPinglun != null && inPutPinglun.getVisibility() == View.VISIBLE) {
            inPutPinglun.setVisibility(View.GONE);
            viewStubPinlun.setVisibility(View.GONE);
            return true;
        }


        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebview != null) {
            mWebview.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebview.clearHistory();

            ((ViewGroup) mWebview.getParent()).removeView(mWebview);
            mWebview.destroy();
            mWebview = null;
        }
        super.onDestroy();
    }


    public void showInputWindow(String param, final String callBack) {
        setGlobalLayoutListener(this, inflatedStub);
        if (!isInflated) {
            inflatedStub = viewStubPinlun.inflate();
            isInflated = true;
            btn_cancel = (TextView) inflatedStub.findViewById(R.id.tv_cancel);
            btn_send = (TextView) inflatedStub.findViewById(R.id.tv_send);
            inPutPinglun = (EditText) inflatedStub.findViewById(R.id.edit_text);
            // 发送
//            inPutPinglun.setImeOptions(EditorInfo.IME_ACTION_SEND);
            inPutPinglun.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            //水平滚动设置为False
            inPutPinglun.setHorizontallyScrolling(false);
            inPutPinglun.setMinLines(4);
            initAnimations_One();
//            initAnimations_Two();
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            inPutPinglun.setLayoutParams(params);
        }
        if (inflatedStub.getVisibility() == GONE) {
            inflatedStub.setVisibility(View.VISIBLE);
            inPutPinglun.setVisibility(View.VISIBLE);
        }
        try {
            JSONObject object = new JSONObject(param);
            Log.i(TAG, "showInputWindow: " + param);
            inPutPinglun.setHint(object.optString("hint"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        inPutPinglun.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(inPutPinglun, InputMethodManager.SHOW_FORCED);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflatedStub.setVisibility(GONE);
                inPutPinglun.setVisibility(GONE);
                inPutPinglun.setText(null);
                inPutPinglun.clearFocus();
                imm.hideSoftInputFromWindow(inPutPinglun.getWindowToken(), 0); //强制隐藏键盘
            }
        });

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("message", inPutPinglun.getText().toString().trim());
                    jsonObject.put("result", true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

//                String json = UrlUtil.getFormatJs(callBack, jsonObject.toString());
                String s = jsonObject.toString();
                Log.i("InputText", "text: " + s);
                mWebview.loadUrl("javascript:returnResult(" + s + ")");
                inflatedStub.setVisibility(GONE);
                inPutPinglun.setVisibility(GONE);
                inPutPinglun.setText(null);
                inPutPinglun.clearFocus();
                imm.hideSoftInputFromWindow(inPutPinglun.getWindowToken(), 0); //强制隐藏键盘
            }
        });

// 监听文字框
//        inPutPinglun.addTextChangedListener(new TextWatcher() {
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!TextUtils.isEmpty(s)) {
//                    if (btn_send.getVisibility() == View.GONE) {
//                        btn_send.setVisibility(View.VISIBLE);
//
//                        btn_send.startAnimation(mShowAction);
//                    }
//
//
//                } else {
//                    btn_send.setVisibility(View.GONE);
//                    btn_send.startAnimation(mHiddenAction);
//
//                }
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });
    }

    private void initAnimations_One() {
        mShowAction = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        mHiddenAction = AnimationUtils.loadAnimation(this, R.anim.right_out);
    }

    /**
     * 弹出软键盘时，view上移操作
     *
     * @param activity    activity
     * @param contentView 上移view
     */
    public static void setGlobalLayoutListener(final Activity activity, final View contentView) {
        View decorView = activity.getWindow().getDecorView();
        if (contentView != null) {
            decorView.getViewTreeObserver().addOnGlobalLayoutListener(getGlobalLayoutListener(activity, contentView));
        }
    }

    public static void removeOnGlobalLayoutListener(final Activity activity, final View contentView) {
        View decorView = activity.getWindow().getDecorView();
        if (contentView != null) {
            decorView.getViewTreeObserver().removeOnGlobalLayoutListener(getGlobalLayoutListener(activity, contentView));
        }
    }


    public static ViewTreeObserver.OnGlobalLayoutListener getGlobalLayoutListener(final Activity activity, final View contentView) {
        return new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(r);

                int height = activity.getResources().getDisplayMetrics().heightPixels;
                int diff = height - r.bottom;

                if (diff != 0) {
                    if (contentView.getPaddingBottom() != diff) {
                        contentView.setPadding(0, 0, 0, diff);
                    }
                } else {
                    if (contentView.getPaddingBottom() != 0) {
                        contentView.setPadding(0, 0, 0, 0);
                    }
                }

                activity.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        };
    }
}
