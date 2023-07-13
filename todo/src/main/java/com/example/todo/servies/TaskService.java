package com.example.todo.servies;

import com.example.todo.common.exception.GeneralException;
import com.example.todo.constants.Constants;
import com.example.todo.constants.enums.Priority;
import com.example.todo.constants.enums.TaskStatus;
import com.example.todo.models.db.Comment;
import com.example.todo.models.db.Task;
import com.example.todo.models.db.User;
import com.example.todo.models.dto.CommentDto;
import com.example.todo.models.dto.OrganizationDto;
import com.example.todo.models.dto.TaskDto;
import com.example.todo.models.dto.UserData;
import com.example.todo.models.request.AssigneeRequest;
import com.example.todo.models.request.CommentRequest;
import com.example.todo.models.request.DataRequest;
import com.example.todo.models.request.TaskRequest;
import com.example.todo.repositories.CommentRepository;
import com.example.todo.repositories.OrganizationRepository;
import com.example.todo.repositories.TaskRepository;
import com.example.todo.repositories.UserRepository;
import com.example.todo.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private OrganizationRepository organizationRepository;

    public Object addTask(DataRequest dataRequest, TaskRequest request) {
        request.validate();
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new GeneralException(Constants.START_DATE_MUST_BE_BEFORE_OR_EQUAL_END_DATE);
        }
        if (request.getStartDate().isBefore(LocalDate.now())) {
            throw new GeneralException(Constants.START_DATE_MUST_BE_AFTER_OR_EQUAL_CURRENT_DATE);
        }
        if (request.getEndDate().isBefore(LocalDate.now())) {
            throw new GeneralException(Constants.END_DATE_MUST_BE_AFTER_OR_EQUAL_CURRENT_DATE);
        }
        User currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            currentUser = userDetails.getUser();
        } else {
            currentUser = this.userRepository.findById(dataRequest.getUserData().getId()).orElseThrow(() -> new GeneralException(Constants.INVALID_USER));
        }
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        Set<User> assignees = new HashSet<>();
        assignees.add(currentUser);
        if (!CollectionUtils.isEmpty(request.getAssignees())) {
            Set<User> users = this.userRepository.findByIdIn(request.getAssignees());
            assignees.addAll(users);
        }
        task.setAssignees(assignees);
        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());
        task.setPriority(Priority.valueOf(request.getPriority()));
        task.setRemind(request.getRemind());
        task.setOrganization(this.organizationRepository.findById(request.getOrganizationId()).orElseThrow(() -> new GeneralException(Constants.INVALID_ORGANIZATION)));
        this.taskRepository.save(task);
        return new HashMap<>();
    }

    public Object updateTask(DataRequest dataRequest, Long id, TaskRequest request) {
        request.validate();
        Task task = this.taskRepository.findOneById(id).orElseThrow(() -> new GeneralException(Constants.OBJECT_NOT_FOUND));
        Set<Long> assignees = task.getAssignees().stream().map(User::getId).collect(Collectors.toSet());
        if (!assignees.contains(dataRequest.getUserData().getId())) {
            throw new GeneralException(Constants.NOT_PERMISSION);
        }
        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new GeneralException(Constants.START_DATE_MUST_BE_BEFORE_OR_EQUAL_END_DATE);
        }
        if (!request.getStartDate().isEqual(task.getStartDate()) && request.getStartDate().isBefore(LocalDate.now())) {
            throw new GeneralException(Constants.START_DATE_MUST_BE_AFTER_OR_EQUAL_CURRENT_DATE);
        }
        if (!request.getEndDate().isEqual(task.getEndDate()) && request.getEndDate().isBefore(LocalDate.now())) {
            throw new GeneralException(Constants.END_DATE_MUST_BE_AFTER_OR_EQUAL_CURRENT_DATE);
        }
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());
        task.setPriority(Priority.valueOf(request.getPriority()));
        task.setRemind(request.getRemind());
        if (!CollectionUtils.isEmpty(request.getAssignees())) {
            Set<User> users = this.userRepository.findByIdIn(request.getAssignees());
            task.getAssignees().addAll(users);
        }
        this.taskRepository.save(task);
        return new HashMap<>();
    }

    public Object deleteTask(DataRequest dataRequest, Long id) {
        Task task = this.taskRepository.findOneById(id).orElseThrow(() -> new GeneralException(Constants.OBJECT_NOT_FOUND));
        Set<Long> assignees = task.getAssignees().stream().map(User::getId).collect(Collectors.toSet());
        if (!assignees.contains(dataRequest.getUserData().getId())) {
            throw new GeneralException(Constants.NOT_PERMISSION);
        }
        this.taskRepository.delete(task);
        return new HashMap<>();
    }

    public Set<TaskDto> findTaskBy(DataRequest dataRequest, Integer pageSize, Integer pageNumber, String start, String end, Priority priority, TaskStatus status, Long organization) {
        Integer fetchCount = pageSize == null ? Constants.DEFAULT_FETCH_COUNT : Math.min(pageSize, Constants.MAX_FETCH_COUNT);
        Integer offset = pageNumber == null ? Constants.DEFAULT_OFFSET : Math.max(0, pageNumber);
        Pageable pageable = PageRequest.of(offset, fetchCount);
        return this.taskRepository.findTaskBy(dataRequest.getUserData().getId(), start, end, priority, status, organization, pageable).stream()
                .map(task -> new TaskDto(task.getId(),
                                task.getTitle(),
                                task.getDescription(),
                                task.getColorCode(),
                                task.getStartDate(),
                                task.getEndDate(),
                                task.getStatus(),
                                task.getPriority(),
                                task.getAssignees().stream()
                                        .map(user -> new UserData(user.getId(), user.getName(), user.getEmail(), user.getStatus()))
                                        .collect(Collectors.toSet()),
                                new OrganizationDto(task.getOrganization().getId(), task.getOrganization().getName(), null, null, null),
                                task.getComments().stream()
                                        .map(comment -> new CommentDto(comment.getId(), comment.getContent(), comment.getCreatedAt(), comment.getUser().getName()))
                                        .collect(Collectors.toSet())
                        )
                )
                .collect(Collectors.toSet());
    }

    public TaskDto findTaskById(Long id) {
        Task task = this.taskRepository.findOneById(id).orElseThrow(() -> new GeneralException(Constants.OBJECT_NOT_FOUND));
        return new TaskDto(task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getColorCode(),
                task.getStartDate(),
                task.getEndDate(),
                task.getStatus(),
                task.getPriority(),
                task.getAssignees().stream()
                        .map(user -> new UserData(user.getId(), user.getName(), user.getEmail(), user.getStatus()))
                        .collect(Collectors.toSet()),
                new OrganizationDto(task.getOrganization().getId(), task.getOrganization().getName(), null, null, null),
                task.getComments().stream()
                        .map(comment -> new CommentDto(comment.getId(), comment.getContent(), comment.getCreatedAt(), comment.getUser().getName()))
                        .collect(Collectors.toSet())
        );
    }

    public Object updateTaskStatus(DataRequest dataRequest, Long id, TaskStatus status) {
        Task task = this.taskRepository.findOneById(id).orElseThrow(() -> new GeneralException(Constants.OBJECT_NOT_FOUND));
        Set<Long> assignees = task.getAssignees().stream().map(User::getId).collect(Collectors.toSet());
        if (!assignees.contains(dataRequest.getUserData().getId())) {
            throw new GeneralException(Constants.NOT_PERMISSION);
        }
        task.setStatus(status);
        this.taskRepository.save(task);
        return new HashMap<>();
    }

    @Transactional
    public Object assigneeTask(DataRequest dataRequest, AssigneeRequest request) {
        request.validate();
        Set<User> users = this.userRepository.findByIdIn(request.getAssignee());
        Task task = this.taskRepository.findOneById(request.getTask()).orElseThrow(() -> new GeneralException(Constants.INVALID_TASK));
        if (CollectionUtils.isEmpty(users)) {
            throw new GeneralException(Constants.USER_NOT_FOUND);
        }
        Set<Long> assignees = task.getAssignees().stream().map(User::getId).collect(Collectors.toSet());
        if (!assignees.contains(dataRequest.getUserData().getId())) {
            throw new GeneralException(Constants.NOT_PERMISSION);
        }
        if (users.containsAll(task.getAssignees())) {
            throw new GeneralException(Constants.ALREADY_ASSIGNED);
        }
        users = users.stream().filter(user -> !task.getAssignees().contains(user) && user.getOrganizations().contains(task.getOrganization()))
                .collect(Collectors.toSet());
        task.getAssignees().addAll(users);
        this.taskRepository.save(task);
        this.sendEmailAssigneeTask(dataRequest.getUserData().getName(),
                task.getTitle(),
                users);
        return new HashMap<>();
    }

    public Object addComment(DataRequest dataRequest, CommentRequest request) {
        request.validate();
        User currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            currentUser = userDetails.getUser();
        } else {
            currentUser = this.userRepository.findById(dataRequest.getUserData().getId()).orElseThrow(() -> new GeneralException(Constants.INVALID_USER));
        }
        Task task = this.taskRepository.findOneById(request.getTask()).orElseThrow(() -> new GeneralException(Constants.INVALID_TASK));
        Comment comment = new Comment();
        comment.setContent(request.getContent());
        comment.setUser(currentUser);
        comment.setTask(task);
        comment = this.commentRepository.save(comment);
        task.getComments().add(comment);
        this.taskRepository.save(task);
        return new HashMap<>();
    }

    private void sendEmailAssigneeTask(String name, String title, Set<User> users) {
        try {
            Map<String, Object> data = new HashMap<String, Object>() {{
                put("name", name);
                put("task", title);
            }};
            sendEmailService.sendMail(String.format("You have been assigned to task %s", title),
                    users.stream().map(User::getEmail).collect(Collectors.toList()),
                    "assignee_task",
                    data,
                    Locale.ENGLISH.getLanguage());
        } catch (Exception e) {
            log.error("Error when send email", e);
        }
    }
}
