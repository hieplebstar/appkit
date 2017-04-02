package com.bstar.powerdata.fragments.help;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.bstar.powerdata.R;
import com.bstar.powerdata.base.ButterKnifeEventFragment;

import butterknife.BindView;

public class HelpFragment extends ButterKnifeEventFragment implements HelpView {

    private static final String VOICE_WEBSITE_URL = "http://wechat03.vicosys.com.hk/page/xtzG7201in01";

    @BindView(R.id.webview_help)
    WebView mWebView;

    public static HelpFragment newInstance() {
        return new HelpFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_help, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.loadUrl(VOICE_WEBSITE_URL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
