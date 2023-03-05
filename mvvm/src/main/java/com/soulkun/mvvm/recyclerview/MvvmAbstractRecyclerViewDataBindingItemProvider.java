package com.soulkun.mvvm.recyclerview;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.ViewDataBinding;

import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.soulkun.mvvm.factory.MvvmDataBindingFactory;

/**
 * @author soulkun
 * @date 2022/8/18 15:56
 * @description {@link MvvmAbstractRecyclerViewDataBindingProviderMultiAdapter}类的专用ItemProvider，提供灵活的Item布局
 */
public abstract class MvvmAbstractRecyclerViewDataBindingItemProvider<Model> extends BaseItemProvider<Model> {

    @Override
    public int getLayoutId() {
        return getItemDataBindingFactory().getLayoutId();
    }

    @Override
    public void convert(BaseViewHolder baseViewHolder, Model model) {
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final MvvmDataBindingFactory itemDataBindingFactory = getItemDataBindingFactory();
        final ViewDataBinding viewDataBinding = itemDataBindingFactory.inflate(LayoutInflater.from(getContext()), parent);
        return new BaseViewHolder(viewDataBinding.getRoot());
    }

    protected abstract MvvmDataBindingFactory getItemDataBindingFactory();

    /* 是否消费绑定事件，返回true会立即刷新视图，否则等待下一帧刷新 */
    protected abstract boolean consumeItemViewHolderBinded(ViewDataBinding viewDataBinding, Model model, int dataPosition);

    protected void consumeItemViewHolderRecycled(ViewDataBinding viewDataBinding){

    }

}
