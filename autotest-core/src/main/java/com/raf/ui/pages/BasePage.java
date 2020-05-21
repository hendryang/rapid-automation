package com.raf.ui.pages;

import com.raf.config.ConfigProvider;
import com.raf.ui.component.PageAction;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

//todo - General - javadocs is not showing on client application

@Log4j2
public class BasePage {
    @Autowired
    protected WebDriver webDriver;


    @Autowired
    protected ConfigProvider configProvider;

    @Autowired
    protected PageAction pageAction;

    protected String pageTitle;
    protected String pageUrl;

    public static final int SHORT_WAIT_SECONDS = 10;
    public static final int MEDIUM_WAIT_SECONDS = 30;
    public static final int LONG_WAIT_SECONDS = 60;
    public static final int SUPER_LONG_WAIT_SECONDS = 300;

    /**
     * @deprecated As of RAF 2.0 , Please choose following options instead: <br>
     * <ul>
     *     <li>Use {@link PageAction} class. It comes with built-in actions to handle basic element interaction</li>
     *     <li>If above doesn't suit your need, you can implement your own explicit wait with {@link PageAction#getFluentWait(int)}</li>
     * </ul>
     */
    @Deprecated
    protected static WebDriverWait shortWait, mediumWait, longWait, superLongWait;


    @PostConstruct
    private void postInit() {
        log.atTrace().log("[RAF] Page {} is instantiated", () -> this.getClass().getName());
    }

    /**
     * @deprecated As of RAF 2.0 , Please choose following options instead: <br>
     * <ul>
     *     <li>Use {@link PageAction} class. It comes with built-in actions to handle basic element interaction</li>
     *     <li>If above doesn't suit your need, you can implement your own explicit wait with {@link PageAction#getFluentWait(int)}</li>
     * </ul>
     */
    @Deprecated
    protected void setupWait() {
        shortWait = new WebDriverWait(webDriver, SHORT_WAIT_SECONDS);
        mediumWait = new WebDriverWait(webDriver, MEDIUM_WAIT_SECONDS);
        longWait = new WebDriverWait(webDriver, LONG_WAIT_SECONDS);
        superLongWait = new WebDriverWait(webDriver, SUPER_LONG_WAIT_SECONDS);
    }

    /**
     * Method to initialize all the elements specified in PageObject's  {@code @FindBy()} annotation.<br>
     * Additionally, the specified page title and URL will also be set which is mainly for verification purpose of - {@link #isAt()}
     *
     * @param thePage   The page object to be initialized (normally is {@code this})
     * @param pageTitle The title of the given page object
     * @param pageUrl   The URL of the given page object ( the base URL will be automatically appended if ignored)
     */
    public void initPage(Object thePage, String pageTitle, String pageUrl) {
        PageFactory.initElements(getWebDriver(), thePage);
        setPageTitle(pageTitle);
        setPageUrl(pageUrl);
    }

    /**
     * Method to initialize all the elements specified in PageObject's  {@code @FindBy()} annotation.<br>
     * Additionally, the specified page title and URL will also be set which is mainly for verification purpose of - {@link #isAt()}
     *
     * @param thePage The page object to be initialized (normally is {@code this})
     */
    public void initPage(Object thePage) {
        initPage(thePage, "", "");
    }

    public WebDriver getWebDriver() {
        return webDriver;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        if (pageTitle.equalsIgnoreCase("")) {
            log.atWarn().log("[RAF] Empty page title is detected on page [{}], the title is used by isAt() assertion and it might fail.", this.getClass().getSimpleName());
            this.pageTitle = pageTitle;
        } else {
            log.atInfo().log("[RAF] Page title for [{}] is set to [{}]", this.getClass().getSimpleName(), pageTitle);
            this.pageTitle = pageTitle;
        }
    }

    /**
     * Method to get the current configured page url. Configuration can be done by either {@link #initPage(Object, String, String)} or {@link #setPageUrl(String)}. <br>
     * This method do <b>NOT</b> return the current URL as shown in browser, to do so please use {@code getWebDriver().getCurrentUrl()}
     *
     * @return configured page url
     */
    public String getPageUrl() {
        return pageUrl;
    }

