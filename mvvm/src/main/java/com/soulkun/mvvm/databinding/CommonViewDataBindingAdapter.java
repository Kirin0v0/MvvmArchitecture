package com.soulkun.mvvm.databinding;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;

import com.soulkun.mvvm.MvvmApplication;
import com.soulkun.mvvm.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author soulkun
 * @date 2022/8/18 18:04
 * @description 通用未指定View的DataBinding绑定
 */
public class CommonViewDataBindingAdapter {

    @BindingAdapter(value = "tv_scrollEnable")
    public static void setTextViewScrollEnable(@NonNull final TextView textView, final boolean isScrollEnable) {
        if (isScrollEnable) {
            textView.setMovementMethod(new ScrollingMovementMethod());
        } else {
            textView.setMovementMethod(new ArrowKeyMovementMethod());
        }
    }

    @BindingAdapter(value = "iv_src_drawableId")
    public static void setSrcDrawableId(@NonNull final ImageView imageView, final int drawableId) {
        imageView.setImageDrawable(MvvmApplication.getInstance().getDrawable(drawableId));
    }

    @BindingAdapter(value = "iv_src_bitmap")
    public static void setSrcDrawable(@NonNull final ImageView imageView, @NonNull final Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    @BindingAdapter(value = "view_visibleOrGone")
    public static void setViewVisibleOrGone(@NonNull final View view, final boolean visibility) {
        if (visibility) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    /**
     * @author soulkun
     * @time 2022/10/16 13:47
     * @description 设置加载本地文件的Bitmap
     */
    @BindingAdapter(value = {"iv_loadLocalBitmap_success", "iv_loadLocalBitmap_failure"})
    public static void loadImageViewLocalBitmap(@NonNull final ImageView imageView, @NonNull final String localBitmapPath, @NonNull final Drawable failureDrawable) {
        final File file = new File(localBitmapPath);
        if (!Objects.equals("", localBitmapPath) && file.exists()) {
            final Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(failureDrawable);
        }
    }

    @BindingAdapter(value = {"iv_loadBitmap_success", "iv_loadBitmap_failure"})
    public static void loadImageViewBitmap(@NonNull final ImageView imageView, final Bitmap bitmap, @NonNull final Drawable failureDrawable) {
        if (bitmap != null && !bitmap.isRecycled()) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageDrawable(failureDrawable);
        }
    }

    /**
     * @author soulkun
     * @time 2022/10/16 13:47
     * @description 设置长按监听器
     */
    @BindingAdapter(value = "view_onLongClick")
    public static void setViewLongClickListener(@NonNull final View view, @NonNull final View.OnLongClickListener onLongClickListener) {
        view.setOnLongClickListener(onLongClickListener);
    }

}