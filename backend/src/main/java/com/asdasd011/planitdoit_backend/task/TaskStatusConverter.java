package com.asdasd011.planitdoit_backend.task;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class TaskStatusConverter implements AttributeConverter<TaskStatus,Integer>{
    @Override
    public Integer convertToDatabaseColumn(TaskStatus status){
        if(status==null) return null;
        return status.getCode();
    }

    @Override
    public TaskStatus convertToEntityAttribute(Integer code){
        if(code==null) return null;
        for(TaskStatus status:TaskStatus.values()){
            if(status.getCode()==code) return status;
        }throw new IllegalArgumentException("unknown task status code: "+code);
    }
}