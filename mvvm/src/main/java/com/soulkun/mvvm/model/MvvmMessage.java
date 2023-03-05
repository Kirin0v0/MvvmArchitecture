package com.soulkun.mvvm.model;

/**
 * @author soulkun
 * @time 2022/8/22 17:47
 * @description MVVM框架下的Message 消息数据类
 * 使用ID区分消息类别，允许携带一种数据类型，只读不改
 * 仅用于 MessageViewModel 消息总线，不得用于 State 和 Request
 */
public class MvvmMessage<D> {

    protected final int id;

    protected final D data;

    public MvvmMessage(final int id) {
        this.id = id;
        this.data = null;
    }

    public MvvmMessage(final int id, final D data) {
        this.id = id;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public D getData() {
        return data;
    }

}
