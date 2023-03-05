package com.soulkun.mvvm.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.soulkun.mvvm.enums.MvvmLifecycleCouple;
import com.soulkun.mvvm.model.MvvmRequest;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author soulkun
 * @time 2022/8/19 9:36
 * @description Mvvm框架实现Request数据请求模块，自动实现生命周期内开启和关闭、中断数据请求
 * 使用步骤：
 * 1.从容器中获取对应的ViewModel，此时线程池生命周期自动绑定容器生命周期，容器销毁时线程池自动销毁，简化操作
 * 2.请求方法全部返回LiveData<MvvmRequest<T>>类型，并在方法中使用execute(MvvmAbstractRequestTask<T> task)方法执行任务
 * 3.使用时，调用{@link #bindTaskLifecycle)}方法绑定执行任务的生命周期，再发起数据请求，在执行任务的生命周期内执行任务，否则不执行或执行取消回调
 *
 * 注意，ViewModel存放的容器生命周期和其待执行任务的生命周期可能存在不同生命周期的问题（如此设计是为了允许执行非任务生命周期中断任务或等待其重新达到任务生命周期后再执行任务的目的），需要业务人员根据需求绑定
 * {@link MvvmAbstractRequestTask#checkCancel()}方法允许重新检查当前中断状态达到中断效果
 */
public abstract class MvvmAbstractRequestViewModel extends ViewModel implements LifecycleEventObserver {

    private ThreadPoolExecutor mRequestThreadPoolExecutor;

    // 使用软引用保证任务太久不执行的可释放性
    private final List<SoftReference<MvvmAbstractRequestTask>> mRequestTaskList = new LinkedList<>();

    // 待执行任务逻辑的执行生命周期，非生命周期则不予执行
    private WeakReference<Lifecycle> mLifecycleWeakRef;
    private MvvmLifecycleCouple mLifecycleCouple;

    // 绑定执行任务的生命周期
    public void bindTaskLifecycle(@NonNull Lifecycle lifecycle, @NonNull MvvmLifecycleCouple lifecycleCouple) {
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

    // 解绑执行任务的生命周期并关闭线程池
    public void unbindTaskLifecycleInAdvance() {
        if (mLifecycleWeakRef != null && mLifecycleWeakRef.get() != null) {
            mLifecycleWeakRef.get().removeObserver(this);
            if (mRequestThreadPoolExecutor != null) {
                mRequestThreadPoolExecutor.shutdownNow();
                mRequestThreadPoolExecutor = null;
            }
            mLifecycleCouple = null;
            mLifecycleWeakRef = null;
        }
    }

    /**
     * @author soulkun
     * @time 2022/10/19 19:10
     * @description 监听Request绑定的生命周期，绑定的生命周期开始时开始执行待执行任务或直接执行，结束后立即销毁线程池
     */
    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if (mLifecycleCouple == null) {
            throw new RuntimeException("非法使用生命周期！");
        }

        switch (mLifecycleCouple) {
            case CREATE_DESTROY: {
                if (event == Lifecycle.Event.ON_CREATE) {
                    mRequestThreadPoolExecutor = initRequestThreadPoolExecutor();
                    executeRequestTaskList();
                } else if (event == Lifecycle.Event.ON_DESTROY) {
                    mRequestThreadPoolExecutor.shutdownNow();
                    mRequestThreadPoolExecutor = null;
                }
            }
            break;
            case START_STOP: {
                if (event == Lifecycle.Event.ON_START) {
                    mRequestThreadPoolExecutor = initRequestThreadPoolExecutor();
                    executeRequestTaskList();
                } else if (event == Lifecycle.Event.ON_STOP) {
                    mRequestThreadPoolExecutor.shutdownNow();
                    mRequestThreadPoolExecutor = null;
                }
            }
            break;
            case RESUME_PAUSE: {
                if (event == Lifecycle.Event.ON_RESUME) {
                    mRequestThreadPoolExecutor = initRequestThreadPoolExecutor();
                    executeRequestTaskList();
                } else if (event == Lifecycle.Event.ON_PAUSE) {
                    mRequestThreadPoolExecutor.shutdownNow();
                    mRequestThreadPoolExecutor = null;
                }
            }
            break;
        }
    }

    @Override
    protected void onCleared() {
        mRequestTaskList.clear();

        super.onCleared();
    }

    /**
     * @author soulkun
     * @time 2022/10/7 9:12
     * @description 内部线程池自动执行任务，到绑定生命周期时允许执行否则存储至List中等待
     */
    protected <T> void execute(MvvmAbstractRequestTask<T> task) {
        if (mRequestThreadPoolExecutor != null && !mRequestThreadPoolExecutor.isShutdown()) {
            mRequestThreadPoolExecutor.execute(task);
        } else {
            synchronized (mRequestTaskList) {
                mRequestTaskList.add(new SoftReference<>(task));
            }
        }
    }

    /**
     * @author soulkun
     * @time 2022/10/19 19:09
     * @description 生成固定的线程池
     */
    private ThreadPoolExecutor initRequestThreadPoolExecutor() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>());
    }

    /**
     * @author soulkun
     * @time 2022/10/19 19:36
     * @description 逐一执行待执行任务列表，并删除执行完毕的任务
     */
    private void executeRequestTaskList() {
        synchronized (mRequestTaskList) {
            final ListIterator<SoftReference<MvvmAbstractRequestTask>> listIterator = mRequestTaskList.listIterator();
            while (listIterator.hasNext()) {
                final SoftReference<MvvmAbstractRequestTask> taskSoftReference = listIterator.next();
                if (taskSoftReference.get() != null) {
                    mRequestThreadPoolExecutor.execute(taskSoftReference.get());
                }
                listIterator.remove();
            }
        }
    }

    /**
     * @author soulkun
     * @description 数据请求任务类，基本实现了在请求数据成功时检测线程池状态，达到页面生命周期毁灭或离开中断请求，以防生命周期毁灭导致的内存占用问题
     */
    protected abstract class MvvmAbstractRequestTask<T> implements Runnable {

        private MutableLiveData<MvvmRequest<T>> requestLiveData;

        protected abstract MvvmRequest<T> doRequest() throws InterruptedException;

        protected abstract void doCancel();

        public MvvmAbstractRequestTask(MutableLiveData<MvvmRequest<T>> requestLiveData) {
            this.requestLiveData = requestLiveData;
        }

        @Override
        public void run() {
            try {
                checkCancel();
                final MvvmRequest<T> request = doRequest();
                if (request == null) {
                    throw new RuntimeException("doRequest()方法不允许返回Null值，请检查代码！");
                }
                checkCancel();
                requestLiveData.postValue(request);
            } catch (InterruptedException e) {
                e.printStackTrace();
                doCancel();
            }
        }

        public void checkCancel() throws InterruptedException {
            if (Thread.currentThread().isInterrupted()) {
                throw new InterruptedException();
            }
        }

    }

}
