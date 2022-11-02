package com.example.rest.common;

public class CommonException extends Exception{
    private int code;
    private String message;
    public CommonException(int code,String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}
