package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.util.MapperUtil;
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
    private UserRepository userRepository;

    @Autowired
    MapperUtil mapperUtil;

    @Autowired
    UserService userService;

    @Autowired
    private TaskService taskService;

    @Override
    public List<ProjectDTO> listAllProjectDetails() throws TicketingProjectException {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Long currentId= Long.parseLong(id);

        User user = userRepository.findById(currentId).orElseThrow(()-> new TicketingProjectException("This manager does not exist!"));
        List<Project> list = projectRepository.findAllByAssignedManager(user);

        if(list.size() == 0){
            throw  new TicketingProjectException("This manager does not have any project assigned!");
        }

        return list.stream().map(each -> {
            ProjectDTO obj= mapperUtil.convertTo(each, new ProjectDTO());
            obj.setUnfinishedTaskCounts(taskService.totalNonCompletedTasks(each.getProjectCode()));
            obj.setCompleteTaskCounts(taskService.totalCompletedTasks(each.getProjectCode()));

            return  obj; }).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO getProjectCode(String code) {
        return mapperUtil.convertTo(projectRepository.findByProjectCode(code),new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> listAllProjects() {
        List<Project> list = projectRepository.findAll(Sort.by("projectCode"));
        return list.stream().map(obj -> mapperUtil.convertTo(obj, new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public ProjectDTO save(ProjectDTO dto) throws TicketingProjectException {
       // dto.setProjectStatus(Status.OPEN);


        Project project= projectRepository.findByProjectCode(dto.getProjectCode());

        if(project != null){
            throw  new TicketingProjectException("Project with this code already existing!");
        }

        Project createdProject = projectRepository.save(mapperUtil.convertTo(dto, new Project()));



        //assign manager before saving - else it throws transient exception
     //   project.setAssignedManager(userRepository.findByUserName(dto.getAssignedManager().getUserName()));

       return mapperUtil.convertTo(projectRepository.save(createdProject), new ProjectDTO());
    }

    @Override
    public ProjectDTO update(ProjectDTO dto) throws TicketingProjectException {
        Project project= projectRepository.findByProjectCode(dto.getProjectCode());

        if(project == null){
            throw new TicketingProjectException("Project does not exist!");
        }

        Project convertedProject= mapperUtil.convertTo(dto, new Project());

        Project updatedProject = projectRepository.save(convertedProject);

        return mapperUtil.convertTo(updatedProject, new ProjectDTO());
    }

    @Override
    public void delete(String code) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(code);

        if(project == null){
            throw new TicketingProjectException("Project does not exist!");
        }


        project.setIsDeleted(true);

        project.setProjectCode(project.getProjectStatus() + "-" + project.getId());
        projectRepository.save(project);

        taskService.deleteByProject(mapperUtil.convertTo(project, new ProjectDTO()));
    }

    @Override
    public ProjectDTO complete(String projectCode) throws TicketingProjectException {
        Project project = projectRepository.findByProjectCode(projectCode);

        if(project == null){
            throw new TicketingProjectException("Project does not exist!");
        }

        project.setProjectStatus(Status.COMPLETE);
        return mapperUtil.convertTo(projectRepository.save(project), new ProjectDTO());
    }

    @Override
    public List<ProjectDTO> readAllByAssignedManager(User user) {
        List<Project> list= projectRepository.findAllByAssignedManager(user);
        return list.stream().map(each -> mapperUtil.convertTo(each, new ProjectDTO())).collect(Collectors.toList());
    }

    @Override
    public List<ProjectDTO> listAllNonCompletedProjects() {

        return projectRepository.findAllByProjectStatusIsNot(Status.COMPLETE)
                .stream().map(each -> mapperUtil.convertTo(each, new ProjectDTO())).collect(Collectors.toList());
    }
}
