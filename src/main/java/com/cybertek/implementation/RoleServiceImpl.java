package com.cybertek.implementation;

import com.cybertek.dto.RoleDTO;
import com.cybertek.entity.Role;
import com.cybertek.mapper.RoleMapper;
import com.cybertek.repository.RoleRepository;
import com.cybertek.service.RoleService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private RoleMapper roleMapper;


    @Override
    public List<RoleDTO> listAllRoles() {

        List<Role> list= roleRepository.findAll();


//        ObjectMapper mapper =new ObjectMapper();
//        // we need to get only required fields for this conversion
//        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//        List<RoleDTO> role1= mapper.convertValue(list, mapper.getTypeFactory().constructCollectionType(List.class, RoleDTO.class));
//        List<RoleDTO> role2= mapper.convertValue(list, new TypeReference<>(){});
//        return role1;

        // convert to DTO and return it
        return list.stream().map(obj -> roleMapper.convertToDto(obj)).collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) {
        return roleMapper.convertToDto(roleRepository.getOne(id));
    }
}
