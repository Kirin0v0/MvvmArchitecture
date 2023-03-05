package com.soulkun.mvvm.livedata;

import androidx.lifecycle.MutableLiveData;

import java.util.Objects;

/**
 * @author soulkun
 * @date 2022/11/18 17:26
 * @description 继承于MutableLiveData的子LiveData，用于解决获取上次非本次的值、防抖处理以及防Null值这三个问题！
 * 注意，该类一般用于双向绑定，不具有决定是否粘性监听的功能！！！
 */
public class MvvmMutableLiveData<T> extends MutableLiveData<T> {

    // 是否允许传入值为Null，默认不允许即false
    private final boolean mIsNullValueAllowed;
    // 是否允许防抖处理，默认防抖即true
    private final boolean mIsDebouncing;
    // 之前的值，当版本>=1时有效
    private T mLastValue = null;

    public MvvmMutableLiveData(final T value) {
        this(value, false);
    }

    public MvvmMutableLiveData(final T value, final boolean isNullValueAllowed) {
        this(value, isNullValueAllowed, true);
    }

    public MvvmMutableLiveData(final T value, final boolean isNullValueAllowed, final boolean isDebouncing) {
        super(value);
        mIsNullValueAllowed = isNullValueAllowed;
        mIsDebouncing = isDebouncing;
    }

    @Override
    public void setValue(final T value) {
        if ((mIsNullValueAllowed || value != null) && (!mIsDebouncing || !Objects.equals(value, getValue()))) {
            setLastValue();
            super.setValue(value);
        }
    }

    @Override
    public void postValue(final T value) {
        if ((mIsNullValueAllowed || value != null) && (!mIsDebouncing || !Objects.equals(value, getValue()))) {
            setLastValue();
            super.postValue(value);
        }
    }

    private void setLastValue() {
        mLastValue = getValue();
    }

    public T getLastValue() {
        return mLastValue;
    }
}
