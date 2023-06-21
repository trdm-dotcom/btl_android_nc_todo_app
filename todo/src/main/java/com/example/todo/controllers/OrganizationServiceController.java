package com.example.todo.controllers;

import com.example.todo.common.exception.GeneralException;
import com.example.todo.models.request.DataRequest;
import com.example.todo.models.request.OrganizationMemberRequest;
import com.example.todo.models.request.OrganizationRequest;
import com.example.todo.models.response.Status;
import com.example.todo.servies.OrganizationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

import static com.example.todo.common.exception.ErrorCodeEnums.INTERNAL_SERVER_ERROR;

@Slf4j
@RestController
@RequestMapping("/api/v1/organization")
public class OrganizationServiceController {
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/create")
    public ResponseEntity<Object> create(HttpServletRequest request, @RequestBody OrganizationRequest organizationRequest) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.organizationService.create(dataRequest, organizationRequest));
        } catch (Exception e) {
            log.error("Error: ", e);
            if (e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            } else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @PostMapping("/member")
    public ResponseEntity<Object> addMember(HttpServletRequest request, @RequestBody OrganizationMemberRequest organizationRequest) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.organizationService.addMember(dataRequest, organizationRequest));
        } catch (Exception e) {
            log.error("Error: ", e);
            if (e instanceof GeneralException) {
                return ResponseEntity.badRequest().body(new Status(((GeneralException) e).getCode(), ((GeneralException) e).getMessageParams()));
            } else {
                return ResponseEntity.badRequest().body(new Status(INTERNAL_SERVER_ERROR.name(), new ArrayList<>()));
            }
        }
    }

    @DeleteMapping("/member")
    public ResponseEntity<Object> removeMember(HttpServletRequest request, @RequestBody OrganizationMemberRequest organizationRequest) {
        try {
            DataRequest dataRequest = this.objectMapper.convertValue(request.getAttribute("dataRequest"), DataRequest.class);
            return ResponseEntity.ok(this.organizationService.removeMember(dataRequest, organizationRequest));
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
