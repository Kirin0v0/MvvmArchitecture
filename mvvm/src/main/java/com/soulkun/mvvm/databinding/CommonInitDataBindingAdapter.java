package com.soulkun.mvvm.databinding;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * @author soulkun
 * @date 2022/10/19 10:21
 * @description DataBinding视图初始化Adapter
 */
public class CommonInitDataBindingAdapter {

    public interface IViewInit{
        void init(@NonNull final View view);
    }

    @BindingAdapter("view_init")
    public static void initView(@NonNull View view, @NonNull IViewInit viewInitImpl) {
        viewInitImpl.init(view);
    }

    public interface IBottomNavigationViewInit{
        void init(@NonNull final BottomNavigationView bottomNavigationView);
    }

    @BindingAdapter("bnv_init")
    public static void initBottomNavigationView(@NonNull final BottomNavigationView bottomNavigationView, @NonNull final IBottomNavigationViewInit bottomNavigationViewInit) {
        bottomNavigationViewInit.init(bottomNavigationView);
    }

    public interface IRecyclerViewInit {
        void init(@NonNull final RecyclerView recyclerView);
    }

    @BindingAdapter("rv_init")
    public static void initRecyclerView(@NonNull RecyclerView recyclerView, @NonNull IRecyclerViewInit recyclerViewInitImpl) {
        recyclerViewInitImpl.init(recyclerView);
    }

}
