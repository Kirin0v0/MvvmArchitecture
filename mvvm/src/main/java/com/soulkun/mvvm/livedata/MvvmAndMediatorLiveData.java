package com.soulkun.mvvm.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author soulkun
 * @date 2022/10/8 17:31
 * @description 监听多观察者的变化，当且仅当全部的观察者最近一次变化满足条件返回true时才会赋值True并返回以供监听
 * 该LiveData用法像与门，只有全部1都会1
 * 使用时，必须addSource传入AndObserver！
 */
public class MvvmAndMediatorLiveData extends MvvmMutableMediatorLiveData<Boolean> {

    private final Map<Integer, Boolean> mLastChangedResultMap = new HashMap<>();

    public MvvmAndMediatorLiveData() {
    }

    public MvvmAndMediatorLiveData(final Boolean value) {
        super(value);
    }

    public MvvmAndMediatorLiveData(final Boolean value, final boolean isNullValueAllowed) {
        super(value, isNullValueAllowed);
    }

    public MvvmAndMediatorLiveData(final Boolean value, final boolean isNullValueAllowed, final boolean isDebouncing) {
        super(value, isNullValueAllowed, isDebouncing);
    }

    public abstract class MvvmAndObserver<T> implements Observer<T> {

        private int hashCode;

        @Override
        public void onChanged(final T t) {
                setNewValue(hashCode, onValueChanged(t));
        }

        public abstract boolean onValueChanged(T t);

        public void setHashCode(final int hashCode) {
            this.hashCode = hashCode;
        }
    }

    /**
     * @author soulkun
     * @time 2022/10/8 17:56
     * @description 增加监听数据源
     */
    public <S> void addSource(@NonNull final LiveData<S> source, @NonNull final MvvmAndObserver<? super S> onChanged) {
        super.addSource(source, onChanged);
        mLastChangedResultMap.put(source.hashCode(), false);
        onChanged.setHashCode(source.hashCode());
    }

    /**
     * @author soulkun
     * @time 2022/10/8 19:42
     * @description 去除监听数据源
     */
    @Override
    public <S> void removeSource(@NonNull final LiveData<S> toRemote) {
        mLastChangedResultMap.remove(toRemote.hashCode());
        super.removeSource(toRemote);
    }

    private void setNewValue(int hashCode, boolean result) {
//        // 消除抖动
//        if (mLastChangedResultMap.get(hashCode) == result) {
//            return;
//        }

        // 重新赋值
        mLastChangedResultMap.put(hashCode, result);

        // 判断与门
        for (Boolean value : mLastChangedResultMap.values()) {
            if (!value) {
                setValue(false);
                return;
            }
        }

        // 最终为True
        setValue(true);
    }

}
