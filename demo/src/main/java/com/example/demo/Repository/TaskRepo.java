package com.example.demo.Repository;


import com.example.demo.Model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepo extends MongoRepository<Task, String> {
}