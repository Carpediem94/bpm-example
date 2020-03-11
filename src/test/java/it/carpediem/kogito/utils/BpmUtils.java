package it.carpediem.kogito.utils;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.restassured.http.ContentType;

public class BpmUtils {

    private String path;


    BpmUtils(String path) {
        this.path = path;
    }

    public String getId(String body) {
        // start new task
        String id = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .post(this.path)
            .then()
                .statusCode(200)
                .body("id", notNullValue()).extract().path("id");

        // check task is added
        given()
                .accept(ContentType.JSON)
            .when()
                .get(this.path)
            .then()
                .statusCode(200)
                .body("$.size()", is(1), "[0].id", is(id));  

        // get just the task referred to id
        given()
                .accept(ContentType.JSON)
            .when()
                .get(this.path + "/" + id)
            .then()
                .statusCode(200)
                .body("id", is(id));

        return id;
    }

    public List<String> getTaskInfo(String id) {
        Map<?,?> taskInfo = given()
                .accept(ContentType.JSON)
            .when()
                .get(this.path + "/" + id + "/tasks")
            .then()
                .statusCode(200)
                .extract().as(Map.class);

        assertEquals(1, taskInfo.size()); // check if there is just one task
        
        List<String> task = new ArrayList<String>();
        taskInfo.forEach((k, v) -> {
            task.add(k.toString());
            task.add(v.toString());
        });

        return task;
    }

    public void resolveTask(String id, String taskName, String taskId, String body) {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .post(this.path + "/" + id + "/" + taskName + "/" + taskId)
            .then()
                .statusCode(200);
    }

    public void checkNoMoreTasks() {
        given()
                .accept(ContentType.JSON)
            .when()
                .get(this.path)
            .then()
                .statusCode(200)
                .body("$.size()", is(0));
    }

    public void claim(String id, String taskName, String taskId) {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{}")
            .when()
                //.post(this.path + "/" + id + "/" + taskName + "/" + taskId + "?phase=claim&user=admin1")
                .post(this.path + "/" + id + "/" + taskName + "/" + taskId + "?phase=claim")
            .then()
                .statusCode(200);
    }

    public void release(String id, String taskName, String taskId) {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{}")
            .when()
                //.post(this.path + "/" + id + "/" + taskName + "/" + taskId + "?phase=claim&user=admin1")
                .post(this.path + "/" + id + "/" + taskName + "/" + taskId + "?phase=release")
            .then()
                .statusCode(200);
    }
}