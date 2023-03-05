package com.soulkun.mvvm.livedata;

/**
 * @author soulkun
 * @time 2022/8/22 17:46
 * @description 完美解决LiveData粘性与非粘性事件的可变LiveData，此外，还支持防Null值以及防抖处理
 *      注意，不允许被Activity等获取到，防止唯一可信源的不可信，同时该类不可用于双向绑定！
 */
public class MvvmMutablePerfectLiveData<T> extends MvvmPerfectLiveData<T> {

    public MvvmMutablePerfectLiveData() {
        super();
    }

    public MvvmMutablePerfectLiveData(T value) {
        super(value);
    }

    public MvvmMutablePerfectLiveData(T value, boolean isNullValueAllowed) {
        super(value, isNullValueAllowed);
    }

    public MvvmMutablePerfectLiveData(T value, boolean isNullValueAllowed, boolean isDebouncing) {
        super(value, isNullValueAllowed, isDebouncing);
    }

    public MvvmMutablePerfectLiveData(boolean isNullValueAllowed) {
        super(isNullValueAllowed);
    }

    public MvvmMutablePerfectLiveData(boolean isNullValueAllowed, boolean isDebouncing) {
        super(isNullValueAllowed, isDebouncing);
    }

    public void setValue(T value) {
        super.setValue(value);
    }

    public void postValue(T value) {
        super.postValue(value);
    }

}
