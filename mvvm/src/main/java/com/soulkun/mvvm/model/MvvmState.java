package com.soulkun.mvvm.model;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;

/**
 * @author soulkun
 * @time 2022/8/22 17:50
 * @description MVVM框架下的State状态数据类
 * 用于State状态的更新，只能在StateViewModel中使用
 */
public class MvvmState<T> extends ObservableField<T> {

    // 是否防止抖动，默认防止抖动，即仅当数据值改变才能通知观察者
    private final boolean debouncing;

    /**
     * 必须提供初值，规避Null安全问题
     * @param value
     */
    public MvvmState(@NonNull T value) {
        this(value, true);
    }

    public MvvmState(@NonNull T value, boolean debouncing) {
        super(value);
        this.debouncing = debouncing;
    }

    @Override
    public void set(@NonNull T value) {
        boolean isChanged = get() != value;
        // 当值改变时提醒观察者
        super.set(value);
        // 允许抖动，即未改变也提醒观察者
        if (!isChanged && !debouncing) {
            notifyChange();
        }
    }

}
