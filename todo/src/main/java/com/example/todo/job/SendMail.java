package com.example.todo.job;

import com.example.todo.models.db.Task;
import com.example.todo.models.dto.UserData;
import com.example.todo.repositories.TaskRepository;
import com.example.todo.servies.SendEmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class SendMail {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private SendEmailService sendEmailService;

    @Scheduled(cron = "${app.schedulers.sendMail}")
    public void runJob() {
        log.info("Start send mail");
        this.jobSendMail();
        log.info("End send mail");
    }

    private void jobSendMail() {
        Map<Long, List<Task>> mapTask = new HashMap<>();
        Map<Long, UserData> mapUser = new HashMap<>();
        this.taskRepository.findByRemindTrueAndDate(LocalDate.now())
                .forEach(task -> {
                    task.getAssignees().forEach(user -> {
                        if (!mapUser.containsKey(user.getId())) {
                            mapUser.put(user.getId(), new UserData(user.getId(), user.getName(), user.getEmail(), user.getStatus()));
                        }
                        if (mapTask.containsKey(user.getId())) {
                            mapTask.get(user.getId()).add(task);
                        } else {
                            mapTask.put(user.getId(), Collections.singletonList(task));
                        }
                    });
                });
        mapTask.forEach((userId, tasks) -> {
            List<String> nameTaskToday = tasks.stream().map(Task::getTitle).collect(Collectors.toList());
            UserData userData = mapUser.get(userId);
            Map<String, Object> dataMail = new HashMap<>();
            dataMail.put("name", userData.getName());
            dataMail.put("tasks", nameTaskToday);
            try {
                this.sendEmailService.sendMail("Task Today", Collections.singletonList(userData.getEmail()), "task_today", dataMail, Locale.ENGLISH.getLanguage());
            } catch (Exception e) {
                log.error("Send mail for user {} {} {} error", userData.getId(), userData.getName(), userData.getEmail(), e);
            }
        });
    }
}
