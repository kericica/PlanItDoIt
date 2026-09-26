package com.asdasd011.planitdoit_backend.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long>{
    List<Task> findByGroupId(Long groupId);
    List<Task> findByGroupId(Long groupId,Sort sort);
    Optional<Task> findByIdAndGroupId(Long id,Long groupId);

    @Query("""
            SELECT t.group.id,COUNT(t)
            FROM Task t
            WHERE t.group.user.id=:userId AND t.taskStatus<>com.asdasd011.planitdoit_backend.task.TaskStatus.COMPLETED
            GROUP BY t.group.id
            """)
    List<Object[]> countIncompleteTasksByGroupForUser(@Param("userId")Long userId);
}