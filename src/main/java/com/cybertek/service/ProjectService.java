package com.cybertek.service;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.entity.User;

import java.util.*;

public interface ProjectService {

    ProjectDTO getProjectCode(String code);
    List<ProjectDTO> listAllProjects();
    void save(ProjectDTO dto);
    void update(ProjectDTO dto);
    void delete(String code);

    void complete(String projectCode);

    List<ProjectDTO> listAllProjectDetails();

    List<ProjectDTO> readAllByAssignedManager(User user);

    List<ProjectDTO> listAllNonCompletedProjects();

}
