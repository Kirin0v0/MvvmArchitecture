package com.soulkun.mvvm.recyclerview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * @author soulkun
 * @date 2023/1/7 16:07
 * @description 专门用于Mvvm框架下RecyclerView适配器的可观察数据列表，在注册Adapter回调后会屏蔽了父类所有方法对集合的修改，交由Adapter自身修改！
 * 该类直接用于Adapter的数据修改，不需要额外对Adapter进行操作，防止Adapter在容器中忘记回收导致的内存泄露！
 * <p>
 * 目前支持 {@link com.soulkun.mvvm.recyclerview.MvvmAbstractRecyclerViewDataBindingBasicAdapter}
 * {@link com.soulkun.mvvm.recyclerview.MvvmAbstractRecyclerViewDataBindingProviderMultiAdapter}
 */
public class MvvmRecyclerViewAdapterObservableArrayList<T> extends ArrayList<T> implements MvvmRecyclerViewAdapterObservableList<T> {

    private transient OnListChangedAdapterCallback<T> mListener;

    public MvvmRecyclerViewAdapterObservableArrayList() {
    }

    public MvvmRecyclerViewAdapterObservableArrayList(@NonNull final Collection<? extends T> c) {
        addAll(c);
    }

    @Override
    public void registerOnListChangedCallback(OnListChangedAdapterCallback listener) {
        mListener = listener;
    }

    @Override
    public void unregisterOnListChangedCallback() {
        mListener = null;
    }

    public T observableSet(final int index, final T element) {
        if (index > size() || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));

        if (mListener != null) {
            return mListener.onItemChanged(element, index);
        } else {
            return super.set(index, element);
        }
    }

    public boolean observableAdd(final T t) {
        if (mListener != null) {
            return mListener.onItemInserted(t, size());
        } else {
            return super.add(t);
        }
    }

    public void observableAdd(final int index, final T element) {
        if (index > size() || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));

        if (mListener != null) {
            mListener.onItemInserted(element, index);
        } else {
            super.add(index, element);
        }
    }

    public T observableRemove(final int index) {
        if (index > size() || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));

        if (mListener != null) {
            return mListener.onItemRemoved(index);
        } else {
            return super.remove(index);
        }
    }

    public boolean observableRemove(@Nullable final Object o) {
        if (mListener != null) {
            final int index = indexOf(o);
            if (index != -1) {
                return mListener.onItemRemoved(index) != null;
            } else {
                return false;
            }
        } else {
            return super.remove(o);
        }
    }

    public void observableClear() {
        if (mListener != null) {
            mListener.onListChanged(Collections.EMPTY_LIST);
        } else {
            super.clear();
        }
    }

    public void observableSetAll(@NonNull final Collection<? extends T> c) {
        if (mListener != null) {
            mListener.onListChanged(c);
        } else {
            super.clear();
            super.addAll(c);
        }
    }

    public void observableSetRange(final int index, @NonNull final Collection<? extends T> c) {
        if (index > size() || index < 0)
            throw new IndexOutOfBoundsException(outOfBoundsMsg(index));

        if (mListener != null) {
            mListener.onItemRangeChanged(c, index);
        } else {
            final Object[] objects = c.toArray();
            for (int i = index, j = 0; i < size() && j < c.size(); i++, j++) {
                set(i, (T) objects[j]);
            }
        }
    }

    public boolean observableAddAll(@NonNull final Collection<? extends T> c) {
        if (mListener != null) {
            return mListener.onItemRangeInserted(c, size());
        } else {
            return super.addAll(c);
        }
    }

    public boolean observableAddAll(final int index, @NonNull final Collection<? extends T> c) {
        if (mListener != null) {
            return mListener.onItemRangeInserted(c, index);
        } else {
            return super.addAll(index, c);
        }
    }

    public void observableRemoveRange(final int fromIndex, final int toIndex) {
        if (mListener != null) {
            mListener.onItemRangeRemoved(fromIndex, toIndex);
        } else {
            super.removeRange(fromIndex, toIndex);
        }
    }

    private String outOfBoundsMsg(int index) {
        return "Index: "+index+", Size: "+size();
    }

}

