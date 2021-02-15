package com.cybertek.repository;

import com.cybertek.entity.Project;
import com.cybertek.entity.Task;
import com.cybertek.entity.User;
import com.cybertek.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("select count(t) from Task t where t.project.projectCode =:projectCode and t.taskStatus <> 'COMPLETE'")
    int totalNonCompleteTasks(String projectCode);

    int countByProjectProjectCodeAndTaskStatus(String projectCode, Status status);


    @Query(value = "select count(*) from tasks t " +
            " join projects p on t.project_id = p.id " +
            " where p.project_code = ?1 and t.task_status = 'COMPLETE'", nativeQuery = true)
    int totalCompleteTasks(String projectCode);

    List<Task> findAllByProject(Project project);

    List<Task> getAllByTaskStatusIsNotAndAssignedEmployee(Status status, User user);

    List<Task> findAllByProjectAssignedManager(User manager);

    List<Task> findAllByTaskStatusAndAssignedEmployee(Status status, User user);

    List<Task> findAllByAssignedEmployee(User user);
}
