package com.asdasd011.planitdoit_backend.group;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long>{
    List<Group> findByUserId(Long userId);
    List<Group> findByUserId(Long userId,Sort sort);
    Optional<Group> findByIdAndUserId(Long id,Long userId);
}