package com.asdasd011.planitdoit_backend.task;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TimeDifficultyConverter implements AttributeConverter<TimeDifficulty,Integer>{
    @Override
    public Integer convertToDatabaseColumn(TimeDifficulty difficulty){
        if(difficulty==null) return null;
        return difficulty.getCode();
    }

    @Override
    public TimeDifficulty convertToEntityAttribute(Integer code){
        if(code==null) return null;
        for(TimeDifficulty difficulty:TimeDifficulty.values()){
            if(difficulty.getCode()==code) return difficulty;
        } throw new IllegalArgumentException("unknown time difficulty code: "+code);
    }
}