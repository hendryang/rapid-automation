package com.raf.ui.pages;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

import java.lang.reflect.Field;
import java.util.Objects;

public class SlowBasePage extends BasePage {

    /**
     * Method to initialize all the elements specified in PageObject's  {@code @FindBy()} annotation.<br>
     * Additionally, the specified page title and URL will also be set which is mainly for verification purpose of - {@link #isAt()}
     *
     * @param thePage The page object to be initialized (normally is {@code this})
     */
    public void initPage(Object thePage) {
        initPage(thePage, "", "");
    }

    /**
     * Method to initialize all the elements specified in PageObject's  {@code @FindBy()} annotation.<br>
     * Additionally, the specified page title and URL will also be set which is mainly for verification purpose of - {@link #isAt()}
     *
     * @param thePage   The page object to be initialized (normally is {@code this})
     * @param pageTitle The title of the given page object
     * @param pageUrl   The URL of the given page object ( the base URL will be automatically appended if ignored)
     */
    @Override
    public void initPage(Object thePage, String pageTitle, String pageUrl) {
        PageFactory.initElements(new DisplayedAjaxElementLocatorFactory(getWebDriver(), 30), thePage);
        setPageTitle(pageTitle);
        setPageUrl(pageUrl);
    }

    private static class DisplayedAjaxElementLocatorFactory implements ElementLocatorFactory {
        private final WebDriver webDriver;
        private final int timeout;

        public DisplayedAjaxElementLocatorFactory(WebDriver webDriver, int timeout) {
            this.webDriver = webDriver;
            this.timeout = timeout;
        }

        @Override
        public ElementLocator createLocator(Field field) {
            return new VisibleAjaxElementLocator(this.webDriver, field, this.timeout);
        }

        private static class VisibleAjaxElementLocator extends AjaxElementLocator {
            public VisibleAjaxElementLocator(WebDriver webDriver, Field field, int timeout) {
                super(webDriver, field, timeout);
            }

            @Override
            protected boolean isElementUsable(WebElement webElement) {
                if (Objects.isNull(webElement)) return false;
                return webElement.isDisplayed();
            }
        }
    }
}
