package com.soulkun.mvvm.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.ObservableField;
import androidx.databinding.ViewDataBinding;

import com.chad.library.adapter.base.loadmore.BaseLoadMoreView;
import com.chad.library.adapter.base.loadmore.LoadMoreStatus;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.soulkun.mvvm.factory.MvvmDataBindingFactory;

public abstract class MvvmAbstractRecyclerViewLoadMoreDataBindingView<LoadMoreDataBinding extends ViewDataBinding> extends BaseLoadMoreView {

    private ObservableField<LoadMoreStatus> mLoadMoreStatus = new ObservableField<>(LoadMoreStatus.Complete);

    protected IMvvmRecyclerViewLoadMoreClickCallback mLoadMoreClickImpl;

    @Override
    public View getRootView(ViewGroup rootView) {
        final MvvmDataBindingFactory mvvmDataBindingFactory = getDataBindingFactory(mLoadMoreStatus);
        final ViewDataBinding viewDataBinding = mvvmDataBindingFactory.inflate(LayoutInflater.from(rootView.getContext()), rootView);
        return viewDataBinding.getRoot();
    }

    /**
     * @author soulkun
     * @time 2022/8/22 17:57
     * @description 加载更多的Loading视图专用，当status符合则展示View
     */
    @BindingAdapter(value = "loadmore_loadingStatus_visibility")
    public static void setLoadMoreLoadingViewVisibility(@NonNull View view, LoadMoreStatus loadMoreStatus) {
        if (loadMoreStatus == LoadMoreStatus.Loading) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * @author soulkun
     * @time 2022/8/22 17:57
     * @description 加载更多的Complete视图专用，当status符合则展示View
     */
    @BindingAdapter(value = "loadmore_completeStatus_visibility")
    public static void setLoadMoreCompleteViewVisibility(@NonNull View view, LoadMoreStatus loadMoreStatus) {
        if (loadMoreStatus == LoadMoreStatus.Complete) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * @author soulkun
     * @time 2022/8/22 17:57
     * @description 加载更多的End视图专用，当status符合则展示View
     */
    @BindingAdapter(value = "loadmore_endStatus_visibility")
    public static void setLoadMoreEndViewVisibility(@NonNull View view, LoadMoreStatus loadMoreStatus) {
        if (loadMoreStatus == LoadMoreStatus.End) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * @author soulkun
     * @time 2022/8/22 17:57
     * @description 加载更多的Fail视图专用，当status符合则展示View
     */
    @BindingAdapter(value = "loadmore_failStatus_visibility")
    public static void setLoadMoreFailViewVisibility(@NonNull View view, LoadMoreStatus loadMoreStatus) {
        if (loadMoreStatus == LoadMoreStatus.Fail) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @Override
    public View getLoadingView(BaseViewHolder baseViewHolder) {
        return null;
    }

    @Override
    public View getLoadEndView(BaseViewHolder baseViewHolder) {
        return null;
    }

    @Override
    public View getLoadFailView(BaseViewHolder baseViewHolder) {
        return null;
    }

    @Override
    public View getLoadComplete(BaseViewHolder baseViewHolder) {
        return null;
    }

    @Override
    public void convert(BaseViewHolder holder, int position, LoadMoreStatus loadMoreStatus) {
        switch (loadMoreStatus) {
            case Loading:
                mLoadMoreStatus.set(LoadMoreStatus.Loading);
                break;
            case Complete:
                mLoadMoreStatus.set(LoadMoreStatus.Complete);
                break;
            case End:
                mLoadMoreStatus.set(LoadMoreStatus.End);
                break;
            case Fail:
                mLoadMoreStatus.set(LoadMoreStatus.Fail);
                break;
            default:
                mLoadMoreStatus.set(LoadMoreStatus.Complete);
                break;
        }
    }

    protected abstract MvvmDataBindingFactory getDataBindingFactory(ObservableField<LoadMoreStatus> state);

    public void setLoadMoreClickImpl(IMvvmRecyclerViewLoadMoreClickCallback mLoadMoreClickImpl) {
        this.mLoadMoreClickImpl = mLoadMoreClickImpl;
    }

}
