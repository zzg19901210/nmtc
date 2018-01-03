package cn.imfc.nmtc.fragment;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.tencent.sonic.sdk.SonicConfig;
import com.tencent.sonic.sdk.SonicEngine;
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
 * \* Time: 下午3:01
 * \* To change this template use File | Settings | File Templates.
 * \* Description:
 * \
 */
public class HomeFragment extends Fragment {
    public static HomeFragment newInstance(String s){
        HomeFragment homeFragment = new HomeFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Home",s);
        homeFragment.setArguments(bundle);
        return homeFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_content, container, false);
        Bundle bundle = getArguments();
        String s = bundle.getString("Home");
        TextView textView = (TextView) view.findViewById(R.id.fragment_text_view);
        textView.setText(s);
        return view;
    }



}