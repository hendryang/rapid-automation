package com.raf.ui.component;

import com.raf.ui.component.buttontoggle.ButtonToggleGroup;
import com.raf.ui.component.dropdown.DropDownMockBuilder;
import com.raf.ui.component.slider.Slider;
import com.raf.ui.component.toggle.ToggleComponent;
import com.raf.ui.pages.BasePage;
import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.raf.ui.component.CustomExpectedConditions.*;
import static com.raf.ui.component.JSCommand.*;

@Log4j2
public class PageAction {

    @Autowired
    private WebDriver webDriver;

    private JavascriptExecutor javascriptExecutor;
    private static PageAction pageActionBuilder;
    private PageActionExceptionHandler paExHandler;
    private FluentWait<WebDriver> fluentWait;

    private static final String RAF = "[RAF]";
    private static final int SHORT_WAIT_SECONDS = 10;
    private static final int MEDIUM_WAIT_SECONDS = 30;
    private int innerTimeOut = MEDIUM_WAIT_SECONDS;

    @PostConstruct
    public void postInitialize() {
        javascriptExecutor = (JavascriptExecutor) webDriver;
        initFluentWait();

        this.paExHandler = new PageActionExceptionHandler();
        pageActionBuilder = new PageAction(webDriver);
    }

    public PageAction() {
    }

    private PageAction(WebDriver webDriver) {
        this.webDriver = webDriver;
        this.paExHandler = new PageActionExceptionHandler();
        javascriptExecutor = (JavascriptExecutor) webDriver;
        initFluentWait();
    }

    /* ====== PAGE ACTION - CONFIGURATION SECTION =====*/
    //create FluentWait object, which can be accessed via getFluentWait(...) method.
    //todo - remove common exceptions list ?
    private void initFluentWait() {

        fluentWait = new FluentWait<>(webDriver)
                .withTimeout(Duration.ofSeconds(MEDIUM_WAIT_SECONDS))
                .pollingEvery(Duration.ofSeconds(1))
                .ignoreAll(Arrays.asList(
                        NoSuchElementException.class,
                        StaleElementReferenceException.class)
                );
    }

    /**
     * Get javascript executor instance
     *
     * @return JavaScriptExecutor instance
     */
    public JavascriptExecutor getJS() {
        return this.javascriptExecutor;
    }

    /**
     * Get instance of WebDriver
     *
     * @return WebDriver instance
     */
    public WebDriver getWebDriver() {
        return this.webDriver;
    }

    /**
     * Method to define maximum wait time to interact with element. <br>
     * By default, the method will keep retrying certain action against the {@code webElement} for maximum of {@value MEDIUM_WAIT_SECONDS} seconds <br>
     *
     * <br><b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Enter text with default timeout (30s)
     *     pageAction.enterText(txtUsername, "hello world");
     *
     *     //Enter text with 10s timeout
     *     pageAction.withTimeout(10).enterText(txtUsername, "hello world");
     *
     *     //chaining with another actions, both enterText actions will have timeout of 10s and click will have timeout of 20s
     *     pageAction
     *         .withTimeout(10)
     *         .enterText(txtUsername, "hello world")
     *         .enterText(txtPassword , "secret!@")
     *         .withTimeout(20)
     *         .click(btnLogin);
     *     }
     * </pre>
     *
     * @param timeoutInSeconds Timeout for the actions
     * @return {@code PageAction} for chaining purpose
     * @see #resetSettings() Reset the settings
     */
    public PageAction withTimeout(int timeoutInSeconds) {
        pageActionBuilder.innerTimeOut = Math.max(timeoutInSeconds, 0);
        log.atInfo().log("{} Timeout for page action is set to {}s", RAF, pageActionBuilder.innerTimeOut);
        return pageActionBuilder;
    }

    public PageAction withIgnoreError() {
        log.atWarn().log("{} Ignore error is set to True, all actions that throw error will be ignored.", RAF);
        pageActionBuilder.paExHandler.setIgnoreError(true);
        return pageActionBuilder;
    }


