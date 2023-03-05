package com.soulkun.mvvm.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.appcompat.widget.TintTypedArray;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.ThemeEnforcement;
import com.google.android.material.navigation.NavigationBarMenuView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.shape.MaterialShapeDrawable;
import com.soulkun.mvvm.R;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * @author soulkun
 * @date 2022/11/24 8:44
 * @description 仿BottomNavigationView的侧边NavigationView
 */
public class MvvmLeftNavigationView extends NavigationBarView {

    static final int MAX_ITEM_COUNT = 5;

    public MvvmLeftNavigationView(@NonNull Context context) {
        this(context, null);
    }

    public MvvmLeftNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.bottomNavigationStyle);
    }

    public MvvmLeftNavigationView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, R.style.Widget_Mvvm_MvvmLeftNavigationView);
    }

    @SuppressLint("RestrictedApi")
    public MvvmLeftNavigationView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        // Ensure we are using the correctly themed context rather than the context that was passed in.
        context = getContext();

        /* Custom attributes */
        @SuppressLint("RestrictedApi") TintTypedArray attributes =
                ThemeEnforcement.obtainTintedStyledAttributes(
                        context, attrs, R.styleable.MvvmLeftNavigationView, defStyleAttr, defStyleRes);

        setItemVerticalTranslationEnabled(
                attributes.getBoolean(
                        R.styleable.MvvmLeftNavigationView_itemVerticalTranslationEnabled, true));

        attributes.recycle();

        if (shouldDrawCompatibilityTopDivider()) {
            addCompatibilityTopDivider(context);
        }
    }

    /**
     * Sets whether the menu items horizontally translate on selection when the combined item widths
     * fill up the screen.
     *
     * @param itemVerticalTranslationEnabled whether the items horizontally translate on selection
     * @see #isItemVerticalTranslationEnabled()
     */
    @SuppressLint("RestrictedApi")
    public void setItemVerticalTranslationEnabled(boolean itemVerticalTranslationEnabled) {
        MvvmLeftNavigationMenuView menuView = (MvvmLeftNavigationMenuView) getMenuView();
        if (menuView.isItemVerticalTranslationEnabled() != itemVerticalTranslationEnabled) {
            menuView.setItemVerticalTranslationEnabled(itemVerticalTranslationEnabled);
            getPresenter().updateMenuView(false);
        }
    }

    /**
     * Returns whether the items horizontally translate on selection when the item widths fill up the
     * screen.
     *
     * @return whether the menu items horizontally translate on selection
     * @see #setItemVerticalTranslationEnabled(boolean)
     */
    @SuppressLint("RestrictedApi")
    public boolean isItemVerticalTranslationEnabled() {
        return ((MvvmLeftNavigationMenuView) getMenuView()).isItemVerticalTranslationEnabled();
    }

    @Override
    public int getMaxItemCount() {
        return MAX_ITEM_COUNT;
    }

    @Override
    public void addView(final View child) {
        super.addView(child, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    /**
     * @hide
     */
    @RestrictTo(LIBRARY_GROUP)
    @Override
    @NonNull
    protected NavigationBarMenuView createNavigationBarMenuView(@NonNull Context context) {
        return new MvvmLeftNavigationMenuView(context);
    }

    /**
     * Returns true a divider must be added in place of shadows to maintain compatibility in pre-21
     * legacy backgrounds.
     */
    private boolean shouldDrawCompatibilityTopDivider() {
        return Build.VERSION.SDK_INT < 21 && !(getBackground() instanceof MaterialShapeDrawable);
    }

    /**
     * Adds a divider in place of shadows to maintain compatibility in pre-21 legacy backgrounds. If a
     * pre-21 background has been updated to a MaterialShapeDrawable, MaterialShapeDrawable will draw
     * shadows instead.
     */
    private void addCompatibilityTopDivider(@NonNull Context context) {
        View divider = new View(context);
        divider.setBackgroundColor(
                ContextCompat.getColor(context, R.color.design_bottom_navigation_shadow_color));
        LayoutParams dividerParams =
                new LayoutParams(
                        getResources().getDimensionPixelSize(R.dimen.design_bottom_navigation_shadow_height),
                        ViewGroup.LayoutParams.MATCH_PARENT);
        divider.setLayoutParams(dividerParams);
        addView(divider);
    }

    /**
     * Set a listener that will be notified when a bottom navigation item is selected. This listener
     * will also be notified when the currently selected item is reselected, unless an {@link
     * BottomNavigationView.OnNavigationItemReselectedListener} has also been set.
     *
     * @param listener The listener to notify
     */
    @Deprecated
    public void setOnNavigationItemSelectedListener(
            @Nullable OnNavigationItemSelectedListener listener) {
        setOnItemSelectedListener(listener);
    }

    /**
     * Set a listener that will be notified when the currently selected bottom navigation item is
     * reselected. This does not require an {@link BottomNavigationView.OnNavigationItemSelectedListener} to be set.
     *
     * @param listener The listener to notify
     */
    @Deprecated
    public void setOnNavigationItemReselectedListener(
            @Nullable OnNavigationItemReselectedListener listener) {
        setOnItemReselectedListener(listener);
    }

    /**
     * Listener for handling selection events on bottom navigation items.
     */
    @Deprecated
    public interface OnNavigationItemSelectedListener extends OnItemSelectedListener {
    }

    /**
     * Listener for handling reselection events on bottom navigation items.
     */
    @Deprecated
    public interface OnNavigationItemReselectedListener extends OnItemReselectedListener {
    }

}
