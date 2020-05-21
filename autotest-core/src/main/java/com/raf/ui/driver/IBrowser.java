package com.raf.ui.driver;

import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

public interface IBrowser {

    //implemented by browser-specific instance (eq. ChromeImpl, FirefoxImpl )
    MutableCapabilities browserOptions();

    //implemented by browser-specific instance (eq. ChromeImpl, FirefoxImpl )
    WebDriver localDriver();

    //implemented by general AbstractBrowser
    WebDriver remoteDriver() throws MalformedURLException;

    //implemented by general AbstractBrowser
    WebDriver getBrowser();

    //implemented by general AbstractBrowser
    WebDriver containerDriver();
}
