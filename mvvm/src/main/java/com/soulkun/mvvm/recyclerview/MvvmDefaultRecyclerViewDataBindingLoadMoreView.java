package com.soulkun.mvvm.recyclerview;

import androidx.databinding.ObservableField;

import com.soulkun.mvvm.BR;
import com.soulkun.mvvm.R;
import com.soulkun.mvvm.factory.MvvmDataBindingFactory;

/**
 * @author soulkun
 * @date 2022/8/19 14:04
 * @description 默认提供的LoadMoreView，默认RecyclerView的Adapter使用
 */
public class MvvmDefaultRecyclerViewDataBindingLoadMoreView extends MvvmAbstractRecyclerViewLoadMoreDataBindingView {

    @Override
    protected MvvmDataBindingFactory getDataBindingFactory(ObservableField state) {
        return MvvmDataBindingFactory.create(R.layout.mvvm_layout_recycler_view_load_more)
                .addBindingParam(BR.mvvmLayoutRecyclerViewLoadMoreStatus, state)
                .addBindingParam(BR.mvvmLayoutRecyclerViewLoadMoreClickImpl, mLoadMoreClickImpl);
    }

}
