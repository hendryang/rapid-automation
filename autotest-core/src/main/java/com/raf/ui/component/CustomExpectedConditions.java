package com.raf.ui.component;

import lombok.extern.log4j.Log4j2;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

import static org.openqa.selenium.support.ui.ExpectedConditions.*;


//https://huddle.eurostarsoftwaretesting.com/use-explicit-waits-selenium-webdriver/
//todo - must be idempotent - hendry
@Log4j2
public class CustomExpectedConditions {

    private CustomExpectedConditions() {
        throw new IllegalStateException("Utility class");
    }

    private static boolean isFirstNull = true;
    private static int innerCounter = 0;

    private static String getLog(String action, WebElement element) {

        return String.format("[RAF] Perform %s on %s", action.toUpperCase(), element.toString());
    }

    private static String getLog(String action) {
        return String.format("[RAF] > %s on %s", action.toUpperCase(), new Timestamp(System.currentTimeMillis()));
    }


    public static ExpectedCondition<Boolean> titleAndUrlMatch(String title, String url) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                //todo - see https://stackoverflow.com/questions/852665/command-line-progress-bar-in-java
                log.atDebug().log("{}", () -> getLog("checking url & title"));
                return urlContains(url).apply(webDriver) && titleIs(title).apply(webDriver);
            }

            @Override
            public String toString() {
                return String.format("Url of the page to contain ['%s'] with title ['%s']", url, title);
            }
        };
    }

    public static ExpectedCondition<Boolean> pageIsLoaded() {
        return new ExpectedCondition<Boolean>() {
            String result = "";

            @Override
            public Boolean apply(WebDriver webDriver) {
                result = ((JavascriptExecutor) webDriver).executeScript(JSCommand.DOCUMENT_READY_STATE).toString();
                return result.equals("complete");
            }

            @Override
            public String toString() {
                return "Page document ready state to be complete, however document = " + result;
            }
        };
    }

    public static ExpectedCondition<Boolean> elementIsVisible(final WebElement element) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                log.atDebug().log("{}", () -> getLog("Waiting element to be visible"));
                return !Objects.isNull(visibilityOf(element).apply(webDriver));
            }

            @Override
            public String toString() {
                return "element is visible";
            }
        };
    }

    public static ExpectedCondition<Boolean> elementsAreVisible(final List<WebElement> elements) {
        return new ExpectedCondition<Boolean>() {
            long invisibleElement = 0;

            @Override
            public Boolean apply(WebDriver webDriver) {
                log.atDebug().log("{}", () -> getLog("Waiting elements to be visible"));
                invisibleElement = elements.stream().filter(e -> Objects.isNull(visibilityOf(e).apply(webDriver))).count();
                return invisibleElement == 0;
                //return elements.stream().noneMatch(e -> Objects.isNull(visibilityOf(e).apply(webDriver)));
            }

            @Override
            public String toString() {
                return String.format("All elements are visible. %d out of %d are still not visible", invisibleElement, elements.size());
            }
        };
    }

    /**
     * Idempotent method that do following actions (repetitively) until hit timeout: <br>
     * <ul>
     *     <li>Scroll the element into the view (center)</li>
     *     <li>Wait until element is clickable (visible and enable)</li>
     *     <li>Clear existing text if any and Enter {@code text}</li>
     *     <li>Ensure that the text is entered correct by checking both its text and value (as long as either 1 is true)</li>
     * </ul>
     * It is only considered success if: <br>
     *      <ul>
     *          <li>Element is visible [satisfy {@link org.openqa.selenium.support.ui.ExpectedConditions#visibilityOf(WebElement)}]</li>
     *          <li>Element is enabled</li>
     *          <li>Element does not hit "StaleElementReferenceException"</li>
     *          <li>{@code getText() and getAttribute("value")} matched the entered text</li>
     *      </ul>
     *
     * @param webElement target web element
     * @param text       string text to be entered
     * @return Boolean - either success or failed.
     */
    public static ExpectedCondition<Boolean> textIsEntered(final WebElement webElement, final String text, final boolean isSafe) {
        return new ExpectedCondition<Boolean>() {
            @SuppressWarnings("ConstantConditions") //to suppress textToBePresentInElement that 'could' throw NPE
            @Override
            public Boolean apply(WebDriver webDriver) {
                log.atDebug().log("{}", () -> getLog("enter text"));
                ((JavascriptExecutor) webDriver).executeScript(JSCommand.SCROLL_INTO_VIEW_CENTER, webElement);
                WebElement ielement = elementToBeClickable(webElement).apply(webDriver);
                if (!Objects.isNull(ielement)) {
                    ielement.clear();
                    ielement.sendKeys(text);
                    if (isSafe) {
                        boolean result1 = textToBePresentInElement(ielement, text).apply(webDriver);
                        boolean result2 = textToBePresentInElementValue(ielement, text).apply(webDriver);
                        return result1 || result2;
                    }
                    return true; //unsafe enter text will return without double checking.
                } else {
                    return false; //continue the loop
                }
            }

            @Override
            public String toString() {
                try {
                    return String.format("Text ('%s') to be the value of element [%s], however found ('%s')", text, webElement, (webElement.getText().equalsIgnoreCase("") ? webElement.getAttribute("value") : webElement.getText()));
                } catch (Exception ex) {
                    return String.format("Text to be entered correctly to the element. reason : %s]", ex.getMessage());
                }
            }
        };
    }

    public static ExpectedCondition<Boolean> textIsCleared(final WebElement webElement, boolean withBackspace) {
        return new ExpectedCondition<Boolean>() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public Boolean apply(WebDriver webDriver) {
                log.atDebug().log("{}", () -> getLog("enter text"));
                ((JavascriptExecutor) webDriver).executeScript(JSCommand.SCROLL_INTO_VIEW_CENTER, webElement);
                WebElement clickableElement = elementToBeClickable(webElement).apply(webDriver);
                if (!Objects.isNull(clickableElement)) {
                    if (!withBackspace) {
                        clickableElement.clear();
                    } else {
                        int trial = 0;
                        while (clickableElement.getAttribute("value").length() > 0 && trial < 100) {
                            clickableElement.sendKeys(Keys.BACK_SPACE);
                            trial++; //to prevent endless loop - todo - need better implementation.
                        }
                    }

                    boolean result1 = textToBePresentInElement(webElement, "").apply(webDriver);
                    boolean result2 = textToBePresentInElementValue(webElement, "").apply(webDriver);
                    return result1 || result2;
                } else {
                    return false; //continue the loop
                }
            }

            @Override
            public String toString() {
                try {
                    return String.format("Text of the webElement [%s] to be empty, however found ('%s')", webElement, (webElement.getText().equalsIgnoreCase("") ? webElement.getAttribute("value") : webElement.getText()));
                } catch (Exception ex) {
                    return String.format("Text of the webElement to be cleared successfully. reason : %s]", ex.getMessage());
                }
            }
        };
    }

    /**
     * Idempotent method that do following actions (repetitively) until hit timeout: <br>
     * <ul>
     *     <li>Scroll the element into the view (center)</li>
     *     <li>Wait until element is clickable and perform click</li>
     * </ul>
     * It is only considered success if: <br>
     *      <ul>
     *          <li>Element is visible [satisfy {@link org.openqa.selenium.support.ui.ExpectedConditions#visibilityOf(WebElement)}]</li>
     *          <li>Element is enabled</li>
     *          <li>Element does not hit "StaleElementReferenceException"</li>
     *      </ul>
     *
     * @param webElement target web element
     * @return WebElement that has been clicked.
     */
    public static ExpectedCondition<WebElement> elementIsClicked(final WebElement webElement) {
        return new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver webDriver) {
                log.atDebug().log("{}", () -> getLog("click"));
                ((JavascriptExecutor) webDriver).executeScript(JSCommand.SCROLL_INTO_VIEW_CENTER, webElement);
                WebElement clickableElement = elementToBeClickable(webElement).apply(webDriver);
                if (!Objects.isNull(clickableElement)) {
                    clickableElement.click();
                    return clickableElement;
                } else {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "Element to be clicked.";
            }
        };
    }

    public static ExpectedCondition<WebElement> elementIsDoubleClicked(final WebElement webElement) {
        return new ExpectedCondition<WebElement>() {
            @Override
            public WebElement apply(WebDriver webDriver) {
                log.atDebug().log("{}", () -> getLog("double click"));
                ((JavascriptExecutor) webDriver).executeScript(JSCommand.SCROLL_INTO_VIEW_CENTER, webElement);
                WebElement clickableElement = elementToBeClickable(webElement).apply(webDriver);
                if (!Objects.isNull(clickableElement)) {
                    new Actions(webDriver)
                            .moveToElement(clickableElement)
                            .doubleClick()
                            .perform();
                    return clickableElement;
                } else {
                    return null;
                }
            }

            @Override
            public String toString() {
                return "Element to be double clicked.";
            }
        };
    }

    public static ExpectedCondition<Boolean> elementIsInvisible(final WebElement webElement) {
        return new ExpectedCondition<Boolean>() {
            @Override
            public Boolean apply(WebDriver webDriver) {
                try {
                    log.atDebug().log("{}", () -> getLog("check invisibility"));
                    if (webElement.isDisplayed()) {
                        innerCounter = 3;
                    }
                    innerCounter++;

                    if (innerCounter >= 3) {
                        innerCounter = 0;
                        return !webElement.isDisplayed();
                    }
                    return false;
                } catch (NullPointerException | StaleElementReferenceException | NotFoundException e) {
                    if (isFirstNull) { //sometimes it detected as "invisible" although it has not even appeared yet, give it more time.
                        isFirstNull = false;
                        return false;
                    } else {
                        isFirstNull = true; //reset state
                        return true;
                    }
                }
            }

            @Override
            public String toString() {
                return "Element is invisible/disappear";
            }
        };
    }
}
