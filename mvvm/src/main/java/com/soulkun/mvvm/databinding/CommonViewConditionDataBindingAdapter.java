package com.soulkun.mvvm.databinding;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.databinding.BindingAdapter;

/**
 * @author soulkun
 * @time 2022/8/19 8:33
 * @description DataBinding用于代替三元表达式的属性，每个方法使用时必须在一个节点控件xml下赋予全部属性才能使用
 */
public class CommonViewConditionDataBindingAdapter {

    @BindingAdapter(value = {"tv_text_condition", "tv_text_conditionTrue", "tv_text_conditionFalse"}, requireAll = true)
    public static void setTextColorByCondition(@NonNull final TextView textView, @NonNull final Boolean condition, @NonNull final String trueText, @NonNull final String falseText) {
        if (condition) {
            textView.setText(trueText);
        } else {
            textView.setText(falseText);
        }
    }

    @BindingAdapter(value = {"tv_textColor_condition", "tv_textColor_conditionTrue", "tv_textColor_conditionFalse"}, requireAll = false)
    public static void setTextColorByCondition(@NonNull final TextView textView, @NonNull final Boolean condition, @NonNull final Integer trueColor, @NonNull final Integer falseColor) {
        if (condition) {
            textView.setTextColor(trueColor);
        } else {
            textView.setTextColor(falseColor);
        }
    }

    @BindingAdapter(value = {"tv_textSize_condition", "tv_textSize_conditionTrue", "tv_textSize_conditionFalse"}, requireAll = true)
    public static void setTextColorByCondition(@NonNull final TextView textView, @NonNull final Boolean condition, @NonNull final Float trueSize, @NonNull final Float falseSize) {
        if (condition) {
            textView.setTextSize(trueSize);
        } else {
            textView.setTextSize(falseSize);
        }
    }

    @BindingAdapter(value = {"view_background_condition", "view_background_conditionTrue", "view_background_conditionFalse"}, requireAll = true)
    public static void setBackgroundDrawableByCondition(@NonNull final View view, @NonNull final Boolean condition, @NonNull final Drawable trueDrawable, @NonNull final Drawable falseDrawable) {
        if (condition) {
            view.setBackground(trueDrawable);
        } else {
            view.setBackground(falseDrawable);
        }
    }

    @BindingAdapter(value = {"view_backgroundColor_condition", "view_backgroundColor_conditionTrue", "view_backgroundColor_conditionFalse"}, requireAll = true)
    public static void setBackgroundColorByCondition(@NonNull final View view, @NonNull final Boolean condition, @NonNull final Integer trueColor, @NonNull final Integer falseColor) {
        if (condition) {
            view.setBackgroundColor(trueColor);
        } else {
            view.setBackgroundColor(falseColor);
        }
    }

    @BindingAdapter(value = {"iv_src_condition", "iv_src_conditionTrue", "iv_src_conditionFalse"}, requireAll = true)
    public static void setSrcByCondition(@NonNull final ImageView imageView, @NonNull final Boolean condition, @NonNull final Drawable trueDrawable, @NonNull final Drawable falseDrawable) {
        if (condition) {
            imageView.setImageDrawable(trueDrawable);
        } else {
            imageView.setImageDrawable(falseDrawable);
        }
    }

    @BindingAdapter(value = {"cv_cardBackgroundColor_condition", "cv_cardBackgroundColor_conditionTrue", "cv_cardBackgroundColor_conditionFalse"}, requireAll = true)
    public static void setCardBackgroundColorByCondition(@NonNull final CardView cardView, @NonNull final Boolean condition, @NonNull final Integer trueColor, @NonNull final Integer falseColor) {
        if (condition) {
            cardView.setCardBackgroundColor(trueColor);
        }else {
            cardView.setCardBackgroundColor(falseColor);
        }
    }
    
    
}
