package com.example.demo;

import com.example.demo.Controller.TaskController;
import com.example.demo.Model.Task;
import com.example.demo.Repository.TaskRepo;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepo taskRepo;

    @Autowired
    private ObjectMapper objectMapper; // for JSON conversion

    @Test
    public void testGetAllTasks() throws Exception {
        Task task1 = new Task();
        task1.setId("1");
        task1.setTitle("Task 1");
        task1.setDescription("Desc 1");
        task1.setDueDate("2025-08-15");
        task1.setStatus(false);
        task1.setPosition(0);

        Task task2 = new Task();
        task2.setId("2");
        task2.setTitle("Task 2");
        task2.setDescription("Desc 2");
        task2.setDueDate("2025-08-16");
        task2.setStatus(true);
        task2.setPosition(1);

        Mockito.when(taskRepo.findAll(Sort.by(Sort.Direction.ASC, "position")))
                .thenReturn(Arrays.asList(task1, task2));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Task 1"))
                .andExpect(jsonPath("$[1].title").value("Task 2"));
    }

    @Test
    public void testCreateTask() throws Exception {
        Task task = new Task();
        task.setId("1");
        task.setTitle("New Task");
        task.setDescription("Test description");
        task.setDueDate("2025-08-15");
        task.setStatus(false);
        task.setPosition(0);

        Mockito.when(taskRepo.save(any(Task.class))).thenReturn(task);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(task)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Task"));
    }

    @Test
    public void testUpdateTask() throws Exception {
        Task existingTask = new Task();
        existingTask.setId("1");
        existingTask.setTitle("Old Task");
        existingTask.setStatus(false);

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setStatus(true);

        Mockito.when(taskRepo.findById("1")).thenReturn(Optional.of(existingTask));
        Mockito.when(taskRepo.save(any(Task.class))).thenReturn(existingTask);

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Task"))
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    public void testDeleteTask() throws Exception {
        Mockito.doNothing().when(taskRepo).deleteById("1");

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isOk());
    }
}

