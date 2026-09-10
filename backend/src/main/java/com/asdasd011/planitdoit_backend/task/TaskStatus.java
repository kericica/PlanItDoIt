package com.asdasd011.planitdoit_backend.task;

public enum TaskStatus{
    TODO(1),
    IN_PROGRESS(2),
    COMPLETED(3);

    private final int code;

    TaskStatus(int code){this.code=code;}
    public int getCode(){return code;}
}