package com.raf.ui.component;

public class JSCommand {

    private JSCommand() {
        throw new IllegalStateException("Utility class");
    }

    public static final String SCROLL_INTO_VIEW = "arguments[0].scrollIntoView();";


    public static final String SCROLL_INTO_VIEW_CENTER = "arguments[0].scrollIntoView({behavior: 'auto', block: 'center', inline: 'nearest'});";

    public static final String DOCUMENT_READY_STATE = "return document.readyState;";

    public static final String DOCUMENT_CLIENT_WIDTH = "return document.documentElement.clientWidth;";

    public static final String DOCUMENT_CLIENT_HEIGHT = "return document.documentElement.clientHeight;";

    public static final String BOUNDING_CLIENT_RECT = "return arguments[0].getBoundingClientRect();";
}
