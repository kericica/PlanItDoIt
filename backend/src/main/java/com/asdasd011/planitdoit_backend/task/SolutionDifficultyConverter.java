package com.asdasd011.planitdoit_backend.task;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class SolutionDifficultyConverter implements AttributeConverter<SolutionDifficulty,Integer>{
    @Override
    public Integer convertToDatabaseColumn(SolutionDifficulty difficulty){
        if(difficulty==null) return null;
        return difficulty.getCode();
    }

    @Override
    public SolutionDifficulty convertToEntityAttribute(Integer code){
        if(code==null) return null;
        for(SolutionDifficulty difficulty:SolutionDifficulty.values()){
            if(difficulty.getCode()==code) return difficulty;
        }throw new IllegalArgumentException("Unknown solution difficulty code: "+code);
    }
}