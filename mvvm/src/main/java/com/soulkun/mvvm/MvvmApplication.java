package com.soulkun.mvvm;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * @author soulkun
 * @time 2023/3/5 13:58
 * @description Mvvm组件库的Application，使用时必须调用{@link MvvmApplication#init(Application)}方法
 */
public class MvvmApplication  {

    private static Application sInstance;
    private static boolean sDebug = true;
    private static ViewModelStoreOwner sViewModelStoreOwner;

    public static void init(Application application) {
        sInstance = application;
        sDebug = sInstance.getApplicationInfo() != null &&
                (sInstance.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        sViewModelStoreOwner = new ViewModelStoreOwner() {
            private ViewModelStore mApplicationViewModelStore;

            @NonNull
            @Override
            public ViewModelStore getViewModelStore() {
                if (mApplicationViewModelStore == null) {
                    mApplicationViewModelStore = new ViewModelStore();
                }
                return mApplicationViewModelStore;
            }
        };
    }

    public static void setDebug(boolean debug) {
        sDebug = debug;
    }

    public static Application getInstance() {
        if(sInstance == null) {
            throw new RuntimeException("请设置Application后再执行该方法");
        }
        return sInstance;
    }

    public static boolean isDebug() {
        return sDebug;
    }

    public static ViewModelStoreOwner getViewModelStoreOwner() {
        return sViewModelStoreOwner;
    }
}
