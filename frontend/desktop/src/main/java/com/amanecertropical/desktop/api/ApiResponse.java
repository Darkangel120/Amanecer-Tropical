package com.amanecertropical.desktop.api;

public class ApiResponse<T> {

    private boolean success;
    private int statusCode;
    private T data;
    private String errorMessage;

    public ApiResponse() {}

    public ApiResponse(boolean success, int statusCode, T data, String errorMessage) {
        this.success = success;
        this.statusCode = statusCode;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ApiResponse{" +
                "success=" + success +
                ", statusCode=" + statusCode +
                ", data=" + data +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
