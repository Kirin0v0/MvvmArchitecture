package com.soulkun.mvvm.navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigator;

import com.soulkun.mvvm.MvvmApplication;

import java.util.Iterator;
import java.util.Objects;

/**
 * Navigation导航 工具类
 */
public class MvvmNavigationUtils {

    /**
     * @author soulkun
     * @time 2022/11/24 11:22
     * @description 判断是否包含某目的地
     */
    public static boolean containsNavigation(@NonNull NavController navController, @NonNull Integer resId) {
        final NavGraph navControllerGraph = navController.getCurrentBackStackEntry().getDestination().getParent();
        @SuppressLint("RestrictedApi") final String displayName = navControllerGraph.getDisplayName();
        final String resourceName = MvvmApplication.getInstance().getResources().getResourceName(resId);
        return Objects.equals(displayName, resourceName);
    }

    /**
     * @author soulkun
     * @time 2022/11/24 11:22
     * @description 获取之前一个的目的地Entry
     */
    public static NavBackStackEntry getPreviousNavDestinationBackStackEntry(@NonNull NavController navController) {
        @SuppressLint("RestrictedApi") final Iterator<NavBackStackEntry> navBackStackEntryIterator = navController.getBackStack().descendingIterator();
        while (navBackStackEntryIterator.hasNext()) {
            final NavBackStackEntry navBackStackEntry = navBackStackEntryIterator.next();
            if (!(navBackStackEntry.getDestination() instanceof NavGraph)) {
                return navBackStackEntryIterator.next();
            }
        }
        return null;
    }

    /**
     * @author soulkun
     * @time 2022/11/24 11:22
     * @description 获取上一个目的地为NavGraph的Entry
     */
    public static NavBackStackEntry getLastNavGraphBackStackEntry(@NonNull NavController navController) {
        @SuppressLint("RestrictedApi") final Iterator<NavBackStackEntry> navBackStackEntryIterator = navController.getBackStack().descendingIterator();
        while (navBackStackEntryIterator.hasNext()) {
            final NavBackStackEntry navBackStackEntry = navBackStackEntryIterator.next();
            if (navBackStackEntry.getDestination() instanceof NavGraph) {
                return navBackStackEntry;
            }
        }
        return null;
    }

    /**
     * @author soulkun
     * @time 2022/9/1 15:07
     * @description 安全导航，防止重复点击导致的崩溃
     */
    public static void navigateSafe(@NonNull NavController navController, @IdRes int resId) {
        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getAction(resId) != null) {
            navController.navigate(resId);
        }
    }

    /**
     * @author soulkun
     * @time 2022/9/1 15:08
     * @description 安全导航，防止重复点击导致的崩溃
     */
    public static void navigateSafe(@NonNull NavController navController, @IdRes int resId, @Nullable Bundle args) {
        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getAction(resId) != null) {
            navController.navigate(resId, args);
        }
    }

    /**
     * @author soulkun
     * @time 2022/9/1 15:08
     * @description 安全导航，防止重复点击导致的崩溃
     */
    public static void navigateSafe(@NonNull NavController navController, @IdRes int resId, @Nullable Bundle args,
                                    @Nullable NavOptions navOptions) {
        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getAction(resId) != null) {
            navController.navigate(resId, args, navOptions);
        }
    }

    /**
     * @author soulkun
     * @time 2022/9/1 15:08
     * @description 安全导航，防止重复点击导致的崩溃
     */
    public static void navigateSafe(@NonNull NavController navController, @IdRes int resId, @Nullable Bundle args, @Nullable NavOptions navOptions,
                                    @Nullable Navigator.Extras navigatorExtras) {
        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getAction(resId) != null) {
            navController.navigate(resId, args, navOptions, navigatorExtras);
        }
    }

}
