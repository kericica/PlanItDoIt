package com.asdasd011.planitdoit_backend.task;

import com.asdasd011.planitdoit_backend.exception.ResourceNotFoundException;
import com.asdasd011.planitdoit_backend.task.dto.CreateTaskRequest;
import com.asdasd011.planitdoit_backend.task.dto.TaskResponse;
import com.asdasd011.planitdoit_backend.task.dto.UpdateTaskRequest;
import com.asdasd011.planitdoit_backend.sort.SortDirection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.time.Instant;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @Test
    void shouldReturnTasksForGroup()throws Exception{
        when(taskService.getTasksForGroup(1L,10L,TaskSort.TITLE,SortDirection.ASC)).thenReturn(List.of(new TaskResponse(100L,"TaskTitle0","exercises 1-10",
            SolutionDifficulty.MID,TimeDifficulty.MID,true,null,TaskStatus.TODO,null,null,10L)));

        mockMvc.perform(get("/api/groups/10/tasks")).andExpect(status().isOk()).andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(100)).andExpect(jsonPath("$[0].title").value("TaskTitle0")).andExpect(jsonPath("$[0].note").value("exercises 1-10"))
            .andExpect(jsonPath("$[0].solutionDifficulty").value("MID")).andExpect(jsonPath("$[0].timeDifficulty").value("MID")).andExpect(jsonPath("$[0].highlighted").value(true))
            .andExpect(jsonPath("$[0].taskStatus").value("TODO")).andExpect(jsonPath("$[0].groupId").value(10));
    }

    @Test
    void shouldReturnOneTask()throws Exception{
        when(taskService.getTaskForGroup(1L,10L,100L)).thenReturn(new TaskResponse(100L,"TaskTitle0","exercises 1-10",SolutionDifficulty.MID,TimeDifficulty.MID,
            true,null,TaskStatus.TODO,null,null,10L));

        mockMvc.perform(get("/api/groups/10/tasks/100")).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(100)).andExpect(jsonPath("$.title").value("TaskTitle0"))
            .andExpect(jsonPath("$.note").value("exercises 1-10")).andExpect(jsonPath("$.solutionDifficulty").value("MID")).andExpect(jsonPath("$.timeDifficulty").value("MID"))
            .andExpect(jsonPath("$.highlighted").value(true)).andExpect(jsonPath("$.taskStatus").value("TODO")).andExpect(jsonPath("$.groupId").value(10));
    }

    @Test
    void shouldCreateTask()throws Exception{
        when(taskService.createTask(1L,10L,new CreateTaskRequest("TaskTitle0","exercises 1-10",SolutionDifficulty.MID,TimeDifficulty.MID,true,null,
            TaskStatus.TODO,10L))).thenReturn(new TaskResponse(100L,"TaskTitle0","exercises 1-10",SolutionDifficulty.MID,TimeDifficulty.MID,true,null,
            TaskStatus.TODO,null,null,10L));

        mockMvc.perform(post("/api/groups/10/tasks").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "title": "TaskTitle0",
                "note": "exercises 1-10",
                "solutionDifficulty": "MID",
                "timeDifficulty": "MID",
                "highlighted": true,
                "taskStatus": "TODO",
                "groupId": 10
            }
            """)).andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(100)).andExpect(jsonPath("$.title").value("TaskTitle0")).andExpect(jsonPath("$.groupId").value(10));
    }

    @Test
    void shouldUpdateTask()throws Exception{
        when(taskService.updateTask(1L,10L,100L,new UpdateTaskRequest("TaskTitle1","exercises 11-20",SolutionDifficulty.HARD,TimeDifficulty.LOT,false,null,
            TaskStatus.IN_PROGRESS,10L))).thenReturn(new TaskResponse(100L,"TaskTitle1","exercises 11-20",SolutionDifficulty.HARD,TimeDifficulty.LOT,false,null,
            TaskStatus.IN_PROGRESS,null,null,10L));

        mockMvc.perform(put("/api/groups/10/tasks/100").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "title": "TaskTitle1",
                "note": "exercises 11-20",
                "solutionDifficulty": "HARD",
                "timeDifficulty": "LOT",
                "highlighted": false,
                "taskStatus": "IN_PROGRESS",
                "groupId": 10
            }
            """)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(100)).andExpect(jsonPath("$.title").value("TaskTitle1")).andExpect(jsonPath("$.solutionDifficulty").value("HARD"))
            .andExpect(jsonPath("$.taskStatus").value("IN_PROGRESS")).andExpect(jsonPath("$.groupId").value(10));
    }

    @Test
    void shouldMoveTaskToAnotherGroup()throws Exception{
        when(taskService.updateTask(eq(1L),eq(10L),eq(100L),any(UpdateTaskRequest.class))).thenReturn(new TaskResponse(100L,"Solve equations","Exercises 1-10",
            SolutionDifficulty.MID,TimeDifficulty.MID,true,null,TaskStatus.TODO,null,null,20L));

        mockMvc.perform(put("/api/groups/10/tasks/100").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "title": "Solve equations",
                "note": "Exercises 1-10",
                "solutionDifficulty": "MID",
                "timeDifficulty": "MID",
                "highlighted": true,
                "taskStatus": "TODO",
                "groupId": 20
            }
            """)).andExpect(status().isOk()).andExpect(jsonPath("$.id").value(100)).andExpect(jsonPath("$.groupId").value(20));

        verify(taskService).updateTask(eq(1L),eq(10L),eq(100L),argThat(request->request.groupId().equals(20L)&& request.title().equals("Solve equations")));
    }

    @Test
    void shouldCreateCompletedTaskWithCompletedAt()throws Exception{
        Instant completedAt=Instant.parse("2026-09-24T10:00:00Z");

        when(taskService.createTask(1L,10L,new CreateTaskRequest("StatusTest TaskTitle","completed status-time con",SolutionDifficulty.HARD,TimeDifficulty.LOT,true,null,
            TaskStatus.COMPLETED,10L))).thenReturn(new TaskResponse(100L,"StatusTest TaskTitle","completed status-time con",SolutionDifficulty.HARD,TimeDifficulty.LOT,true,null,
            TaskStatus.COMPLETED,null,completedAt,10L));

        mockMvc.perform(post("/api/groups/10/tasks").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "title": "StatusTest TaskTitle",
                "note": "completed status-time con",
                "solutionDifficulty": "HARD",
                "timeDifficulty": "LOT",
                "highlighted": true,
                "taskStatus": "COMPLETED",
                "groupId": 10
            }
            """)).andExpect(status().isCreated()).andExpect(jsonPath("$.taskStatus").value("COMPLETED")).andExpect(jsonPath("$.completedAt").value("2026-09-24T10:00:00Z"));
    }

    @Test
    void shouldClearCompletedAtWhenTaskIsNoLongerCompleted()throws Exception{
        when(taskService.updateTask(1L,10L,100L,new UpdateTaskRequest("UpdatedStatus TaskTitle","updated note",SolutionDifficulty.MID,TimeDifficulty.MID,false,null,
            TaskStatus.IN_PROGRESS,10L))).thenReturn(new TaskResponse(100L,"UpdatedStatus TaskTitle","updated note",SolutionDifficulty.MID,TimeDifficulty.MID,false,null,
            TaskStatus.IN_PROGRESS,null,null,10L));

        mockMvc.perform(put("/api/groups/10/tasks/100").contentType(MediaType.APPLICATION_JSON).content("""
            {
                "title": "UpdatedStatus TaskTitle",
                "note": "updated note",
                "solutionDifficulty": "MID",
                "timeDifficulty": "MID",
                "highlighted": false,
                "taskStatus": "IN_PROGRESS",
                 "groupId": 10
            }
            """)).andExpect(status().isOk()).andExpect(jsonPath("$.taskStatus").value("IN_PROGRESS")).andExpect(jsonPath("$.completedAt").doesNotExist());
    }

    @Test
    void shouldDeleteTask()throws Exception{
        doNothing().when(taskService).deleteTask(1L,10L,100L);

        mockMvc.perform(delete("/api/groups/10/tasks/100")).andExpect(status().isNoContent());

        verify(taskService).deleteTask(1L,10L,100L);
    }

    @Test
    void shouldReturnNotFoundWhenGroupDoesNotBelongToUser()throws Exception{
        when(taskService.getTasksForGroup(1L,10L,TaskSort.TITLE,SortDirection.ASC)).thenThrow(new ResourceNotFoundException("group not found"));

        mockMvc.perform(get("/api/groups/10/tasks")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.message").value("group not found"));
    }

    @Test
    void shouldReturnNotFoundWhenTaskDoesNotExist()throws Exception{
        when(taskService.getTaskForGroup(1L,10L,100L)).thenThrow(new ResourceNotFoundException("task not found"));

        mockMvc.perform(get("/api/groups/10/tasks/100")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.message").value("task not found"));
    }

    @Test
    void shouldSortTasksBySolutionDifficultyDescending()throws Exception{
        when(taskService.getTasksForGroup(1L,10L,TaskSort.SOLUTION_DIFFICULTY,SortDirection.DESC)).thenReturn(List.of(
            new TaskResponse(200L,"Hard task",null,SolutionDifficulty.HARD,TimeDifficulty.MID,false,null,TaskStatus.TODO,null,null,10L),
            new TaskResponse(100L, "Easy task",null,SolutionDifficulty.EASY,TimeDifficulty.MID,false,null,TaskStatus.TODO,null,null,10L)));

        mockMvc.perform(get("/api/groups/10/tasks").param("sort","SOLUTION_DIFFICULTY").param("direction","DESC")).andExpect(status().isOk())
            .andExpect(jsonPath("$[0].title").value("Hard task")).andExpect(jsonPath("$[1].title").value("Easy task"));
    }
}