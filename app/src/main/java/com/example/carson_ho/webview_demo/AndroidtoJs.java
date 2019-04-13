package com.example.carson_ho.webview_demo;

import android.util.Log;
import android.webkit.JavascriptInterface;

/**
 * Description:
 * Author     : Jusenr
 * Email      : jusenr@163.com
 * Date       : 2019/04/14  0:50.
 */
public class AndroidtoJs extends Object {

    // 定义JS需要调用的方法
    // 被JS调用的方法必须加入@JavascriptInterface注解
    @JavascriptInterface
    public void hello(String msg) {
        Log.i("AndroidtoJs", "hello: "+msg);
        System.out.println("AndroidtoJs-JS调用了Android的hello方法");
    }
}
