package com.cybertek.implementation;

import com.cybertek.dto.RoleDTO;
import com.cybertek.entity.Role;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.util.MapperUtil;
import com.cybertek.repository.RoleRepository;
import com.cybertek.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private MapperUtil mapperUtil;


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
        return list.stream().map(obj -> mapperUtil.convertTo(obj, new RoleDTO())).collect(Collectors.toList());
    }

    @Override
    public RoleDTO findById(Long id) throws TicketingProjectException {
        Role role = roleRepository.findById(id).orElseThrow(()->new TicketingProjectException("Role does not exist!"));
        return mapperUtil.convertTo(role, new RoleDTO());
    }
}
