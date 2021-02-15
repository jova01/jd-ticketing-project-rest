package com.cybertek.mapper;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.User;
import com.cybertek.repository.ProjectRepository;
import com.cybertek.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProjectMapper {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;


    public Project convertToEntity(ProjectDTO dto){
        //User manager = userRepository.findByUserName(dto.getAssignedManager().getUserName());
        Project project = modelMapper.map(dto, Project.class);
       // project.setAssignedManager(manager);
        return project;
    }

    public ProjectDTO convertToDto(Project entity){
        return modelMapper.map(entity, ProjectDTO.class);
    }

}
