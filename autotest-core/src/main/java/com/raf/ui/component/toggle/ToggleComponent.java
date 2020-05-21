package com.raf.ui.component.toggle;

import com.raf.ui.component.PageAction;
import org.openqa.selenium.WebElement;

public class ToggleComponent {
    PageAction pageAction;

    private WebElement toggleComponent;
    private static final String IS_SWITCHED_ON = "toggle--on";

    public ToggleComponent(PageAction po, WebElement toggleComponent) {
        pageAction = po;
        this.toggleComponent = toggleComponent;
    }

    public ToggleComponent changeState() {
        pageAction.clickElement(toggleComponent);
        return this;
    }

    public ToggleComponent switchOn() {
        if (!isOn()) {
            changeState();
        }
        return this;
    }

    public ToggleComponent switchOff() {
        if (isOn()) {
            changeState();
        }
        return this;
    }

    public boolean isOn() {
        return toggleComponent.getAttribute("class").contains(IS_SWITCHED_ON);
    }
}
