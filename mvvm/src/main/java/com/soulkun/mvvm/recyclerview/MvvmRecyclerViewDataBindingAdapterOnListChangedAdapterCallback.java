package com.soulkun.mvvm.recyclerview;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.Collection;

/**
 * @author soulkun
 * @date 2023/1/7 16:03
 * @description Mvvm实现List数据将要改变时由Adapter托管数据改变逻辑
 * 注意，可能存在内存泄露，需要回调及时置空！
 */
public class MvvmRecyclerViewDataBindingAdapterOnListChangedAdapterCallback<T> extends MvvmRecyclerViewAdapterObservableList.OnListChangedAdapterCallback<T> {

    private final BaseQuickAdapter mAdapter;

    public MvvmRecyclerViewDataBindingAdapterOnListChangedAdapterCallback(final BaseQuickAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean onListChanged(final Collection<? extends T> data) {
        mAdapter.setList(data);
        return true;
    }

    @Override
    public T onItemChanged(final T data, final int position) {
        final T oldValue = (T) mAdapter.getData().get(position);
        mAdapter.setData(position, data);
        return oldValue;
    }

    @Override
    public boolean onItemInserted(final T data, final int position) {
        mAdapter.addData(position, data);
        return true;
    }

    @Override
    public T onItemRemoved(final int position) {
        final T oldValue = (T) mAdapter.getData().get(position);
        mAdapter.removeAt(position);
        return oldValue;
    }

    @Override
    public boolean onItemRangeChanged(final Collection<? extends T> data, final int position) {
        final Object[] objects = data.toArray();
        int count = 0;
        for (int i = position, j = 0; i < mAdapter.getData().size() && j < data.size(); i++, j++) {
            mAdapter.getData().set(i, (T) objects[j]);
            count++;
        }
        mAdapter.notifyItemRangeChanged(position, count);
        return true;
    }

    @Override
    public boolean onItemRangeInserted(final Collection<? extends T> data, final int position) {
        mAdapter.addData(position, data);
        return true;
    }

    @Override
    public boolean onItemRangeRemoved(final int positionStart, final int positionEnd) {
        for (int i = positionStart; i < positionEnd; i++) {
            mAdapter.getData().remove(i);
        }
        mAdapter.notifyItemRangeRemoved(positionStart, positionEnd - positionStart);
        return true;
    }
}
