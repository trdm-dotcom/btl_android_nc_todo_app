package com.example.todo.controllers;

import com.example.todo.models.request.TaskRequest;
import com.example.todo.servies.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/task")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @PostMapping()
    public ResponseEntity<Object> addTask(@RequestBody TaskRequest request) {
        try {
            this.taskService.addTask(request);
            return ResponseEntity.ok().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
