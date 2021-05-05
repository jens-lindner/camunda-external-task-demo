package com.example.demo;

import com.demo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Component
public class DrinkBatchWorker {

    public static final long LOCK_DURATION_MILLIS = 10_000L;
    @Autowired
    private ExternalTasksClient externalTasksClient;

    @Scheduled(fixedRate = 30_000)
    public void processDrinkSelectionInBatch() {
        FetchExternalTaskTopicDto topic = new FetchExternalTaskTopicDto().topicName("Drink").lockDuration(LOCK_DURATION_MILLIS);
        FetchExternalTasksDto fetchExternalTasksDto = new FetchExternalTasksDto()
                .maxTasks(15)
                .workerId("drink-worker")
                .asyncResponseTimeout(30_000L)
                .addTopicsItem(topic);

        ResponseEntity<List<LockedExternalTaskDto>> listResponseEntity = externalTasksClient.fetchAndLock(fetchExternalTasksDto);

        List<LockedExternalTaskDto> tasks = listResponseEntity.getBody();

        System.out.println("Received " + tasks.size() + " tasks!");
        long currentTimeMillis = System.currentTimeMillis();
        String selectedDrink = currentTimeMillis % 2 == 0 ? "Sekt" : "Selters";
        System.out.println("All those drinks will be " + selectedDrink);

        for (LockedExternalTaskDto task : tasks) {
            System.out.println(new Date() + ", working on task " + task.getId());

            Map<String, VariableValueDto> variables= new HashMap<>();
            variables.put("selectedDrink", new VariableValueDto().value(selectedDrink));

            CompleteExternalTaskDto completeExternalTaskDto = new CompleteExternalTaskDto().variables(variables).workerId("drink-worker");
            externalTasksClient.completeExternalTaskResource(task.getId(), completeExternalTaskDto);
        }
    }
}
