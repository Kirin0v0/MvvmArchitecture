package com.soulkun.mvvm.factory;

import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModel;

/**
 * @author soulkun
 * @time 2022/8/19 20:39
 * @description MVVM框架下的DataBinding配置工厂类，基本生成DataBinding都要通过该方法
 */
public class MvvmDataBindingFactory {

    private final int mLayoutId;

    private int mViewModelVariableId;

    private ViewModel mStateViewModel;

    private final SparseArray<Object> mBindingParams = new SparseArray<>();

    private MvvmDataBindingFactory(@NonNull Integer layoutId) {
        this.mLayoutId = layoutId;
    }

    private MvvmDataBindingFactory(@NonNull Integer layoutId, @NonNull Integer viewModelVariableId, @NonNull ViewModel stateViewModel) {
        this.mLayoutId = layoutId;
        this.mViewModelVariableId = viewModelVariableId;
        this.mStateViewModel = stateViewModel;
    }

    /**
     * @author soulkun
     * @time 2022/9/29 17:41
     * @description 传入布局ID，创建工厂对象
     */
    public static MvvmDataBindingFactory create(@NonNull Integer layoutId) {
        return new MvvmDataBindingFactory(layoutId);
    }

    /**
     * @author soulkun
     * @time 2022/9/29 17:41
     * @description 传入ID和StateViewModel，创建工厂对象
     */
    public static MvvmDataBindingFactory create(@NonNull Integer layoutId, @NonNull Integer viewModelVariableId, @NonNull ViewModel stateViewModel) {
        return new MvvmDataBindingFactory(layoutId, viewModelVariableId, stateViewModel);
    }

    /**
     * @author soulkun
     * @time 2022/9/29 17:42
     * @description 添加绑定参数
     */
    public MvvmDataBindingFactory addBindingParam(@NonNull Integer variableId, @NonNull Object object) {
        if (mBindingParams.get(variableId) == null) {
            mBindingParams.put(variableId, object);
        }
        return this;
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    public int getViewModelVariableId() {
        return mViewModelVariableId;
    }

    public ViewModel getStateViewModel() {
        return mStateViewModel;
    }

    public SparseArray<Object> getBindingParams() {
        return mBindingParams;
    }

    /**
     * @author soulkun
     * @time 2022/10/18 17:58
     * @description 绑定视图生成ViewDataBinding
     */
    public ViewDataBinding inflate(@NonNull final LayoutInflater layoutInflater, final ViewGroup rootView) {
        final ViewDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), rootView, false);
        // 绑定参数
        if (getViewModelVariableId() != 0 && getStateViewModel() != null) {
            viewDataBinding.setVariable(getViewModelVariableId(), getStateViewModel());
        }
        final SparseArray<Object> bindingParams = getBindingParams();
        for (int i = 0, length = bindingParams.size(); i < length; i++) {
            viewDataBinding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i));
        }
        return viewDataBinding;
    }

    /**
     * @author soulkun
     * @time 2022/10/18 17:58
     * @description 绑定视图生成ViewDataBinding
     */
    public ViewDataBinding inflate(@NonNull final LayoutInflater layoutInflater, final ViewGroup rootView, final boolean attachToParent) {
        final ViewDataBinding viewDataBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), rootView, attachToParent);
        // 绑定参数
        if (getViewModelVariableId() != 0 && getStateViewModel() != null) {
            viewDataBinding.setVariable(getViewModelVariableId(), getStateViewModel());
        }
        final SparseArray<Object> bindingParams = getBindingParams();
        for (int i = 0, length = bindingParams.size(); i < length; i++) {
            viewDataBinding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i));
        }
        return viewDataBinding;
    }

    /**
     * @author soulkun
     * @time 2022/10/19 8:52
     * @description 绑定生成View
     */
    public ViewDataBinding setContentView(@NonNull final Activity activity) {
        final ViewDataBinding viewDataBinding = DataBindingUtil.setContentView(activity, getLayoutId());
        if (getViewModelVariableId() != 0 && getStateViewModel() != null) {
            viewDataBinding.setVariable(getViewModelVariableId(), getStateViewModel());
        }
        final SparseArray<Object> bindingParams = getBindingParams();
        for (int i = 0, length = bindingParams.size(); i < length; i++) {
            viewDataBinding.setVariable(bindingParams.keyAt(i), bindingParams.valueAt(i));
        }
        return viewDataBinding;
    }

}