    /**
     * Method to reset the wait time to default {@value MEDIUM_WAIT_SECONDS} and set ignore error to False<br>
     * Only relevant if the timeout has been modified by {@link #withTimeout(int)}
     *
     * <br><b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Basic reset to default timeout (30s)
     *     pageAction.resetTimeout();
     *
     *     //Use 10s timeout for entering text and then reset it back to default before clicking
     *     pageAction
     *          .withTimeout(10)
     *          .enterText(txtUsername, "hello world")
     *          .resetTimeout()
     *          .click(btnSignUp);
     *     }
     * </pre>
     *
     * @return {@code PageAction} for chaining purpose
     * @see #withTimeout(int) Modify the timeout
     */
    public PageAction resetSettings() {
        log.atDebug().log("{} Timeout for page action is reset back to {}s", RAF, MEDIUM_WAIT_SECONDS);
        pageActionBuilder.innerTimeOut = MEDIUM_WAIT_SECONDS;
        pageActionBuilder.paExHandler.setIgnoreError(false);
        return pageActionBuilder;
    }

    /**
     * Get the FluentWait instance and with specified {@code durationInSeconds} timeout. <br>
     * <br>
     * <b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Wait for title to contains "test" and keep repeat the actions for 30s
     *     pageAction.getFluentWait(30).until(ExpectedConditions.titleIs("test"));
     *      }
     * </pre>
     *
     * @param durationInSeconds custom maximum wait time in seconds
     * @return FluentWait instance
     */
    public FluentWait<WebDriver> getFluentWait(int durationInSeconds) {
        return fluentWait.withTimeout(Duration.ofSeconds(durationInSeconds));
    }

    /**
     * <p>Get the FluentWait instance and with specified {@code durationInSeconds} timeout. <br>
     * It is also possible to supply list of exceptions to be ignored if they happen to occur in the middle of the repetitive {@code wait.until(...)} operations,
     * List of ignored exceptions will not interrupt and terminate the <b>until(...)</b>, this also means it'll not be thrown hence making {@code try...catch...} to be useless.
     * However, it'll be part of the <b>caused by</b> in {@code TimeoutException} stacktrace. </p>
     * <br>
     * <b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Wait for title to contains "test" and keep repeat the actions for 30s
     *     //even NoSuchElementException or InvalidElementStateException occur
     *
     *     getFluentWait(innerTimeOut,
     *                 NoSuchElementException.class,
     *                 InvalidElementStateException.class)
     *                 .until(titleIs("test"));
     *      }
     * </pre>
     *
     * @param durationInSeconds  custom maximum wait time in seconds
     * @param exceptionsToIgnore - list of exceptions to be ignored if they occured.
     * @return FluentWait instance
     */
    @SafeVarargs
    public final FluentWait<WebDriver> getFluentWait(int durationInSeconds, Class<? extends Throwable>... exceptionsToIgnore) {
        return fluentWait
                .withTimeout(Duration.ofSeconds(durationInSeconds))
                .ignoreAll(Arrays.asList(exceptionsToIgnore));
    }

    public PageAction goToURL(String url) {
        webDriver.navigate().to(url);
        return this;
    }

    /*===== PAGE ACTION - INTERACTING WITH PRIMITIVE ELEMENT =====*/

    //todo - screenshot capability here.

