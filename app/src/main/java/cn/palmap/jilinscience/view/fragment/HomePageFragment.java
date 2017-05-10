package cn.palmap.jilinscience.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.palmaplus.pwebview.PWebView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.base.BaseFragment;

/**
 * Created by 王天明 on 2017/5/8.
 */

public class HomePageFragment extends BaseFragment {

    @BindView(R.id.webView) PWebView webView;

    Unbinder unbinder;

    private static final String homeUrl = "http://misc.ipalmap.com/jlstm/#/?";
//    private static final String homeUrl = "http://10.0.10.192:8080/jlstm/";

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        webView.loadURL(homeUrl);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public void goBack() {
        webView.goBack();
    }
}
