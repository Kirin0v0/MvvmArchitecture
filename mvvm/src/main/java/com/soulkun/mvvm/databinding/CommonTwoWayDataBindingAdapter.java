package com.soulkun.mvvm.databinding;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.databinding.BindingAdapter;
import androidx.databinding.InverseBindingAdapter;
import androidx.databinding.InverseBindingListener;

import java.util.Objects;

/**
 * @author soulkun
 * @date 2022/10/16 13:52
 * @description 双向绑定DataBinding适配器
 */
public class CommonTwoWayDataBindingAdapter {

    /*
     * CheckBox双向绑定
     */
    @BindingAdapter("cb_onChecked")
    public static void setCheckBoxChecked(@NonNull CheckBox checkBox, Boolean checked) {
        if (checked == null) {
            return;
        }

        if (checkBox.isChecked() != checked) {
            checkBox.setChecked(checked);
        }
    }

    @InverseBindingAdapter(attribute = "cb_onChecked", event = "cb_checkedAttrChanged")
    public static boolean isCheckBoxChecked(@NonNull CheckBox checkBox) {
        return checkBox.isChecked();
    }

    @BindingAdapter("cb_checkedAttrChanged")
    public static void setCheckBoxOnCheckedChangedListener(@NonNull CheckBox checkBox, @NonNull InverseBindingListener checkedChangedListener) {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                checkedChangedListener.onChange();
            }
        });
    }

    /*
     * Spinner双向绑定
     */
    @BindingAdapter("spinner_onSelected")
    public static void setSpinnerSelected(@NonNull Spinner spinner, Integer selectedIndex) {
        if (selectedIndex == null) {
            return;
        }

        if (spinner.getSelectedItemPosition() != selectedIndex) {
            spinner.setSelection(selectedIndex);
        }
    }

    @InverseBindingAdapter(attribute = "spinner_onSelected", event = "spinner_selectedAttrChanged")
    public static int getSpinnerSelected(@NonNull Spinner spinner) {
        return spinner.getSelectedItemPosition();
    }

    @BindingAdapter("spinner_selectedAttrChanged")
    public static void setSpinnerOnSelectedChangedListener(@NonNull Spinner spinner, @NonNull InverseBindingListener selectedChangedListener) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, final View view, final int position, final long id) {
                selectedChangedListener.onChange();
            }

            @Override
            public void onNothingSelected(final AdapterView<?> parent) {
            }
        });
    }

}
