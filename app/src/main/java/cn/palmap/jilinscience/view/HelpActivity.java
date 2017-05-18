package cn.palmap.jilinscience.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import cn.palmap.jilinscience.App;
import cn.palmap.jilinscience.R;

/**
 * Created by stone on 2017/5/15.
 */

public class HelpActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        App.getInstance().addActivity(this);
    }
}
