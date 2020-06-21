package vshevchenko.impl;

public class ConditionDarkModeEnabled  {

    }
//}
//public class ConditionDarkModeEnabled implements Condition {
//
//    private AuthenticationContext authenticationContext;
//
//    private PluginSettingsFactory pluginSettingsFactory;
//
//    public ConditionDarkModeEnabled(AuthenticationContext authenticationContext, PluginSettingsFactory pluginSettingsFactory) {
//        this.authenticationContext = authenticationContext;
//        this.pluginSettingsFactory = pluginSettingsFactory;
//    }
//
//    @Inject
//    public void setAuthenticationContext(AuthenticationContext authenticationContext) {
//        this.authenticationContext = authenticationContext;
//    }
//
//    @Inject
//    public void setPluginSettingsFactory(PluginSettingsFactory pluginSettingsFactory)
//    {
//        this.pluginSettingsFactory = pluginSettingsFactory;
//    }
//
//    @Override
//    public void init(Map<String, String> params) throws PluginParseException {
//    }
//
//    @Override
//    public boolean shouldDisplay(Map<String, Object> context) {
//        ApplicationUser principal = authenticationContext.getCurrentUser();
//
//        if (principal == null) {
//            return false;
//        }
//
//        try {
//            return pluginSettingsFactory.createGlobalSettings().get(getUserKey(principal)).equals("enabled");
//        } catch (IllegalArgumentException e) {
//            return false;
//        }
//    }
//
//    private String getUserKey(ApplicationUser principal) {
//        return String.join("", "dark-mode-", Integer.toString(principal.getId()));
//    }
//}
