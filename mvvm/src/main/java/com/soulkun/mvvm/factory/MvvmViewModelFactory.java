package com.soulkun.mvvm.factory;

import androidx.activity.ComponentActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.NavController;

import com.soulkun.mvvm.MvvmApplication;
import com.soulkun.mvvm.component.MvvmAbstractService;
import com.soulkun.mvvm.navigation.MvvmNavigationUtils;

/**
 * @author soulkun
 * @time 2022/8/19 15:29
 * @description MVVM框架下的ViewModelFactory 工厂类，根据生命周期管理ViewModel
 */
public class MvvmViewModelFactory {

    /**
     * @author soulkun
     * @time 2022/8/19 15:28
     * @description 获取生命周期绑定Activity的ViewModel
     */
    public static <T extends ViewModel> T getActivityViewModel(@NonNull ComponentActivity componentActivity, @NonNull Class<T> modelClass) {
        return new ViewModelProvider(componentActivity).get(modelClass);
    }

    /**
     * @author soulkun
     * @time 2022/8/19 15:28
     * @description 获取生命周期绑定Fragment的ViewModel
     */
    public static <T extends ViewModel> T getFragmentViewModel(@NonNull Fragment fragment, @NonNull Class<T> modelClass) {
        return new ViewModelProvider(fragment).get(modelClass);
    }

    /**
     * @author soulkun
     * @time 2022/8/19 15:28
     * @description 获取生命周期绑定BaseService的ViewModel
     */
    public static <T extends ViewModel> T getServiceViewModel(@NonNull MvvmAbstractService service, @NonNull Class<T> modelClass) {
        return new ViewModelProvider(service).get(modelClass);
    }

    /**
     * @author soulkun
     * @time 2022/8/19 15:29
     * @description 获取生命周期绑定Graph的ViewModel
     */
    public static <T extends ViewModel> T getGraphViewModel(@NonNull NavController navController, @NonNull Class<T> modelClass) {
        final ViewModelStoreOwner viewModelStoreOwner = MvvmNavigationUtils.getLastNavGraphBackStackEntry(navController);
        return new ViewModelProvider(viewModelStoreOwner).get(modelClass);
    }

    /**
     * @author soulkun
     * @time 2022/8/19 15:29
     * @description 获取生命周期绑定Application的ViewModel
     */
    public static <T extends ViewModel> T getApplicationViewModel(@NonNull Class<T> modelClass) {
        return new ViewModelProvider(MvvmApplication.getViewModelStoreOwner()).get(modelClass);
    }

    /**
     * @author soulkun
     * @time 2022/10/13 20:32
     * @description 获取生命周期绑定ViewModelStoreOwner的ViewModel
     */
    public static <T extends ViewModel> T getViewModel(@NonNull ViewModelStoreOwner viewModelStoreOwner, @NonNull Class<T> modelClass) {
        return new ViewModelProvider(viewModelStoreOwner).get(modelClass);
    }

}