    /**
     * Method to set the current page URL. <br>
     * <ul>
     *     <li>If absolute URL is provided (eq. {@code https://myprojecturl.com}), it will be used AS-IS and overwrite the <b>baseUri</b> in <b>uiconfig.ini</b> file <br></li>
     *     <li>If relative URL is provided (eq. {@code /dashboard}), it will be appended to the <b>baseUri</b> in <b>uiconfig.ini</b> file.</li>
     * </ul>
     *
     * @param pageUrl the URL of the page (either absolute or relative)
     * @see #initPage(Object, String, String)
     */
    public void setPageUrl(String pageUrl) {
        if (pageUrl.startsWith("http") || pageUrl.equalsIgnoreCase("")) {
            this.pageUrl = pageUrl;
        } else {
            String base = configProvider.getUiBaseUri();
            if (StringUtils.endsWith(base, "/")) {
                base = StringUtils.substringBeforeLast(base, "/");
            }
            if (StringUtils.startsWith(pageUrl, "/")) {
                pageUrl = StringUtils.substringAfter(pageUrl, "/");
            }
            this.pageUrl = StringUtils.join(base, "/", pageUrl);
        }
    }

    /**
     * Method to verify if the page landed correctly based on the {@link #pageTitle} and {@link #pageUrl} <br>
     *
     * @return true if page title and url matched after landing, otherwise false.
     */
    public boolean isAt() {
        if (pageUrl.equalsIgnoreCase("")) {
            log.atError().log("[RAF] Class {} has NO page URL, isAt() will always return false", this.getClass().getSimpleName());
            return false;
        }

        if (pageTitle.equalsIgnoreCase("")) { //if only url exist
            try {
                pageAction
                        .waitUntilPageIsLoaded()
                        .getFluentWait(30)
                        .until(ExpectedConditions.urlContains(this.getPageUrl()));
                log.atWarn().log("[RAF] Class {} has NO page title, isAt() will always return false", this);
                return true;
            } catch (TimeoutException timeOut) {
                log.atError().withThrowable(timeOut).log("[RAF] Page URL doesn't match [Title checking is skipped as the expected wasn't defined]");
                return false;
            }
        } else { //if both url and title exist
            try {
                pageAction
                        .waitUntilPageIsLoaded()
                        .waitUntilPageTitleAndURLMatch(this.getPageTitle(), this.getPageUrl());
                log.atDebug().log("[RAF] Landed on the page");
                return true;
            } catch (TimeoutException timeOut) {
                log.atError().withThrowable(timeOut).log("[RAF] Page title and URL doesn't match");
                return false;
            }
        }
    }

    public boolean isAt(WebElement webElement) {
//        if (isCheckTitleAndUrl && !isAt()) {
//            return false;
//        }
        pageAction.waitUntilElementIsVisible(webElement);
        return true;
    }

    /**
     * Method to go to current page based on specified {@link #pageUrl}<br>
     */
    public void goTo() {
        if (pageUrl.equalsIgnoreCase("")) {
            log.atWarn().log("[RAF] Page at class {} has no URL defined, you cannot perform goTo()", this.getClass().getSimpleName());
        } else {
            long debugStartTime = System.nanoTime();
            this.webDriver.get(pageUrl);
            log.atTrace().log("[RAF] Speed -> Operation GoTo " + pageUrl + " took : {} ms", () -> (System.nanoTime() - debugStartTime) / 1_000_000);
            //   this.isAt();
        }
    }

    /**
     * @return webdriver instance
     * @deprecated As of RAF 2.0 , Please choose following options instead: <br>
     * <ul>
     *     <li>Use {@link PageAction} class. It comes with built-in actions to handle basic element interaction</li>
     *     <li>If above doesn't suit your need, you can retrieve the driver with {@link PageAction#getWebDriver()} </li>
     * </ul>
     */
    @Deprecated
    public WebDriver getDriver() {
        return this.webDriver;
    }
}
