package com.cybertek.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;

@Component
public class MapperUtil {

    @Autowired
    private ModelMapper modelMapper;

    public <T> T convertTo(Object object, T convertObject){
        return modelMapper.map(object, (Type) convertObject.getClass());
    }

}
