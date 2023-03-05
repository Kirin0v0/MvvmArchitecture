package com.soulkun.mvvm.recyclerview;

import java.util.Collection;
import java.util.List;

/**
 * @author soulkun
 * @time 2023/1/7 16:45
 * @description 专门用于RecyclerView适配器的可观察数据列表的接口，由{@link MvvmRecyclerViewAdapterObservableArrayList}继承实现
 */
public interface MvvmRecyclerViewAdapterObservableList<T> extends List<T> {

    // 注册回调，交由Adapter
    void registerOnListChangedCallback(OnListChangedAdapterCallback<T> callback);

    // 接触Adapter的数据回调，防止内存泄露
    void unregisterOnListChangedCallback();

    /**
     * @author soulkun
     * @time 2023/1/7 16:46
     * @description 当ArrayList数据将要改变时执行Adapter数据回调
     */
    abstract class OnListChangedAdapterCallback<T> {

        /**
         * @author soulkun
         * @time 2023/1/7 16:48
         * @description 当列表发生整体改变
         */
        public abstract boolean onListChanged(Collection<? extends T> data);

        /**
         * @author soulkun
         * @time 2023/1/7 16:50
         * @description 当某个位置的数据发生改变
         */
        public abstract T onItemChanged(T data, int position);

        /**
         * @author soulkun
         * @time 2023/1/7 16:50
         * @description 当某个位置添加一条新数据
         */
        public abstract boolean onItemInserted(T data, int position);

        /**
         * @author soulkun
         * @time 2023/1/7 16:50
         * @description 当某个位置删除一条旧数据
         */
        public abstract T onItemRemoved(int position);

        /**
         * @author soulkun
         * @time 2023/1/7 16:50
         * @description 当某范围位置的数据发生改变
         */
        public abstract boolean onItemRangeChanged(Collection<? extends T> data, int position);

        /**
         * @author soulkun
         * @time 2023/1/7 16:50
         * @description 当某范围位置添加新数据集合
         */
        public abstract boolean onItemRangeInserted(Collection<? extends T> data, int position);

        /**
         * @author soulkun
         * @time 2023/1/7 16:50
         * @description 当某个位置删除连续旧数据下标，positionEnd为截止位置，不参与删除
         */
        public abstract boolean onItemRangeRemoved(int positionStart, int positionEnd);
        
    }
    
}