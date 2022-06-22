/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.rest.api.management.rest.resource;

import io.gravitee.common.http.MediaType;
import io.gravitee.rest.api.management.rest.security.Permission;
import io.gravitee.rest.api.management.rest.security.Permissions;
import io.gravitee.rest.api.management.rest.utils.HttpHeadersUtil;
import io.gravitee.rest.api.model.*;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.model.documentation.PageQuery;
import io.gravitee.rest.api.model.permissions.RolePermission;
import io.gravitee.rest.api.model.permissions.RolePermissionAction;
import io.gravitee.rest.api.service.AccessControlService;
import io.gravitee.rest.api.service.ApiService;
import io.gravitee.rest.api.service.PageService;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.exceptions.ForbiddenAccessException;
import io.gravitee.rest.api.service.exceptions.PageMarkdownTemplateActionException;
import io.gravitee.rest.api.service.exceptions.PageSystemFolderActionException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author Guillaume GILLON (guillaume.gillon@outlook.com)
 * @author GraviteeSource Team
 */
@Tag(name = "API Pages")
public class ApiPagesResource extends AbstractResource {

    @Inject
    private ApiService apiService;

    @Inject
    private PageService pageService;

    @Inject
    private AccessControlService accessControlService;

    @Context
    private ResourceContext resourceContext;

    @SuppressWarnings("UnresolvedRestParam")
    @PathParam("api")
    @Parameter(name = "api", hidden = true)
    private String api;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "List pages", description = "User must have the READ permission to use this service")
    @ApiResponse(
        responseCode = "200",
        description = "List of pages",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            array = @ArraySchema(schema = @Schema(implementation = PageEntity.class))
        )
    )
    @ApiResponse(responseCode = "500", description = "Internal server error")
    public List<PageEntity> getApiPages(
        @HeaderParam("Accept-Language") String acceptLang,
        @QueryParam("homepage") Boolean homepage,
        @QueryParam("type") PageType type,
        @QueryParam("parent") String parent,
        @QueryParam("name") String name,
        @QueryParam("root") Boolean rootParent,
        @QueryParam("translated") boolean translated
    ) {
        final String acceptedLocale = HttpHeadersUtil.getFirstAcceptedLocaleName(acceptLang);

        final ExecutionContext executionContext = GraviteeContext.getExecutionContext();
        final ApiEntity apiEntity = apiService.findById(executionContext, api);

        if (
            Visibility.PUBLIC.equals(apiEntity.getVisibility()) ||
            hasPermission(executionContext, RolePermission.API_DOCUMENTATION, api, RolePermissionAction.READ)
        ) {
            return pageService
                .search(
                    executionContext.getEnvironmentId(),
                    new PageQuery.Builder().api(api).homepage(homepage).type(type).parent(parent).name(name).rootParent(rootParent).build(),
                    translated ? acceptedLocale : null
                )
                .stream()
                .filter(page -> isDisplayable(apiEntity, page))
                .map(
                    page -> {
                        // check if the page is used as GeneralCondition by an active Plan
                        // and update the PageEntity to transfer the information to the FrontEnd
                        page.setGeneralConditions(pageService.isPageUsedAsGeneralConditions(executionContext, page, api));
                        return page;
                    }
                )
                .collect(Collectors.toList());
        }
        throw new ForbiddenAccessException();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create a page", description = "User must have the MANAGE_PAGES permission to use this service")
    @ApiResponse(
        responseCode = "201",
        description = "Page successfully created",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PageEntity.class))
    )
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.API_DOCUMENTATION, acls = RolePermissionAction.CREATE) })
    public Response createApiPage(@Parameter(name = "page", required = true) @Valid @NotNull NewPageEntity newPageEntity) {
        if (newPageEntity.getType().equals(PageType.SYSTEM_FOLDER)) {
            throw new PageSystemFolderActionException("Create");
        } else if (newPageEntity.getType().equals(PageType.MARKDOWN_TEMPLATE)) {
            throw new PageMarkdownTemplateActionException("Create");
        }

        int order = pageService.findMaxApiPageOrderByApi(api) + 1;
        newPageEntity.setOrder(order);
        newPageEntity.setLastContributor(getAuthenticatedUser());
        PageEntity newPage = pageService.createPage(GraviteeContext.getExecutionContext(), api, newPageEntity);
        if (newPage != null) {
            return Response.created(this.getLocationHeader(newPage.getId())).entity(newPage).build();
        }

        return Response.serverError().build();
    }

    @POST
    @Path("/_fetch")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "Refresh all pages by calling their associated fetcher",
        description = "User must have the MANAGE_PAGES permission to use this service"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Pages successfully refreshed",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PageEntity.class))
    )
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.API_DOCUMENTATION, acls = RolePermissionAction.UPDATE) })
    public Response fetchAllApiPages() {
        String contributor = getAuthenticatedUser();
        pageService.fetchAll(GraviteeContext.getExecutionContext(), new PageQuery.Builder().api(api).build(), contributor);
        return Response.noContent().build();
    }

    @Path("{page}")
    public ApiPageResource getApiPageResource() {
        return resourceContext.getResource(ApiPageResource.class);
    }

    @POST
    @Path("/_import")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Import pages", description = "User must be ADMIN to use this service")
    @ApiResponse(
        responseCode = "201",
        description = "Page successfully created",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PageEntity.class))
    )
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.API_DOCUMENTATION, acls = RolePermissionAction.CREATE) })
    public List<PageEntity> importApiPageFiles(@Parameter(name = "page", required = true) @Valid @NotNull ImportPageEntity pageEntity) {
        pageEntity.setLastContributor(getAuthenticatedUser());
        return pageService.importFiles(GraviteeContext.getExecutionContext(), api, pageEntity);
    }

    @PUT
    @Path("/_import")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Import pages", description = "User must be ADMIN to use this service")
    @ApiResponse(
        responseCode = "201",
        description = "Page successfully updated",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PageEntity.class))
    )
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.API_DOCUMENTATION, acls = RolePermissionAction.CREATE) })
    public List<PageEntity> updateApiPageImportFiles(
        @Parameter(name = "page", required = true) @Valid @NotNull ImportPageEntity pageEntity
    ) {
        pageEntity.setLastContributor(getAuthenticatedUser());
        return pageService.importFiles(GraviteeContext.getExecutionContext(), api, pageEntity);
    }

    private boolean isDisplayable(ApiEntity api, PageEntity page) {
        return (
            (isAuthenticated() && isAdmin()) ||
            accessControlService.canAccessPageFromConsole(GraviteeContext.getExecutionContext(), api, page)
        );
    }
}
