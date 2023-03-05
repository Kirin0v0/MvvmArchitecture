package com.soulkun.mvvm.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

import com.soulkun.mvvm.enums.MvvmLifecycleCouple;

import java.lang.ref.WeakReference;

/**
 * @author soulkun
 * @time 2022/8/22 17:35
 * @description MVVM思想封装的广播接收器，用于接收特定广播，实现了生命周期动态绑定
 * 使用时，创建对象并调用{@link #bind(Lifecycle, MvvmLifecycleCouple)}}方法即可
 */
public abstract class MvvmAbstractBroadcastReceiver extends BroadcastReceiver implements LifecycleEventObserver {

    private final Context mContext;
    private final IntentFilter mIntentFilter;

    // 绑定生命周期，弱引用及时回收
    private WeakReference<Lifecycle> mLifecycleWeakRef;
    private MvvmLifecycleCouple mLifecycleCouple;

    public MvvmAbstractBroadcastReceiver(@NonNull final Context context, @NonNull final IntentFilter intentFilter) {
        mContext = context;
        mIntentFilter = intentFilter;
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

    // 解绑生命周期的同时将广播解除
    public void unbindInAdvance() {
        if (mLifecycleWeakRef != null && mLifecycleWeakRef.get() != null) {
            mLifecycleWeakRef.get().removeObserver(this);
            mContext.unregisterReceiver(this);
            mLifecycleCouple = null;
            mLifecycleWeakRef = null;
        }
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (mLifecycleCouple == null) {
            throw new RuntimeException("非法使用生命周期！");
        }

        switch (mLifecycleCouple) {
            case CREATE_DESTROY: {
                if (event == Lifecycle.Event.ON_CREATE) {
                    mContext.registerReceiver(this, mIntentFilter);
                } else if (event == Lifecycle.Event.ON_DESTROY) {
                    mContext.unregisterReceiver(this);
                }
            }
            break;
            case START_STOP: {
                if (event == Lifecycle.Event.ON_START) {
                    mContext.registerReceiver(this, mIntentFilter);
                } else if (event == Lifecycle.Event.ON_STOP) {
                    mContext.unregisterReceiver(this);
                }
            }
            break;
            case RESUME_PAUSE: {
                if (event == Lifecycle.Event.ON_RESUME) {
                    mContext.registerReceiver(this, mIntentFilter);
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    mContext.unregisterReceiver(this);
                }
            }
            break;
        }
    }

    public Context getContext() {
        return mContext;
    }
}