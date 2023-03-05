package com.soulkun.mvvm.databinding;

import android.view.View;

import androidx.annotation.NonNull;

/**
 * @author soulkun
 * @date 2022/10/8 11:03
 * @description 通用标题栏类，面向（Back-Title-Icon）三点一线的标题栏
 */
public class CommonTitleBarDataBindingInclude {

    // 点击事件
    public interface ITitleBarClick {

        void clickBack(@NonNull View view);

        void clickTitle(@NonNull View view);

        void clickIcon(@NonNull View view);

    }

    // 标题栏模式
    public enum TitleBarMode {
        MODE_TITLE, MODE_ICON, MODE_TITLE_ICON;
    }

    // 标题栏标题重心
    public enum TitleBarGravity {
        GRAVITY_START, GRAVITY_CENTER
    }

}
