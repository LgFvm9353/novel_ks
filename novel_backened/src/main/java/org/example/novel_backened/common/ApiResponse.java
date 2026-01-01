package org.example.novel_backened.common;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setCode(0);
        res.setMessage("ok");
        res.setData(data);
        return res;
    }

    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> res = new ApiResponse<>();
        res.setCode(1);
        res.setMessage(message);
        res.setData(null);
        return res;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

