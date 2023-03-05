package com.soulkun.mvvm.navigation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.FragmentNavigator;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.lang.ref.WeakReference;
import java.util.Iterator;

/**
 * @author soulkun
 * @date 2022/12/15 15:48
 * @description Navigation拓展类，一般用于与控件的结合导航
 */
public class MvvmNavigationExtensions {

    /**
     * @author soulkun
     * @time 2022/10/17 14:34
     * @description 建立BottomNavigationView和NavController的关联
     * 动态监听当前页面是否为BottomNavigationView的主页面，不是则会自动隐藏
     */
    public static void setupWithNavController(@NonNull BottomNavigationView bottomNavigationView, @NonNull NavController navController) {
        // 正常绑定
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // 短按代替长按
        final ViewGroup viewGroup = (ViewGroup) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    return v.performClick();
                }
            });
        }

        // 追加逻辑
        final WeakReference<BottomNavigationView> weakReference = new WeakReference<>(bottomNavigationView);
        navController.addOnDestinationChangedListener(
                new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller,
                                                     @NonNull NavDestination destination, @Nullable Bundle arguments) {
                        BottomNavigationView view = weakReference.get();
                        if (view == null) {
                            navController.removeOnDestinationChangedListener(this);
                            return;
                        }

                        @SuppressLint("RestrictedApi") final Iterator<NavBackStackEntry> navBackStackEntryIterator = navController.getBackStack().descendingIterator();
                        NavBackStackEntry currentFragmentNavBackStackEntry = null;
                        NavBackStackEntry previousNavBackStackEntry = null;
                        while (navBackStackEntryIterator.hasNext()) {
                            final NavBackStackEntry navBackStackEntry = navBackStackEntryIterator.next();
                            if (navBackStackEntry.getDestination() instanceof FragmentNavigator.Destination) {
                                currentFragmentNavBackStackEntry = navBackStackEntry;

                                final NavBackStackEntry next = navBackStackEntryIterator.next();
                                if (next != null && next.getDestination() instanceof NavGraph) {
                                    previousNavBackStackEntry = next;
                                }
                                break;
                            }
                        }

                        Menu menu = view.getMenu();
                        if (currentFragmentNavBackStackEntry != null) {
                            for (int h = 0, size = menu.size(); h < size; h++) {
                                MenuItem item = menu.getItem(h);
                                if (currentFragmentNavBackStackEntry.getDestination().getId() == item.getItemId()) {
                                    view.setVisibility(View.VISIBLE);
                                    return;
                                }
                            }
                        }
                        if (previousNavBackStackEntry != null) {
                            for (int h = 0, size = menu.size(); h < size; h++) {
                                MenuItem item = menu.getItem(h);
                                if (previousNavBackStackEntry.getDestination().getId() == item.getItemId()) {
                                    view.setVisibility(View.VISIBLE);
                                    return;
                                }
                            }
                        }
                        view.setVisibility(View.GONE);
                    }
                });
    }

    /**
     * @author soulkun
     * @time 2022/10/17 14:34
     * @description 建立LeftNavigationView和NavController的关联
     * 动态监听当前页面是否为LeftNavigationView的主页面，不是则会自动隐藏
     */
    public static void setupWithNavController(@NonNull MvvmLeftNavigationView leftNavigationView, @NonNull NavController navController) {
        leftNavigationView.setOnNavigationItemSelectedListener(
                new MvvmLeftNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        return NavigationUI.onNavDestinationSelected(item, navController);
                    }
                });

        final WeakReference<MvvmLeftNavigationView> weakReference =
                new WeakReference<>(leftNavigationView);

        navController.addOnDestinationChangedListener(
                new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller,
                                                     @NonNull NavDestination destination, @Nullable Bundle arguments) {
                        MvvmLeftNavigationView view = weakReference.get();
                        if (view == null) {
                            navController.removeOnDestinationChangedListener(this);
                            return;
                        }
                        Menu menu = view.getMenu();
                        for (int h = 0, size = menu.size(); h < size; h++) {
                            MenuItem item = menu.getItem(h);
                            if (matchDestination(destination, item.getItemId())) {
                                item.setChecked(true);
                            }
                        }
                    }
                });

        // 短按代替长按
        final ViewGroup viewGroup = (ViewGroup) leftNavigationView.getChildAt(0);
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            viewGroup.getChildAt(i).setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    return v.performClick();
                }
            });
        }

        // 追加逻辑
        navController.addOnDestinationChangedListener(
                new NavController.OnDestinationChangedListener() {
                    @Override
                    public void onDestinationChanged(@NonNull NavController controller,
                                                     @NonNull NavDestination destination, @Nullable Bundle arguments) {
                        MvvmLeftNavigationView view = weakReference.get();
                        if (view == null) {
                            navController.removeOnDestinationChangedListener(this);
                            return;
                        }

                        @SuppressLint("RestrictedApi") final Iterator<NavBackStackEntry> navBackStackEntryIterator = navController.getBackStack().descendingIterator();
                        NavBackStackEntry currentFragmentNavBackStackEntry = null;
                        NavBackStackEntry previousNavBackStackEntry = null;
                        while (navBackStackEntryIterator.hasNext()) {
                            final NavBackStackEntry navBackStackEntry = navBackStackEntryIterator.next();
                            if (navBackStackEntry.getDestination() instanceof FragmentNavigator.Destination) {
                                currentFragmentNavBackStackEntry = navBackStackEntry;

                                final NavBackStackEntry next = navBackStackEntryIterator.next();
                                if (next != null && next.getDestination() instanceof NavGraph) {
                                    previousNavBackStackEntry = next;
                                }
                                break;
                            }
                        }

                        Menu menu = view.getMenu();
                        if (currentFragmentNavBackStackEntry != null) {
                            for (int h = 0, size = menu.size(); h < size; h++) {
                                MenuItem item = menu.getItem(h);
                                if (currentFragmentNavBackStackEntry.getDestination().getId() == item.getItemId()) {
                                    view.setVisibility(View.VISIBLE);
                                    return;
                                }
                            }
                        }
                        if (previousNavBackStackEntry != null) {
                            for (int h = 0, size = menu.size(); h < size; h++) {
                                MenuItem item = menu.getItem(h);
                                if (previousNavBackStackEntry.getDestination().getId() == item.getItemId()) {
                                    view.setVisibility(View.VISIBLE);
                                    return;
                                }
                            }
                        }
                        view.setVisibility(View.GONE);
                    }
                });
    }

    private static boolean matchDestination(@NonNull NavDestination destination, @IdRes int destId) {
        NavDestination currentDestination = destination;
        while (currentDestination.getId() != destId && currentDestination.getParent() != null) {
            currentDestination = currentDestination.getParent();
        }
        return currentDestination.getId() == destId;
    }

}
