package com.raf.ui.driver;

import com.raf.ui.driver.browsers.ChromeImpl;
import com.raf.ui.driver.browsers.FirefoxImpl;
import io.cucumber.java.Scenario;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.WebDriver;
import org.testcontainers.lifecycle.TestDescription;

import java.util.Objects;
import java.util.Optional;

@Log4j2
public class BrowserFactory {
    private WebDriver webDriver;
    private AbstractBrowser abstractBrowser;

    public BrowserFactory(String browser, String method, String remoteURL, boolean isVideoRecording) {
        initializeWebDriver(browser, method, remoteURL, isVideoRecording);
    }

    // Instantiate webDriver instance based on following configuration details:
    // - browser = chrome/firefox/edge
    // - method  = local/remote/container
    // - remoteURL = URL for remote HUB (only relevant for remote method)
    private void initializeWebDriver(String browser, String method, String remoteURL, boolean isVideoRecording) {
        long debugStartTime = System.nanoTime();

        if ("FIREFOX".equalsIgnoreCase(browser)) {
            abstractBrowser = new FirefoxImpl(method, remoteURL, isVideoRecording);
        } else {
            abstractBrowser = new ChromeImpl(method, remoteURL, isVideoRecording);
        }
        webDriver = abstractBrowser.getBrowser();
        log.atTrace().log("[RAF] Speed -> WebDriver getBrowser() took : {} ms", () -> (System.nanoTime() - debugStartTime) / 1_000_000);
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    // Destroy docker container (if exists - ie. using container method). This is called in Hook for @After
    public void destroy(Scenario scenario) {
        LocalBrowserWebDriverContainer localContainer = abstractBrowser.getBrowserContainer();
        if (!Objects.isNull(localContainer)) {
            localContainer.afterTest(new TestDescription() {
                @Override
                public String getTestId() {
                    return scenario.getId();
                }

                @Override
                public String getFilesystemFriendlyName() {
                    return scenario.getName();
                }
            }, Optional.of(scenario).filter(Scenario::isFailed).map(__ -> new RuntimeException()));
            localContainer.stop();
            log.atInfo().log("[RAF] Container stopped.");
        }
    }
}