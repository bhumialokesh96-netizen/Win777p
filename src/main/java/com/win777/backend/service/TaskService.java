package com.win777.backend.service;

import com.win777.backend.entity.Task;
import com.win777.backend.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    public List<Task> getAllActiveTasks() {
        return taskRepository.findByStatus("ACTIVE");
    }
    
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }
}
