package com.soulkun.mvvm.component;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;

import com.soulkun.mvvm.MvvmApplication;
import com.soulkun.mvvm.factory.MvvmDataBindingFactory;
import com.soulkun.mvvm.factory.MvvmViewModelFactory;

/**
 * @author soulkun
 * @time 2022/8/22 17:37
 * @description MVVM框架的Activity基础类
 */
public abstract class MvvmAbstractActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getSimpleName();

    private ViewDataBinding mDataBinding;

    // 每当手动获取DataBinding实例时出现该提示，原则上尽量少用
    private TextView mTvStrictModeTip;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewModel();

        final MvvmDataBindingFactory dataBindingFactory = getDataBindingFactory();

        if (dataBindingFactory != null) {
            final ViewDataBinding viewDataBinding = dataBindingFactory.setContentView(this);
            viewDataBinding.setLifecycleOwner(this);
            mDataBinding = viewDataBinding;

            mDataBinding.executePendingBindings();

            initView(mDataBinding);
        }
    }

    @Override
    protected void onDestroy() {
        if (mDataBinding != null) {
            mDataBinding.unbind();
            mDataBinding = null;
        }
        super.onDestroy();
    }

    protected abstract void initViewModel();

    protected abstract MvvmDataBindingFactory getDataBindingFactory();

    protected void initView(final ViewDataBinding viewDataBinding) {
    }

    protected ViewDataBinding getDataBinding() {
        if (isDebug() && mDataBinding != null) {
            if (mTvStrictModeTip == null) {
                mTvStrictModeTip = new TextView(getApplicationContext());
                mTvStrictModeTip.setAlpha(0.4f);
                mTvStrictModeTip.setGravity(Gravity.CENTER);
                mTvStrictModeTip.setTextSize(14f);
                mTvStrictModeTip.setBackgroundColor(Color.WHITE);
                String tip = "尽量少用getDataBinding，注意Null安全";
                mTvStrictModeTip.setText(tip);

                // 添加提醒布局
                View view = mDataBinding.getRoot();
                while (true) {
                    if (view instanceof ScrollView) {
                        view = ((ScrollView) view).getChildAt(0);
                    }else if (view instanceof ViewGroup){
                        ((ViewGroup) view).addView(mTvStrictModeTip, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        break;
                    }else {
                        break;
                    }
                }
            }
        }
        return mDataBinding;
    }

    protected <T extends ViewModel> T getActivityViewModel(@NonNull Class<T> modelClass) {
        return MvvmViewModelFactory.getActivityViewModel(this, modelClass);
    }

    protected <T extends ViewModel> T getApplicationViewModel(@NonNull Class<T> modelClass) {
        return MvvmViewModelFactory.getApplicationViewModel(modelClass);
    }

    private boolean isDebug() {
        return MvvmApplication.isDebug();
    }

}
