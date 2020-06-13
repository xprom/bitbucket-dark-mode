package vshevchenko.impl.servlet;

import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.soy.renderer.SoyException;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class AbstractServlet extends HttpServlet {
    private final SoyTemplateRenderer soyTemplateRenderer;

    public AbstractServlet(SoyTemplateRenderer soyTemplateRenderer) {
        this.soyTemplateRenderer = soyTemplateRenderer;
    }

    protected void render(HttpServletResponse resp, String templateName, Map<String, Object> data) throws IOException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            soyTemplateRenderer.render(
                    resp.getWriter(),
                    "vshevchenko.bitbucket-dark-mode:darkmode-template-soy",
                    templateName,
                    data
            );
        } catch (SoyException e) {
            LoggerFactory.getLogger(AbstractServlet.class).error(e.getMessage());
            throw e;
        }
    }

    protected String getBaseUrl() {
        ApplicationPropertiesService propertiesService = ComponentLocator.getComponent(ApplicationPropertiesService.class);
        return Objects.requireNonNull(propertiesService.getBaseUrl()).toString();
    }
}