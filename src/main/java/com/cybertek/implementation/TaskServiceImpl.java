package com.cybertek.implementation;

import com.cybertek.dto.ProjectDTO;
import com.cybertek.dto.TaskDTO;
import com.cybertek.entity.Project;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import com.cybertek.exception.TicketingProjectException;
import com.cybertek.mapper.MapperUtil;
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
    MapperUtil mapperUtil;

    @Autowired
    UserRepository userRepository;

    @Override
    public List<TaskDTO> listAllTaskByStatusIsNot(Status status) throws TicketingProjectException {

        String id = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findById(Long.parseLong(id)).orElseThrow(() -> new TicketingProjectException("User does not exist!"));

        List<Task> list = taskRepository.getAllByTaskStatusIsNotAndAssignedEmployee(status, user);

        return list.stream().map(each -> mapperUtil.convertTo(each, new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public TaskDTO findById(Long id) throws TicketingProjectException {
        Task task = taskRepository.findById(id).orElseThrow(() -> new TicketingProjectException("Task does not exist!"));

        return mapperUtil.convertTo(task, new TaskDTO());

    }

    @Override
    public int totalNonCompletedTasks(String projectCode) {
        return taskRepository.totalNonCompleteTasks(projectCode);
    }

    @Override
    public void deleteByProject(ProjectDTO project) {
        List<TaskDTO> tasks = listAllByProject(project);
        tasks.forEach(task -> {
            try {
                delete(task.getId());
            } catch (TicketingProjectException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public List<TaskDTO> listAllByProject(ProjectDTO project) {
        List<Task> list = taskRepository.findAllByProject(mapperUtil.convertTo(project, new Project()));
        return list.stream().map(each -> mapperUtil.convertTo(each, new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public int totalCompletedTasks(String projectCode) {
        return taskRepository.totalCompleteTasks(projectCode);
    }

    @Override
    public List<TaskDTO> listAllTasks() {
        List<Task> list = taskRepository.findAll();
        return taskRepository.findAll().stream().map(each -> mapperUtil.convertTo(each, new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public TaskDTO save(TaskDTO dto) {
        dto.setTaskStatus(Status.OPEN);
        dto.setAssignedDate(LocalDate.now());
        Task task = mapperUtil.convertTo(dto, new Task());

        Task save = taskRepository.save(task);

        return mapperUtil.convertTo(save, new TaskDTO());
    }

    @Override
    public TaskDTO update(TaskDTO dto) throws TicketingProjectException {
        taskRepository.findById(dto.getId()).orElseThrow(() -> new TicketingProjectException("Task does not exist!"));
        Task convertedTask = mapperUtil.convertTo(dto, new Task());
        Task save = taskRepository.save(convertedTask);

        return mapperUtil.convertTo(save, new TaskDTO());
    }

    @Override
    public void delete(Long id) throws TicketingProjectException {
        Task foundTask = taskRepository.findById(id).orElseThrow(()-> new TicketingProjectException("Task does not exist!"));
        foundTask.setIsDeleted(true);
        taskRepository.save(foundTask);
    }

    @Override
    public List<TaskDTO> listAllTasksByProjectManager() throws TicketingProjectException {


        String id = SecurityContextHolder.getContext().getAuthentication().getName();


        User user = userRepository.findById(Long.parseLong(id)).orElseThrow(()-> new TicketingProjectException("This user does not exist!"));

        List<Task> tasks = taskRepository.findAllByProjectAssignedManager(user);

        return tasks.stream().map(each -> mapperUtil.convertTo(each, new TaskDTO())).collect(Collectors.toList());
    }

    @Override
    public TaskDTO updateStatus(TaskDTO taskDTO) throws TicketingProjectException {
        Task task = taskRepository.findById(taskDTO.getId()).orElseThrow(()-> new TicketingProjectException("Task does not exist!"));
        task.setTaskStatus(taskDTO.getTaskStatus());
        Task save = taskRepository.save(task);
        return mapperUtil.convertTo(save, new TaskDTO());

    }

    @Override
    public List<TaskDTO> readAllByEmployee(User user) {
        List<Task> list = taskRepository.findAllByAssignedEmployee(user);
        return list.stream().map(each -> mapperUtil.convertTo(each, new TaskDTO())).collect(Collectors.toList());
    }

//    @Override
//    public List<TaskDTO> listAllTaskByStatus(Status status) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        User user = userRepository.findByUserName(username);
//
//        List<Task> list = taskRepository.findAllByTaskStatusAndAssignedEmployee(status, user);
//
//        return list.stream().map(each -> mapperUtil.convertTo(each, new TaskDTO())).collect(Collectors.toList());
//    }
}
