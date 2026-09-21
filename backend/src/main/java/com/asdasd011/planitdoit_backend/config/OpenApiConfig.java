package com.asdasd011.planitdoit_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info=@Info(
        title="PlanItDoIt API",
        version="v1",
        description="REST API for PlanItDoIt learning support application."
    )
)
public class OpenApiConfig{
    
}