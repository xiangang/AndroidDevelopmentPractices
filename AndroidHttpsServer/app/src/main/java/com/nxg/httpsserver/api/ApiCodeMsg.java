package com.nxg.httpsserver.api;

public class ApiCodeMsg {

    private final int code;
    private final String msg;

    public ApiCodeMsg(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "ApiCodeMsg{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    /**
     * 返回码
     */
    static class CODE {
        //成功
        static final int REQUEST_SUCCESS = 200;
        static final int REQUEST_ERROR_CONTEXT_IS_NULL = 201;
        static final int REQUEST_ERROR_404 = 404;
        static final int REQUEST_ERROR_400 = 400;


    }

    /**
     * 描述信息
     */
    static class MSG {

        //成功
        static final String REQUEST_SUCCESS = "request success!";
        static final String REQUEST_ERROR_CONTEXT_IS_NULL = "HttpServer init error, context is null!";
        static final String REQUEST_ERROR_404 = "No this url！";
        static final String REQUEST_ERROR_400 = "请求报文语法错误或参数错误";


    }

    //通用的异常
    public static ApiCodeMsg REQUEST_ERROR_CONTEXT_IS_NULL = new ApiCodeMsg(CODE.REQUEST_ERROR_CONTEXT_IS_NULL, MSG.REQUEST_ERROR_CONTEXT_IS_NULL);
    public static ApiCodeMsg REQUEST_ERROR_404 = new ApiCodeMsg(CODE.REQUEST_ERROR_404, MSG.REQUEST_ERROR_404);
    public static ApiCodeMsg REQUEST_ERROR_400 = new ApiCodeMsg(CODE.REQUEST_ERROR_400, MSG.REQUEST_ERROR_400);
}
