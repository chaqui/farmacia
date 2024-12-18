package com.farmacia.exception;

import lombok.Getter;

@Getter
public class HttpException extends Exception
{
    private static final long serialVersionUID = 1L;

    private Integer code;

    public HttpException(String message) {
        super(message);
    }

    public HttpException(String message, Integer code) {
        super(message);
        this.code = code;
    }

}
