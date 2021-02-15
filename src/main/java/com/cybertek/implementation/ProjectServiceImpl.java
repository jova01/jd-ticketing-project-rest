package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.UserDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.UserMapper;
import com.cybertek.repository.ProjectRepository;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TaskService taskService;

    @Override
    public List<ProjectDTO> listAllProjectDetails() {
        String username= SecurityContextHolder.getContext().getAuthentication().getName();

        UserDTO userDTO = userService.findByUserName(username);
        User user = userMapper.convertToEntity(userDTO);
        List<Project> list = projectRepository.findAllByAssignedManager(user);

        return list.stream().map(each -> {
            ProjectDTO obj= projectMapper.convertToDto(each);
            obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(each.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(each.getProjectCode()));

            return  obj; }).collect(Collectors.toList());
    }

    @Autowired
    private UserRepository userRepository;


    @Override
    public ProjectDTO getProjectCode(String code) {
        return projectMapper.convertToDto(projectRepository.findByProjectCode(code));
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> list = projectRepository.findAll(Sort.by("projectCode"));
        return list.stream().map(obj -> projectMapper.convertToDto(obj)).collect(Collectors.toList());
    }

    @Override
    public void save(ProjectDTO dto) {
        dto.setProjectStatus(Status.OPEN);
        Project project= projectMapper.convertToEntity(dto);

        //assign manager before saving - else it throws transient exception
     //   project.setAssignedManager(userRepository.findByUserName(dto.getAssignedManager().getUserName()));

        projectRepository.save(project);
    }

    @Override
    public void update(ProjectDTO dto) {
        Project project= projectRepository.findByProjectCode(dto.getProjectCode());
        Project convertedProject= projectMapper.convertToEntity(dto);
        convertedProject.setId(project.getId());
        convertedProject.setProjectStatus(project.getProjectStatus());
        convertedProject.setAssignedManager(project.getAssignedManager());
        projectRepository.save(convertedProject);
    }

    @Override
    public void delete(String code) {
        Project project = projectRepository.findByProjectCode(code);
        project.setIsDeleted(true);

        project.setProjectCode(project.getProjectStatus() + "-" + project.getId());
        projectRepository.save(project);

        taskService.deleteByProject(projectMapper.convertToDto(project));
    }

    @Override
    public void complete(String projectCode) {
        Project project = projectRepository.findByProjectCode(projectCode);
        project.setProjectStatus(Status.COMPLETE);
        projectRepository.save(project);
    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list= projectRepository.findAllByAssignedManager(user);
        return list.stream().map(projectMapper::convertToDto).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream().map(projectMapper::convertToDto).collect(Collectors.toList());
    }
}
