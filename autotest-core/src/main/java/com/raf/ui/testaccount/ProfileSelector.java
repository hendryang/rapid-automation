package com.raf.ui.testaccount;

import lombok.extern.log4j.Log4j2;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;

import java.io.IOException;

@Log4j2
public class ProfileSelector {

    //utility class
    private ProfileSelector() {
    }

    public static String getEmailFromProfile(String profile) {
        String theEmail = "";
        try {
            Section section = null;
            section = new Ini(ProfileSelector.class.getClassLoader().getResourceAsStream("config/uiconfig.ini")).get("user-profile");
            theEmail = section.get(profile);
        } catch (IOException | NullPointerException ex) {
            log.atError().withThrowable(ex).log("[RAF] Unable to access the configuration file for User Profile.");
        }
        return theEmail;
    }
}
