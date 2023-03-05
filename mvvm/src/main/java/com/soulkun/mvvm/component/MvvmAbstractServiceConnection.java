package com.soulkun.mvvm.component;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.soulkun.mvvm.enums.MvvmLifecycleCouple;

import java.lang.ref.WeakReference;

/**
 * @author soulkun
 * @date 2022/8/19 17:21
 * @description MVVM框架下的ServiceConnection抽象类，与生命周期绑定，自动绑定解绑，并执行相应逻辑
 * 使用时，创建对象并调用{@link #bind(Lifecycle, MvvmLifecycleCouple)}方法即可
 */
public abstract class MvvmAbstractServiceConnection<S extends MvvmAbstractService, B extends Binder> implements LifecycleEventObserver, ServiceConnection {

    private final Context mContext;
    private final Class<S> mServiceClass;
    private final Bundle mBundle;

    private WeakReference<Lifecycle> mLifecycleWeakRef;
    private MvvmLifecycleCouple mLifecycleCouple;

    public MvvmAbstractServiceConnection(Context context, Class<S> serviceClass, Bundle bundle) {
        this.mContext = context;
        this.mServiceClass = serviceClass;
        this.mBundle = bundle;
    }

    // 绑定生命周期
    public void bind(@NonNull Lifecycle lifecycle, @NonNull MvvmLifecycleCouple lifecycleCouple) {
        if (mLifecycleWeakRef != null) {
            final Lifecycle bindLifecycle = mLifecycleWeakRef.get();
            // 判断绑定的生命周期已经释放或销毁允许重新绑定
            if (bindLifecycle == null || bindLifecycle.getCurrentState() == Lifecycle.State.DESTROYED) {
                // 重新绑定生命周期
                mLifecycleWeakRef = new WeakReference<>(lifecycle);
                mLifecycleCouple = lifecycleCouple;
                lifecycle.addObserver(this);
                return;
            }
            throw new RuntimeException("先前绑定的生命周期尚未销毁，无法再次绑定！");
        } else {
            // 重新绑定生命周期
            mLifecycleWeakRef = new WeakReference<>(lifecycle);
            mLifecycleCouple = lifecycleCouple;
            lifecycle.addObserver(this);
        }
    }

    // 解除生命周期，并不走解除服务连接的流程
    public void unbindInAdvance() {
        if (mLifecycleWeakRef != null && mLifecycleWeakRef.get() != null) {
            mLifecycleWeakRef.get().removeObserver(this);
            doOnServiceUnbind();
            mContext.unbindService(this);
            mLifecycleCouple = null;
            mLifecycleWeakRef = null;
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        doOnServiceBind((B) service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        doOnServiceUnbind();
    }

    @Override
    public void onBindingDied(ComponentName name) {
        doOnServiceUnbind();
    }

    @Override
    public void onNullBinding(ComponentName name) {
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (mLifecycleCouple == null) {
            throw new RuntimeException("非法使用生命周期！");
        }

        switch (mLifecycleCouple) {
            case CREATE_DESTROY: {
                if (event == Lifecycle.Event.ON_CREATE) {
                    S.bindService(mContext, mServiceClass, this, mBundle, Context.BIND_AUTO_CREATE);
                } else if (event == Lifecycle.Event.ON_DESTROY) {
                    doOnServiceUnbind();
                    mContext.unbindService(this);
                }
            }
            break;
            case START_STOP: {
                if (event == Lifecycle.Event.ON_START) {
                    S.bindService(mContext, mServiceClass, this, mBundle, Context.BIND_AUTO_CREATE);
                } else if (event == Lifecycle.Event.ON_STOP) {
                    doOnServiceUnbind();
                    mContext.unbindService(this);
                }
            }
            break;
            case RESUME_PAUSE: {
                if (event == Lifecycle.Event.ON_RESUME) {
                    S.bindService(mContext, mServiceClass, this, mBundle, Context.BIND_AUTO_CREATE);
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    doOnServiceUnbind();
                    mContext.unbindService(this);
                }
            }
            break;
        }
    }

    /**
     * @author soulkun
     * @time 2022/8/22 17:42
     * @description 在Service绑定成功时
     */
    public abstract void doOnServiceBind(B binder);

    /**
     * @author soulkun
     * @time 2022/8/22 17:42
     * @description 在Service解除绑定时
     */
    public abstract void doOnServiceUnbind();

}
