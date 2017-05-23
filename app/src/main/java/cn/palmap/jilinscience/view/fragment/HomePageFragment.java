package cn.palmap.jilinscience.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.palmaplus.pwebview.PWebView;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.base.BaseFragment;
import cn.palmap.jilinscience.utils.DialogUtils;

/**
 * Created by 王天明 on 2017/5/8.
 */

public class HomePageFragment extends BaseFragment {

    @BindView(R.id.webView) PWebView webView;

    Unbinder unbinder;

    private static final String homeUrl = "http://misc.ipalmap.com/jlstm-app/";
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
        dealWithJPushMessage();
    }

    private void dealWithJPushMessage() {
        Intent intent = getActivity().getIntent();
        String action = intent.getAction();
        Bundle bundle = intent.getExtras();
        if ("open_message".equals(action)) {
            String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
            String myValue = "";
            try {
                JSONObject extrasJson = new JSONObject(extras);
                myValue = extrasJson.optString("message");
            } catch (Exception e) {
                return;
            }
            webView.loadURL(myValue);
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public boolean canGoBack() {
        return webView.canGoBack();
    }

    public boolean isHomePage() {
        if (homeUrl.equals(webView.getUrl())){
            return false;
        } else {
            webView.loadURL(homeUrl);
            return true;
        }

    }

    public void goBack() {
        webView.goBack();
    }
}
