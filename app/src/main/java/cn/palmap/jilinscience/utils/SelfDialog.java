package cn.palmap.jilinscience.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import cn.palmap.jilinscience.R;

/**
 * Created by stone on 2017/5/11.
 */

public class SelfDialog extends Dialog {

    private TextView forgetPassword;//忘记密码
    private TextView inputAgain;//重新输入
    private TextView titleTv;//消息标题文本
    private boolean forgetPswIsVisible;
    private String  dialogTitle;

    private OnForgetPswOnclickListener mOnForgetPswclickListener;//忘记密码按钮被点击了的监听器
    private OnInputAgainOnclickListener mOnInputAgainclickListener;//重新输入按钮被点击了的监听器

    /**
     * 设置忘记密码按钮的显示内容和监听
     *
     * @param onForgetPswclickListener
     */
    public void setForgetPswOnclickListener(OnForgetPswOnclickListener onForgetPswclickListener) {
        mOnForgetPswclickListener = onForgetPswclickListener;
    }

    /**
     * 设置重新输入按钮的显示内容和监听
     *
     * @param onInputAgainclickListener
     */
    public void setInputAgainOnclickListener(OnInputAgainOnclickListener onInputAgainclickListener) {
        mOnInputAgainclickListener = onInputAgainclickListener;
    }

    public SelfDialog(Context context, Builder builder) {
        super(context);
        this.forgetPswIsVisible = builder.forgetPswIsVisible;
        this.dialogTitle = builder.dialogTitle;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.self_dialog);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(false);
        //初始化界面控件
        initView();
        //初始化界面控件的事件
        initEvent();

    }

    /**
     * 初始化界面的忘记密码和重新输入监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        inputAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnInputAgainclickListener != null) {
                    mOnInputAgainclickListener.onInputAgainClick();
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnForgetPswclickListener != null) {
                    mOnForgetPswclickListener.onForgetPswClick();
                }
            }
        });
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        inputAgain = (TextView) findViewById(R.id.input_again);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
        titleTv = (TextView) findViewById(R.id.title);
        titleTv.setText(dialogTitle);
        setForgetPswViewVisible(forgetPswIsVisible);
    }

    public void setForgetPswViewVisible(boolean isVisible) {
        if (isVisible) {
            forgetPassword.setVisibility(View.VISIBLE);
        } else {
            forgetPassword.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface OnInputAgainOnclickListener {
        void onInputAgainClick();
    }

    public interface OnForgetPswOnclickListener {
        void onForgetPswClick();
    }

    static class Builder{
        private String dialogTitle;
        private boolean forgetPswIsVisible;
        public Builder title(String dialogTitle){
            this.dialogTitle=dialogTitle;
            return this;
        }
        public Builder forgetPswVisible(boolean forgetPswIsVisible){
            this.forgetPswIsVisible=forgetPswIsVisible;
            return this;
        }
        public SelfDialog build(Context context){
            return new SelfDialog(context,this);
        }
    }
}