package com.asdasd011.planitdoit_backend.task;

public enum SolutionDifficulty{
    UNSET(0),
    EASY(1),
    MID(2),
    HARD(3);

    private final int code;

    SolutionDifficulty(int code){this.code=code;}
    public int getCode(){return code;}
}