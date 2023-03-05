package com.soulkun.mvvm.component;

import android.os.Binder;

/**
 * @author soulkun
 * @date 2022/8/19 20:03
 * @description 组合模式组合Binder需要的功能以及Service需要的管理助手接口
 */
public abstract class MvvmAbstractServiceBinder extends Binder implements IMvvmServiceBinderHelper {

    @Override
    public Binder bind() {
        return this;
    }

}
