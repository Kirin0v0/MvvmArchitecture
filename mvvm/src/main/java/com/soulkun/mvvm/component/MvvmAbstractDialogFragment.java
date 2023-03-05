package com.soulkun.mvvm.component;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.soulkun.mvvm.MvvmApplication;
import com.soulkun.mvvm.factory.MvvmDataBindingFactory;
import com.soulkun.mvvm.factory.MvvmViewModelFactory;

import java.lang.ref.WeakReference;

/**
 * @author soulkun
 * @time 2022/8/22 17:39
 * @description MVVM框架下的DialogFragment基础类
 */
public abstract class MvvmAbstractDialogFragment extends DialogFragment {

    protected final String TAG = this.getClass().getSimpleName();

    private ViewDataBinding mDataBinding;

    // 每当手动获取Binding实例时出现该提示，原则上尽量少用
    private TextView mTvStrictModeTip;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initViewModel();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable final Bundle savedInstanceState) {
        // 生成内存不泄露的Dialog
        return new NoLeakDialog(requireContext(), getTheme());
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initWindow(getDialog().getWindow());
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

    protected abstract MvvmDataBindingFactory getDataBindingFactory();

    protected void initView(final ViewDataBinding viewDataBinding) {
    }

    protected abstract void initWindow(final Window window);

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

    private boolean isDebug() {
        return MvvmApplication.isDebug();
    }

    /**
     * @author soulkun
     * @date 2022/10/14 9:59
     * @description 解决Dialog在DialogFragment中自带的内存泄露问题
     */
    public static class NoLeakDialog extends Dialog {

        public NoLeakDialog(@NonNull final Context context) {
            super(context);
        }

        public NoLeakDialog(@NonNull final Context context, final int themeResId) {
            super(context, themeResId);
        }

        public NoLeakDialog(@NonNull final Context context, final boolean cancelable, @Nullable final DialogInterface.OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        public void setOnCancelListener(@Nullable final DialogInterface.OnCancelListener listener) {
            super.setOnCancelListener(new OnCancelListener(listener));
        }

        @Override
        public void setOnDismissListener(@Nullable final DialogInterface.OnDismissListener listener) {
            super.setOnDismissListener(new OnDismissListener(listener));
        }

        public static class OnCancelListener implements DialogInterface.OnCancelListener {

            private WeakReference<DialogInterface.OnCancelListener> mCancelListenerWeakReference;

            public OnCancelListener(final DialogInterface.OnCancelListener cancelListener) {
                mCancelListenerWeakReference = new WeakReference<>(cancelListener);
            }

            @Override
            public void onCancel(final DialogInterface dialog) {
                if (mCancelListenerWeakReference.get() != null) {
                    mCancelListenerWeakReference.get().onCancel(dialog);
                }
            }
        }

        public static class OnDismissListener implements DialogInterface.OnDismissListener {

            private WeakReference<DialogInterface.OnDismissListener> mDismissListenerWeakReference;

            public OnDismissListener(final DialogInterface.OnDismissListener dismissListener) {
                mDismissListenerWeakReference = new WeakReference<>(dismissListener);
            }

            @Override
            public void onDismiss(final DialogInterface dialog) {
                if (mDismissListenerWeakReference.get() != null) {
                    mDismissListenerWeakReference.get().onDismiss(dialog);
                }
            }

        }

    }

}
