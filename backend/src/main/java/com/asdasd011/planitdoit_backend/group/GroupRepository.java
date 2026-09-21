package com.asdasd011.planitdoit_backend.group;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long>{
    List<Group> findByUserId(Long userId);
}