package com.raf.steps;

import com.raf.pageobjects.YoutubePage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;

public class YoutubeSteps {
    @Autowired
    YoutubePage youtubePage;

    @Given("User opened youtube")
    public void userOpenedYoutube() {
        youtubePage.goTo();
    }

    @Then("The video was uploaded by {string}")
    public void theVideoWasUploadedBy(String expectedUploader) {
        Assert.assertEquals(youtubePage.getUploader(), expectedUploader, "Uploader information is incorrect.");
    }

    @When("User search {string} and select video number {string}")
    public void userSearchAndSelectVideoNumber(String query, String seq) {
        youtubePage.searchYoutube(query);
        youtubePage.selectVideo(Integer.parseInt(seq));
    }

    @Then("Video is in {string} mode")
    public void videoIsInPlayMode(String mode) {
        if (mode.equalsIgnoreCase("play")) {
            Assert.assertFalse(youtubePage.isVideoPaused(), "video not in play mode");
        } else {
            Assert.assertTrue(youtubePage.isVideoPaused(), "video not in pause mode");
        }
        youtubePage.enjoyTheVideo(3);
    }

    @And("User {string} the video")
    public void userTheVideo(String action) {
        if (action.equalsIgnoreCase("play")) {
            youtubePage.playVideo();
        } else {
            youtubePage.pauseVideo();
        }
    }

}
