package com.soulkun.mvvm.recyclerview;

/**
 * @author soulkun
 * @time 2022/8/19 20:38
 * @description RecyclerView的LoadMoreView点击回调接口
 */
public interface IMvvmRecyclerViewLoadMoreClickCallback {

    void onClickFailView();

    void onClickEndView();

}