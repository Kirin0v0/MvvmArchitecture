package com.soulkun.mvvm.recyclerview;

import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseProviderMultiAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnLoadMoreListener;
import com.chad.library.adapter.base.module.BaseLoadMoreModule;
import com.chad.library.adapter.base.module.LoadMoreModule;
import com.chad.library.adapter.base.provider.BaseItemProvider;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.soulkun.mvvm.factory.MvvmDataBindingFactory;

import java.util.List;

/**
 * @author soulkun
 * @date 2022/8/18 15:43
 * @description 灵活的RecyclerView适配器，实现加载更多接口，允许设置头、尾、空布局，相比较基础版，能够灵活地设置Item类型，适用于多种内容Item
 */
public abstract class MvvmAbstractRecyclerViewDataBindingProviderMultiAdapter<Model>
        extends BaseProviderMultiAdapter<Model> implements LoadMoreModule, IMvvmRecyclerViewDataBindingBasicView {

    // 唯一不复用布局
    private MvvmDataBindingFactory mHeaderDataBindingFactory;
    private MvvmDataBindingFactory mFooterDataBindingFactory;
    private MvvmDataBindingFactory mEmptyDataBindingFactory;

    private ViewDataBinding mHeaderViewDataBinding;
    private ViewDataBinding mFooterViewDataBinding;
    private ViewDataBinding mEmptyViewDataBinding;

    private MvvmAbstractRecyclerViewLoadMoreDataBindingView mLoadMoreDataBindingView;

    public MvvmAbstractRecyclerViewDataBindingProviderMultiAdapter(@NonNull final List<Model> modelList) {
        super(modelList);
        initItemProviders();
    }

    /**
     * @author soulkun
     * @time 2022/8/18 16:01
     * @description 适配器初始化接入RecyclerView时初始化其他布局
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        // 监听可观察数据变化
        if (getData() instanceof MvvmRecyclerViewAdapterObservableArrayList) {
            ((MvvmRecyclerViewAdapterObservableArrayList<Model>) getData()).registerOnListChangedCallback(new MvvmRecyclerViewDataBindingAdapterOnListChangedAdapterCallback(this));
        }

        if (mHeaderDataBindingFactory != null) {
            mHeaderViewDataBinding = mHeaderDataBindingFactory.inflate(LayoutInflater.from(getContext()), recyclerView);
            addHeaderView(mHeaderViewDataBinding.getRoot());
        }

        if (mFooterDataBindingFactory != null) {
            mFooterViewDataBinding = mFooterDataBindingFactory.inflate(LayoutInflater.from(getContext()), recyclerView);
            addFooterView(mFooterViewDataBinding.getRoot());
        }

        if (mEmptyDataBindingFactory != null) {
            mEmptyViewDataBinding = mEmptyDataBindingFactory.inflate(LayoutInflater.from(getContext()), recyclerView);
            setEmptyView(mEmptyViewDataBinding.getRoot());
        }
    }

    @Override
    public void onDetachedFromRecyclerView(final RecyclerView recyclerView) {
        // 监听可观察数据变化
        if (getData() instanceof MvvmRecyclerViewAdapterObservableArrayList) {
            ((MvvmRecyclerViewAdapterObservableArrayList<Model>) getData()).unregisterOnListChangedCallback();
        }

        if (mHeaderViewDataBinding != null) {
            mHeaderViewDataBinding.unbind();
            mHeaderViewDataBinding = null;
        }

        if (mFooterViewDataBinding != null) {
            mFooterViewDataBinding.unbind();
            mFooterViewDataBinding = null;
        }

        if (mEmptyViewDataBinding != null) {
            mEmptyViewDataBinding.unbind();
            mEmptyViewDataBinding = null;
        }

        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * @author soulkun
     * @time 2022/8/18 13:28
     * @description 绑定全部类型ViewHolder，处理数据绑定
     */
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        final int itemViewType = holder.getItemViewType();
        if (itemViewType == HEADER_VIEW) {
            assert mHeaderViewDataBinding != null;
            if (consumeBasicViewHolderBinded(ViewType.HEADER, mHeaderViewDataBinding)) {
                mHeaderViewDataBinding.executePendingBindings();
            }
        } else if (itemViewType == FOOTER_VIEW) {
            assert mFooterViewDataBinding != null;
            if (consumeBasicViewHolderBinded(ViewType.FOOTER, mFooterViewDataBinding)) {
                mFooterViewDataBinding.executePendingBindings();
            }
        } else if (itemViewType == EMPTY_VIEW) {
            assert mEmptyViewDataBinding != null;
            if (consumeBasicViewHolderBinded(ViewType.EMPTY, mEmptyViewDataBinding)) {
                mEmptyViewDataBinding.executePendingBindings();
            }
        } else if (itemViewType != LOAD_MORE_VIEW) {
            final BaseItemProvider<Model> itemProvider = getItemProvider(itemViewType);
            if (itemProvider instanceof MvvmAbstractRecyclerViewDataBindingItemProvider) {
                final ViewDataBinding viewDataBinding = DataBindingUtil.bind(holder.itemView);
                if (((MvvmAbstractRecyclerViewDataBindingItemProvider<Model>) itemProvider).consumeItemViewHolderBinded(viewDataBinding, getItem(position - getHeaderLayoutCount()), position - getHeaderLayoutCount())) {
                    viewDataBinding.executePendingBindings();
                }
            }
        }
    }

    /**
     * @author soulkun
     * @time 2022/8/18 15:55
     * @description Recycle回收时数据解绑
     */
    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        super.onViewRecycled(holder);
        final int itemViewType = holder.getItemViewType();
        if (itemViewType == HEADER_VIEW) {
            assert mHeaderViewDataBinding != null;
            consumeBasicViewHolderBinded(ViewType.HEADER, mHeaderViewDataBinding);
        } else if (itemViewType == FOOTER_VIEW) {
            assert mFooterViewDataBinding != null;
            consumeBasicViewHolderBinded(ViewType.FOOTER, mFooterViewDataBinding);
        } else if (itemViewType == EMPTY_VIEW) {
            assert mEmptyViewDataBinding != null;
            consumeBasicViewHolderBinded(ViewType.EMPTY, mEmptyViewDataBinding);
        } else if (itemViewType != LOAD_MORE_VIEW) {
            final BaseItemProvider<Model> itemProvider = getItemProvider(itemViewType);
            if (itemProvider instanceof MvvmAbstractRecyclerViewDataBindingItemProvider) {
                final ViewDataBinding viewDataBinding = DataBindingUtil.bind(holder.itemView);
                ((MvvmAbstractRecyclerViewDataBindingItemProvider<Model>) itemProvider).consumeItemViewHolderRecycled(viewDataBinding);
            }
        }
    }

    /**
     * @author soulkun
     * @time 2022/8/18 16:49
     * @description add必须添加MvvmAbstractRecyclerViewDataBindingItemProvider的子类
     */
    @Override
    public void addItemProvider(BaseItemProvider<Model> provider) {
        assert provider instanceof MvvmAbstractRecyclerViewDataBindingItemProvider;
        super.addItemProvider(provider);
    }

    /**
     * @author soulkun
     * @time 2022/8/18 13:28
     * @description 实现加载更多功能
     */
    @Override
    public BaseLoadMoreModule addLoadMoreModule(BaseQuickAdapter<?, ?> baseQuickAdapter) {
        final BaseLoadMoreModule loadMoreModule = new BaseLoadMoreModule(this);
        mLoadMoreDataBindingView = getLoadMoreView();
        if (mLoadMoreDataBindingView == null) {
            mLoadMoreDataBindingView = new MvvmDefaultRecyclerViewDataBindingLoadMoreView();
        }
        return loadMoreModule;
    }

    /**
     * @author soulkun
     * @time 2022/8/19 15:02
     * @description 设置LoadMore点击事件回调
     */
    public void setOnLoadViewMoreClickCallback(IMvvmRecyclerViewLoadMoreClickCallback iMvvmRecyclerViewLoadMoreClickCallback) {
        if (mLoadMoreDataBindingView != null) {
            mLoadMoreDataBindingView.setLoadMoreClickImpl(iMvvmRecyclerViewLoadMoreClickCallback);
        }
    }

    /**
     * @author soulkun
     * @time 2022/8/18 13:29
     * @description 加载更多监听器
     */
    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        if (getLoadMoreModule() != null) {
            getLoadMoreModule().setOnLoadMoreListener(onLoadMoreListener);
        }
    }

    protected abstract void initItemProviders();

    protected MvvmAbstractRecyclerViewLoadMoreDataBindingView getLoadMoreView() {
        return null;
    }

    /**
     * @author soulkun
     * @time 2022/8/18 16:36
     * @description 外部设置其他布局，注意，要在setAdapter之前调用，否则无效
     */
    @Override
    public void setBasicViewDataBindingFactory(ViewType viewType, com.soulkun.mvvm.factory.MvvmDataBindingFactory dataBindingFactory) {
        switch (viewType) {
            case HEADER: {
                mHeaderDataBindingFactory = dataBindingFactory;
            }
            break;
            case FOOTER: {
                mFooterDataBindingFactory = dataBindingFactory;
            }
            break;
            case EMPTY: {
                mEmptyDataBindingFactory = dataBindingFactory;
            }
            break;
        }
    }

    /**
     * @author soulkun
     * @time 2022/8/18 16:36
     * @description 消费绑定ViewHolder事件，返回true会立即刷新视图，否则等待下一帧刷新
     */
    @Override
    public boolean consumeBasicViewHolderBinded(ViewType viewType, ViewDataBinding viewDataBinding) {
        return false;
    }

    /**
     * @author soulkun
     * @time 2022/8/18 16:36
     * @description 消费Recycler回收ViewHolder事件
     */
    @Override
    public void consumeBasicViewHolderRecycled(ViewType viewType, ViewDataBinding viewDataBinding) {
    }

}
