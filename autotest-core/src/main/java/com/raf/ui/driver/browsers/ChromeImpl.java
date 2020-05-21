package com.raf.ui.driver.browsers;

import com.raf.ui.driver.AbstractBrowser;
import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.log4j.Log4j2;
import org.ini4j.Ini;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log4j2
public class ChromeImpl extends AbstractBrowser {
    private Ini ini;
    private static final String BROWSER_CONFIG = "config/browser.ini";
    private static final String CHROME_GENERAL = "chrome-general";
    private static final String CHROME_ARGUMENTS = "chrome-arguments";
    private static final String CHROME_PREFERENCES = "chrome-preferences";
    private static final String CHROME_CAPABILITIES = "chrome-capabilities";
    private static final String DEFAULT = "default";

    public ChromeImpl(String method, String remoteURL, boolean isVideoRecording) {
        super(method, remoteURL, isVideoRecording);
        try {
            ini = new Ini(getClass().getClassLoader().getResourceAsStream(BROWSER_CONFIG));
        } catch (IOException | NullPointerException ex) {
            log.atError().withThrowable(ex).log("[RAF] Failed to retrieve browser configuration file located at '{}'.", BROWSER_CONFIG);
        }
    }

    @Override
    public WebDriver localDriver() {
        long debugStartTime = System.nanoTime();
        String chromeVersion = ini.get(CHROME_GENERAL).get("version");
        if (chromeVersion.equalsIgnoreCase(DEFAULT)) {
            WebDriverManager.chromedriver().setup();
        } else {
            WebDriverManager.chromedriver().version(chromeVersion).setup();
            log.atDebug().log("[RAF] User requested to use chrome version " + chromeVersion);
        }
        log.atTrace().log("[RAF] Speed -> WebDriverManager setup took : {} ms", () -> (System.nanoTime() - debugStartTime) / 1_000_000);
        log.atDebug().log("[RAF] Test will be running against chrome version : {}", () -> WebDriverManager.chromedriver().getDownloadedVersion());
        long debugStartTime2 = System.nanoTime();
        ChromeDriver c = new ChromeDriver((ChromeOptions) browserOptions());
        log.atTrace().log("[RAF] Speed -> new (ChromeDriver) took : {} ms", () -> (System.nanoTime() - debugStartTime2) / 1_000_000);
        return c;
    }

    /**
     * Build entire chrome options with following details:
     * <ul>
     *     <li>Chrome arguments with {@link #setChromeArguments(ChromeOptions)}</li>
     *     <li>Chrome capabilities with {@link #setChromeCapabilities(ChromeOptions)}</li>
     *     <li>Chrome preferences via with {@link #setChromePreferences(ChromeOptions)}</li>
     *     <li>Global capabilities via with {@link #setGlobalDesiredCapabilities(ChromeOptions)}</li>
     * </ul>
     *
     * @return ChromeOptions to be used by both Local and Remote WebDriver
     * @see <a href="https://src.chromium.org/viewvc/chrome/trunk/src/chrome/common/pref_names.cc?view=markup">Chrome Preferences - Reference 1</a>
     * @see <a href="https://chromium.googlesource.com/chromium/src/+/master/chrome/common/pref_names.cc">Chrome Preferences - Reference 2</a>
     * @see <a href="https://github.com/GoogleChrome/chrome-launcher/blob/master/docs/chrome-flags-for-tools.md">Chrome Flags - Reference 1</a>
     * @see <a href="https://peter.sh/experiments/chromium-command-line-switches/">Chrome Flags - Reference 2</a>
     */
    @Override
    public MutableCapabilities browserOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        this
                .setChromeArguments(chromeOptions)
                .setChromePreferences(chromeOptions)
                .setChromeCapabilities(chromeOptions)
                .setGlobalDesiredCapabilities(chromeOptions);

