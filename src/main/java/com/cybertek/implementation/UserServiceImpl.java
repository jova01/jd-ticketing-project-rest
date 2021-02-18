package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.User;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
import com.cybertek.mapper.RoleMapper;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    MapperUtil mapperUtil;

    @Autowired
    RoleMapper roleMapper;

    @Lazy
    @Autowired
    ProjectService projectService;

    @Autowired
    TaskService taskService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public List<UserDTO> listAllUsers() {
        List<User> list = userRepository.findAll(Sort.by("firstName"));
        return list.stream().map(obj -> mapperUtil.convertTo(obj, new UserDTO())).collect(Collectors.toList());
    }

    @Override
    public UserDTO findByUserName(String username) {
        return mapperUtil.convertTo(userRepository.findByUserName(username), new UserDTO());
    }

    @Override
    public void save(UserDTO dto) {
        User foundUser = userRepository.findByUserName(dto.getUserName());
        dto.setEnabled(true);

        User obj= mapperUtil.convertTo(dto, new User());
        obj.setPassWord(passwordEncoder.encode(obj.getPassWord()));

        userRepository.save(obj);
    }

    @Override
    public UserDTO update(UserDTO dto) {
        User user = userRepository.findByUserName(dto.getUserName());

        User convertedUser = mapperUtil.convertTo(dto, new User());
        convertedUser.setPassWord(passwordEncoder.encode(convertedUser.getPassWord()));
        convertedUser.setEnabled(true);
        convertedUser.setId(user.getId());

        userRepository.save(convertedUser);

        return findByUserName(dto.getUserName());
    }

    @Override
    public void deleteByUserName(String username) {
        System.out.println("DELETE " + username);
        userRepository.deleteByUserName(username);
    }

    @Override
    public void delete(String username) throws TicketingProjectException {
        User user = userRepository.findByUserName(username);

        if(user == null){
            throw new TicketingProjectException("User Does Not Exists");
        }

        if(!checkIfUserCanBeDeleted(user)){
            throw new TicketingProjectException("User can not be deleted. It is linked by a project on taks");
        }

        user.setUserName(user.getUserName() + "-" + user.getId());
        user.setIsDeleted(true);
        userRepository.save(user);
    }

    @Override
    public List<UserDTO> listAllByRole(String role) {
        List<User> users = userRepository.findAllByRoleDescriptionIgnoreCase(role);

        return users.stream().map(obj -> mapperUtil.convertTo(obj, new UserDTO())).collect(Collectors.toList());
    }

    @Override
    public Boolean checkIfUserCanBeDeleted(User user) {
        switch(user.getRole().getDescription()){
            case "Manager" :
                List<ProjectDTO> projectList= projectService.readAllByAssignedManager(user);
                return projectList.size()== 0;
            case "Employee":
                List<TaskDTO> taskList = taskService.readAllByEmployee(user);
                return taskList.size()==0;
            default:
                return true;
        }
    }
}
