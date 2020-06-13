package vshevchenko.impl.servlet;

import com.atlassian.bitbucket.project.ProjectService;
import com.atlassian.bitbucket.repository.Repository;
import com.atlassian.bitbucket.repository.RepositoryService;
import com.atlassian.bitbucket.user.ApplicationUser;
import com.atlassian.bitbucket.user.UserService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.collections.map.HashedMap;
import org.slf4j.LoggerFactory;
import vshevchenko.impl.fileTree.ServiceInterface;
import vshevchenko.impl.logger.Log;
import vshevchenko.impl.servlet.AbstractServlet;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@ExportAsService
public class WatchersProfileServlet extends AbstractServlet {
    protected final Log logger;
    protected final UserService userService;
    protected final ServiceInterface fileTreeService;
    protected final UserManager userManager;

    @Inject
    public WatchersProfileServlet(
            ServiceInterface fileTreeService,
            @ComponentImport SoyTemplateRenderer soyTemplateRenderer,
            @ComponentImport RepositoryService repositoryService,
            @ComponentImport ProjectService projectService,
            @ComponentImport UserService userService,
            @ComponentImport UserManager userManager,
            @ComponentImport PluginSettingsFactory pluginSettingsFactory
    ) {
        super(soyTemplateRenderer, repositoryService, projectService, pluginSettingsFactory);
        this.userService = userService;
        this.userManager = userManager;
        this.fileTreeService = fileTreeService;
        this.logger = new Log(pluginSettingsFactory, LoggerFactory.getLogger(WatchersProfileServlet.class));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            this.fileTreeService.setUser(
                    this.getUser(req)
            );

            HashedMap data = new HashedMap();

            if (req.getParameter("clearSubscription") != null && this.isProfilePageForCurrentUser(req)) {
                this.fileTreeService.clearData();
                data.put("clearStatusOk", true);
            }

            data.putAll(this.getContext(req));

            HashMap<String, HashSet<HashMap<String, String>>> result = new HashMap<>();
            for (String projectKey : this.fileTreeService.getProfile().keySet()) {
                for (Integer repoId : this.fileTreeService.getProfile().get(projectKey).keySet()) {
                    for (String path : this.fileTreeService.getProfile().get(projectKey).get(repoId)) {
                        try {
                            result.putIfAbsent(repositoryService.getById(repoId).getProject().getName(), new HashSet<>());
                            ParserUrlResult parserUrlResult = this.fileTreeService.parseUrl(path);
                            result.get(repositoryService.getById(repoId).getProject().getName()).add(
                                    this.constructResultData(parserUrlResult.path, repositoryService.getById(repoId))
                            );
                        } catch (NullPointerException e) {
                            continue;
                        }
                    }
                }
            }

            if (this.isProfilePageForCurrentUser(req)) {
                data.put("isCurrentUserProfile", true);
            }

            data.put("repos", result);
            render(resp, "plugin.watchers.profile", data);
        } catch (Exception e) {
            this.logger.error(e);
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    protected HashMap<String, String> constructResultData(String path, Repository repository) {
        HashMap<String, String> result = new HashMap<>();

        if (repository == null) {
            result.put("repo", "repoIsNull");
        } else {
            result.put("repo", repository.getName());
            result.put("repoSlug", repository.getSlug());
            result.put("projectKey", repository.getProject().getKey());
        }

        result.put("path", path);
        return result;
    }

    /**
     * Открыта старница для текущего пользователя
     *
     * @param req
     * @return
     */
    protected boolean isProfilePageForCurrentUser(HttpServletRequest req) {
        try {
            return this.getUser(req) != null && this.getUser(req).getId() == userService.getUserByName(userManager.getRemoteUser(req).getUsername()).getId();
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            HashMap<String, Object> data = new HashMap<>();

            // снимаес с подписки
            // пришел запрос на отписку пользователя
            if (this.isProfilePageForCurrentUser(req)) {
                if (req.getParameterMap().get("file") != null) {
                    data.put("uri", req.getParameterMap().get("file")[0]);
                    String[] pathInfo = this.fileTreeService.getFileName(req.getParameterMap().get("file")[0]);
                    if (this.fileTreeService.isWatchedCurrentFile(pathInfo)) {
                        this.fileTreeService.saveWatchingCurrentFile(this.fileTreeService.parseUrl(req.getParameterMap().get("file")[0]));
                        data.put("result", "success");
                    } else {
                        data.put("result", "userIsNoSubscribed");
                    }
                }
            }

            resp.getWriter().write(new Gson().toJson(data));
        } catch (NullPointerException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Получить пользователя из url
     *
     * @param req
     * @return
     * @throws NullPointerException
     */
    protected ApplicationUser getUser(HttpServletRequest req) throws Exception {
        // Get userSlug from path
        String pathInfo = req.getPathInfo();

        String userSlug = pathInfo.substring(1);
        if (userService.getUserBySlug(userSlug) == null) {
            throw new NullPointerException();
        }

        return userService.getUserBySlug(userSlug);
    }

    /**
     * @param req
     * @return
     * @throws NullPointerException
     */
    protected Map<String, Object> getContext(HttpServletRequest req) throws NullPointerException {
        Map<String, Object> data = Maps.newHashMap();

        // Get userSlug from path
        String pathInfo = req.getPathInfo();

        String userSlug = pathInfo.substring(1); // Strip leading slash
        ApplicationUser user = userService.getUserBySlug(userSlug);

        if (user == null) {
            throw new NullPointerException();
        }

        data.put("baseUrl", this.getBaseUrl());
        data.put("user", user);
        return data;
    }
}