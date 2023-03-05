package com.soulkun.mvvm.recyclerview;

import androidx.databinding.ViewDataBinding;

import com.soulkun.mvvm.factory.MvvmDataBindingFactory;

/**
 * @author soulkun
 * @date 2022/8/18 16:27
 * @description RecyclerView头、尾、空布局接口类
 */
public interface IMvvmRecyclerViewDataBindingBasicView {

    public enum ViewType {
        HEADER, FOOTER, EMPTY
    }

    void setBasicViewDataBindingFactory(ViewType viewType, MvvmDataBindingFactory dataBindingFactory);

    boolean consumeBasicViewHolderBinded(ViewType viewType, ViewDataBinding viewDataBinding);

    void consumeBasicViewHolderRecycled(ViewType viewType, ViewDataBinding viewDataBinding);

}
