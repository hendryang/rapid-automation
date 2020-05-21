package com.raf.deprecated;

import org.openqa.selenium.By;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.WebElement;

import java.util.List;

public abstract class CustomBy {

    public static By dataAutomation(String dataautomation) {
        return new ByDataAutomation(dataautomation);
    }

    public static class ByDataAutomation extends By {
        private final String dataautomation;

        public ByDataAutomation(String dataautomation) {
            this.dataautomation = dataautomation;
        }

        @Override
        public List<WebElement> findElements(SearchContext context) {
            return context.findElements(By.cssSelector(
                    String.format("[%s='%s']", "data-automation", dataautomation)
            ));
        }
    }
}


