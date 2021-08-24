package com.zhs.pdf2image.model;


import lombok.Getter;

import java.io.Serializable;

@Getter
public class Result<T> implements Serializable {

    private static final long serialVersionUID = -5876274825677709598L;

    private int code;

    private T data;

    private String desc;


    public static <T> Result<T> fail(int code, String desc, T flag) {
        Result<T> result = new Result<>();
        result.code = code;
        result.desc = desc;
        result.data = flag;
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<>();
        result.desc = "success";
        result.data = object;
        result.code = 0;
        return result;
    }


}
