package com.xxxx.app.net;

/*
*@Description: 自定义异常封装
*@Author: hl
*@Time: 2018/9/29 15:38
*/
public class ApiException extends RuntimeException{
    private String message;
    private int erroCode;

    public ApiException(int resultCode) {
        getApiExceptionMessage(resultCode, "");
    }

    public ApiException(int resultCode, String detailMessage) {
        getApiExceptionMessage(resultCode, detailMessage);
    }

    @Override
    public String getMessage() {
        return message;
    }

    public int getErroCode() {
        return erroCode;
    }

    /**
     * 由于服务器传递过来的错误信息直接给用户看的话，用户未必能够理解
     * 需要根据错误码对错误信息进行一个转换，在显示给用户
     * @param code
     * @return
     */
    private void getApiExceptionMessage(int code, String _message){
        erroCode = code;
        switch (code) {
            case ExceptionHandle.ERROR.TOKEN:
                message = "Token过期";
                break;
            case ExceptionHandle.ERROR.REQUEST_ERROR:
                message = "请求错误";
                if (null != _message && !_message.equals("")){
                    message = _message;
                    ///< 特殊处理
                    if (message.contains("limittimes")){
                        message = "次数过多限制!";
                    }
                }
                break;
            case ExceptionHandle.ERROR.NO_NETWORK:
                message = "网络未连接";
                break;
            case ExceptionHandle.ERROR.THIRD_BIND:
                message = "需要绑定";
                break;
            default:
                message = "未知错误";
        }
    }
}