    /**
     * <p>Method to enter {@code inputString} value into the {@code webElement}. The {@code webElement} is typically a textbox. <br>
     * By default, the method will wait for maximum of {@value MEDIUM_WAIT_SECONDS} seconds if there's issue in entering text into {@code webElement}.
     * Timeout can be optionally set with {@link #withTimeout(int)} <br></p>
     *
     * <br><b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Enter text with default timeout (30s)
     *     pageAction.enterText(txtUsername, "hello world");
     *
     *     //Enter text with 10s timeout
     *     pageAction.withTimeout(10).enterText(txtUsername, "hello world");
     *
     *     //chaining with another actions after enter text
     *     pageAction
     *         .enterText(txtUsername, "hello world")
     *         .enterText(txtPassword , "secret!@")
     *         .click(btnLogin);
     *     }
     * </pre>
     *
     * @param webElement  The web element to be handled
     * @param inputString The string value to be keyed into the element
     * @return {@code PageAction} for chaining purpose
     * @see #withTimeout(int) Modify the timeout
     * @see #clearText(WebElement) Clear the text
     */
    public PageAction enterText(WebElement webElement, String inputString) {
        log.atDebug().log("{} Entering text '{}' to '{}' with timeout '{}s'", RAF, inputString, webElement, innerTimeOut);
        paExHandler.handleException(() -> getFluentWait(innerTimeOut,
                NoSuchElementException.class,
                InvalidElementStateException.class)
                .until(textIsEntered(webElement, inputString, true)), "generateHintForEnterText"

        );
        return this;
//        try {
//            log.atDebug().log("{} Entering text '{}' to '{}' with timeout '{}s'", RAF, inputString, webElement, innerTimeOut);
//            getFluentWait(innerTimeOut,
//                    NoSuchElementException.class,
//                    InvalidElementStateException.class)
//                    .until(textIsEntered(webElement, inputString));
//            return this;
//        } catch (TimeoutException timeOut) {
//            generateHintForEnterText(timeOut);
//            throw timeOut;
//        } catch (Exception e) { //basically if there's any exception that is not being ignored. (and it was thrown in the try-block)
//            logNewCase(e.getMessage());
//            throw e;
//        }
    }

    //enter text without checking if the entered text is correct (mainly for negative testing)
    public PageAction enterTextUnsafe(WebElement webElement, String inputString) {
        log.atDebug().log("{} Entering text '{}' to '{}' with timeout '{}s'", RAF, inputString, webElement, innerTimeOut);
        paExHandler.handleException(() -> getFluentWait(innerTimeOut,
                NoSuchElementException.class,
                InvalidElementStateException.class)
                .until(textIsEntered(webElement, inputString, false)), "generateHintForEnterText"

        );
        return this;
    }

    /**
     * <p>Method to clear any existing value at {@code webElement}. The {@code webElement} is typically a text box. <br>
     * By default, the method will wait for maximum of {@value MEDIUM_WAIT_SECONDS} seconds if there's problem in clearing text of {@code webElement}.
     * Timeout can be optionally set with {@link #withTimeout(int)} <br>
     * This method will automatically check if both text and value is empty before proceed, otherwise exception is thrown.<br></p>
     *
     * <br><b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //clear text with default timeout (30s)
     *     pageAction.clearText(txtUsername);
     *     }
     * </pre>
     *
     * @param webElement The web element to be handled
     * @return {@code PageAction} for chaining purpose
     * @see #withTimeout(int) Modify the timeout
     * @see #enterText(WebElement, String) Enter text into element
     */
    public PageAction clearText(WebElement webElement) {
        log.atDebug().log("{} Clearing text of '{}' with timeout '{}s'", RAF, webElement, innerTimeOut);
        paExHandler.handleException(() -> getFluentWait(innerTimeOut,
                NoSuchElementException.class,
                InvalidElementStateException.class)
                .until(textIsCleared(webElement, false)), "generateHintForEnterText"
        );
        return this;
//        try {
//            log.atDebug().log("{} Clearing text of '{}' with timeout '{}s'", RAF, webElement, innerTimeOut);
//            getFluentWait(innerTimeOut,
//                    NoSuchElementException.class,
//                    InvalidElementStateException.class)
//                    .until(textIsCleared(webElement));
//            return this;
//        } catch (TimeoutException timeOut) {
//            generateHintForEnterText(timeOut);
//            throw timeOut;
//        } catch (Exception e) { //basically if there's any exception that is not being ignored.
//            logNewCase(e.getMessage());
//            throw e;
//        }
    }

    public PageAction clearTextByBackspace(WebElement webElement) {
        log.atDebug().log("{} Clearing text of '{}' with timeout '{}s'", RAF, webElement, innerTimeOut);
        paExHandler.handleException(() -> getFluentWait(innerTimeOut,
                NoSuchElementException.class,
                InvalidElementStateException.class)
                .until(textIsCleared(webElement, true)), "generateHintForEnterText"
        );
        return this;
    }

