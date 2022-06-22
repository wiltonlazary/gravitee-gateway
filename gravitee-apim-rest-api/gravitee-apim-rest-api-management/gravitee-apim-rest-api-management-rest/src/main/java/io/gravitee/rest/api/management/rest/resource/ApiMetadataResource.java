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
import io.gravitee.rest.api.model.ApiMetadataEntity;
import io.gravitee.rest.api.model.NewApiMetadataEntity;
import io.gravitee.rest.api.model.UpdateApiMetadataEntity;
import io.gravitee.rest.api.model.api.ApiEntity;
import io.gravitee.rest.api.model.permissions.RolePermission;
import io.gravitee.rest.api.model.permissions.RolePermissionAction;
import io.gravitee.rest.api.service.ApiMetadataService;
import io.gravitee.rest.api.service.common.ExecutionContext;
import io.gravitee.rest.api.service.common.GraviteeContext;
import io.gravitee.rest.api.service.search.SearchEngineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * @author Azize ELAMRANI (azize.elamrani at graviteesource.com)
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
@Tag(name = "API Metadata")
public class ApiMetadataResource extends AbstractResource {

    @Inject
    private ApiMetadataService metadataService;

    @Inject
    private SearchEngineService searchEngineService;

    @SuppressWarnings("UnresolvedRestParam")
    @PathParam("api")
    @Parameter(name = "api", hidden = true)
    private String api;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "List metadata for the given API",
        description = "User must have the API_METADATA[READ] permission to use this service"
    )
    @ApiResponse(
        responseCode = "200",
        description = "List of metadata",
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON,
            array = @ArraySchema(schema = @Schema(implementation = ApiMetadataEntity.class))
        )
    )
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.API_METADATA, acls = RolePermissionAction.READ) })
    public List<ApiMetadataEntity> getApiMetadatas() {
        return metadataService.findAllByApi(api);
    }

    @GET
    @Path("{metadata}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(
        summary = "A metadata for the given API and metadata id",
        description = "User must have the API_METADATA[READ] permission to use this service"
    )
    @ApiResponse(
        responseCode = "200",
        description = "A metadata",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiMetadataEntity.class))
    )
    @ApiResponse(responseCode = "404", description = "Metadata not found")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.API_METADATA, acls = RolePermissionAction.READ) })
    public ApiMetadataEntity getApiMetadata(@PathParam("metadata") String metadata) {
        return metadataService.findByIdAndApi(metadata, api);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create an API metadata", description = "User must have the API_METADATA[CREATE] permission to use this service")
    @ApiResponse(
        responseCode = "201",
        description = "A new API metadata",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiMetadataEntity.class))
    )
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.API_METADATA, acls = RolePermissionAction.CREATE) })
    public Response createApiMetadata(@Valid @NotNull final NewApiMetadataEntity metadata) {
        // prevent creation of a metadata on an another API
        metadata.setApiId(api);

        final ExecutionContext executionContext = GraviteeContext.getExecutionContext();
        final ApiMetadataEntity apiMetadataEntity = metadataService.create(executionContext, metadata);
        ApiEntity apiEntity = apiService.fetchMetadataForApi(executionContext, apiService.findById(executionContext, api));
        searchEngineService.index(executionContext, apiEntity, false);
        return Response.created(this.getLocationHeader(apiMetadataEntity.getKey())).entity(apiMetadataEntity).build();
    }

    @PUT
    @Path("{metadata}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Update an API metadata", description = "User must have the API_METADATA[UPDATE] permission to use this service")
    @ApiResponse(
        responseCode = "201",
        description = "API metadata",
        content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ApiMetadataEntity.class))
    )
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.API_METADATA, acls = RolePermissionAction.UPDATE) })
    public Response updateApiMetadata(
        @PathParam("metadata") String metadataPathParam,
        @Valid @NotNull final UpdateApiMetadataEntity metadata
    ) {
        // prevent update of a metadata on an another API
        metadata.setApiId(api);

        return Response.ok(metadataService.update(GraviteeContext.getExecutionContext(), metadata)).build();
    }

    @DELETE
    @Path("{metadata}")
    @Operation(summary = "Delete a metadata", description = "User must have the API_METADATA[DELETE] permission to use this service")
    @ApiResponse(responseCode = "204", description = "Metadata successfully deleted")
    @ApiResponse(responseCode = "500", description = "Internal server error")
    @Permissions({ @Permission(value = RolePermission.API_METADATA, acls = RolePermissionAction.DELETE) })
    public Response deleteApiMetadata(@PathParam("metadata") String metadata) {
        metadataService.delete(GraviteeContext.getExecutionContext(), metadata, api);
        ApiEntity apiEntity = apiService.fetchMetadataForApi(
            GraviteeContext.getExecutionContext(),
            apiService.findById(GraviteeContext.getExecutionContext(), api)
        );
        searchEngineService.index(GraviteeContext.getExecutionContext(), apiEntity, false);
        return Response.noContent().build();
    }
}
