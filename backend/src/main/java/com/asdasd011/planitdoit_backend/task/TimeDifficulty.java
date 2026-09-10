package com.asdasd011.planitdoit_backend.task;

public enum TimeDifficulty{
    FLASH(1),
    MID(2),
    LOT(3);

    private final int code;

    TimeDifficulty(int code){this.code=code;}
    public int getCode(){return code;}
}