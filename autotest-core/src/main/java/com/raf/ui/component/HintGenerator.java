package com.raf.ui.component;

import lombok.extern.log4j.Log4j2;

import java.util.Objects;

@Log4j2
public class HintGenerator {

    private HintGenerator() {
        throw new IllegalStateException("Utility class");
    }

    private static final String RAF = "[RAF]";

    //todo include ability to log JIRA ticket directly with given logs.
    public static void logNewCase(String causeDetails) {
        log.atError().log("{} You just hit a new case that framework does not consider, please provide this log to RAF. Detail : {}", RAF, causeDetails);
    }

    public static void generateHintForEnterText(Throwable exception) {
        if (!Objects.isNull(exception.getCause())) { //if null, it indicates the error is purely timeout (nothing caused it)
            String causeDetails = exception.getCause().toString().toLowerCase();
            if (causeDetails.contains("invalidelementstateexception")) {
                log.atError().log("{} Trying to enter/clear text to element that cannot accept text, such as: Button, scrollbar, etc.", RAF);
            } else if (causeDetails.contains("nosuchelementexception")) {
                log.atError().log("{} Trying to interact with element that is no longer in the DOM/page", RAF);
            } else {
                logNewCase(causeDetails);
            }
        } else {
            log.atError().log("{} Timeout occur when entering/clearing text of element. Possible reason -> [element was disable | not displayed | stale | text entered wrongly | text unable to be cleared ]", RAF);
        }
    }

    public static void generateHintForClickElement(Throwable exception) {
        if (!Objects.isNull(exception.getCause())) { //if null, it indicates the error is purely timeout (nothing caused it)
            String causeDetails = exception.getCause().toString().toLowerCase();
            if (causeDetails.contains("nosuchelementexception")) {
                log.atError().log("{} Trying to interact with element that is no longer in the DOM/page", RAF);
            } else if (causeDetails.contains("elementclickinterceptedexception")) {
                log.atError().log("{} Trying to click element that is hidden/blocked by another element (eq. Header)", RAF);
            } else {
                logNewCase(causeDetails);
            }
        } else {
            log.atError().log("{} Timeout occur when clicking element. Possible reason -> [element was disable | not displayed | stale ]", RAF);
        }
    }

    public static void generateHintForPageLoad(Throwable exception) {
        if (!Objects.isNull(exception.getCause())) { //if null, it indicates the error is purely timeout (nothing caused it)
            String causeDetails = exception.getCause().toString().toLowerCase();
            logNewCase(causeDetails);
        } else {
            log.atError().log("{} Timeout occur when Loading page. Possible reason -> [ document.readyState never 'complete' ]", RAF);
        }
    }

    public static void generateHint(Throwable exception, String action, String reasons) {
        if (!Objects.isNull(exception.getCause())) { //if null, it indicates the error is purely timeout (nothing caused it)
            String causeDetails = exception.getCause().toString().toLowerCase();
            logNewCase(causeDetails);
        } else {
            log.atError().log("{} Timeout occur when {}. Possible reason -> [ {} ]", RAF, action, reasons);
        }
    }
}
