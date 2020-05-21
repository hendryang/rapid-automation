package com.raf.steps;


import com.raf.api.APIUtils;
import com.raf.api.APIWorld;
import com.raf.api.CommonAPI;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

//import cucumber.api.java.en.Then;
//import cucumber.api.java.en.When;

public class ApiTest {
    @Autowired
    APIWorld apiWorld;

    @Autowired
    APIUtils apiUtils;

    @Autowired
    CommonAPI commonAPI;

    @When("I GET {string} with")
    public void iGETWith(String path, DataTable params) {
        commonAPI.sendGetRequest(path, params.asMap(String.class, String.class), Collections.EMPTY_MAP);
    }

    @Then("I should get status {string}")
    public void iShouldGetStatus(String status) {
        Assert.assertEquals("HTTP Status is not " + status, status, Integer.toString(apiWorld.response.getStatusCode()));
    }

    @Then("response should be")
    public void responseShouldBe(String expectedResponseBody) {
        expectedResponseBody = apiUtils.replaceData(expectedResponseBody, apiWorld.customDataHolder);

        String diffMsg = apiUtils.compareExpectedAndActualResponse(expectedResponseBody,
                apiWorld.response.asString());
        Assert.assertEquals("", diffMsg);
    }

    @When("I POST {string} with unique title {string}")
    public void iPOSTWithUniqueTitle(String path, String uniqueTitle, String body) {
        String uniqueTitleValue = apiUtils.processData(uniqueTitle);

        commonAPI.sendPostRequest(path, apiUtils.replaceData(body, Collections.singletonMap(uniqueTitle, uniqueTitleValue)));
        apiWorld.customDataHolder.put(uniqueTitle, uniqueTitleValue);
    }

    @When("I PUT existing {string} with new title {string}")
    public void iPUTExistingWithNewTitle(String path, String newTitle, String newBody) {
        //Create a new to-do
        String uniqueTitle = apiUtils.processData("TestCreate_<autogendatetime>");
        String body = "{\"title\": \"" + uniqueTitle + "\",\"completed\": true}";
        commonAPI.sendPostRequest("todos", body);

        //Edit to-do
        String previousID = apiWorld.response.jsonPath().getString("id");
        String newTitleValue = apiUtils.processData(newTitle);

        commonAPI.sendPutRequest(path, Collections.singletonMap("id", previousID),
                apiUtils.replaceData(newBody, Collections.singletonMap(newTitle, newTitleValue)));

        apiWorld.customDataHolder.put(newTitle, newTitleValue);
    }

    @When("I DELETE existing {string}")
    public void iDELETEExisting(String path) {
        //Create a new to-do
        String uniqueTitle = apiUtils.processData("TestCreate_<autogendatetime>");
        String body = "{\"title\": \"" + uniqueTitle + "\",\"completed\": true}";
        commonAPI.sendPostRequest("todos", body);

        //Delete to-do
        String previousID = apiWorld.response.jsonPath().getString("id");
        commonAPI.sendDeleteRequest(path, Collections.singletonMap("id", previousID));
    }
}
