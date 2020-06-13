package vshevchenko.impl.servlet;

import com.atlassian.bitbucket.project.Project;
import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.server.ApplicationPropertiesService;
import com.atlassian.sal.api.component.ComponentLocator;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import org.slf4j.LoggerFactory;
import vshevchenko.impl.logger.Log;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class AbstractServlet extends HttpServlet {
    protected final SoyTemplateRenderer soyTemplateRenderer;
    protected final RepositoryService repositoryService;
    protected final ProjectService projectService;
    protected final Log logger;

    public AbstractServlet(
            SoyTemplateRenderer soyTemplateRenderer,
            RepositoryService repositoryService,
            ProjectService projectService,
            PluginSettingsFactory pluginSettingsFactory
    ) {
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.projectService = projectService;
        this.repositoryService = repositoryService;
        this.logger = new Log(pluginSettingsFactory, LoggerFactory.getLogger(AbstractServlet.class));
    }

    protected void render(HttpServletResponse resp, String templateName, Map<String, Object> data) throws IOException, ServletException {
        resp.setContentType("text/html;charset=UTF-8");
        try {
            soyTemplateRenderer.render(
                    resp.getWriter(),
                    "vshevchenko.favorite-file-notifiler:watcher-template-soy",
                    templateName,
                    data
            );
        } catch (SoyException e) {
            this.logger.error(e);
        }
    }

    protected String getBaseUrl() {
        ApplicationPropertiesService propertiesService = ComponentLocator.getComponent(ApplicationPropertiesService.class);
        return propertiesService.getBaseUrl().toString();
    }

    public static class ParserUrlResult {
        public Repository repository;
        public Project project;
        public String path;
        public CharSequence[] fullPath;

        public ParserUrlResult(Repository repository, Project project, String path, CharSequence[] fullPath) {
            this.repository = repository;
            this.project = project;
            this.path = path;
            this.fullPath = fullPath;
        }
    }
}