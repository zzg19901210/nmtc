package cn.imfc.nmtc;


import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.DownloadListener;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
import com.tencent.sonic.sdk.SonicSession;
import com.tencent.sonic.sdk.SonicSessionConfig;

import java.util.ArrayList;

import cn.imfc.nmtc.fragment.HomeFragment;
import cn.imfc.nmtc.fragment.WebX5Fragment;
import cn.imfc.nmtc.sonic.SonicJavaScriptInterface;
import cn.imfc.nmtc.sonic.SonicRuntimeImpl;
import cn.imfc.nmtc.sonic.SonicSessionClientImpl;
import cn.imfc.nmtc.view.TitleBar;
import cn.imfc.nmtc.view.webview.ProgressWebView;
import cn.imfc.nmtc.view.webview.WebViewProgressBar;
import cn.imfc.nmtc.view.webview.utils.X5WebView;
import cn.imfc.nmtc.view.webview.utils.X5WebViewProgress;

/**
 * \* Created with IntelliJ IDEA.
 * \* User: zhangzhigang
 * \* Date: 2017/12/28
 * \* Time: 上午8:43
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class MainActivity extends AppCompatActivity implements BottomNavigationBar.OnTabSelectedListener{

    public final static String PARAM_URL = "param_url";

    public final static String PARAM_MODE = "param_mode";

    private ViewGroup mViewParent;

    private X5WebViewProgress webView;

    private TitleBar titleBar;

    private SonicSession sonicSession;

    private boolean mIsSelected;

    private ImageView mCollectView;


    private WebViewProgressBar progressBar;//进度条的矩形（进度线）
    private Handler handler;

    private ArrayList<Fragment> fragments;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //String url = intent.getStringExtra(PARAM_URL);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        //去掉Titlebar
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        //避免输入法界面弹出后遮挡输入光标的问题
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);


        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        setTileBar("首页");
        //init();
        BottomNavigationBar bottomNavigationBar = (BottomNavigationBar) findViewById(R.id.bottom_navigation_bar);
        bottomNavigationBar.setMode(BottomNavigationBar.MODE_FIXED);
        bottomNavigationBar
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC
                );
        bottomNavigationBar.addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "主页").setActiveColorResource(R.color.primary_main))
                .addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "资讯").setActiveColorResource(R.color.primary_main))
                .addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "直播").setActiveColorResource(R.color.primary_main))
                .addItem(new BottomNavigationItem(R.mipmap.ic_launcher_round, "发现").setActiveColorResource(R.color.primary_main))
                .addItem(new BottomNavigationItem(R.mipmap.ic_launcher, "我").setActiveColorResource(R.color.primary_main))
                .setFirstSelectedPosition(0)
                .initialise();

        fragments = getFragments();
        setDefaultFragment();
        bottomNavigationBar.setTabSelectedListener(this);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    /** * 设置默认的 */
    private void setDefaultFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.layFrame, HomeFragment.newInstance("Home"));
        transaction.commit();
    }
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(HomeFragment.newInstance("Home"));
        fragments.add(WebX5Fragment.newInstance("News"));
        fragments.add(HomeFragment.newInstance("Live"));
        fragments.add(WebX5Fragment.newInstance("Sifar"));
        fragments.add(HomeFragment.newInstance("Me"));
        return fragments;
    }

    public void setTileBar(String title) {

        boolean isImmersive = false;

        if (hasKitKat() && !hasLollipop()) {
            isImmersive = true;
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        } else if (hasLollipop()) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            isImmersive = true;
        }

        titleBar = (TitleBar) findViewById(R.id.title_bar);

        titleBar.setImmersive(isImmersive);

        titleBar.setBackgroundColor(Color.parseColor("#d30b0b"));

        //titleBar.setLeftImageResource(R.mipmap.back_green);
        //titleBar.setLeftText("返回");
        //titleBar.setLeftTextColor(Color.WHITE);
        //titleBar.setLeftClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View v) {
        //        finish();
        //    }
        //});

        titleBar.setTitle(title);
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setSubTitleColor(Color.WHITE);
        titleBar.setDividerColor(Color.GRAY);

        titleBar.setActionTextColor(Color.WHITE);
        //mCollectView = (ImageView) titleBar.addAction(new TitleBar.ImageAction(R.mipmap.collect) {
        //    @Override
        //    public void performAction(View view) {
        //        Toast.makeText(MainActivity.this, "点击了收藏", Toast.LENGTH_SHORT).show();
        //        mCollectView.setImageResource(R.mipmap.fabu);
        //        titleBar.setTitle(mIsSelected ? "文章详情\n朋友圈" : "帖子详情");
        //        mIsSelected = !mIsSelected;
        //    }
        //});
        //
        //titleBar.addAction(new TitleBar.TextAction("发布") {
        //    @Override
        //    public void performAction(View view) {
        //        Toast.makeText(MainActivity.this, "点击了发布", Toast.LENGTH_SHORT).show();
        //    }
        //});

    }

    private void initProgressBar() {
        progressBar = new WebViewProgressBar(this);
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

    public void init() {

        Intent intent = getIntent();
        mViewParent = (ViewGroup) findViewById(R.id.main_web_view);
        //初始化webView
        webView = new X5WebViewProgress(this, null);
        mViewParent.addView(webView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.FILL_PARENT,
                FrameLayout.LayoutParams.FILL_PARENT));
        initProgressBar();

        String url = "http://vote.lrkpzx.com/static/app/lottery_wechat/home_page.html";

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        // init sonic engine if necessary, or maybe u can do this when application created
        if (!SonicEngine.isGetInstanceAllowed()) {
            SonicEngine.createInstance(new SonicRuntimeImpl(getApplication()), new SonicConfig.Builder().build());
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
            throw new UnknownError("create session fail!");
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
                    return (WebResourceResponse) sonicSession.getSessionClient().requestResource(url);
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

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (null != sonicSession) {
            sonicSession.destroy();
            sonicSession = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            //webView.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://cn.imfc.nmtc/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://cn.imfc.nmtc/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /**
     * 设置监听选项点击事件
     * @param position
     */
    @Override
    public void onTabSelected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                if (fragment.isAdded()) {
                    ft.replace(R.id.layFrame, fragment);
                } else {
                    ft.add(R.id.layFrame, fragment);
                }
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabUnselected(int position) {
        if (fragments != null) {
            if (position < fragments.size()) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                Fragment fragment = fragments.get(position);
                ft.remove(fragment);
                ft.commitAllowingStateLoss();
            }
        }
    }

    @Override
    public void onTabReselected(int position) {

    }
}