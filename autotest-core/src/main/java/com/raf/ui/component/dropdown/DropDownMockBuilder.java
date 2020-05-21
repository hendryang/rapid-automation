package com.raf.ui.component.dropdown;

import com.raf.ui.component.PageAction;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.function.Consumer;

public class DropDownMockBuilder {
    private PageAction pageAction;
    public WebElement mainSelector;
    public WebElement btnToggle;
    public WebElement optionsBlock;
    public List<WebElement> listOfOptions;
    public List<WebElement> innerLabel;
    public WebElement headerElement;

    public DropDownMockBuilder(PageAction po) {
        this.pageAction = po;
    }

    //https://medium.com/beingprofessional/think-functional-advanced-builder-pattern-using-lambda-284714b85ed5

    /**
     * Builder method to build the mocking of actual dropdown component. <br>
     *
     * <br><b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Click a button with default timeout (30s)
     *     DropDownMock multiDropdown = poc.buildDropdown().with(dd -> {
     *             dd.btnToggle = basicSfd.btnSelectSkills;
     *             dd.listOfOptions = basicSfd.ddSelectSkillsInd;
     *             dd.optionsBlock = basicSfd.ddSelectSkillsAll;
     *         }).build();
     *     }
     * </pre>
     *
     * <b>Details on the parameter:</b> <pre>
     *  {@link #mainSelector} : the WebElement that represents {@code <select>...</select>} component. (if not provided, it'll assume as {@link #optionsBlock})
     *  {@link #btnToggle}    : the small button beside dropdown to display list of dropdown options.
     *  {@link #optionsBlock} : the WebElement that represents an element that wrap all the options.
     *  {@link #listOfOptions}: the <b>List of WebElement</b> that represents all individual options that can be selected.
     *  {@link #innerLabel}   : the <b>List of WebElement</b>  that represents label or text of the options. (if not provided, it'll assume as {@link #listOfOptions})
     *
     * </pre>
     *
     * @param builderFunction The parameter to build {@link DropDownMock}
     * @return {@code Drop} for chaining purpose
     */

    public DropDownMockBuilder with(Consumer<DropDownMockBuilder> builderFunction) {
        builderFunction.accept(this);
        return this;
    }

    /**
     * Build the component with the given parameter on {@link #with(Consumer)} method. <br>
     *
     * <br><b>Sample Usage : </b>
     * <pre>
     *     {@code
     *     //Specify parameter and build the actual component
     *     DropDownMockBuilder ddBuilder = pageAction.buildDropdown().with(dd -> {
     *             dd.btnToggle = basicSfd.btnSelectSkills;
     *             dd.listOfOptions = basicSfd.ddSelectSkillsInd;
     *             dd.optionsBlock = basicSfd.ddSelectSkillsAll;
     *         })
     *     DropDownMock multiDropdown = ddBuilder.build(); //build based on above provision.
     *     }
     * </pre>
     *
     * @return {@link DropDownMock} The mocking of actual dropdown component.
     */

    public DropDownMock build() {
        return new DropDownMock(pageAction, mainSelector, headerElement, btnToggle, optionsBlock, listOfOptions, innerLabel);
    }
}