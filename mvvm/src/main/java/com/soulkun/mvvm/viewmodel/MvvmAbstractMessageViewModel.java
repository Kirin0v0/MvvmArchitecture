package com.soulkun.mvvm.viewmodel;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.soulkun.mvvm.livedata.MvvmMutablePerfectLiveData;
import com.soulkun.mvvm.livedata.MvvmPerfectLiveData;
import com.soulkun.mvvm.model.MvvmMessage;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author soulkun
 * @date 2022/11/7 16:04
 * @description MVVM框架实现消息总线，接收粘性事件则推送最近的一条消息，否则只推送新消息
 * 接收消息使用{@link #observe}等方法，发送消息使用{@link #send}和{@link #post}方法
 */
public abstract class MvvmAbstractMessageViewModel<M extends MvvmMessage> extends ViewModel implements LifecycleEventObserver {

    private final static int DEFAULT_MESSAGE_QUEUE_LENGTH = 16;
    private final static long DEFAULT_MESSAGE_CLEAR_TIME = 5 * 60 * 1000L;

    private final Map<Integer, MessageObserver> mIdToObserverMap = new ConcurrentHashMap<>();

    private final MvvmFixedLengthList<MvvmMutablePerfectLiveData<M>> mMessageQueue = new MvvmFixedLengthList<>(initMessageQueueLength(), new IFixedLengthListCallback<MvvmMutablePerfectLiveData<M>>() {
        @Override
        public void onRemoveFirst(MvvmMutablePerfectLiveData<M> liveData) {
            // 元素删除时
            mClearMessageHandler.removeMessages(liveData.hashCode());
            mIdToObserverMap.values().forEach(messageObserver -> liveData.removeObserver(messageObserver));
        }
    });

    // 清空LiveData消息的Handler
    private final MessageClearHandler mClearMessageHandler = new MessageClearHandler(Looper.getMainLooper());

    @Override
    public void onStateChanged(final @NonNull LifecycleOwner source, final @NonNull Lifecycle.Event event) {
        if (event == Lifecycle.Event.ON_DESTROY) {
            // 解绑LifeCycle
            unBind(source);
        }
    }

    /**
     * @author soulkun
     * @time 2022/11/7 16:51
     * @description 观察生命周期敏感的非粘性事件，注意，必须在主线程上调用
     */
    public void observe(final @NonNull LifecycleOwner lifecycleOwner, final @NonNull Observer<M> observer) {
        lifecycleOwner.getLifecycle().addObserver(this);
        new MessageObserver(false, observer).bind(lifecycleOwner);
    }

    /**
     * @author soulkun
     * @time 2022/11/7 16:51
     * @description 观察生命周期敏感的粘性事件，注意，必须在主线程上调用
     */
    public void observeSticky(final @NonNull LifecycleOwner lifecycleOwner, final @NonNull Observer<M> observer) {
        lifecycleOwner.getLifecycle().addObserver(this);
        new MessageObserver(true, observer).bind(lifecycleOwner);
    }

    /**
     * @author soulkun
     * @time 2022/11/7 16:51
     * @description 永久观察非粘性事件，需要手动解除，否则造成内存泄漏，注意，必须在主线程上调用
     */
    public void observeForever(final @NonNull Observer<M> observer) {
        new MessageObserver(false, observer).bind(null);
    }

    /**
     * @author soulkun
     * @time 2022/11/7 16:51
     * @description 永久观察粘性事件，需要手动解除，否则造成内存泄漏，注意，必须在主线程上调用
     */
    public void observeStickyForever(final @NonNull Observer<M> observer) {
        new MessageObserver(true, observer).bind(null);
    }

    /**
     * @author soulkun
     * @time 2022/11/7 16:54
     * @description 解除观察，注意，必须在主线程上调用
     */
    public void unObserve(final @NonNull Observer<M> observer) {
        final MessageObserver messageObserver = mIdToObserverMap.get(observer.hashCode());
        if (messageObserver != null) {
            messageObserver.unObserve();
        }
    }

    /**
     * @author soulkun
     * @time 2022/11/7 16:56
     * @description 解除生命周期的观察，注意，必须在主线程上调用
     */
    public void unBind(final @NonNull LifecycleOwner lifecycleOwner) {
        mMessageQueue.forEach(liveData -> liveData.removeObservers(lifecycleOwner));
        final Iterator<Map.Entry<Integer, MessageObserver>> iterator = mIdToObserverMap.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<Integer, MessageObserver> entry = iterator.next();
            if (entry.getValue().mLifecycleOwner != null && entry.getValue().mLifecycleOwner == lifecycleOwner) {
                iterator.remove();
            }
        }
    }

    /**
     * @author soulkun
     * @time 2022/11/7 17:02
     * @description 主线程发送消息
     */
    public boolean send(final @NonNull M message) {
        if (authenticateMessage(message)) {
            // 观察
            final MvvmMutablePerfectLiveData<M> liveData = new MvvmMutablePerfectLiveData<>(message, false);
            mIdToObserverMap.values().forEach(messageObserver -> messageObserver.handleMessage(liveData));
            mMessageQueue.add(liveData);

            // 发送清空消息
            mClearMessageHandler.sendMessage(liveData);

            return true;
        }else {
            return false;
        }
    }

    /**
     * @author soulkun
     * @time 2022/11/7 17:02
     * @description 跨线程发送消息
     */
    public boolean post(final @NonNull M message) {
        if (authenticateMessage(message)) {
            new Handler(Looper.getMainLooper()).post(() -> {
                // 观察
                final MvvmMutablePerfectLiveData<M> liveData = new MvvmMutablePerfectLiveData<>(message, false);
                mIdToObserverMap.values().forEach(messageObserver -> messageObserver.handleMessage(liveData));
                mMessageQueue.add(liveData);

                // 发送清空消息
                mClearMessageHandler.sendMessage(liveData);
            });

            return true;
        }else {
            return false;
        }
    }

    /**
     * @author soulkun
     * @time 2022/11/7 16:12
     * @description 消息鉴权，返回true才允许发送
     */
    protected abstract boolean authenticateMessage(final @NonNull M message);

    /**
     * @author soulkun
     * @time 2022/11/7 16:12
     * @description 初始化消息队列长度
     */
    protected int initMessageQueueLength() {
        return DEFAULT_MESSAGE_QUEUE_LENGTH;
    }

    /**
     * @author soulkun
     * @time 2022/11/7 16:13
     * @description 初始化消息清除时间
     */
    protected long initMessageClearTime() {
        return DEFAULT_MESSAGE_CLEAR_TIME;
    }

    private class MessageClearHandler extends Handler {

        public MessageClearHandler(final @NonNull Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(final @NonNull Message msg) {
            final MvvmMutablePerfectLiveData liveData = (MvvmMutablePerfectLiveData) msg.obj;
            // 删除LiveData
            mMessageQueue.remove(liveData);
            // 删除观察者
            mIdToObserverMap.values().forEach(messageObserver -> liveData.removeObserver(messageObserver));
        }

        public void sendMessage(MvvmMutablePerfectLiveData<M> liveData) {
            sendMessageDelayed(Message.obtain(this, liveData.getValue().hashCode(), liveData), initMessageClearTime());
        }

    }

    private class MessageObserver implements Observer<M> {

        private final boolean mIsSticky;

        private final Observer<M> mObserver;

        private boolean mIsForever;

        private LifecycleOwner mLifecycleOwner;

        public MessageObserver(final boolean isSticky, final Observer<M> observer) {
            mIsSticky = isSticky;
            mObserver = observer;
        }

        @Override
        public void onChanged(final M m) {
            mObserver.onChanged(m);
        }

        // 绑定生命周期
        private void bind(final LifecycleOwner lifecycleOwner) {
            // 设置参数
            mLifecycleOwner = lifecycleOwner;
            mIsForever = lifecycleOwner == null;

            // 存储Observer
            int id = mObserver.hashCode();
            final MessageObserver observer = mIdToObserverMap.get(id);
            if (observer != null) {
                observer.unObserve();
            }
            mIdToObserverMap.put(id, this);

            // 绑定LiveData
            if (mIsForever) {
                final ListIterator<MvvmMutablePerfectLiveData<M>> iterator = mMessageQueue.listIterator();
                while (iterator.hasNext()) {
                    final MvvmMutablePerfectLiveData<M> next = iterator.next();
                    if (iterator.hasNext()) {
                        next.observeForever(this);
                    } else {
                        if (mIsSticky) {
                            // 最近一个消息粘性观察
                            next.observeStickyForever(this);
                        } else {
                            next.observeForever(this);
                        }
                    }
                }
            } else {
                final ListIterator<MvvmMutablePerfectLiveData<M>> iterator = mMessageQueue.listIterator();
                while (iterator.hasNext()) {
                    final MvvmMutablePerfectLiveData<M> next = iterator.next();
                    if (iterator.hasNext()) {
                        next.observe(lifecycleOwner, this);
                    } else {
                        if (mIsSticky) {
                            // 最近一个消息粘性观察
                            next.observeSticky(lifecycleOwner, this);
                        } else {
                            next.observe(lifecycleOwner, this);
                        }
                    }
                }
            }
        }

        // 解绑LiveData
        private void unObserve() {
            final int id = mObserver.hashCode();
            mMessageQueue.forEach(liveData -> liveData.removeObserver(this));
            mIdToObserverMap.remove(id);
        }

        // 处理消息，无论Observer是否粘性都当作粘性处理
        private void handleMessage(MvvmPerfectLiveData<M> message) {
            if (mIsForever) {
                message.observeStickyForever(this);
            } else {
                message.observeSticky(mLifecycleOwner, this);
            }
        }

    }

    /**
     * @author soulkun
     * @time 2022/8/22 17:58
     * @description 固定长度的List数据结构类，当List达到最大长度时新增数据会将List中的第一个数据删除，并执行删除回调
     */
    private class MvvmFixedLengthList<E> extends LinkedList<E> {

        private final int maxLength;

        private final IFixedLengthListCallback<E> iFixedLengthListCallback;

        public MvvmFixedLengthList(final int maxLength, final IFixedLengthListCallback<E> iFixedLengthListCallback) {
            this.maxLength = maxLength;
            this.iFixedLengthListCallback = iFixedLengthListCallback;
        }

        @Override
        public boolean add(E e) {
            if (size() + 1 > maxLength) {
                final E removeElement = super.removeFirst();
                if (iFixedLengthListCallback != null) {
                    iFixedLengthListCallback.onRemoveFirst(removeElement);
                }
            }
            return super.add(e);
        }

    }

    private interface IFixedLengthListCallback<E>{
        void onRemoveFirst(E t);
    }

}
