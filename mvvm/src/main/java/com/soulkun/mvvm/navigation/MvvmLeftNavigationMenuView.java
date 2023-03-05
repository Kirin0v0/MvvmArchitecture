/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.soulkun.mvvm.navigation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.core.view.ViewCompat;

import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarMenuView;
import com.soulkun.mvvm.R;

import static androidx.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * @hide For internal use only.
 */
@SuppressLint("RestrictedApi")
@RestrictTo(LIBRARY_GROUP)
public class MvvmLeftNavigationMenuView extends NavigationBarMenuView {
    private final int inactiveItemMaxHeight;
    private final int inactiveItemMinHeight;
    private final int activeItemMaxHeight;
    private final int activeItemMinHeight;
    private final int itemWidth;

    private boolean itemVerticalTranslationEnabled;
    private int[] tempChildHeights;

    public MvvmLeftNavigationMenuView(@NonNull Context context) {
        super(context);

        FrameLayout.LayoutParams params =
                new FrameLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        setLayoutParams(params);

        final Resources res = getResources();
        inactiveItemMaxHeight =
                res.getDimensionPixelSize(R.dimen.mvvm_left_navigation_item_max_height);
        inactiveItemMinHeight =
                res.getDimensionPixelSize(R.dimen.mvvm_left_navigation_item_min_height);
        activeItemMaxHeight =
                res.getDimensionPixelSize(R.dimen.mvvm_left_navigation_active_item_max_height);
        activeItemMinHeight =
                res.getDimensionPixelSize(R.dimen.mvvm_left_navigation_active_item_min_height);
        itemWidth = res.getDimensionPixelSize(R.dimen.mvvm_left_navigation_width);

        tempChildHeights = new int[MvvmLeftNavigationView.MAX_ITEM_COUNT];
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final MenuBuilder menu = getMenu();
        final int height = MeasureSpec.getSize(heightMeasureSpec);
        // Use visible item count to calculate widths
        final int visibleCount = menu.getVisibleItems().size();
        // Use total item counts to measure children
        final int totalCount = getChildCount();

        final int widthSpec = MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY);

        if (isShifting(getLabelVisibilityMode(), visibleCount)
                && isItemVerticalTranslationEnabled()) {
            final View activeChild = getChildAt(getSelectedItemPosition());
            int activeItemHeight = activeItemMinHeight;
            if (activeChild.getVisibility() != View.GONE) {
                // Do an AT_MOST measure pass on the active child to get its desired width, and resize the
                // active child view based on that width
                activeChild.measure(
                        widthSpec, MeasureSpec.makeMeasureSpec(activeItemMaxHeight, MeasureSpec.AT_MOST));
                activeItemHeight = Math.max(activeItemHeight, activeChild.getMeasuredHeight());
            }
            final int inactiveCount = visibleCount - (activeChild.getVisibility() != View.GONE ? 1 : 0);
            final int activeMaxAvailable = height - inactiveCount * inactiveItemMinHeight;
            final int activeHeight =
                    Math.min(activeMaxAvailable, Math.min(activeItemHeight, activeItemMaxHeight));
            final int inactiveMaxAvailable =
                    (height - activeHeight) / (inactiveCount == 0 ? 1 : inactiveCount);
            final int inactiveHeight = Math.min(inactiveMaxAvailable, inactiveItemMaxHeight);
            int extra = height - activeHeight - inactiveHeight * inactiveCount;

            for (int i = 0; i < totalCount; i++) {
                if (getChildAt(i).getVisibility() != View.GONE) {
                    tempChildHeights[i] = (i == getSelectedItemPosition()) ? activeHeight : inactiveHeight;
                    // Account for integer division which sometimes leaves some extra pixel spaces.
                    // e.g. If the nav was 10px wide, and 3 children were measured to be 3px-3px-3px, there
                    // would be a 1px gap somewhere, which this fills in.
                    if (extra > 0) {
                        tempChildHeights[i]++;
                        extra--;
                    }
                } else {
                    tempChildHeights[i] = 0;
                }
            }
        } else {
            final int maxAvailable = height / (visibleCount == 0 ? 1 : visibleCount);
            final int childHeight = Math.min(maxAvailable, activeItemMaxHeight);
            int extra = height - childHeight * visibleCount;
            for (int i = 0; i < totalCount; i++) {
                if (getChildAt(i).getVisibility() != View.GONE) {
                    tempChildHeights[i] = childHeight;
                    if (extra > 0) {
                        tempChildHeights[i]++;
                        extra--;
                    }
                } else {
                    tempChildHeights[i] = 0;
                }
            }
        }

        int totalHeight = 0;
        for (int i = 0; i < totalCount; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.measure(
                    widthSpec, MeasureSpec.makeMeasureSpec(tempChildHeights[i], MeasureSpec.EXACTLY));
            LayoutParams params = child.getLayoutParams();
            params.height = child.getMeasuredHeight();
            totalHeight += child.getMeasuredHeight();
        }
//        setMeasuredDimension(
//                View.resolveSizeAndState(itemWidth, widthSpec, 0),
//                View.resolveSizeAndState(
//                        totalHeight, MeasureSpec.makeMeasureSpec(totalHeight, MeasureSpec.EXACTLY), 0));
        setMeasuredDimension(
                View.resolveSizeAndState(itemWidth, widthSpec, 0),
                View.resolveSizeAndState(
                        totalHeight, heightMeasureSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        final int width = right - left;
        final int height = bottom - top;

        int total = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            total += child.getMeasuredHeight();
        }

        int margin = (height - total) / (count + 1);
        int used = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (i == 0) {
                used += margin;
            }
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                child.layout(0, height - used - child.getMeasuredHeight() - margin, width, height - used - margin);
            } else {
                child.layout(0, used, width, child.getMeasuredHeight() + used);
            }
            used += child.getMeasuredHeight() + margin;
        }
    }

    /**
     * Sets whether the menu items horizontally translate on selection when the combined item widths
     * fill the screen.
     *
     * @param itemVerticalTranslationEnabled whether the menu items horizontally translate on
     *                                       selection
     * @see #isItemVerticalTranslationEnabled()
     */
    public void setItemVerticalTranslationEnabled(boolean itemVerticalTranslationEnabled) {
        this.itemVerticalTranslationEnabled = itemVerticalTranslationEnabled;
    }

    /**
     * Returns whether the menu items horizontally translate on selection when the combined item
     * widths fill the screen.
     *
     * @return whether the menu items horizontally translate on selection
     * @see #setItemVerticalTranslationEnabled(boolean)
     */
    public boolean isItemVerticalTranslationEnabled() {
        return itemVerticalTranslationEnabled;
    }

    @Override
    @NonNull
    protected NavigationBarItemView createNavigationBarItemView(@NonNull Context context) {
        return new MvvmLeftNavigationItemView(context);
    }
}
