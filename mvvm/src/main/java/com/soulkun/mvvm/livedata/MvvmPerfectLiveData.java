package com.soulkun.mvvm.livedata;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author soulkun
 * @time 2022/8/22 17:46
 * @description 完美解决LiveData的粘性和非粘性事件，允许可控观察，此外，还支持防Null值以及防抖处理
 */
public class MvvmPerfectLiveData<T> extends LiveData<T> {

    public static final int START_VERSION = -1;

    private final AtomicInteger mCurrentVersion = new AtomicInteger(START_VERSION);

    // 是否允许传入值为Null，默认不允许即false
    private final boolean mIsNullValueAllowed;
    // 是否允许防抖处理，默认防抖即true
    private final boolean mIsDebouncing;
    // 之前的值，当版本>=1时有效
    private T mLastValue = null;

    public MvvmPerfectLiveData() {
        this(false, true);
    }

    public MvvmPerfectLiveData(boolean isNullValueAllowed) {
        this(isNullValueAllowed, true);
    }

    public MvvmPerfectLiveData(boolean isNullValueAllowed, boolean isDebouncing) {
        super();
        this.mIsNullValueAllowed = isNullValueAllowed;
        this.mIsDebouncing = isDebouncing;
    }

    public MvvmPerfectLiveData(T value) {
        this(value, false);
    }

    public MvvmPerfectLiveData(T value, boolean isNullValueAllowed) {
        this(value, isNullValueAllowed, true);
    }

    public MvvmPerfectLiveData(T value, boolean isNullValueAllowed, boolean isDebouncing) {
        super(value);
        this.mIsNullValueAllowed = isNullValueAllowed;
        this.mIsDebouncing = isDebouncing;
        mCurrentVersion.getAndIncrement();
    }

    /**
     * @author soulkun
     * @time 2022/10/16 15:54
     * @description 观察生命周期敏感的非粘性事件，使用时注意，只能观察到方法调用后更新的值！
     */
    @Override
    public void observe(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, wrapObserver(observer, mCurrentVersion.get()));
    }

    /**
     * @author soulkun
     * @time 2022/10/16 15:54
     * @description 观察生命周期不敏感的非粘性事件，使用时注意，只能观察到方法调用后更新的值！
     */
    @Override
    public void observeForever(@NonNull Observer<? super T> observer) {
        super.observeForever(wrapObserver(observer, mCurrentVersion.get()));
    }

    /**
     * @author soulkun
     * @time 2022/10/16 15:54
     * @description 观察生命周期不敏感的粘性事件，使用时注意，能观察到方法调用前更新的值和调用后更新的值！
     */
    public void observeSticky(@NonNull LifecycleOwner owner, @NonNull Observer<? super T> observer) {
        super.observe(owner, wrapObserver(observer, START_VERSION));
    }

    /**
     * @author soulkun
     * @time 2022/10/16 15:54
     * @description 观察生命周期不敏感的粘性事件，使用时注意，能观察到方法调用前更新的值和调用后更新的值！
     */
    public void observeStickyForever(@NonNull Observer<? super T> observer) {
        super.observeForever(wrapObserver(observer, START_VERSION));
    }

    /**
     * @author soulkun
     * @time 2022/10/16 15:56
     * @description 手动删除观察者，一般用于生命周期不敏感
     */
    public void removeObserver(@NonNull Observer<? super T> observer) {
        if (observer.getClass().isAssignableFrom(ObserverWrapper.class)) {
            super.removeObserver(observer);
        } else {
            super.removeObserver(wrapObserver(observer, START_VERSION));
        }
    }

    /**
     * @author soulkun
     * @time 2022/10/12 19:11
     * @description 手动将数据清空，避免无用数据长时间存储在ViewModel导致内存溢出
     *    忽略是否允许为空值，且不会被监听观察者回调
     */
    public void clear() {
        super.setValue(null);
    }

    // 返回最近一次非本次的数值
    public T getLastValue() {
        return mLastValue;
    }

    // 返回当前版本号
    public int getCurrentVersion() {
        return mCurrentVersion.get();
    }

    @Override
    protected void setValue(T value) {
        if ((mIsNullValueAllowed || value != null) && (!mIsDebouncing || !Objects.equals(value, getValue()))) {
            mCurrentVersion.getAndIncrement();
            setLastValue();
            super.setValue(value);
        }
    }

    @Override
    protected void postValue(T value) {
        if ((mIsNullValueAllowed || value != null) && (!mIsDebouncing || !Objects.equals(value, getValue()))) {
            mCurrentVersion.getAndIncrement();
            setLastValue();
            super.postValue(value);
        }
    }

    // 设置最近一次非本次数值
    private void setLastValue() {
        if (mCurrentVersion.get() >= 1) {
            mLastValue = getValue();
        }
    }

    private class ObserverWrapper implements Observer<T> {

        private final Observer<? super T> mObserver;

        private int mVersion;

        public ObserverWrapper(Observer<? super T> mObserver, int mVersion) {
            this.mObserver = mObserver;
            this.mVersion = mVersion;
        }

        @Override
        public void onChanged(T t) {
            if (mCurrentVersion.get() > mVersion) {
                mObserver.onChanged(t);
                mVersion = mCurrentVersion.get();
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ObserverWrapper that = (ObserverWrapper) o;
            return Objects.equals(mObserver, that.mObserver);
        }

        @Override
        public int hashCode() {
            return Objects.hash(mObserver);
        }
    }

    private ObserverWrapper wrapObserver(@NonNull Observer<? super T> observer, int version) {
        return new ObserverWrapper(observer, version);
    }

}
