package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.mapper.ProjectMapper;
import com.cybertek.mapper.TaskMapper;
import com.cybertek.repository.TaskRepository;
import com.cybertek.repository.UserRepository;
import com.cybertek.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    ProjectMapper projectMapper;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<TaskDTO> listAllTaskByStatusIsNot(Status status) {

        String username= SecurityContextHolder.getContext().getAuthentication().getName();

        User user= userRepository.findByUserName(username);

        List<Task> list= taskRepository.getAllByTaskStatusIsNotAndAssignedEmployee(status, user);

        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public TaskDTO findById(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) {
            return taskMapper.convertToDTO(task.get());
        }
        return null;
    }

    @Override
    public int totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompleteTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO project) {
        List<TaskDTO> tasks = listAllByProject(project);
        tasks.forEach(task -> delete(task.getId()));
    }

    @Override
    public List<TaskDTO> listAllByProject(ProjectDTO project) {
        List<Task> list= taskRepository.findAllByProject(projectMapper.convertToEntity(project));
        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public int totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompleteTasks(projectCode);
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        return taskRepository.findAll().stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public Task save(TaskDTO dto) {
        dto.setTaskStatus(Status.OPEN);
        dto.setAssignedDate(LocalDate.now());
        Task task = taskMapper.convertToEntity(dto);
        return taskRepository.save(task);
    }

    @Override
    public void update(TaskDTO dto) {
        Optional<Task> task = taskRepository.findById(dto.getId());
        Task convertedTask = taskMapper.convertToEntity(dto);
        if (task.isPresent()) {
            convertedTask.setId(task.get().getId());
            convertedTask.setTaskStatus(task.get().getTaskStatus());
            convertedTask.setAssignedDate(task.get().getAssignedDate());
            taskRepository.save(convertedTask);
        }
    }

    @Override
    public void delete(Long id) {
        Optional<Task> foundTask = taskRepository.findById(id);
        if (foundTask.isPresent()) {
            foundTask.get().setIsDeleted(true);
            taskRepository.save(foundTask.get());
        }

    }

    @Override
    public List<TaskDTO> listAllTasksByProjectManager() {
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        User user= userRepository.findByUserName(username);

        List<Task> tasks= taskRepository.findAllByProjectAssignedManager(user);

        return tasks.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public void updateStatus(TaskDTO taskDTO) {
       Optional<Task> task= taskRepository.findById(taskDTO.getId());

       if(task.isPresent()){
           task.get().setTaskStatus(taskDTO.getTaskStatus());
           taskRepository.save(task.get());
       }
    }

    @Override
    public List<TaskDTO> listAllTaskByStatus(Status status) {
        String username= SecurityContextHolder.getContext().getAuthentication().getName();
        User user= userRepository.findByUserName(username);

        List<Task> list= taskRepository.findAllByTaskStatusAndAssignedEmployee(status, user);

        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public List<TaskDTO> readAllByEmployee(User user) {
        List<Task> list= taskRepository.findAllByAssignedEmployee(user);
        return list.stream().map(taskMapper::convertToDTO).collect(Collectors.toList());
    }
}
