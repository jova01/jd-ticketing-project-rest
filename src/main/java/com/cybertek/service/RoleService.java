package com.cybertek.service;

import com.cybertek.dto.RoleDTO;
import com.cybertek.exception.TicketingProjectException;
import org.springframework.stereotype.Service;

import java.util.*;

public interface RoleService {

    List<RoleDTO> listAllRoles();
    RoleDTO findById(Long id) throws TicketingProjectException;

}
