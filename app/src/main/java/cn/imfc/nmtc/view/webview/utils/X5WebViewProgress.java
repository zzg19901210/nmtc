package cn.imfc.nmtc.view.webview.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.JsResult;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebSettings.LayoutAlgorithm;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import cn.imfc.nmtc.view.webview.WebViewProgressBar;

public class X5WebViewProgress extends WebView {

	private Handler handler;
	private WebView mWebView;

	private WebViewProgressBar progressBar;//进度条的矩形（进度线）
	private WebViewClient client = new WebViewClient() {
		/**
		 * 防止加载网页时调起系统浏览器
		 */
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}
	};

	@SuppressLint("SetJavaScriptEnabled")
	public X5WebViewProgress(Context arg0, AttributeSet arg1) {
		super(arg0, arg1);
		this.setWebViewClient(client);
		// this.setWebChromeClient(chromeClient);
		// WebStorage webStorage = WebStorage.getInstance();

		//实例化进度条
		progressBar = new WebViewProgressBar(arg0);
		//设置进度条的size
		progressBar.setLayoutParams(new ViewGroup.LayoutParams
				(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		//刚开始时候进度条不可见
		progressBar.setVisibility(GONE);
		//把进度条添加到webView里面
		addView(progressBar);
		//初始化handle
		handler = new Handler();
		initWebViewSettings();

		this.getView().setClickable(true);
	}


	private void initWebViewSettings() {
		WebSettings webSetting = this.getSettings();
		webSetting.setJavaScriptEnabled(true);
		webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
		webSetting.setAllowFileAccess(true);
		webSetting.setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		webSetting.setSupportZoom(true);
		webSetting.setBuiltInZoomControls(true);
		webSetting.setUseWideViewPort(true);
		webSetting.setSupportMultipleWindows(true);
		// webSetting.setLoadWithOverviewMode(true);
		webSetting.setAppCacheEnabled(true);
		// webSetting.setDatabaseEnabled(true);
		webSetting.setDomStorageEnabled(true);
		webSetting.setGeolocationEnabled(true);
		webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
		// webSetting.setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);
		webSetting.setPluginState(WebSettings.PluginState.ON_DEMAND);
		// webSetting.setRenderPriority(WebSettings.RenderPriority.HIGH);
		webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);


		//setWebViewClient(new MyWebClient());
		setWebChromeClient(new MyWebChromeClient());
		// this.getSettingsExtension().setPageCacheCapacity(IX5WebSettings.DEFAULT_CACHE_CAPACITY);//extension
		// settings 的设计
	}

	@Override
	protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
		boolean ret = super.drawChild(canvas, child, drawingTime);
		canvas.save();
		Paint paint = new Paint();
		paint.setColor(0x7fff0000);
		paint.setTextSize(24.f);
		paint.setAntiAlias(true);
		if (getX5WebViewExtension() != null) {
			canvas.drawText(this.getContext().getPackageName() + "-pid:"
					+ android.os.Process.myPid(), 10, 50, paint);
			canvas.drawText(
					"X5  Core:" + QbSdk.getTbsVersion(this.getContext()), 10,
					100, paint);
		} else {
			canvas.drawText(this.getContext().getPackageName() + "-pid:"
					+ android.os.Process.myPid(), 10, 50, paint);
			canvas.drawText("Sys Core", 10, 100, paint);
		}
		canvas.drawText(Build.MANUFACTURER, 10, 150, paint);
		canvas.drawText(Build.MODEL, 10, 200, paint);
		canvas.restore();
		return ret;
	}

	public X5WebViewProgress(Context arg0) {
		super(arg0);
		setBackgroundColor(85621);
	}

	/**
	 * 自定义WebChromeClient
	 */
	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsConfirm(WebView arg0, String arg1, String arg2,
								   JsResult arg3) {
			return super.onJsConfirm(arg0, arg1, arg2, arg3);
		}

		View myVideoView;
		View myNormalView;
		IX5WebChromeClient.CustomViewCallback callback;

		@Override
		public void onProgressChanged(WebView webView, int newProgress) {
			if (newProgress == 100) {
				progressBar.setProgress(100);
				handler.postDelayed(runnable, 200);//0.2秒后隐藏进度条
			} else if (progressBar.getVisibility() == GONE) {
				progressBar.setVisibility(VISIBLE);
			}
			//设置初始进度10，这样会显得效果真一点，总不能从1开始吧
			if (newProgress < 10) {
				newProgress = 10;
			}
			//不断更新进度
			progressBar.setProgress(newProgress);
			super.onProgressChanged(webView, newProgress);
		}


		// /////////////////////////////////////////////////////////
		//
		/**
		 * 全屏播放配置
		 */
		@Override
		public void onShowCustomView(View view,
									 IX5WebChromeClient.CustomViewCallback customViewCallback) {
			//FrameLayout normalView = (FrameLayout) findViewById(R.id.web_filechooser);
			//ViewGroup viewGroup = (ViewGroup) normalView.getParent();
			//viewGroup.removeView(normalView);
			//viewGroup.addView(view);
			//myVideoView = view;
			//myNormalView = normalView;
			//callback = customViewCallback;
		}

		@Override
		public void onHideCustomView() {
			if (callback != null) {
				callback.onCustomViewHidden();
				callback = null;
			}
			if (myVideoView != null) {
				ViewGroup viewGroup = (ViewGroup) myVideoView.getParent();
				viewGroup.removeView(myVideoView);
				viewGroup.addView(myNormalView);
			}
		}

		@Override
		public boolean onJsAlert(WebView arg0, String arg1, String arg2,
								 JsResult arg3) {
			/**
			 * 这里写入你自定义的window alert
			 */
			return super.onJsAlert(null, arg1, arg2, arg3);
		}
	}
	private class MyWebClient extends WebViewClient {
		/**
		 * 加载过程中 拦截加载的地址url
		 *
		 * @param view
		 * @param url  被拦截的url
		 * @return
		 */
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mWebView.loadUrl(url);
			return true;
		}
		/**
		 * 页面加载过程中，加载资源回调的方法
		 *
		 * @param view
		 * @param url
		 */
		@Override
		public void onLoadResource(WebView view, String url) {
			super.onLoadResource(view, url);
		}
		/**
		 * 页面加载完成回调的方法
		 *
		 * @param view
		 * @param url
		 */
		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			// 关闭图片加载阻塞
			view.getSettings().setBlockNetworkImage(false);
		}
		/**
		 * 页面开始加载调用的方法
		 *
		 * @param view
		 * @param url
		 * @param favicon
		 */
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
		}
		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale) {
			super.onScaleChanged(view, oldScale, newScale);
			X5WebViewProgress.this.requestFocus();
			X5WebViewProgress.this.requestFocusFromTouch();
		}
	}
	/**
	 *刷新界面（此处为加载完成后进度消失）
	 */
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			progressBar.setVisibility(View.GONE);
		}
	};
}
