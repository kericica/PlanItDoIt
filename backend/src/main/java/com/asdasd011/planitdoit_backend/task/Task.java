package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.group.Group;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name="tasks")
public class Task{
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String note;
    private SolutionDifficulty solutionDifficulty;
    private TimeDifficulty timeDifficulty;
    private Boolean highlighted;
    private LocalDate deadline;
    private TaskStatus taskStatus;
    private Instant createdAt;
    private Instant completedAt;

    @ManyToOne
    @JoinColumn(name="group_id",nullable=false)
    private Group group;

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}

    public String getNote(){return note;}
    public void setNote(String note){this.note=note;}

    public SolutionDifficulty getSolutionDifficulty(){return solutionDifficulty;}
    public void setSolutionDifficulty(SolutionDifficulty solutionDifficulty){this.solutionDifficulty=solutionDifficulty;}

    public TimeDifficulty getTimeDifficulty(){return timeDifficulty;}
    public void setTimeDifficulty(TimeDifficulty timeDifficulty){this.timeDifficulty=timeDifficulty;}

    public Boolean isHighlighted(){return highlighted;}
    public void setHighlighted(Boolean highlighted){this.highlighted=highlighted;}

    public LocalDate getDeadline(){return deadline;}
    public void setDeadlinde(LocalDate deadline){this.deadline=deadline;}

    public TaskStatus getTaskStatus(){return taskStatus;}
    public void setTaskStatus(TaskStatus taskStatus){this.taskStatus=taskStatus;}

    public Instant getCreatedAt(){return createdAt;}
    public void setCreatedAt(Instant createdAt){this.createdAt=createdAt;}

    public Instant getCompletedAt(){return completedAt;}
    public void setCompletedAt(Instant completedAt){this.completedAt=completedAt;}

    public Group getGroup(){return group;}
    public void setGroup(Group group){this.group=group;}
}