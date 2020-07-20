package com.airforce.healthchecker.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airforce.healthchecker.R;

public class FragmentMy extends Fragment {

    private WebView mWebView;
    private String myUrl = "file:///android_asset"; // 접속 URL (내장HTML의 경우 왼쪽과 같이 쓰고 아니면 걍 URL)

    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
//
//        super.onCreate(savedInstanceState);
//        getActivity().setContentView(R.layout.activity_main);
        // 웹뷰 셋팅팅
        mWebView = (WebView) view.findViewById(R.id.webview);
        mWebView.getSettings().setJavaScriptEnabled(true);
        //mWebView.loadUrl("http://www.pois.co.kr/mobile/login.do");

        mWebView.loadUrl(myUrl + "/html/index.html"); // 접속 URL
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.setWebViewClient(new WebViewClient());

        return view;
    }

    @Override
    public void onActivityCreated(Bundle saved) {
        super.onActivityCreated(saved);
        setRetainInstance(true);
    }

}
