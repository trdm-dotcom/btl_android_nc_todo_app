package com.example.todo.servies;

import com.example.todo.constants.Constants;
import com.example.todo.models.db.Category;
import com.example.todo.models.db.Task;
import com.example.todo.models.request.CategoryRequest;
import com.example.todo.models.request.TaskRequest;
import com.example.todo.repositories.CategoryRepository;
import com.example.todo.repositories.TaskRepository;
import com.example.todo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private UserRepository userRepository;

    public void addCategory(CategoryRequest categoryRequest) {
        Category category = new Category();
        category.setTitle(categoryRequest.getTitle());
        category.setDescription(categoryRequest.getDescription());
        category.setColorCode(categoryRequest.getColorCode());
        this.categoryRepository.save(category);
    }

    public void updateCategory(CategoryRequest categoryRequest) {
        Category category = this.categoryRepository.findById(categoryRequest.getId()).orElseThrow(() -> new RuntimeException(Constants.OBJECT_NOT_FOUND));
        category.setTitle(categoryRequest.getTitle());
        category.setDescription(categoryRequest.getDescription());
        category.setColorCode(categoryRequest.getColorCode());
        this.categoryRepository.save(category);
    }

    public void addTask(TaskRequest taskRequest) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCategory(this.categoryRepository.findById(taskRequest.getCategory()).orElseThrow(() -> new RuntimeException(Constants.INVALID_CATEGORY)));
        task.setUser(this.userRepository.findById(taskRequest.getUserData().getId()).orElseThrow(() -> new RuntimeException(Constants.INVALID_USER)));
        task.setDate(taskRequest.getDate());
        task.setDate(taskRequest.getDate());
        task.setStartTime(taskRequest.getStart());
        task.setEndTime(taskRequest.getEnd());
        this.taskRepository.save(task);
    }

    public void updateTask(TaskRequest taskRequest) {
        Task task = this.taskRepository.findById(taskRequest.getId()).orElseThrow(() -> new RuntimeException(Constants.OBJECT_NOT_FOUND));
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setCategory(this.categoryRepository.findById(taskRequest.getCategory()).orElseThrow(() -> new RuntimeException(Constants.INVALID_CATEGORY)));
        task.setDate(taskRequest.getDate());
        task.setDate(taskRequest.getDate());
        task.setStartTime(taskRequest.getStart());
        task.setEndTime(taskRequest.getEnd());
        this.taskRepository.save(task);
    }

    public void deleteTask(Long id) {
        Task task = this.taskRepository.findById(id).orElseThrow(() -> new RuntimeException(Constants.OBJECT_NOT_FOUND));
        this.taskRepository.delete(task);
    }

    public void findBy() {

    }
}
