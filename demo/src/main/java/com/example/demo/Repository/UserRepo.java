package com.example.demo.Repository;

import com.example.demo.Model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepo extends MongoRepository<Users,String> {

}
