package com.raf.pageobjects;

import com.raf.ui.pages.BasePage;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

import static io.cucumber.spring.CucumberTestContext.SCOPE_CUCUMBER_GLUE;

@Log4j2
@Component
@Scope(SCOPE_CUCUMBER_GLUE)
public class YoutubePage extends BasePage {
    @PostConstruct
    public void postInitialize() {
        String PAGE_TITLE = "";
        String PAGE_URL = "http://youtube.com";
        initPage(this, PAGE_TITLE, PAGE_URL);
    }

    @FindBy(css = "div#search-input input")
    public WebElement txtSearch;

    @FindBy(css = "#search-icon-legacy")
    public WebElement btnSearch;

    @FindBy(css = "div#content ytd-video-renderer:nth-child(1) a#thumbnail")
    public WebElement firstVideo;

    @FindBy(css = "div#info h1.title yt-formatted-string")
    public WebElement txtVideoTitle;

    @FindBy(css = "button.ytp-play-button")
    public WebElement btnPlayPause;

    @FindBy(css = "div#upload-info a")
    public WebElement txtUploader;

    @FindBy(css = "video.html5-main-video")
    public WebElement mainVideo;

    public void searchYoutube(String query) {
        pageAction.enterText(txtSearch, query)
                .clickElement(btnSearch);
    }

    public void selectVideo(int number) {
        WebElement videoThumbnail = pageAction.findElement(By.cssSelector("div#content ytd-video-renderer:nth-child(" + number + ") a#thumbnail"));
        pageAction.clickElement(videoThumbnail);
    }

    public String getVideoTitle() {
        return pageAction.getText(txtVideoTitle);
    }

    public void playVideo() {
        if (isVideoPaused())
            pageAction.clickElement(btnPlayPause);
    }

    public void pauseVideo() {
        if (!isVideoPaused())
            pageAction.clickElement(btnPlayPause);
    }

    public boolean isVideoPaused() {
        hoverToVideo();
        return pageAction.getAttribute(btnPlayPause, "title").contains("Play");
    }

    public String getUploader() {
        return pageAction.getText(txtUploader);
    }

    public void enjoyTheVideo(int seconds) {
        pageAction.sleep(seconds);
    }

    private void hoverToVideo() {
        pageAction
                .withTimeout(3)
                .mouseHoverAt(txtUploader)
                .mouseHoverAt(mainVideo);
    }
}