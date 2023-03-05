package com.soulkun.mvvm.model;

/**
 * @author soulkun
 * @time 2022/8/22 17:48
 * @description MVVM框架下的Request事件类
 * 用于RequestViewModel请求返回数据
 */
public class MvvmRequest<T> {

    private final Status status;

    private final T response;

    /**
     * @author soulkun
     * @time 2022/10/14 16:33
     * @description 传入结果数据，默认状态为网络请求并成功
     */
    public MvvmRequest(T response) {
        status = new Status();
        this.response = response;
    }

    /**
     * @author soulkun
     * @time 2022/10/14 16:34
     * @description 传入结果来源和数据
     */
    public MvvmRequest(Source source, T response) {
        this.status = new Status(source);
        this.response = response;
    }

    /**
     * @author soulkun
     * @time 2022/10/14 16:34
     * @description 传入结果和数据
     */
    public MvvmRequest(boolean success, Source source, T response) {
        this.status = new Status(success, source);
        this.response = response;
    }

    /**
     * @author soulkun
     * @time 2022/10/14 16:34
     * @description 传入结果状态和数据
     */
    public MvvmRequest(Status status, T response) {
        this.status = status;
        this.response = response;
    }

    public boolean isSuccess() {
        return status != null && status.isSuccess();
    }

    public Status getStatus() {
        return status;
    }

    public T getResponse() {
        return response;
    }

    public interface IResultCallback<T> {
        void onResponse(MvvmRequest<T> mvvmRequest);

        void onCancel();
    }

    /**
     * @author soulkun
     * @time 2022/10/14 16:33
     * @description 请求结果状态
     */
    public static class Status {

        private final String code;
        private final boolean success;
        private final Enum<Source> source;

        /**
         * @author soulkun
         * @time 2022/10/14 16:34
         * @description 默认成功和网络请求
         */
        public Status() {
            this("", true, Source.NETWORK);
        }

        /**
         * @author soulkun
         * @time 2022/10/14 16:34
         * @description 默认成功
         */
        public Status(Source source) {
            this("", true, source);
        }

        public Status(boolean success, Source source) {
            this("", success, source);
        }

        public Status(String code, boolean success, Source source) {
            this.code = code;
            this.success = success;
            this.source = source;
        }

        public String getCode() {
            return code;
        }

        public boolean isSuccess() {
            return success;
        }

        public Enum<Source> getSource() {
            return source;
        }
    }

    public enum Source {
        NETWORK, DATABASE, LOCAL
    }

}
