package com.raf.libinjection;

import com.raf.config.ConfigProvider;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Log4j2
@Configuration
public class ConfigBeans {
    private static final String UI_CONFIG = "config/uiconfig.ini";
    private static final String API_CONFIG = "config/apiconfig.ini";
    private static final String ENV = System.getProperty("env", "qa").toLowerCase();
    private static final String GENERAL_CONFIG = "general-config";
    private static final String BASE_URI = "baseUri";
    private static final String TOKEN_URI = "tokenUri";
    private static final String LOGIN_BASE_URI = "loginBaseUri";
    private static final String BROWSER = "seleniumBrowser";
    private static final String METHOD = "seleniumMethod";
    private static final String REMOTE_URL = "seleniumRemoteURL";
    private static final String RECORDING = "seleniumRecording";
    private static final String IMPLICIT_TIMEOUT = "defaultImplicitTimeout";

    @Bean
    public ConfigProvider initConfigProvider() {
        log.atTrace().log("[RAF] ConfigBeans -> init ConfigProvider ");
        ConfigProvider configProvider = new ConfigProvider();
        InputStream apiProperties = getClass().getClassLoader().getResourceAsStream(API_CONFIG);
        InputStream uiProperties = getClass().getClassLoader().getResourceAsStream(UI_CONFIG);

        try {
            //throw error when unable to find any configuration files (ui and api).
            if (Objects.isNull(apiProperties) && Objects.isNull(uiProperties)) {
                throw new NullPointerException(String.format("Unable to find both [%s] and [%s] under project resources.", UI_CONFIG, API_CONFIG));
            }

            // Reading config/apiconfig.ini for API-related test configuration
            if (!Objects.isNull(apiProperties)) {
                Ini ini = new Ini(apiProperties);
                String apiBaseUri = ini.get(ENV, BASE_URI);
                if (Objects.isNull(apiBaseUri)) {
                    throw new NullPointerException(String.format("[RAF] Unable to find setting [%s] under section [%s] in [%s] config file.", BASE_URI, ENV, API_CONFIG));
                } else if (apiBaseUri.equalsIgnoreCase("")) {
                    log.atWarn().log("[RAF] Value of [{}] in section [{}] under '{}' config file is empty.", BASE_URI, ENV, API_CONFIG);
                }
                configProvider.setApiBaseUri(apiBaseUri);
                configProvider.setApiTokenUri(ini.get(ENV, TOKEN_URI));
            } else {
                log.atWarn().log("[RAF] Unable to find [{}] config file under your resources.", API_CONFIG);
            }

            // Reading config/uiconfig.ini for UI-related test configuration
            if (!Objects.isNull(uiProperties)) {
                Ini ini = new Ini(uiProperties);
                Section section = ini.get(ENV);
                String loglevel = ini.get(GENERAL_CONFIG, "loglevel", String.class).toUpperCase();

                //set logging level
                Configurator.setAllLevels(LogManager.getRootLogger().getName(), Level.getLevel(loglevel));
                log.atDebug().log("[RAF] Configuration for logger: " + loglevel);
                if (Objects.isNull(section.get(BASE_URI))) {
                    throw new NullPointerException(String.format("[RAF] Unable to find setting [%s] under section [%s] in [%s] config file.", BASE_URI, ENV, UI_CONFIG));
                } else if (section.get(BASE_URI).equalsIgnoreCase("") || section.get(BASE_URI).equalsIgnoreCase("\"\"")) {
                    log.atWarn().log("[RAF] Value of [{}] in section [{}] under '{}' config file is empty.", BASE_URI, ENV, API_CONFIG);
                }
                configProvider.setEnv(ENV)
                        .setUiBaseUri(section.get(BASE_URI))
                        .setUiLoginBaseUri(section.getOrDefault(LOGIN_BASE_URI, ""))
                        .setUiSeleniumBrowser(section.getOrDefault(BROWSER, ""))
                        .setUiSeleniumMethod(section.getOrDefault(METHOD, ""))
                        .setUiSeleniumRemoteURL(section.getOrDefault(REMOTE_URL, ""))
                        .setUiSeleniumRecording(Boolean.parseBoolean(section.getOrDefault(RECORDING, "false")))
                        .setDefaultImplicitTimeout(section.getOrDefault(IMPLICIT_TIMEOUT, "0"));
            } else {
                log.atWarn().log("[RAF] Unable to find [{}] config file under your resources.", UI_CONFIG);
            }
        } catch (IOException ex) {
            log.atError().withThrowable(ex).log("[RAF] Some Problem reading the configuration files, refer to below stacktrace for more details.");
        } catch (NullPointerException ex) {
            log.atError().withThrowable(ex).log("[RAF] Misconfiguration detected, refer to below NullPointerException stacktrace for more details.");
        }

        log.atDebug().log("[RAF] Configuration for Test \t: {}", configProvider);
        return configProvider;
    }
}