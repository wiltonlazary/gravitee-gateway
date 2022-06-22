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
package io.gravitee.rest.api.management.rest.resource.param;

import io.swagger.v3.oas.annotations.Parameter;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
public class AuditParam {

    @QueryParam("environment")
    private String environmentId;

    @QueryParam("api")
    private String apiId;

    @QueryParam("application")
    private String applicationId;

    @QueryParam("type")
    @Parameter(description = "filter on the type of audit")
    private AuditType type;

    @QueryParam("event")
    @Parameter(description = "filter by the name of an event.", example = "APPLICATION_UPDATED, API_CREATED, METADATA_DELETED, ...")
    private String event;

    @QueryParam("from")
    @Parameter(description = "Timestamp used to define the start date of the time window to query")
    private long from;

    @QueryParam("to")
    @Parameter(description = "Timestamp used to define the end date of the time window to query")
    private long to;

    @QueryParam("size")
    @Parameter(description = "Number of elements per page")
    @DefaultValue("20")
    private int size;

    @QueryParam("page")
    @DefaultValue("1")
    private int page;

    public String getEnvironmentId() {
        return environmentId;
    }

    public void setEnvironmentId(String environmentId) {
        this.environmentId = environmentId;
    }

    public String getApiId() {
        return apiId;
    }

    public void setApiId(String apiId) {
        this.apiId = apiId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public AuditType getType() {
        return type;
    }

    public void setType(AuditType type) {
        this.type = type;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
