package com.soulkun.mvvm.component;

import android.os.Binder;

/**
 * @author soulkun
 * @date 2022/8/19 19:52
 * @description Service管理Binder的Helper助手接口，抽象提取Service的绑定与解绑过程
 */
public interface IMvvmServiceBinderHelper {

    Binder bind();

    void unbind();

}