    /**
     * Method to click the {@code webElement}. The {@code webElement} is typically a button or anything clickable. <br>
     * By default, the method will wait for maximum of {@value MEDIUM_WAIT_SECONDS} seconds if there's an issue with clicking {@code webElement} element.
     * Timeout can be optionally set with {@link #withTimeout(int)} <br>
     *
     * <br><b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Click a button with default timeout (30s)
     *     pageAction.clickElement(btnLogMeIn);
     *
     *     //Enter text with 10s timeout
     *     pageAction.withTimeout(10).clickElement(btnLogMeIn);
     *
     *     //chaining with another actions
     *     pageAction
     *         .enterText(txtUsername, "hello world")*
     *         .click(btnVerifyUsername);
     *         .enterText(txtPassword , "secret!@")
     *         .click(btnVerifyPassword);
     *     }
     * </pre>
     *
     * @param webElement The web element to be clicked
     * @return {@code PageAction} for chaining purpose
     * @see #withTimeout(int) Modify the timeout
     */
    public PageAction clickElement(WebElement webElement) {
        //todo what if webElement is null?
        log.atDebug().log("{} Clicking element '{}' with timeout '{}s'", RAF, webElement, innerTimeOut);
        paExHandler.handleException(() -> getFluentWait(innerTimeOut,
                NoSuchElementException.class,
                ElementClickInterceptedException.class)
                .until(elementIsClicked(webElement)), "generateHintForClickElement");

        return this;

//        try {
//            log.atDebug().log("{} Clicking element '{}' with timeout '{}s'", RAF, webElement, innerTimeOut);
//            getFluentWait(innerTimeOut,
//                    NoSuchElementException.class,
//                    ElementClickInterceptedException.class)
//                    .until(elementIsClicked(webElement));
//            return this;
//        } catch (TimeoutException timeOut) {
//            generateHintForClickElement(timeOut);
//            throw timeOut;
//        } catch (Exception e) { //basically if there's any exception that is not being ignored.
//            logNewCase(e.getMessage());
//            throw e;
//        }
    }

    /**
     * Method to click the {@code webElement} if it exist. The {@code webElement} is typically a button or anything clickable. <br>
     * By default, the method will wait for maximum of {@value SHORT_WAIT_SECONDS} seconds if {@code webElement} does not exist.
     * Timeout can be optionally set with {@link #withTimeout(int)} <br>
     * Normally use to click a button that its existence is nondeterministic and random, such as pop-up or "accept cookies"
     * <br><b>Sample Usage : </b>
     * <pre>
     *
     *     {@code
     *     //Click a button with default timeout (10s)
     *     pageAction.clickElementIfExists(btnRandom); //if it doesn't exist, it will go on with the next test. otherwise, it'll be clicked
     *
     *     //Normally, lower timeout is encouraged
     *     pageAction.withTimeout(3).clickElementIfExists(btnRandom);
     *      }
     * </pre>
     *
     * @param webElement The web element to be clicked
     * @return {@code PageAction} for chaining purpose
     * @see #withTimeout(int) Modify the timeout
     */
    public PageAction clickElementIfExists(WebElement webElement) {
        log.atDebug().log("{} Clicking element '{}' if exists, with timeout '{}s'", RAF, webElement, SHORT_WAIT_SECONDS);

        paExHandler.setIgnoreError(true);
        paExHandler.handleException(() -> getFluentWait(SHORT_WAIT_SECONDS,
                Throwable.class) //basically ignore everything.
                .until(elementIsClicked(webElement)), ""
        );
        paExHandler.setIgnoreError(false);
        return this;
//        try {
//            log.atDebug().log("{} Clicking element '{}' if exists, with timeout '{}s'", RAF, webElement, SHORT_WAIT_SECONDS);
//            getFluentWait(SHORT_WAIT_SECONDS,
//                    Throwable.class) //basically ignore everything.
//                    .until(elementIsClicked(webElement));
//            return this;
//        } catch (Exception e) { //basically if there's any exception that is not being ignored - we ignore it again :)
//            log.atDebug().log("{} Element does not exist or not clickable, skip the click.", RAF);
//            return this;
//        }
    }

