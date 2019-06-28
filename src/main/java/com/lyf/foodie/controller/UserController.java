package com.lyf.foodie.controller;

import com.lyf.foodie.entity.User;
import com.lyf.foodie.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/users/{userId}")
    public void upload(@PathVariable("userId") String userId) {
        if (!userRepository.findById(userId).isPresent()) {
            userRepository.save(User.builder().id(userId).name("李易菲").build());
        }
    }
}
