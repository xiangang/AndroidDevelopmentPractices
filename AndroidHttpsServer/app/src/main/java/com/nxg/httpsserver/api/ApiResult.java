package com.nxg.httpsserver.api;

/**
 * 封装Api方法json返回值
 * @param <T>
 */
public class ApiResult<T> {

    private int code;
    private String msg;
    private String message;
    private T data;

    public ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ApiResult(int code, String msg, String message, T data) {
        this.code = code;
        this.msg = msg;
        this.message = message;
        this.data = data;
    }

    private ApiResult(T data) {
        this.code = ApiCodeMsg.CODE.REQUEST_SUCCESS;
        this.msg = ApiCodeMsg.MSG.REQUEST_SUCCESS;
        this.data = data;
    }

    private ApiResult(ApiCodeMsg mg) {
        if (mg == null) {
            return;
        }
        this.code = mg.getCode();
        this.msg = mg.getMsg();
    }


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    /**
     * 成功
     *
     * @param <T> data
     * @return data
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(data);
    }

    /**
     * 失败
     *
     * @param <T> data
     * @return empty
     */
    public static <T> ApiResult<T> fail(ApiCodeMsg mg) {
        return new ApiResult<>(mg);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}


