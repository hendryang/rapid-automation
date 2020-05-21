package com.raf.config;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class ConfigProvider {
    private String env;
    private String apiBaseUri;
    private String apiTokenUri;
    private String uiBaseUri;
    private String uiLoginBaseUri;
    private String uiSeleniumBrowser;
    private String uiSeleniumMethod;
    private String uiSeleniumRemoteURL;
    private boolean uiSeleniumRecording;
    private long defaultImplicitTimeout;

    private static final String DEFAULT = "default";

    @Override
    public String toString() {
        return "ConfigProvider{" +
                "env='" + env + '\'' +
                ", apiBaseUri='" + apiBaseUri + '\'' +
                ", apiTokenUri='" + apiTokenUri + '\'' +
                ", uiBaseUri='" + uiBaseUri + '\'' +
                ", uiLoginBaseUri='" + uiLoginBaseUri + '\'' +
                ", uiSeleniumBrowser='" + uiSeleniumBrowser + '\'' +
                ", uiSeleniumMethod='" + uiSeleniumMethod + '\'' +
                ", uiSeleniumRemoteURL='" + uiSeleniumRemoteURL + '\'' +
                ", uiSeleniumRecording='" + uiSeleniumRecording + '\'' +
                ", defaultImplicitTimeout=" + defaultImplicitTimeout +
                '}';
    }

    public ConfigProvider setEnv(String env) {
        this.env = env;
        return this;
    }

    public String getEnv() {
        return env;
    }

    //todo - review API config
    public ConfigProvider setApiBaseUri(String apiBaseUri) {
        this.apiBaseUri = apiBaseUri;
        return this;
    }

    public String getApiBaseUri() {
        return apiBaseUri;
    }

    public ConfigProvider setApiTokenUri(String apiTokenUri) {
        this.apiTokenUri = apiTokenUri;
        return this;
    }

    public String getApiTokenUri() {
        return apiTokenUri;
    }

    public ConfigProvider setUiBaseUri(String uiBaseUri) {
        this.uiBaseUri = uiBaseUri;
        return this;
    }

    public String getUiBaseUri() {
        return uiBaseUri;
    }

    public ConfigProvider setUiLoginBaseUri(String uiLoginBaseUri) {
        this.uiLoginBaseUri = uiLoginBaseUri;
        return this;
    }

    public String getUiLoginBaseUri() {
        return uiLoginBaseUri;
    }


    public ConfigProvider setUiSeleniumBrowser(String uiSeleniumBrowser) {
        this.uiSeleniumBrowser = uiSeleniumBrowser;
        if (uiSeleniumBrowser.equalsIgnoreCase(DEFAULT) || uiSeleniumBrowser.equalsIgnoreCase("")) {
            this.uiSeleniumBrowser = "chrome";
        }
        return this;
    }

    public String getUiSeleniumBrowser() {
        return uiSeleniumBrowser;
    }

    public ConfigProvider setUiSeleniumMethod(String uiSeleniumMethod) {
        this.uiSeleniumMethod = uiSeleniumMethod;
        if (uiSeleniumMethod.equalsIgnoreCase(DEFAULT) || uiSeleniumMethod.equalsIgnoreCase("")) {
            this.uiSeleniumMethod = "local";
        }
        return this;
    }

    public String getUiSeleniumMethod() {
        return uiSeleniumMethod;
    }

    public ConfigProvider setUiSeleniumRemoteURL(String uiSeleniumRemoteURL) {
        this.uiSeleniumRemoteURL = uiSeleniumRemoteURL;
        if (uiSeleniumRemoteURL.equalsIgnoreCase(DEFAULT) || uiSeleniumRemoteURL.equalsIgnoreCase("")) {
            this.uiSeleniumRemoteURL = "";
        }
        return this;
    }

    public String getUiSeleniumRemoteURL() {
        return uiSeleniumRemoteURL;
    }


    public ConfigProvider setDefaultImplicitTimeout(String timeout) {

        if (timeout.equalsIgnoreCase(DEFAULT) || timeout.equalsIgnoreCase("0") || timeout.equalsIgnoreCase("")) {
            this.defaultImplicitTimeout = 0L;
        } else {
            this.defaultImplicitTimeout = Long.parseLong(timeout);
        }
        return this;
    }

    public long getDefaultImplicitTimeout() {
        return this.defaultImplicitTimeout;
    }


    public boolean isUiSeleniumRecording() {
        return uiSeleniumRecording;
    }

    public ConfigProvider setUiSeleniumRecording(boolean uiSeleniumRecording) {
        this.uiSeleniumRecording = uiSeleniumRecording;
        return this;
    }
}