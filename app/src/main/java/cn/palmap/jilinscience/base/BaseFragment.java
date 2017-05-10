package cn.palmap.jilinscience.base;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 王天明 on 2016/8/18.
 */
public abstract class BaseFragment extends Fragment {

    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected  View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        return inflaterView(inflater,container,savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rootView = view;
        initView();
        initData();
        dataBind();
    }

    /**
     * 初始化视图,在初始化数据之后
     */
    public void initView(){}
    /**
     * 初始化数据
     */
    public void initData(){}

    public void dataBind(){}

    public View getRootView() {
        return rootView;
    }

    public <V extends View> V findView(int viewID){
        return (V) rootView.findViewById(viewID);
    }
}