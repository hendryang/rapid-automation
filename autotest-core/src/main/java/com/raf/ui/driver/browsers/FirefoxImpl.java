package com.raf.ui.driver.browsers;

import com.raf.ui.driver.AbstractBrowser;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

//todo - placeholder
public class FirefoxImpl extends AbstractBrowser {

    public FirefoxImpl(String method, String remoteURL, Boolean isVideoRecording) {
        super(method, remoteURL, isVideoRecording);
    }

    @Override
    public MutableCapabilities browserOptions() {
        return null;
    }

    @Override
    public WebDriver localDriver() {
        return null;
    }
}
