package com.example.demo;

import com.demo.api.ExternalTaskApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="external-tasks-api", url="http://localhost:8080/engine-rest")
public interface ExternalTasksClient extends ExternalTaskApi {
}
