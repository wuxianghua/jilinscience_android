package cn.palmap.jilinscience.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import com.palmaplus.pwebview.PWebView;

import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;

/**
 * Created by stone on 2017/5/15.
 */

public class ContactActivity extends AppCompatActivity implements View.OnClickListener{
    private PWebView webView;
    private ImageView mImageView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        App.getInstance().addActivity(this);
        initView();
        initEvent();
        webView.loadURL("http://misc.ipalmap.com/jlstm-wx/#/guide/4");
    }

    private void initEvent() {
        mImageView.setOnClickListener(this);
    }

    private void initView() {
        webView = (PWebView) findViewById(R.id.webview_contact);
        mImageView = (ImageView) findViewById(R.id.imageBack);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageBack:
                finish();
                break;
        }
    }
}
