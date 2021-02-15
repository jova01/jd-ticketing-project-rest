package com.cybertek.controller;

import com.cybertek.dto.ProjectDTO;

import com.cybertek.service.ProjectService;
import com.cybertek.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.*;

@Controller
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;


    @GetMapping("/create")
    public String createProject(Model model) {

        model.addAttribute("project", new ProjectDTO());
        model.addAttribute("projects", projectService.listAllProjects());
        model.addAttribute("managers", userService.listAllByRole("manager"));

        return "/project/create";
    }

    @PostMapping("/create")
    public String insertProject(@ModelAttribute("project") ProjectDTO projectDTO) {
        toString(projectDTO);
        projectService.save(projectDTO);
        return "redirect:/project/create";
    }

    @GetMapping("/delete/{projectcode}")
    public String deleteProject(@PathVariable String projectcode) {
        projectService.delete(projectcode);
        return "redirect:/project/create";
    }

    @GetMapping("/complete/{projectcode}")
    public String completeProject(@PathVariable String projectcode) {
        projectService.complete(projectcode);
        return "redirect:/project/create";
    }

    @GetMapping("/update/{projectcode}")
    public String editProject(@PathVariable String projectcode, Model model) {

        model.addAttribute("project", projectService.getProjectCode(projectcode));
        model.addAttribute("projects", projectService.listAllProjects());
        model.addAttribute("managers", userService.listAllByRole("manager"));

        return "/project/update";
    }

    @PostMapping("/update/{projectcode}")
    public String updateProject(@PathVariable String projectcode, ProjectDTO project) {
        projectService.update(project);
        return "redirect:/project/create";
    }

    @GetMapping("/manager/complete/{projectCode}")
    public String getProjectByManager(@PathVariable String projectCode) {

        projectService.complete(projectCode);

        return "redirect:/project/manager/complete";
    }

    @GetMapping("/manager/complete")
    public String getProjectByManager(Model model) {
        List<ProjectDTO> projects = projectService.listAllProjectDetails();


        model.addAttribute("projects", projects);
        return "/manager/project-status";
    }

    public void toString(ProjectDTO projectDTO) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            System.out.println(mapper.writeValueAsString(projectDTO));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
