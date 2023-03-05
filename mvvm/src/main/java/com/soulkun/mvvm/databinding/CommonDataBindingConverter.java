package com.soulkun.mvvm.databinding;

import androidx.annotation.NonNull;
import androidx.databinding.InverseMethod;

/**
 * @author soulkun
 * @date 2022/10/11 17:15
 * @description DataBinding格式转换器
 *  注意，转换器尽量不要用于{@link android.widget.EditText}等指定数字格式的双向绑定，因为用户输入可能会导致解析错误
 */
public class CommonDataBindingConverter {

    @InverseMethod("stringToInt")
    public static String intToString(int value) {
        return String.valueOf(value);
    }

    public static int stringToInt(@NonNull String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
