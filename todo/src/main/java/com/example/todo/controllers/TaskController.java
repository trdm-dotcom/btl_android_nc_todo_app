package com.example.todo.controllers;

import com.example.todo.common.exception.GeneralException;
import com.example.todo.constants.enums.Priority;
import com.example.todo.constants.enums.TaskStatus;
import com.example.todo.models.dto.TaskDto;
import com.example.todo.models.request.AssigneeRequest;
import com.example.todo.models.request.CommentRequest;
import com.example.todo.models.request.DataRequest;
import com.example.todo.models.request.TaskRequest;
import com.example.todo.models.response.Status;
import com.example.todo.servies.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Set;

import static com.example.todo.common.exception.ErrorCodeEnums.INTERNAL_SERVER_ERROR;

@Slf4j
@RestController
@RequestMapping("/api/v1/task")
public class TaskController {
    @Autowired
    private TaskService taskService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<Object> addTask(HttpServletRequest request, @RequestBody TaskRequest taskRequest) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.taskService.addTask(dataRequest, taskRequest));
        } catch (Exception e) {
            log.error("Error: ", e);
            if (e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            } else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @PutMapping("/{task}")
    public ResponseEntity<Object> updateTask(HttpServletRequest request, @RequestBody TaskRequest taskRequest, @PathVariable("task") Long task) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.taskService.updateTask(dataRequest, task, taskRequest));
        } catch (Exception e) {
            log.error("Error: ", e);
            if (e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            } else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @DeleteMapping("/{task}")
    public ResponseEntity<Object> deleteTask(HttpServletRequest request, @PathVariable("task") Long task) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.taskService.deleteTask(dataRequest, task));
        } catch (Exception e) {
            log.error("Error: ", e);
            if (e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            } else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @GetMapping
    public ResponseEntity<Set<TaskDto>> getTasks(HttpServletRequest request,
                                                 @RequestParam(value = "pageSize", required = false) Integer pageSize,
                                                 @RequestParam(value = "pageNumber", required = false) Integer pageNumber,
                                                 @RequestParam(value = "start", required = false) String start,
                                                 @RequestParam(value = "end", required = false) String end,
                                                 @RequestParam(value = "priority", required = false) Priority priority,
                                                 @RequestParam(value = "taskStatus", required = false) TaskStatus taskStatus,
                                                 @RequestParam(value = "organization", required = false) Long organization) {
        DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
        return ResponseEntity.ok(this.taskService.findTaskBy(dataRequest, pageSize, pageNumber, start, end, priority, taskStatus, organization));
    }

    @GetMapping("/{task}")
    public ResponseEntity<Object> getTask(@PathVariable("task") Long task) {
        try {
            return ResponseEntity.ok(this.taskService.findTaskById(task));
        } catch (Exception e) {
            log.error("Error: ", e);
            if (e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            } else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @PutMapping("/{task}/status/{status}")
    public ResponseEntity<Object> completeTask(HttpServletRequest request, @PathVariable("task") Long task, @PathVariable("status") TaskStatus status) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.taskService.updateTaskStatus(dataRequest, task, status));
        } catch (Exception e) {
            log.error("Error: ", e);
            if (e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            } else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @PutMapping("/assignee")
    public ResponseEntity<Object> assigneeTask(HttpServletRequest request, @RequestBody AssigneeRequest assigneeRequest) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.taskService.assigneeTask(dataRequest, assigneeRequest));
        } catch (Exception e) {
            log.error("Error: ", e);
            if (e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            } else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @PostMapping("/comment")
    public ResponseEntity<Object> addComment(HttpServletRequest request, @RequestBody CommentRequest commentRequest) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.taskService.addComment(dataRequest, commentRequest));
        } catch (Exception e) {
            log.error("Error: ", e);
            if (e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            } else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }
}
