package com.raf.ui.driver.listeners;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.events.AbstractWebDriverEventListener;

public class WebDriverEventListener extends AbstractWebDriverEventListener {
    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        //wip
        throw new UnsupportedOperationException();
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        //wip
        throw new UnsupportedOperationException();
    }
}
