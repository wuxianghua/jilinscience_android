package cn.palmap.jilinscience.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.palmaplus.pwebview.PWebView;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;
import cn.palmap.jilinscience.base.BaseFragment;
import cn.palmap.jilinscience.model.User;

/**
 * Created by 王天明 on 2017/5/8.
 */

public class HomePageFragment extends BaseFragment {

    @BindView(R.id.webView) PWebView webView;

    Unbinder unbinder;
    private User mUser;
    private boolean isLoad;

    private static final String homeUrl = "http://misc.ipalmap.com/jlstm-app";
//    private static final String homeUrl = "http://10.0.10.192:8080/jlstm/";

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, rootView);
        mUser = App.getInstance().getUser();
        final Intent intent = getActivity().getIntent();
        if (mUser != null) {
            webView.loadURL(homeUrl+"/#/main"+"?"+mUser.getLoginName());
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (!isLoad) {
                        dealWithJPushMessage(mUser.getLoginName(),intent);
                    }
                }
            });
        } else {
            webView.loadURL(homeUrl+"/#/main");
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    if (!isLoad) {
                        dealWithJPushMessage(null,intent);
                    }
                }
            });
        }
    }


    public void onNewIntent(Intent intent) {

        if (mUser != null) {
            dealWithJPushMessage(mUser.getLoginName(),intent);
        } else {
            dealWithJPushMessage(null,intent);
        }
    }

    private void dealWithJPushMessage(String mLoginName,Intent intent) {
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
            webView.loadURL(homeUrl+"/#/intro/"+myValue+"?"+mLoginName);
            isLoad = true;
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

    public void goBack() {
        webView.goBack();
    }
}
