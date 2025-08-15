package com.example.demo.Controller;

import com.example.demo.Model.Task;
import com.example.demo.Repository.TaskRepo;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/tasks")
//@CrossOrigin(origins = "http://localhost:5173/")
public class TaskController {

    private final TaskRepo taskRepo;

    public TaskController(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    @GetMapping
    public List<Task>getAllTasks(){
        return taskRepo.findAll(Sort.by(Sort.Direction.ASC,"position"));
    }

    @PostMapping
    public Task createTask (@RequestBody Task task){
        return taskRepo.save(task);
    }
    @PutMapping("/reorder")
    public void reorderTasks(@RequestBody List<Task> reorderedTasks) {
        for (Task task : reorderedTasks) {
            taskRepo.findById(task.getId()).ifPresent(existing -> {
                existing.setPosition(task.getPosition()); // Correct spelling
                taskRepo.save(existing);
            });
        }
    }


    @PutMapping("/{id}")
    public Task updateTask(@PathVariable String id, @RequestBody Task updatedTask) {
        return taskRepo.findById(id).map(task -> {
            if (updatedTask.getTitle() != null) {
                task.setTitle(updatedTask.getTitle());
            }
            if (updatedTask.getDescription() != null) {
                task.setDescription(updatedTask.getDescription());
            }
            task.setStatus(updatedTask.isStatus()); // always update status
            if (updatedTask.getDueDate() != null) {
                task.setDueDate(updatedTask.getDueDate());
            }
            return taskRepo.save(task);
        }).orElseThrow(() -> new RuntimeException("Task not found with id " + id));
    }


    @DeleteMapping("/{id}")
    public void deleteTask (@PathVariable String id){
        taskRepo.deleteById(id);
    }


}
