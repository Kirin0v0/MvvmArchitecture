package com.soulkun.mvvm.component;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleService;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

/**
 * @author soulkun
 * @time 2022/8/22 17:40
 * @description Mvvm框架下的Service基础类
 */
public abstract class MvvmAbstractService extends LifecycleService implements ViewModelStoreOwner {

    protected final String TAG = this.getClass().getSimpleName();

    private ViewModelStore mViewModelStore;

    protected final MvvmAbstractServiceBinder mAbstractServiceBinder;

    {
        final MvvmAbstractServiceBinder newServiceBinder = getNewServiceBinder();
        if (newServiceBinder != null) {
            mAbstractServiceBinder = newServiceBinder;
        } else {
            mAbstractServiceBinder = new MvvmDefaultServiceBinder();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mViewModelStore = new ViewModelStore();
    }

    @Nullable
    @Override
    public IBinder onBind(@NonNull Intent intent) {
        super.onBind(intent);
        return mAbstractServiceBinder.bind();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        mAbstractServiceBinder.unbind();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        mViewModelStore.clear();
        super.onDestroy();
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mViewModelStore;
    }

    /**
     * @author soulkun
     * @time 2022/8/22 17:40
     * @description 管理Binder通信类的创建与清除
     */
    protected abstract MvvmAbstractServiceBinder getNewServiceBinder();

    public static void startService(Context context, Class<? extends MvvmAbstractService> clazz, Bundle bundle) {
        final Intent intent = new Intent(context, clazz);
        if (bundle != null && !bundle.isEmpty()) {
            intent.putExtras(bundle);
        }
        context.startService(intent);
    }

    public static void bindService(Context context, Class<? extends MvvmAbstractService> clazz, ServiceConnection serviceConnection, Bundle bundle, int flags) {
        final Intent intent = new Intent(context, clazz);
        if (bundle != null && !bundle.isEmpty()) {
            intent.putExtras(bundle);
        }
        context.bindService(intent, serviceConnection, flags);
    }

    public static void stopService(Context context, Class<? extends MvvmAbstractService> clazz) {
        final Intent intent = new Intent(context, clazz);
        context.stopService(intent);
    }

    /**
     * @author soulkun
     * @time 2022/8/22 17:41
     * @description 默认实现类
     */
    public static class MvvmDefaultServiceBinder extends MvvmAbstractServiceBinder{
        @Override
        public void unbind() {
        }
    }

}
