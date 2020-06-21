package vshevchenko.impl;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Date;
import java.util.Map;

public class ConditionDarkModeBanner implements Condition {

    private AuthenticationContext authenticationContext;

    private PluginSettingsFactory pluginSettingsFactory;

    public ConditionDarkModeBanner(AuthenticationContext authenticationContext, PluginSettingsFactory pluginSettingsFactory) {
        this.authenticationContext = authenticationContext;
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Inject
    public void setAuthenticationContext(AuthenticationContext authenticationContext) {
        this.authenticationContext = authenticationContext;
    }

    @Inject
    public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory) {
        this.pluginSettingsFactory = pluginSettingsFactory;
    }

    @Override
    public void init(Map<String, String> params) throws PluginParseException {
    }

    @Override
    public boolean shouldDisplay(Map<String, Object> context) {
        ApplicationUser principal = authenticationContext.getCurrentUser();

        if (principal == null) {
            return false;
        }

        try {
            PluginSettings plaginSettings = pluginSettingsFactory.createGlobalSettings();
            String enabled = (String) plaginSettings.get(getUserKey(principal));
            if (enabled == null) {
                return isFirstOpenig(principal);
            }

            if (enabled.equals("enabled")) {
                return false;
            }

            return isFirstOpenig(principal);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // если это первое открытие - то показываем банне
    private boolean isFirstOpenig(ApplicationUser principal) {
        PluginSettings plaginSettings = pluginSettingsFactory.createGlobalSettings();
        String enabled = (String) plaginSettings.get(getUserKeyFirstSee(principal));
        if (enabled == null) {
            return true;
        }

        if (enabled.equals("2")) {
            return false;
        }

        return true;
    }

    private String getUserKey(ApplicationUser principal) {
        return String.join("", "dark-mode-", Integer.toString(principal.getId()));
    }

    private String getUserKeyFirstSee(ApplicationUser principal) {
        return String.join("", "dark-mode-banner-", Integer.toString(principal.getId()));
    }
}
