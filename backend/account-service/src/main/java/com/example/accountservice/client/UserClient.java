package com.example.accountservice.client;

//import com.example.accountservice.config.FeignClientConfig;
import com.example.accountservice.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "user-service", path = "/api/v0/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserDto getUserById(@PathVariable("id") Long id,@RequestHeader("X-Service-Auth") String serviceAuth);

}
