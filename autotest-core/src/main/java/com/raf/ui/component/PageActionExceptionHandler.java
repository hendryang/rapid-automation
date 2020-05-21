package com.raf.ui.component;

import org.openqa.selenium.TimeoutException;

import java.util.Arrays;
import java.util.List;

public class PageActionExceptionHandler {
    private boolean ignoreError = false;

    @FunctionalInterface
    interface CallableEx<T, E extends Exception> {
        T run() throws E;
    }

    public <T> void handleException(CallableEx<T, Exception> method, String hintGenerator, String... hintMessages) {
        try {
            method.run();
        } catch (TimeoutException timeOut) {
            switch (hintGenerator) {
                case "generateHintForClickElement":
                    HintGenerator.generateHintForClickElement(timeOut);
                    break;
                case "generateHintForEnterText":
                    HintGenerator.generateHintForEnterText(timeOut);
                    break;
                case "generateHint":
                    List<String> localHintMsg = Arrays.asList(hintMessages);
                    HintGenerator.generateHint(timeOut, localHintMsg.get(0), localHintMsg.get(1));
                    break;
            }

            if (!ignoreError) {
                throw timeOut;
            }
        } catch (Exception e) {
            //basically if there's any exception that is not being ignored. (and it was thrown in the try-block)
            HintGenerator.logNewCase(e.getMessage());
            if (!ignoreError) {
                throw new RuntimeException(e);
            }
        }
    }

    public void setIgnoreError(boolean ignoreError) {
        this.ignoreError = ignoreError;
    }
}