    /**
     * Method to double click the {@code webElement}. The {@code webElement} is typically a button or anything clickable. <br>
     * By default, the method will wait for maximum of {@value MEDIUM_WAIT_SECONDS} seconds if {@code webElement} does not exist.
     * Timeout can be optionally set with {@link #withTimeout(int)} <br>
     *
     * <br><b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Click a button with default timeout (30s)
     *     pageAction.doubleClickElement(btnDblClickMe);}
     * </pre>
     *
     * @param webElement The web element to be double clicked
     * @return {@code PageAction} for chaining purpose
     * @see #withTimeout(int) Modify the timeout
     */
    public PageAction doubleClickElement(WebElement webElement) {
        log.atDebug().log("{} Double clicking element '{}' with timeout '{}s'", RAF, webElement, innerTimeOut);
        paExHandler.handleException(() -> getFluentWait(innerTimeOut,
                NoSuchElementException.class,
                ElementClickInterceptedException.class)
                .until(elementIsDoubleClicked(webElement)), "generateHintForClickElement"
        );
        return this;
//        try {
//            getFluentWait(innerTimeOut,
//                    NoSuchElementException.class,
//                    ElementClickInterceptedException.class)
//                    .until(elementIsDoubleClicked(webElement));
//            return this;
//        } catch (TimeoutException timeOut) {
//            generateHintForClickElement(timeOut);
//            throw timeOut;
//        } catch (Exception e) {
//            logNewCase(e.getMessage());
//            throw e;
//        }
    }

    /*===== PAGE ACTION - Waiting for certain actions to be completed =====*/

