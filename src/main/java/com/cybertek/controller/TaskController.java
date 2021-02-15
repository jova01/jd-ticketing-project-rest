package com.cybertek.controller;

import com.cybertek.dto.TaskDTO;
import com.cybertek.enums.Status;
import com.cybertek.service.ProjectService;
import com.cybertek.service.TaskService;
import com.cybertek.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;

@Controller
@RequestMapping("/task")
public class TaskController {

    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @Autowired
    TaskService taskService;

    @GetMapping("/create")
    public String createTask(Model model){
        model.addAttribute("task", new TaskDTO());
        model.addAttribute("projects", projectService.listAllNonCompletedProjects());
        model.addAttribute("employees",userService.listAllByRole("employee"));
        model.addAttribute("tasks", taskService.listAllTasks());
        return "task/create";
    }

    @PostMapping("/create")
    public String insertTask(TaskDTO task){
        taskService.save(task);
        return "redirect:/task/create";
    }

    @GetMapping("/delete/{id}")
    public String deleteTask(@PathVariable("id") Long id){
        taskService.delete(id);
        return "redirect:/task/create";
    }

    @GetMapping("/update/{id}")
    public String editTask(@PathVariable("id") Long id, Model model){
        model.addAttribute("task", taskService.findById(id));
        model.addAttribute("projects", projectService.listAllNonCompletedProjects());
        model.addAttribute("employees", userService.listAllByRole("employee"));
        model.addAttribute("tasks", taskService.listAllTasks());
        return "task/update";
    }

    @PostMapping("/update/{id}")
    public String updateTask(@PathVariable("id") Long id, TaskDTO task){
        taskService.update(task);
        return "redirect:/task/create";
    }

    @GetMapping("/employee")
    public String edit(Model model){
        List<TaskDTO> tasks= taskService.listAllTaskByStatusIsNot(Status.COMPLETE);
        model.addAttribute("tasks", tasks);

        return "task/employee-tasks";
    }

    @GetMapping("/employee/edit/{id}")
    public String employee_update(@PathVariable Long id, Model model){

        TaskDTO task= taskService.findById(id);
        List<TaskDTO> tasks= taskService.listAllTaskByStatusIsNot(Status.COMPLETE);


        model.addAttribute("task", task);
        model.addAttribute("users", userService.listAllByRole("employee"));
        model.addAttribute("projects",projectService.listAllNonCompletedProjects());
        model.addAttribute("tasks",tasks);
        model.addAttribute("statuses", Status.values());

        return "task/employee-update";
    }

    @PostMapping("/employee/update/{id}")
    public String employee_update(@PathVariable Long id, TaskDTO taskDTO){
        taskService.update(taskDTO);
        return "redirect:/task/employee";
    }

    @GetMapping("/employee/archive")
    public String employee_archive(Model model){

        List<TaskDTO> tasks=taskService.listAllTaskByStatus(Status.COMPLETE);
        model.addAttribute("tasks", tasks);

        return "task/employee-archive";
    }
}
