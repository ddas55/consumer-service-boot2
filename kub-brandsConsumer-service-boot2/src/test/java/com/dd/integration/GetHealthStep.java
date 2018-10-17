package com.dd.integration;

import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


public class GetHealthStep extends CucumberRoot {
    private ResponseEntity<String> response; // output

    @When("^the client calls /healthz$")
    public void the_client_issues_GET_health() throws Throwable {
        response = testRestTemplate.getForEntity("/consumer/healthz", String.class);
        System.out.println("------- GetHealthStep: " + response);
    }

    @Then("^the client receives response status code of (\\d+)$")
    public void the_client_receives_status_code_of(int statusCode) throws Throwable {
        HttpStatus currentStatusCode = response.getStatusCode();
        System.out.println("------- GetHealthStep: " + currentStatusCode);
        assertThat("status code is incorrect : " +
                response.getBody(), currentStatusCode.value(), is(statusCode));
    }

}