    /**
     * Force thread to sleep or wait for the specified {@code seconds} seconds<br>
     * This method is <b>NOT RECOMMENDED</b>, please use fluent or explicit wait instead.
     * <br><br>
     * <b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Sleep for 1 second before clicking element
     *     pageAction.sleep(1).clickElement(btnLogMeIn);
     *     }
     * </pre>
     *
     * @param seconds - seconds to wait
     * @return {@code PageAction} for chaining purpose
     */
    public PageAction sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ex) {
            log.atError().withThrowable(ex).log("[RAF] Thread interrupted");
            Thread.currentThread().interrupt();
        }
        return this;
    }

    /**
     * Method to wait until page is completely loaded. <br>
     * Page is considered loaded when document.readyState == "complete"
     * <br><br>
     * <b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Click a button with default timeout (30s)
     *     pageAction.waitUntilPageIsLoaded().clickElement(btnLogMeIn);
     *     }
     * </pre>
     *
     * @return {@code PageAction} for chaining purpose
     */
    public PageAction waitUntilPageIsLoaded() {
        log.atDebug().log("{} Waiting for page to be loaded (document=ready) with timeout '{}s'", RAF, innerTimeOut);

        paExHandler.handleException(() -> getFluentWait(innerTimeOut)
                .until(pageIsLoaded()), "generateHint", "Loading page", "document.readyState never 'complete'"
        );
        return this;
//        try {
//            getFluentWait(innerTimeOut)
//                    .until(pageIsLoaded());
//            return this;
//        } catch (TimeoutException timeOut) {
//            generateHint(timeOut, "Loading page", "document.readyState never 'complete'");
//            throw timeOut;
//        } catch (Exception e) {
//            logNewCase(e.getMessage());
//            throw e;
//        }
    }

    /**
     * Method to wait until page title is equals to {@code title} and URL contains {@code url} <br>
     * <br><br>
     * <b>Sample Usage : </b>
     * <pre>
     * </pre>
     *
     * @param title page title to be matched
     * @param url   page url to be matched
     * @return {@code PageAction} for chaining purpose
     */
    public PageAction waitUntilPageTitleAndURLMatch(String title, String url) {
        log.atDebug().log("{} Waiting until page title to be {} and URL contains {} with timeout '{}s", RAF, title, url, innerTimeOut);

        paExHandler.handleException(() -> getFluentWait(innerTimeOut)
                        .until(titleAndUrlMatch(title, url)),
                "generateHint", "waiting for the page title and url match with expected", "wrong url | wrong title | page does not loaded successfully"

        );
        return this;
//        try {
//            getFluentWait(innerTimeOut)
//                    .until(titleAndUrlMatch(title, url));
//            return this;
//        } catch (TimeoutException timeOut) {
//            generateHint(timeOut,
//                    "waiting for the page title and url match with expected",
//                    "wrong url | wrong title | page does not loaded successfully");
//            throw timeOut;
//        } catch (Exception e) {
//            logNewCase(e.getMessage());
//            throw e;
//        }
    }

    /**
     * Method to wait until the specified {@code webElement} is disappear or invisible <br>
     * <br><br>
     * <b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Wait for spinner to finish spin/disappear before click submit
     *     pageAction.waitUntilElementIsInvisible ( spinnerLoading ).clickElement(btnSubmit);
     *     }
     * </pre>
     *
     * @param webElement target web element
     * @return {@code PageAction} for chaining purpose
     */
    public PageAction waitUntilElementIsInvisible(WebElement webElement) {
        log.atDebug().log("{} Waiting until element {} disappear/invisible with timeout '{}s", RAF, webElement, innerTimeOut);
        paExHandler.handleException(() -> getFluentWait(innerTimeOut)
                .until(elementIsInvisible(webElement)), "generateHint", "waiting for element to disappear", "element never disappear"
        );
        return this;

//        try {
//            getFluentWait(innerTimeOut)
//                    .until(elementIsInvisible(webElement));
//            return this;
//        } catch (TimeoutException timeOut) {
//            generateHint(timeOut,
//                    "waiting for element to disappear", "element never disappear");
//            throw timeOut;
//        } catch (Exception e) {
//            logNewCase(e.getMessage());
//            throw e;
//        }
    }

    public PageAction waitUntilAllElementsAreVisible(List<WebElement> webElements) {
        if (webElements.isEmpty())
            throw new IllegalArgumentException("[RAF] Unable to waitUntilAllElementsAreVisible, as there's no element(s) to wait for!");
        log.atDebug().log("{} Waiting until element {} to be visible with timeout '{}s'", RAF, webElements, innerTimeOut);

        paExHandler.handleException(() -> getFluentWait(innerTimeOut)
                        .until(elementsAreVisible(webElements)), "generateHint", "waiting for element to be visible",
                "Element never appear | display set to none"
        );
        return this;

//        try {
//            getFluentWait(innerTimeOut)
//                    .until(elementsAreVisible(webElements));
//            return this;
//        } catch (TimeoutException timeOut) {
//            generateHint(timeOut,
//                    "waiting for element to be visible",
//                    "Element never appear | display set to none");
//            throw timeOut;
//        } catch (Exception e) {
//            logNewCase(e.getMessage());
//            throw e;
//        }
    }

    /**
     * <p>Method to wait until the specified {@code webElement} visible in the page <br>
     * This method is used by {@link BasePage#isAt(WebElement)} method to ensure user has landed in the page correctly</p>
     * <br>
     * <b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Explicitly wait for table to be visible before proceed to next action
     *     pageAction.waitUntilElementIsVisible ( mainTable );
     *     }
     * </pre>
     *
     * @param webElement target web element
     * @return {@code PageAction} for chaining purpose
     */
    public PageAction waitUntilElementIsVisible(WebElement webElement) {
        log.atDebug().log("{} Waiting until element {} to be visible with timeout '{}s'", RAF, webElement, innerTimeOut);
        paExHandler.handleException(() -> getFluentWait(innerTimeOut)
                .until(elementIsVisible(webElement)), "generateHint", "waiting for element to be visible", "Element never appear | display set to none"
        );
        return this;
//        try {
//            getFluentWait(innerTimeOut)
//                    .until(elementIsVisible(webElement));
//            return this;
//        } catch (TimeoutException timeOut) {
//            generateHint(timeOut,
//                    "waiting for element to be visible",
//                    "Element never appear | display set to none");
//            throw timeOut;
//        } catch (Exception e) {
//            logNewCase(e.getMessage());
//            throw e;
//        }
    }

    public PageAction dragAndDrop(WebElement fromElement, WebElement toElement) {
        ((JavascriptExecutor) webDriver).executeScript(SCROLL_INTO_VIEW_CENTER, fromElement);
        getFluentWait(innerTimeOut).until(ExpectedConditions.elementToBeClickable(fromElement));
        new Actions(webDriver).dragAndDrop(fromElement, toElement).perform();
        return this;
    }

    public PageAction dragAndDropBy(WebElement webElement, int xOffset, int yOffset) {
        ((JavascriptExecutor) webDriver).executeScript(SCROLL_INTO_VIEW_CENTER, webElement);
        getFluentWait(innerTimeOut).until(ExpectedConditions.elementToBeClickable(webElement));
        new Actions(webDriver).dragAndDropBy(webElement, xOffset, yOffset).perform();
        return this;
    }

    /*===== PAGE ACTION - Retrieving information from webElement =====*/
    public String getText(WebElement webElement) {
        waitUntilElementIsVisible(webElement);
        if (webElement.getTagName().equalsIgnoreCase("input")) {
            return (webElement.getText().equalsIgnoreCase("")) ? webElement.getAttribute("value") : webElement.getText();
        }
        return webElement.getText();
    }

    public String getValue(WebElement webElement) {
        waitUntilElementIsVisible(webElement);
        return webElement.getAttribute("value");
    }

    public String getTooltipText(WebElement webElement, WebElement tooltipElement) {
        return mouseHoverAt(webElement)
                .getText(tooltipElement);
    }

    public PageAction mouseHoverAt(WebElement webElement) {
        waitUntilElementIsVisible(webElement);
        javascriptExecutor.executeScript(SCROLL_INTO_VIEW_CENTER, webElement);
        new Actions(webDriver).moveToElement(webElement).perform();
        return this;
    }

    //todo experimental
    public PageAction doCtrl_C() {
        new Actions(webDriver).keyDown(Keys.CONTROL).sendKeys("c").keyUp(Keys.CONTROL).perform();
        return this;
    }

    //todo experimental
    public PageAction doCtrl_V() {
        new Actions(webDriver).keyDown(Keys.CONTROL).sendKeys("v").keyUp(Keys.CONTROL).perform();
        return this;
    }

    public String getCurrentURL() {
        return webDriver.getCurrentUrl();
    }

    //see: http://makeseleniumeasy.com/2017/05/16/method-1-getattribute-why-what-and-how-to-use/
    public String getAttribute(WebElement webElement, String attribute) {
        waitUntilElementIsVisible(webElement);
        return webElement.getAttribute(attribute);
    }

    public String getCssValue(WebElement webElement, String cssPropertyName) {
        waitUntilElementIsVisible(webElement);
        return webElement.getCssValue(cssPropertyName);
    }

    public List<String> getCssValues(WebElement webElement, String... cssPropertyNames) {
        waitUntilElementIsVisible(webElement);
        return Arrays.stream(cssPropertyNames)
                .map(webElement::getCssValue)
                .collect(Collectors.toList());
    }

    public String getClipboardContents() {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        //odd: the Object param of getContents is not currently used
        Transferable contents = clipboard.getContents(null);
        boolean hasTransferableText = (contents != null) && contents.isDataFlavorSupported(DataFlavor.stringFlavor);
        if (hasTransferableText) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IOException ex) {
                log.atError().withThrowable(ex).log("[RAF] Unable to get text from clipboard");
            }
        }
        return result;
    }

    public boolean isElementWithinViewport(WebElement webElement) {
        this.waitUntilElementIsVisible(webElement);

        long documentWidth = (Long) javascriptExecutor.executeScript(DOCUMENT_CLIENT_WIDTH);
        long documentHeight = (Long) javascriptExecutor.executeScript(DOCUMENT_CLIENT_HEIGHT);
        Map<String, Object> elementBoundary = (Map<String, Object>) javascriptExecutor.executeScript(BOUNDING_CLIENT_RECT, webElement);

        double elementRightBound = ((Number) elementBoundary.get("x")).doubleValue() + ((Number) elementBoundary.get("width")).doubleValue();
        double elementBottomBound = ((Number) elementBoundary.get("y")).doubleValue() + ((Number) elementBoundary.get("height")).doubleValue();
        return ((Number) elementBoundary.get("x")).doubleValue() >= 0
                && elementRightBound <= documentWidth
                && ((Number) elementBoundary.get("y")).doubleValue() >= 0
                && elementBottomBound <= documentHeight;
    }

    /**
     * @param webElement element to be assessed.
     * @param webElement target web element
     * @return true if element exist
     * @see #isElementWithinViewport(WebElement)
     */
    public boolean isElementExist(WebElement webElement) {
        try {
            return webElement.isDisplayed();
        } catch (NoSuchElementException ex) {
            return false;
        }
    }

    /*===== PAGE ACTION - Component Builder =====*/

    public DropDownMockBuilder buildDropdown() {
        return new DropDownMockBuilder(this);
    }

    public ToggleComponent buildToggleComponent(WebElement webElement) {
        return new ToggleComponent(this, webElement);
    }

    public ButtonToggleGroup buildButtonToggleGroup(WebElement webElement) {
        return new ButtonToggleGroup(this, webElement);
    }

    public Slider buildSlider(WebElement webElement) {
        return new Slider(this, webElement);
    }

    /*===== PAGE ACTION - Miscellaneous utilities =====*/
    public void debug() {
        new Actions(webDriver).sendKeys(Keys.F12).perform();
        javascriptExecutor.executeScript("debugger");
    }

    //todo - check if highlighter script <style> is already exist
    public PageAction highlight(WebElement webElement) {

        String myscript = "let tempStyle = document.createElement('style'); " +
                "tempStyle.appendChild(document.createTextNode('.testautomation {border: 2px dotted red; border-radius: 5px; box-shadow: 0 0 0 9999px rgba(0,0,0,.8);}'));" +
                "document.head.appendChild(tempStyle)";

        log.atTrace().log("[RAF-POC] executed JS : {}", myscript);
        javascriptExecutor.executeScript(myscript); //inject <script> to css

        try {
            javascriptExecutor.executeScript("arguments[0].setAttribute ('class', arguments[0].className + ' testautomation')", webElement);
        } catch (Exception ex) {
            log.atTrace().withThrowable(ex).log("[RAF-POC] Failed to highlight");
            //ignore
        }
        return this;
    }

    public PageAction unHighlight(WebElement webElement) {
        try {
            javascriptExecutor.executeScript("arguments[0].setAttribute ('class', arguments[0].className.replace ('testautomation',''))", webElement);
        } catch (Exception ex) {
            log.atTrace().withThrowable(ex).log("[RAF-POC] Failed to UN-highlight");
            //ignore
        }
        return this;
    }

    public void refreshPage() {
        webDriver.navigate().refresh();
    }

    //todo - properly handle the findElement
    public WebElement findElement(By locator) {
        try {
            return getFluentWait(innerTimeOut).until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (Exception ex) {
            log.atError().withThrowable(ex).log("[RAF] Error ");
            throw ex;
        }
    }

    public WebElement findElement(WebElement rootElement, By locator) {
        try {
            return getFluentWait(innerTimeOut).until(ExpectedConditions.presenceOfNestedElementLocatedBy(rootElement, locator));
        } catch (Exception ex) {
            log.atError().withThrowable(ex).log("[RAF] Error ");
            throw ex;
        }
    }

    public void scrollToEndPage() {
        javascriptExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight)");
    }
}