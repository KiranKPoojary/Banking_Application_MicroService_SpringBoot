//package com.example.notificationservice.client;
//
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//
//import java.util.List;
//
//@FeignClient(name = "account-service", path = "/api/v0/accounts")
//public interface AccountClient {
//
//    @GetMapping("/user/{userId}")
//    List<AccountDto> getAccountsByUserId(@PathVariable("userId") Long userId);
//}