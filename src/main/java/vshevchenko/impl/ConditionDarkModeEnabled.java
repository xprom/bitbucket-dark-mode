package vshevchenko.impl;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.plugin.PluginParseException;
import com.atlassian.plugin.web.Condition;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import javax.inject.Inject;
import java.util.Map;

public class ConditionDarkModeEnabled implements Condition {

    private AuthenticationContext authenticationContext;

    private PluginSettingsFactory pluginSettingsFactory;

    public ConditionDarkModeEnabled(AuthenticationContext authenticationContext, PluginSettingsFactory pluginSettingsFactory) {
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
                return false;
            }

            return enabled.equals("enabled");
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private String getUserKey(ApplicationUser principal) {
        return String.join("", "dark-mode-", Integer.toString(principal.getId()));
    }
}
