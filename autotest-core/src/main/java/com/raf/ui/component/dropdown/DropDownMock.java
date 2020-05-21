package com.raf.ui.component.dropdown;

import com.raf.ui.component.PageAction;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.UnexpectedTagNameException;

import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class DropDownMock {
    private final WebElement mainSelector;
    private final WebElement btnToggle;
    private final WebElement headerElement;
    private final List<WebElement> listOfOptions;
    private final WebElement optionsBlock;
    private final List<WebElement> innerLabel;
    PageAction poc;

    public DropDownMock(PageAction po, WebElement mainSelector, WebElement headerElement, WebElement btnToggle, WebElement optionsBlock, List<WebElement> listOfOptions, List<WebElement> innerLabel) {
        this.poc = po;
        this.btnToggle = btnToggle;
        this.headerElement = headerElement;
        this.optionsBlock = optionsBlock;
        this.listOfOptions = listOfOptions;
        this.mainSelector = (mainSelector == null) ? optionsBlock : mainSelector;
        this.innerLabel = (innerLabel == null) ? listOfOptions : innerLabel;
    }

    public void getState() {

    }

    public DropDownMock clickDropdown() {
        if (btnToggle != null)
            poc.clickElement(btnToggle);
        return this;
    }

    public String getCurrentlySelectedOption() {
        String result = "";
        try {
            Select dropdown = new Select(this.mainSelector);
            result = poc.getText(dropdown.getFirstSelectedOption());
        } catch (UnexpectedTagNameException | NullPointerException | NoSuchElementException e) { //either it's already collapsed or it's not <select>
            if (headerElement != null) {
                result = poc.getText(headerElement);
            } else {
                System.out.println("[RAF] Unable to get selected item from dropdown. Please either provide the <select> element or headerElement");
            }
        }
        return result;
    }

    public DropDownMock selectItem(String itemToSelect, boolean isRegex) {
        try {
            //trying out with Select wrapper (only works for standard <select>...</select> HTML element
            poc.waitUntilElementIsVisible(this.mainSelector);
            Select dropdown = new Select(this.mainSelector);
            if (isRegex) { //if selecting by regex
                String matchedItem = dropdown
                        .getOptions()
                        .stream()
                        .filter(webElement -> Pattern.compile(itemToSelect).matcher(webElement.getText()).matches())
                        .findFirst()
                        .get()
                        .getText();
                dropdown.selectByVisibleText(matchedItem);
            } else {
                dropdown.selectByVisibleText(itemToSelect);
            }
        } catch (UnexpectedTagNameException e) { //if the dropdown is non-select, we will depend on given optionsBlock and listOfOptions.
            System.out.println("[RAF] not supported non-select dropdown, will use " + this.optionsBlock + " as the dropdown");
            if (listOfOptions.size() > 0) {
                //regex checker
                Predicate<WebElement> optionFilter = webElement -> webElement.getText().trim().equals(itemToSelect);  //define predicate (filter criteria) if it's non-regex.
                if (isRegex)
                    optionFilter = webElement -> Pattern.matches(itemToSelect, webElement.getText().trim()); //define predicate if it's regex.

                poc.clickElement(innerLabel.stream()
                        .filter(optionFilter)
                        .findFirst()
                        .get());
            } else {
                System.err.println("[RAF] It's not list of input either. Sorry, your dropdown list type is currently not supported.");
                throw e;
            }
        }
        poc.sleep(2); //todo - get rid of sleep. need to be smart enough to know that the option has been selected!
        return this;
    }

    //todo - implement dropdown select multiple items
    //testing here
    public DropDownMock selectMultipleItems(List<String> listOfItemToSelect, boolean isRegex) {
        return this;
    }

    //todo - implement dropdown select all
    public DropDownMock selectAll() {
        return this;
    }
}
