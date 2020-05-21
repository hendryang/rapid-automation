package com.raf.config;

//todo - WIP
public class BrowserConfig {
    private boolean isCloseBrowser;

    public BrowserConfig setCloseBrowser(boolean command) {
        isCloseBrowser = command;
        return this;
    }

    public boolean isCloseBrowser() {
        return isCloseBrowser;
    }
}
