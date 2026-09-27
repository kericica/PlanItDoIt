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
        SELECT t.group.id,COUNT(t),SUM(CASE
                                        WHEN t.taskStatus=:completedStatus THEN 1
                                        ELSE 0
                                    END)
        FROM Task t
        WHERE t.group.user.id=:userId
        GROUP BY t.group.id
    """)
    List<Object[]> getCompletionCountsByGroupForUser(@Param("userId")Long userId,@Param("completedStatus")TaskStatus completedStatus);

    @Query("""
        SELECT COUNT(t),SUM(CASE
                                WHEN t.taskStatus=:completedStatus THEN 1
                                ELSE 0
                            END)
        FROM Task t
        WHERE t.group.id=:groupId
    """)
    Object[] getCompletionCountsForGroup(@Param("groupId") Long groupId,@Param("completedStatus") TaskStatus completedStatus);

    @Query("""
        SELECT t.timeDifficulty,COUNT(t),SUM(CASE
                                                WHEN t.taskStatus=:completedStatus THEN 1
                                                ELSE 0
                                            END)
        FROM Task t
        WHERE t.group.id=:groupId
        GROUP BY t.timeDifficulty
    """)
    List<Object[]> getCompletionCountsByTimeDifficulty(@Param("groupId") Long groupId,@Param("completedStatus")TaskStatus completedStatus);
}