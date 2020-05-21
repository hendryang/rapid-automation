package com.raf.ui.driver;

import lombok.extern.log4j.Log4j2;
import org.ini4j.Ini;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.util.unit.DataSize;
import org.testcontainers.containers.DefaultRecordingFileFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
public abstract class AbstractBrowser implements IBrowser {
    private static final String REMOTECAPABILITIES = "all-desired_capabilities";
    private static final String BROWSERCONFIG = "config/browser.ini";
    private static final String DEFAULT = "default";
    protected String method;
    protected String remoteURL;
    protected boolean isVideoRecording;
    protected String platform;
    //    private BrowserWebDriverContainer localContainer;
    private LocalBrowserWebDriverContainer localContainer;

    public AbstractBrowser(String method, String remoteURL, boolean isVideoRecording) {
        log.atTrace().log("[RAF] Execute -> AbstractBrowser constructor({},{})", method, remoteURL);
        this.method = method; // to define either LOCAL (default) or REMOTE run
        this.remoteURL = remoteURL; // to define selenium HUB URL - applicable only for REMOTE method
        this.localContainer = null;
        this.isVideoRecording = isVideoRecording;
    }

    // Read the desired capabilities from BROWSERCONFIG file section REMOTECAPABILITIES, filter out all with value with "default", and return the only configured desired capabilities as a Map<>
    public Map<String, Object> getCapabilities() {
        Map<String, Object> preferences = new HashMap<>();
        Ini ini = null;
        try (InputStream file = getClass().getClassLoader().getResourceAsStream(BROWSERCONFIG)) {
            ini = new Ini(file);
            preferences = ini.get(REMOTECAPABILITIES).entrySet().stream()
                    .filter(map -> !map.getValue().equalsIgnoreCase(DEFAULT))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return preferences;
        } catch (IOException | NullPointerException knownEx) {
            log.atError().withThrowable(knownEx).log("[RAF] Failed to retrieve browser's global capabilities. Hint: Ensure you have [{}] section under '{}' file", REMOTECAPABILITIES, BROWSERCONFIG);
        } catch (Exception unknownEx) {
            log.atError().withThrowable(unknownEx).log("[RAF] Failed to retrieve browser's global capabilities. Hint: <unknown>");
        }
        return preferences;
    }


    @Override
    public WebDriver remoteDriver() {
        //todo remove seleniumHubUrl and enable exception handling
        // hardcoreded URL : String seleniumHubURL = System.getProperty("seleniumHubURL", "http://10.240.12.201:4444/wd/hub");

        RemoteWebDriver remoteWebDriver = null;

        try {

            remoteWebDriver = new RemoteWebDriver(new URL(this.remoteURL), new ChromeOptions());
            // Upload file to WebElement in case of running remotely - See: https://github.com/heusserm/JavaSamples/blob/master/SeleniumSamples/File_upload.java
            remoteWebDriver.setFileDetector(new LocalFileDetector());
        } catch (MalformedURLException ex) {
            log.atError().withThrowable(ex).log("[RAF] Failed to establish Remote WebDriver with URL '{}'. Hint: Invalid URL format!", this.remoteURL);
        }
        return remoteWebDriver;
    }

    @Override
    public WebDriver containerDriver() {
        RemoteWebDriver containerWebDriver = null;
        try {
            Path path = Paths.get(System.getProperty("user.dir"), "target", "VideoRecording");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            if (isVideoRecording) {
                log.atWarn().log("[RAF] VCN Recording is enabled to record test failure! Find the video file in ../target/VideoRecording directory.");
//                localContainer = new BrowserWebDriverContainer("selenium/standalone-chrome-debug:3.141.59")
//                        .withCapabilities(this.browserOptions());
//                        .withRecordingMode(BrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING, new File(path.toString()))
//                        .withRecordingFileFactory(new DefaultRecordingFileFactory());
                localContainer = new LocalBrowserWebDriverContainer()
                        .withRecordingMode(LocalBrowserWebDriverContainer.VncRecordingMode.RECORD_FAILING, new File(path.toString()))
                        .withRecordingFileFactory(new DefaultRecordingFileFactory());
            } else {
                localContainer = new LocalBrowserWebDriverContainer()
                        .withCapabilities(this.browserOptions())
                        .withRecordingMode(LocalBrowserWebDriverContainer.VncRecordingMode.SKIP, new File(path.toString()));
            }

            localContainer.withCapabilities(this.browserOptions());
            localContainer.setShmSize(DataSize.parse("2GB").toBytes());
            localContainer.start();

            containerWebDriver = localContainer.getWebDriver();
            containerWebDriver.setFileDetector(new LocalFileDetector());
            log.atInfo().log("[RAF] Selenium Remote Address  : {}", localContainer.getSeleniumAddress());
            log.atInfo().log("[RAF] VNC Debug Address : {}", localContainer.getVncAddress());
        } catch (Exception ex) {
            log.atError().withThrowable(ex).log("[RAF] Failed to spin up docker Container WebDriver with Selenium Address '{}'. " +
                    "Hint: Check your docker configuration/setting.", localContainer.getSeleniumAddress());
        }
        return containerWebDriver;
    }

    public LocalBrowserWebDriverContainer getBrowserContainer() {
        return localContainer;
    }

    @Override
    // Factory method to decide type of method to spin up the browser
    public WebDriver getBrowser() {
        WebDriver driver = null;
        switch (this.method.toUpperCase()) {
            case "REMOTE": // Call this remoteDriver() which uses browser-specific browserOptions() implementation
                log.atDebug().log("[RAF] Requested to use Remote WebDriver with Hub URL : {}.", this.remoteURL);
                driver = remoteDriver();
                break;
            case "LOCAL": // Call browser-specific implementation of Local webdriver
                log.atDebug().log("[RAF] Requested to use Local WebDriver.");
                driver = localDriver();
                break;
            case "CONTAINER": //call browser-specific implemetns of docker container
                log.atDebug().log("[RAF] Requested to use Selenium Docker Container WebDriver.");
                driver = containerDriver();
                break;
            default:
                log.atWarn().log("[RAF] Provided seleniumMethod={} is not valid, it will be defaulted to LOCAL. Please fix this in your config/uiconfig.ini file", this.method);
                driver = localDriver();
                break;
        }
        return driver;
    }
}