        //todo - Move chrome proxy settings to browser.ini
        Proxy proxy = new Proxy();
        proxy.setAutodetect(false);
        proxy.setProxyType(Proxy.ProxyType.MANUAL);
        proxy.setNoProxy("");
        chromeOptions.setCapability("proxy", proxy);
        return chromeOptions;
    }

    // Settings chrome arguments by reading from [CHROME_ARGUMENTS] section
    private ChromeImpl setChromeArguments(ChromeOptions chromeOptions) {
        List<String> chromeSwitches = new ArrayList<>();
        Map<String, String> arguments = ini.get(CHROME_ARGUMENTS);
        arguments.entrySet().stream()
                .filter(map -> !(map.getValue().equalsIgnoreCase(DEFAULT) || map.getValue().equalsIgnoreCase("false")))  //If browser.ini has value of 'default' or 'false', it'll be ignored.
                .forEach(opt -> {
                    if ("true".equalsIgnoreCase(opt.getValue())) { //if 'true', get the Key from Map<K,V> and use it as part of the switch list.
                        chromeSwitches.add(opt.getKey());
                    } else { // otherwise , add the whole thing (eq. --window-size=1920,1080)
                        chromeSwitches.add(opt.getKey() + "=" + opt.getValue());
                    }
                });
        log.atDebug().log("[RAF] Chrome Switches \t\t: {}", chromeSwitches);
        chromeOptions.addArguments(chromeSwitches);
        return this;
    }

    // Settings chrome preferences by reading from [CHROME_PREFERENCES] section
    private ChromeImpl setChromePreferences(ChromeOptions chromeOptions) {
        HashMap<String, Object> chromePreferences = new HashMap<>();
        Map<String, String> preferences = ini.get(CHROME_PREFERENCES);
        preferences.entrySet().stream()
                .filter(map -> !(map.getValue().equalsIgnoreCase(DEFAULT) || map.getValue().equalsIgnoreCase("0")))
                .forEach(opt -> {
                    switch (opt.getValue().toLowerCase()) {
                        case "1":
                        case "2":
                            chromePreferences.put(opt.getKey(), Integer.parseInt(opt.getValue()));
                            break;
                        case "true":
                        case "false":
                            chromePreferences.put(opt.getKey(), Boolean.parseBoolean(opt.getValue()));
                            break;
                        default:
                            chromePreferences.put(opt.getKey(), opt.getValue());
                            break;
                    }
                });
        log.atDebug().log("[RAF] Chrome Preferences \t: [{}]", chromePreferences);
        chromeOptions.setExperimentalOption("prefs", chromePreferences);
        return this;
    }

    // Settings chrome capabilities by reading from [CHROME_CAPABILITIES] section
    private ChromeImpl setChromeCapabilities(ChromeOptions chromeOptions) {
        Map<String, String> options = ini.get(CHROME_CAPABILITIES);
        options.entrySet().stream()
                .filter(map -> !map.getValue().equalsIgnoreCase(DEFAULT))
                .forEach(opt -> {
                    switch (opt.getKey().toLowerCase()) {
                        case "runheadless":
                            chromeOptions.setHeadless(Boolean.parseBoolean(opt.getValue()));
                            break;
                        case "acceptinsecurecerts":
                            chromeOptions.setAcceptInsecureCerts(Boolean.parseBoolean(opt.getValue()));
                            break;
                        case "pageloadstrategy":
                            chromeOptions.setPageLoadStrategy(PageLoadStrategy.fromString(opt.getValue()));
                            break;
                        case "unhandledpromptbehavior":
                            chromeOptions.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.fromString(opt.getValue()));
                            break;
                        default:
                            log.atWarn().log("[RAF] browser.ini contains invalid configuration under section [{}], Please contact RAF team if you need this setting.", opt.getKey() + "=" + opt.getValue());
                            break;
                    }
                });
        return this;
    }

    // Settings chrome global capabilities from its parent's getCapabilities() method.
    private ChromeImpl setGlobalDesiredCapabilities(ChromeOptions chromeOptions) {
        getCapabilities().forEach(chromeOptions::setCapability);
        log.atDebug().log("[RAF] Chrome Capabilities \t: [{}]", chromeOptions);
        return this;
    }
}