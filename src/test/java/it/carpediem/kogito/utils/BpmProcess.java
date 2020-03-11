package it.carpediem.kogito.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

public class BpmProcess {

    private final BpmUtils utils;

    public BpmProcess(String path) {
        this.utils = new BpmUtils(path);
    }

    public void approve(String body, String firstTask) {
        // init task
        String id = utils.getId(body);

        List<String> task = utils.getTaskInfo(id);
        String taskId = task.get(0);
        String taskName = task.get(1);
        assertEquals(firstTask, taskName);
        utils.claim(id, taskName, taskId);
        utils.resolveTask(id, taskName, taskId, "{\"approved\": \"true\"}");

        // check no more task are pending
        utils.checkNoMoreTasks();
    }

    public void reject(String body, String firstTask) {
        // init task
        String id = utils.getId(body);

        // first task
        List<String> task = utils.getTaskInfo(id);
        String taskId = task.get(0);
        String taskName = task.get(1);
        assertEquals(firstTask, taskName);
        utils.claim(id, taskName, taskId);
        utils.resolveTask(id, taskName, taskId, "{\"approved\": \"false\"}");

        // check no more task are pending
        utils.checkNoMoreTasks();
    }

    public void reassign(String body, String firstTask) {
        String id = utils.getId(body);

        // first task
        List<String> task = utils.getTaskInfo(id);
        String taskId = task.get(0);
        String taskName = task.get(1);
        assertEquals(firstTask, taskName);
        utils.claim(id, taskName, taskId);

        // release
        utils.release(id, taskName, taskId);
        // new claim
        utils.claim(id, taskName, taskId);
        utils.resolveTask(id, taskName, taskId, "{\"approved\": \"false\"}");

        // check no more task are pending
        utils.checkNoMoreTasks();
    }
}