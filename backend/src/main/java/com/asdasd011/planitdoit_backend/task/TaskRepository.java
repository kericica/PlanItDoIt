package com.asdasd011.planitdoit_backend.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task,Long>{
    List<Task> findByGroupId(Long groupId);
    Optional<Task> findByIdAndGroupId(Long id,Long groupId);
}