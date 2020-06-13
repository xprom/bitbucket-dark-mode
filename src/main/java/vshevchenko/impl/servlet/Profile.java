package vshevchenko.impl.servlet;

import com.atlassian.bitbucket.auth.AuthenticationContext;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.Maps;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@ExportAsService
public class Profile extends AbstractServlet {
    private final PluginSettingsFactory pluginSettingsFactory;
    private final AuthenticationContext authenticationContext;

    @Inject
    public Profile(
            @ComponentImport SoyTemplateRenderer soyTemplateRenderer,
            @ComponentImport AuthenticationContext authenticationContext,
            @ComponentImport PluginSettingsFactory pluginSettingsFactory
    ) {
        super(soyTemplateRenderer);
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.authenticationContext = authenticationContext;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HashedMap data = new HashedMap();
            data.putAll(this.getContext(req));

            render(resp, "plugin.darkmode.profile", data);
        } catch (Exception e) {
            LoggerFactory.getLogger(AbstractServlet.class).error(e.getMessage());
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected Map<String, Object> getContext(HttpServletRequest req) {
        Map<String, Object> data = Maps.newHashMap();


        data.put("baseUrl", this.getBaseUrl());
        data.put("currentUser", this.authenticationContext.getCurrentUser());
        return data;
    }
}