package com.raf.ui.component.table;

import com.raf.ui.component.PageAction;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

public class TableMock {
    private WebElement scrollHorizontalBar;
    private WebElement mainSelector;
    private WebElement listOfHeader;
    private PageAction poc;


    public TableMock(PageAction po, WebElement mainSelector, WebElement listOfHeader, WebElement scrollHorizontalBar) {

        this.poc = po;
        this.mainSelector = mainSelector;
        this.listOfHeader = listOfHeader;
        this.scrollHorizontalBar = scrollHorizontalBar;
    }

    //todo - get list of headers from given table.
    public List<String> getListOfHeaders() {
        return new ArrayList<>();
    }

    //todo - click on header.
    public void clickOnHeader(String headerName) {

    }
}
