package com.example.todo.servies;

import com.example.todo.common.exception.GeneralException;
import com.example.todo.constants.Constants;
import com.example.todo.models.db.Organization;
import com.example.todo.models.db.Task;
import com.example.todo.models.db.User;
import com.example.todo.models.dto.OrganizationDto;
import com.example.todo.models.request.DataRequest;
import com.example.todo.models.request.OrganizationMemberRequest;
import com.example.todo.models.request.OrganizationRequest;
import com.example.todo.repositories.OrganizationRepository;
import com.example.todo.repositories.TaskRepository;
import com.example.todo.repositories.UserRepository;
import com.example.todo.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OrganizationService {
    @Autowired
    private OrganizationRepository organizationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    @Transactional
    public Object create(DataRequest dataRequest, OrganizationRequest request) {
        request.validate();
        User currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            currentUser = userDetails.getUser();
        } else {
            currentUser = this.userRepository.findById(dataRequest.getUserData().getId()).orElseThrow(() -> new GeneralException(Constants.INVALID_USER));
        }
        Organization organization = new Organization();
        organization.setName(request.getName());
        organization = this.organizationRepository.save(organization);
        currentUser.getOrganizations().add(organization);
        this.userRepository.save(currentUser);
        return new HashMap<>();
    }

    public Object addMember(DataRequest dataRequest, OrganizationMemberRequest request) {
        request.validate();
        User currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            currentUser = userDetails.getUser();
        } else {
            currentUser = this.userRepository.findById(dataRequest.getUserData().getId()).orElseThrow(() -> new GeneralException(Constants.INVALID_USER));
        }
        Organization organization = this.organizationRepository.findById(request.getOrganizationId()).orElseThrow(() -> new GeneralException(Constants.INVALID_ORGANIZATION));
        if (!currentUser.getOrganizations().contains(organization)) {
            throw new GeneralException(Constants.NOT_PERMISSION);
        }
        User user = this.userRepository.findById(request.getUserId()).orElseThrow(() -> new GeneralException(Constants.USER_NOT_FOUND));
        user.getOrganizations().add(organization);
        this.userRepository.save(user);

        return new HashMap<>();
    }

    @Transactional
    public Object removeMember(DataRequest dataRequest, OrganizationMemberRequest request) {
        request.validate();
        User currentUser;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            currentUser = userDetails.getUser();
        } else {
            currentUser = this.userRepository.findById(dataRequest.getUserData().getId()).orElseThrow(() -> new GeneralException(Constants.INVALID_USER));
        }
        Organization organization = this.organizationRepository.findById(request.getOrganizationId()).orElseThrow(() -> new GeneralException(Constants.INVALID_ORGANIZATION));
        if (!currentUser.getOrganizations().contains(organization)) {
            throw new GeneralException(Constants.NOT_PERMISSION);
        }
        User user = this.userRepository.findById(request.getUserId()).orElseThrow(() -> new GeneralException(Constants.USER_NOT_FOUND));
        List<Task> tasks = this.taskRepository.findTaskBy(dataRequest.getUserData().getId(), null, null, null, null, null, null).getContent();
        user.getOrganizations().remove(organization);
        this.organizationRepository.save(organization);
        tasks.forEach(task -> {
            task.getAssignees().remove(user);
        });
        this.taskRepository.saveAll(tasks);
        return new HashMap<>();
    }

    public Set<OrganizationDto> getAll(DataRequest dataRequest) {
        return this.organizationRepository.getOrganizationsByUsersId(dataRequest.getUserData().getId())
                .stream().map(it -> new OrganizationDto(it.getId(), it.getName(), null, it.getLeft(), it.getDone())).collect(Collectors.toSet());
    }
}
