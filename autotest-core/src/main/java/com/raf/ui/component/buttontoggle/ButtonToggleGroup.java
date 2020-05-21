package com.raf.ui.component.buttontoggle;

import com.raf.exceptionhandler.WrongComponentBuilderException;
import com.raf.ui.component.PageAction;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Hendry Ang
 * @version %I%
 * @since 1.0.0-RC7
 */
public class ButtonToggleGroup {

    //Selector information
    private static final String BUTTON_TOGGLE = "#toggle";
    private PageAction pageAction;
    private List<WebElement> listBtnToggle;

    public ButtonToggleGroup(PageAction po, WebElement toggleGroup) {
        pageAction = po;
        listBtnToggle = toggleGroup.findElements(By.cssSelector(BUTTON_TOGGLE));
        if (listBtnToggle.isEmpty()) {
            throw new WrongComponentBuilderException("Unable to build Group component, no options/buttons detected.", null);
        }
    }

    /**
     * Select the option(button) with given text.
     *
     * @param optionName Name of the button to click/select
     * @return itself for chaining purpose.
     */
    public ButtonToggleGroup selectOption(String optionName) {
        pageAction.clickElement(getOption(optionName));
        return this;
    }

    /**
     * @param optionName Name of the button to check
     * @return selection state of the button with {@code optionName}
     */
    public boolean isOptionSelected(String optionName) {
        return pageAction
                .getAttribute(getOption(optionName).findElement(By.cssSelector("a")), "class")
                .equalsIgnoreCase("is-active");
    }

    /**
     * @return currently selected option or button (web element)
     */
    public WebElement getCurrentSelectedOption() {
        return listBtnToggle.stream()
                .map(e -> e.findElement(By.cssSelector("a")))
                .filter(e -> pageAction.getAttribute(e, "class").equalsIgnoreCase("is-active"))
                .findFirst()
                .orElse(null);
    }

    /**
     * @return currently selected option or button (String text of the option's name)
     */
    public String getCurrentSelectedOptionText() {
        return pageAction.getText(this.getCurrentSelectedOption());
    }

    /**
     * @return list of all available options in the group (web element)
     */
    public List<WebElement> getAllOptions() {
        return listBtnToggle;
    }

    /**
     * @return list of all available options in the group (string text of the option's name)
     */
    public List<String> getAllOptionsText() {
        return this.getAllOptions()
                .stream()
                .map(pageAction::getText)
                .collect(Collectors.toList());
    }

    /**
     * @param optionName Name of the option
     * @return web element of the option that match the {@code optionName}
     * @throws NullPointerException if {@code optionName} is not available
     */
    public WebElement getOption(String optionName) {
        WebElement option = listBtnToggle.stream()
                .filter(e -> e.getText().equalsIgnoreCase(optionName))
                .findFirst()
                .orElse(null);
        if (Objects.isNull(option)) {
            throw new NullPointerException("Unable to find option " + optionName + " in button toggle group");
        } else {
            return option;
        }
    }
}
