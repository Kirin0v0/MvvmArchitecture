package com.soulkun.mvvm.component;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;
import androidx.navigation.fragment.NavHostFragment;

import com.soulkun.mvvm.MvvmApplication;
import com.soulkun.mvvm.factory.MvvmDataBindingFactory;
import com.soulkun.mvvm.factory.MvvmViewModelFactory;
import com.soulkun.mvvm.navigation.MvvmNavigationUtils;

/**
 * @author soulkun
 * @time 2022/8/22 17:39
 * @description MVVM框架下的Fragment基础类
 */
public abstract class MvvmAbstractFragment extends Fragment {

    protected final String TAG = this.getClass().getSimpleName();

    private ViewDataBinding mDataBinding;

    // 每当手动获取Binding实例时出现该提示，原则上尽量少用
    private TextView mTvStrictModeTip;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final MvvmDataBindingFactory dataBindingFactory = getDataBindingFactory();

        if (dataBindingFactory != null) {
            final ViewDataBinding viewDataBinding = dataBindingFactory.inflate(inflater, container);
            viewDataBinding.setLifecycleOwner(getViewLifecycleOwner());
            mDataBinding = viewDataBinding;

            mDataBinding.executePendingBindings();

            initView(mDataBinding);

            return mDataBinding.getRoot();
        }else {
            return null;
        }
    }

    @Override
    public void onDestroyView() {
        if (mDataBinding != null) {
            mDataBinding.unbind();
            mDataBinding = null;
        }
        super.onDestroyView();
    }

    public NavController findNavController() {
        return NavHostFragment.findNavController(this);
    }

    protected abstract void initViewModel();

    protected void initView(@NonNull final ViewDataBinding viewDataBinding) {
    }

    protected abstract MvvmDataBindingFactory getDataBindingFactory();

    protected ViewDataBinding getDataBinding() {
        if (isDebug() && mDataBinding != null) {
            if (mTvStrictModeTip == null) {
                mTvStrictModeTip = new TextView(MvvmApplication.getInstance());
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
        return MvvmViewModelFactory.getActivityViewModel(requireActivity(), modelClass);
    }

    protected <T extends ViewModel> T getFragmentViewModel(@NonNull Class<T> modelClass) {
        return MvvmViewModelFactory.getFragmentViewModel(this, modelClass);
    }

    protected <T extends ViewModel> T getParentFragmentViewModel(@NonNull Class<T> modelClass) {
        return MvvmViewModelFactory.getFragmentViewModel(requireParentFragment(), modelClass);
    }

    protected <T extends ViewModel> T getGraphViewModel(@NonNull Class<T> modelClass) {
        final NavController navController = findNavController();
        return navController == null ? getActivityViewModel(modelClass) : MvvmViewModelFactory.getGraphViewModel(navController, modelClass);
    }

    protected <T extends ViewModel> T getApplicationViewModel(@NonNull Class<T> modelClass) {
        return MvvmViewModelFactory.getApplicationViewModel(modelClass);
    }

    protected void safeNavigate(@IdRes int resId) {
        MvvmNavigationUtils.navigateSafe(findNavController(), resId);
    }

    protected void safeNavigate(@IdRes int resId, @Nullable Bundle args) {
        MvvmNavigationUtils.navigateSafe(findNavController(), resId, args);
    }

    protected void safeNavigate(@IdRes int resId, @Nullable Bundle args, @Nullable NavOptions navOptions) {
        MvvmNavigationUtils.navigateSafe(findNavController(), resId, args, navOptions);
    }

    protected void safeNavigate(@IdRes int resId, @Nullable Bundle args, @Nullable NavOptions navOptions, @Nullable Navigator.Extras navigatorExtras) {
        MvvmNavigationUtils.navigateSafe(findNavController(), resId, args, navOptions, navigatorExtras);
    }

    private boolean isDebug() {
        return MvvmApplication.isDebug();
    }

}
