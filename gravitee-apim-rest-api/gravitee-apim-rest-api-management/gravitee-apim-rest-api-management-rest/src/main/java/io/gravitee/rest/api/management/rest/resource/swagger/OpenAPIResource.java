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
package io.gravitee.rest.api.management.rest.resource.swagger;

import io.gravitee.rest.api.management.rest.resource.AbstractResource;
import io.swagger.v3.oas.annotations.Operation;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;

/**
 * @author Florent CHAMFROY (florent.chamfroy at graviteesource.com)
 * @author GraviteeSource Team
 */
@Path("/openapi.{type:json|yaml}")
public class OpenAPIResource extends AbstractResource {

    @GET
    @Produces({ "application/json", "application/yaml" })
    @Operation(hidden = true)
    public Response getOpenApi(@PathParam("type") String type) throws Exception {
        return Response.ok(this.getClass().getClassLoader().getResourceAsStream("openapi." + type)).build();
    }
}
