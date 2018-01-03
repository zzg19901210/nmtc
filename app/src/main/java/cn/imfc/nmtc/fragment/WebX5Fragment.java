package cn.imfc.nmtc.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;

import cn.imfc.nmtc.R;
import cn.imfc.nmtc.sonic.SonicJavaScriptInterface;
import cn.imfc.nmtc.sonic.SonicRuntimeImpl;
import cn.imfc.nmtc.sonic.SonicSessionClientImpl;
import cn.imfc.nmtc.view.webview.WebViewProgressBar;
import cn.imfc.nmtc.view.webview.utils.X5WebViewProgress;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: zhangzhigang
 * \* Date: 2018/1/2
 * \* Time: 下午3:35
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class WebX5Fragment extends Fragment {


    private ViewGroup mViewParent;

    private X5WebViewProgress webView;

    private SonicSession sonicSession;



    private WebViewProgressBar progressBar;//进度条的矩形（进度线）
    private Handler handler;

    public static WebX5Fragment newInstance(String s){
        WebX5Fragment webX5FragmentFragment = new WebX5Fragment();
        Bundle bundle = new Bundle();
        bundle.putString("Home",s);
        webX5FragmentFragment.setArguments(bundle);
        return webX5FragmentFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_web_content, container, false);
        Bundle bundle = getArguments();
        init(view);
        return view;
    }

    private void initProgressBar() {
        progressBar = new WebViewProgressBar(getContext());
        //设置进度条的size
        progressBar.setLayoutParams(new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        //刚开始时候进度条不可见
        progressBar.setVisibility(View.GONE);
        //把进度条添加到webView里面
        webView.addView(progressBar);
        //初始化handle
        handler = new Handler();
    }

    public void init(View view ) {
        Intent intent = getActivity().getIntent();
        mViewParent = (ViewGroup) view.findViewById(R.id.main_web_view);
        //初始化webView
        webView = new X5WebViewProgress(getContext(), null);
        mViewParent.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));
        initProgressBar();

        String url = "http://vote.lrkpzx.com/static/app/lottery_wechat/home_page.html";

        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);


        // init sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getActivity().getApplication()), new SonicConfig.Builder().build());
        }

        SonicSessionClientImpl sonicSessionClient = null;

        // if it's sonic mode , startup sonic session at first time
        SonicSessionConfig.Builder sessionConfigBuilder = new SonicSessionConfig.Builder();
        // create sonic session and run sonic flow
        sonicSession = SonicEngine.getInstance().createSession(url, sessionConfigBuilder.build());
        if (null != sonicSession) {
            sonicSession.bindClient(sonicSessionClient = new SonicSessionClientImpl());
        } else {
            // this only happen when a same sonic session is already running,
            // u can comment following code to feedback for default mode to
            //throw new UnknownError("create session fail!");
        }

        // start init flow ... in the real world, the init flow may cost a long time as startup
        // runtime、init configs....

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (sonicSession != null) {
                    sonicSession.getSessionClient().pageFinish(url);
                }
            }

            @TargetApi(21)
            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return shouldInterceptRequest(view, request.getUrl().toString());
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (sonicSession != null) {

                    android.webkit.WebResourceResponse  webResourceResponse=(android.webkit.WebResourceResponse)sonicSession.getSessionClient().requestResource(url);

                    //if( webResourceResponse instanceof WebResourceResponse){
                    //    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
                    //}else{
                    //    return  null;
                    //}

                }
                return null;
            }

        });


        WebSettings webSettings = webView.getSettings();

        // add java script interface
        // note:if api level if lower than 17(android 4.2), addJavascriptInterface has security
        // issue, please use x5 or see https://developer.android.com/reference/android/webkit/
        // WebView.html#addJavascriptInterface(java.lang.Object, java.lang.String)
        webSettings.setJavaScriptEnabled(true);
        webView.removeJavascriptInterface("searchBoxJavaBridge_");

        intent.putExtra(SonicJavaScriptInterface.PARAM_LOAD_URL_TIME, System.currentTimeMillis());
        webView.addJavascriptInterface(new SonicJavaScriptInterface(sonicSessionClient, intent), "sonic");

        // init webview settings
        webSettings.setAllowContentAccess(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);


        // webview is ready now, just tell session client to bind
        if (sonicSessionClient != null) {
            sonicSessionClient.bindWebView(webView);
            sonicSessionClient.clientReady();
        } else { // default mode
            webView.loadUrl(url);
        }

    }





}