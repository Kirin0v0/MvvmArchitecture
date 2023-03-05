package com.soulkun.mvvm.livedata;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

/**
 * @author soulkun
 * @date 2022/10/16 17:07
 * @description LiveData转换拓展类，基于{@link androidx.lifecycle.Transformations}类设计以供MVVM模块的封装LiveData使用
 */
public class MvvmLiveDataExtensions {

    /**
     * @author soulkun
     * @time 2022/10/16 17:09
     * @description 监听LiveData A，并根据其返回值类型X转换为类型Y，最终返回一个LiveData B以供使用
     * 详细信息查看{@link androidx.lifecycle.Transformations#map(LiveData, Function)}
     */
    @MainThread
    @NonNull
    public static <X, Y> MvvmPerfectLiveData<Y> map(
            @NonNull LiveData<X> source,
            @NonNull final Function<X, Y> mapFunction) {
        final MvvmMutableMediatorLiveData<Y> result = new MvvmMutableMediatorLiveData<>();
        result.addSource(source, new Observer<X>() {
            @Override
            public void onChanged(@Nullable X x) {
                result.setValue(mapFunction.apply(x));
            }
        });

        return result;
    }

    /**
     * @author soulkun
     * @time 2022/10/16 17:09
     * @description 监听LiveData A，并根据其返回值类型X直接返回一个LiveData B以供使用
     * 详细信息查看{@link androidx.lifecycle.Transformations#switchMap(LiveData, Function)}
     */
    @MainThread
    @NonNull
    public static <X, Y> MvvmPerfectLiveData<Y> switchMap(
            @NonNull LiveData<X> source,
            @NonNull final Function<X, LiveData<Y>> switchMapFunction) {
        final MvvmMutableMediatorLiveData<Y> result = new MvvmMutableMediatorLiveData<>();
        result.addSource(source, new Observer<X>() {
            LiveData<Y> mSource;

            @Override
            public void onChanged(@Nullable X x) {
                LiveData<Y> newLiveData = switchMapFunction.apply(x);
                if (mSource == newLiveData) {
                    return;
                }
                if (mSource != null) {
                    result.removeSource(mSource);
                }
                mSource = newLiveData;
                if (mSource != null) {
                    result.addSource(mSource, new Observer<Y>() {
                        @Override
                        public void onChanged(@Nullable Y y) {
                            result.setValue(y);
                        }
                    });
                }
            }
        });

        return result;
    }

    /**
     * @author soulkun
     * @time 2022/10/16 17:09
     * @description 监听LiveData A变化直到其第一次变化为止，并直接返回一个同类LiveData A的对象以供使用，只监听一次目标LiveData变化！
     * 详细信息查看{@link androidx.lifecycle.Transformations#distinctUntilChanged(LiveData)}
     */
    @MainThread
    @NonNull
    public static <X> MvvmPerfectLiveData<X> distinctUntilChanged(@NonNull LiveData<X> source) {
        final MvvmMutableMediatorLiveData<X> outputLiveData = new MvvmMutableMediatorLiveData<>();
        outputLiveData.addSource(source, new Observer<X>() {
            boolean mFirstTime = true;

            @Override
            public void onChanged(X currentValue) {
                final X previousValue = outputLiveData.getValue();
                if (mFirstTime
                        || (previousValue == null && currentValue != null)
                        || (previousValue != null && !previousValue.equals(currentValue))
                ) {
                    mFirstTime = false;
                    outputLiveData.setValue(currentValue);
                }
            }
        });
        return outputLiveData;
    }

}
