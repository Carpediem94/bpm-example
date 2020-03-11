package it.carpediem.kogito.flows;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import it.carpediem.kogito.utils.*;

@QuarkusTest
public class AddUserTest {

    private final String body = "{\"person\" : {\"firstName\" : \"John\", \"lastName\" : \"Doe\", \"email\" : \"j.doe@gmail.com\"}}";

    private final String path = "/addUser";
    private final String task = "approveUser";

    private final BpmProcess process = new BpmProcess(path);

    @Test
    public void testApproveAddUser() {
        process.approve(this.body, this.task);
    }

    @Test
    public void testRejectAddUser() {
        process.reject(this.body, this.task);
    }

    @Test
    public void testReassignTaskAddUser() {
        process.reassign(this.body, this.task);
    }
}