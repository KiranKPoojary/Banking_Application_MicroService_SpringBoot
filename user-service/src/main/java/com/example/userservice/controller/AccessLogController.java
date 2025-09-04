package com.example.userservice.controller;

import com.example.userservice.entity.AccessLog;
import com.example.userservice.repository.AccessLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v0/access-logs")
@RequiredArgsConstructor
public class AccessLogController {


    private AccessLogRepository accessLogRepository;

    public AccessLogController(AccessLogRepository accessLogRepository) {
        this.accessLogRepository = accessLogRepository;
    }

//    @PostMapping("/log")
//    public ResponseEntity<String> logAction(
//            @RequestParam Long userId,
//            @RequestParam UserAction action,
//            HttpServletRequest request) {
//
//        userService.logUserAction(userId, action, request);
//        return ResponseEntity.ok("Action logged successfully");
//    }


    // Create AccessLog
    @PostMapping
    public AccessLog createAccessLog(@RequestBody AccessLog accessLog) {
        return accessLogRepository.save(accessLog);
    }

    // Get All Logs
    @GetMapping
    public List<AccessLog> getAllLogs() {
        return accessLogRepository.findAll();
    }

    // Get Log by ID
    @GetMapping("/{id}")
    public ResponseEntity<AccessLog> getLogById(@PathVariable Long id) {
        Optional<AccessLog> log = accessLogRepository.findById(id);
        return log.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update Log (rarely needed, usually logs are immutable)
    @PutMapping("/{id}")
    public ResponseEntity<AccessLog> updateLog(@PathVariable Long id, @RequestBody AccessLog logDetails) {
        return accessLogRepository.findById(id).map(log -> {
            log.setAction(logDetails.getAction());
            log.setIpAddress(logDetails.getIpAddress());
            log.setTimestamp(logDetails.getTimestamp());
            return ResponseEntity.ok(accessLogRepository.save(log));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete Log (optional, usually logs are kept for auditing)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLog(@PathVariable Long id) {
        return accessLogRepository.findById(id).map(log -> {
            accessLogRepository.delete(log);
            return ResponseEntity.noContent().<Void>build();
        }).orElseGet(() -> ResponseEntity.notFound().<Void>build());
    }
}
