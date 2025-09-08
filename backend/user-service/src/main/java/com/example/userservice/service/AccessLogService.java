package com.example.userservice.service;

import com.example.userservice.entity.AccessLog;
import com.example.userservice.repository.AccessLogRepository;
import org.springframework.stereotype.Service;

@Service
public class AccessLogService {
    AccessLogRepository accessLogRepository;

    public AccessLogService(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

    public AccessLog saveAccess(AccessLog accessLog) {
        return accessLogRepository.save(accessLog);
    }


}
