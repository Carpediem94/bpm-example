package it.example;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*import org.infinispan.client.hotrod.RemoteCache;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterAll;*/
import org.junit.jupiter.api.Test;
/*import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;*/

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

@QuarkusTest
//@Testcontainers
public class AddUserTest {

    private final String person = "{\"person\" : {\"firstName\" : \"John\", \"lastName\" : \"Doe\", \"email\" : \"j.doe@gmail.com\"}}";
    
    private final String path = "/addUser";
    private final String approveUser = "approveUser";

    /* @ClassRule
    public static GenericContainer INFINISPAN = 
        new GenericContainer<>("jboss/infinispan-server:latest")
            .withExposedPorts(11222)
            .withEnv("USER", "infinispan")
            .withEnv("PASS", "infinispan");
    
    @AfterAll
    public static void cleanup() {
        INFINISPAN_SERVER.stop();
    }

    @Test
    public void containerRunTest() {
        assertTrue(INFINISPAN.isRunning());
    } */

    @Test
    public void testApproveUser() {
        // init task
        String id = getId();

        // task approveUser
        List<String> task1 = getTaskInfo(id);
        String taskId1 = task1.get(0);
        String taskName1 = task1.get(1);
        assertEquals(this.approveUser, taskName1);
        claim(id, taskName1, taskId1);
        resolveTask(id, taskName1, taskId1, "{\"approved\": \"true\"}", "approved");

        // check no more task are pending
        checkNoMoreTasks();
    }

    @Test
    public void testRefuseUser() {
        // init task
        String id = getId();

        // task approveUser
        List<String> task = getTaskInfo(id);
        String taskId = task.get(0);
        String taskName = task.get(1);
        assertEquals(this.approveUser, taskName);
        claim(id, taskName, taskId);
        resolveTask(id, taskName, taskId, "{\"approved\": \"false\"}", "approved");

        // check no more task are pending
        checkNoMoreTasks();
    }

    private String getId() {
        // start new task
        String id = given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(this.person)
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

    private List<String> getTaskInfo(String id) {
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

    private void resolveTask(String id, String taskName, String taskId, String body, String field) {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body(body)
            .when()
                .post(this.path + "/" + id + "/" + taskName + "/" + taskId)
            .then()
                .statusCode(200)
                .extract().jsonPath().getString(field);
    }

    private void checkNoMoreTasks() {
        given()
                .accept(ContentType.JSON)
            .when()
                .get(this.path)
            .then()
                .statusCode(200)
                .body("$.size()", is(0));
    }

    private void claim(String id, String taskName, String taskId) {
        given()
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .body("{}")
            .when()
                .post(this.path + "/" + id + "/" + taskName + "/" + taskId + "?phase=claim&user=admin1&group=MTE")
                //.post(this.path + "/" + id + "/" + taskName + "/" + taskId + "?phase=claim")
            .then()
                .statusCode(200);
    }
}