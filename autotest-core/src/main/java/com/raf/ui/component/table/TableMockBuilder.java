package com.raf.ui.component.table;

import com.raf.ui.component.PageAction;
import org.openqa.selenium.WebElement;

import java.util.function.Consumer;

public class TableMockBuilder {
    private PageAction pageAction;
    public WebElement scrollHorizontalBar;
    public WebElement mainSelector;
    public WebElement listOfHeader;

    public TableMockBuilder(PageAction po) {
        this.pageAction = po;
    }

    //https://medium.com/beingprofessional/think-functional-advanced-builder-pattern-using-lambda-284714b85ed5


    public TableMockBuilder with(Consumer<TableMockBuilder> builderFunction) {
        builderFunction.accept(this);
        return this;
    }


    public TableMock build() {
        return new TableMock(pageAction, null, null, null);
    }
}