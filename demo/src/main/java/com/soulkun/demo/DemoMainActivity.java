package com.soulkun.demo;

import com.soulkun.mvvm.component.MvvmAbstractActivity;
import com.soulkun.mvvm.factory.MvvmDataBindingFactory;

public class DemoMainActivity extends MvvmAbstractActivity {

    @Override
    protected void initViewModel() {
    }

    @Override
    protected MvvmDataBindingFactory getDataBindingFactory() {
        return MvvmDataBindingFactory.create(R.layout.demo_activity_main);
    }

}