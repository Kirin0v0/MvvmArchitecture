package com.soulkun.mvvm.livedata;

import android.annotation.SuppressLint;

import androidx.annotation.CallSuper;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.internal.SafeIterableMap;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Observer;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author soulkun
 * @date 2022/10/9 11:27
 * @description 允许设置初始值的MediatorLiveData，该类解决了{@link MediatorLiveData}无法第一时间订阅{@link MvvmMutablePerfectLiveData}的问题
 *  无特殊情况一般使用该类代替{@link MediatorLiveData}类
 *  此外，还解决获取上次非本次的值、防抖处理以及防Null值这三个问题！
 */
public class MvvmMutableMediatorLiveData<T> extends MvvmMutablePerfectLiveData<T> {

    @SuppressLint("RestrictedApi")
    private final SafeIterableMap<LiveData<?>, MvvmSource<?>> mSources = new SafeIterableMap<>();

    public MvvmMutableMediatorLiveData() {
    }

    public MvvmMutableMediatorLiveData(final boolean isNullValueAllowed) {
        super(isNullValueAllowed);
    }

    public MvvmMutableMediatorLiveData(final boolean isNullValueAllowed, final boolean isDebouncing) {
        super(isNullValueAllowed, isDebouncing);
    }

    public MvvmMutableMediatorLiveData(final T value) {
        super(value);
    }

    public MvvmMutableMediatorLiveData(final T value, final boolean isNullValueAllowed) {
        super(value, isNullValueAllowed);
    }

    public MvvmMutableMediatorLiveData(final T value, final boolean isNullValueAllowed, final boolean isDebouncing) {
        super(value, isNullValueAllowed, isDebouncing);
    }

    @MainThread
    public <S> void addSource(@NonNull final LiveData<S> source, @NonNull final Observer<? super S> onChanged) {
        MvvmSource<S> e = new MvvmSource<>(source, onChanged);
        @SuppressLint("RestrictedApi") MvvmSource<?> existing = mSources.putIfAbsent(source, e);
        if (existing != null && existing.mObserver != onChanged) {
            throw new IllegalArgumentException(
                    "This source was already added with the different observer");
        }
        if (existing != null) {
            return;
        }
        if (hasActiveObservers()) {
            e.plug();
        }
    }

    @MainThread
    public <S> void removeSource(@NonNull final LiveData<S> toRemote) {
        @SuppressLint("RestrictedApi") MvvmSource<?> source = mSources.remove(toRemote);
        if (source != null) {
            source.unplug();
        }
    }

    @CallSuper
    @Override
    protected void onActive() {
        for (Map.Entry<LiveData<?>, MvvmSource<?>> source : mSources) {
            source.getValue().plug();
        }
    }

    @CallSuper
    @Override
    protected void onInactive() {
        for (Map.Entry<LiveData<?>, MvvmSource<?>> source : mSources) {
            source.getValue().unplug();
        }
    }

    /**
     * @author soulkun
     * @time 2022/10/16 15:53
     * @description Mvvm模块专用的Source源
     */
    private static class MvvmSource<V> implements Observer<V> {

        private static final int START_VERSION = -1;

        final LiveData<V> mLiveData;
        final Observer<? super V> mObserver;
        int mVersion = START_VERSION;

        MvvmSource(LiveData<V> liveData, final Observer<? super V> observer) {
            mLiveData = liveData;
            mObserver = observer;
        }

        void plug() {
            if (mLiveData instanceof MvvmPerfectLiveData) {
                ((MvvmPerfectLiveData<V>) mLiveData).observeStickyForever(this);
            }else {
                mLiveData.observeForever(this);
            }
        }

        void unplug() {
            mLiveData.removeObserver(this);
        }

        @Override
        public void onChanged(@Nullable V v) {
            if (mLiveData instanceof MvvmPerfectLiveData) {
                if (mVersion != ((MvvmPerfectLiveData<V>) mLiveData).getCurrentVersion()) {
                    mVersion = ((MvvmPerfectLiveData<V>) mLiveData).getCurrentVersion();
                    mObserver.onChanged(v);
                }
            }else {
                final Class<LiveData> liveDataClass = LiveData.class;
                try {
                    // 反射获取非MvvmPerfectLiveData外的LiveData的当前版本号，性能较差
                    // 若性能需求较大，因此非MvvmPerfectLiveData父类的其他LiveData类推荐直接使用MediatorLiveData
                    final Method getVersion = liveDataClass.getDeclaredMethod("getVersion");
                    getVersion.setAccessible(true);
                    int version = (int) getVersion.invoke(mLiveData);

                    if (mVersion != version) {
                        mVersion = version;
                        mObserver.onChanged(v);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
