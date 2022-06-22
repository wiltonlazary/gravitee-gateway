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
package io.gravitee.definition.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.gravitee.common.http.HttpStatusCode;
import java.io.Serializable;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class Cors implements Serializable {

    public static int DEFAULT_ERROR_STATUS_CODE = HttpStatusCode.BAD_REQUEST_400;

    @JsonProperty("enabled")
    private boolean enabled;

    @JsonProperty("allowOrigin")
    private Set<String> accessControlAllowOrigin;

    @JsonIgnore
    private Set<Pattern> accessControlAllowOriginRegex;

    @JsonProperty("exposeHeaders")
    private Set<String> accessControlExposeHeaders;

    @JsonProperty("maxAge")
    private int accessControlMaxAge = -1;

    @JsonProperty("allowCredentials")
    private boolean accessControlAllowCredentials;

    @JsonProperty("allowMethods")
    private Set<String> accessControlAllowMethods;

    @JsonProperty("allowHeaders")
    private Set<String> accessControlAllowHeaders;

    //FIXME Should be removed as it is never used, neither by UI or API neither by cors processors
    @JsonIgnore
    private int errorStatusCode = DEFAULT_ERROR_STATUS_CODE;

    @JsonProperty("runPolicies")
    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean runPolicies;

    public static int getDefaultErrorStatusCode() {
        return DEFAULT_ERROR_STATUS_CODE;
    }

    public static void setDefaultErrorStatusCode(int defaultErrorStatusCode) {
        DEFAULT_ERROR_STATUS_CODE = defaultErrorStatusCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Set<String> getAccessControlAllowOrigin() {
        return accessControlAllowOrigin;
    }

    public void setAccessControlAllowOrigin(Set<String> accessControlAllowOrigin) {
        this.accessControlAllowOrigin = accessControlAllowOrigin;
    }

    public Set<String> getAccessControlExposeHeaders() {
        return accessControlExposeHeaders;
    }

    public void setAccessControlExposeHeaders(Set<String> accessControlExposeHeaders) {
        this.accessControlExposeHeaders = accessControlExposeHeaders;
    }

    public int getAccessControlMaxAge() {
        return accessControlMaxAge;
    }

    public void setAccessControlMaxAge(int accessControlMaxAge) {
        this.accessControlMaxAge = accessControlMaxAge;
    }

    public boolean isAccessControlAllowCredentials() {
        return accessControlAllowCredentials;
    }

    public void setAccessControlAllowCredentials(boolean accessControlAllowCredentials) {
        this.accessControlAllowCredentials = accessControlAllowCredentials;
    }

    public Set<String> getAccessControlAllowMethods() {
        return accessControlAllowMethods;
    }

    public void setAccessControlAllowMethods(Set<String> accessControlAllowMethods) {
        this.accessControlAllowMethods = accessControlAllowMethods;
    }

    public Set<String> getAccessControlAllowHeaders() {
        return accessControlAllowHeaders;
    }

    public void setAccessControlAllowHeaders(Set<String> accessControlAllowHeaders) {
        this.accessControlAllowHeaders = accessControlAllowHeaders;
    }

    public int getErrorStatusCode() {
        return errorStatusCode;
    }

    public void setErrorStatusCode(int errorStatusCode) {
        this.errorStatusCode = errorStatusCode;
    }

    public Set<Pattern> getAccessControlAllowOriginRegex() {
        return accessControlAllowOriginRegex;
    }

    public void setAccessControlAllowOriginRegex(Set<Pattern> accessControlAllowOriginRegex) {
        this.accessControlAllowOriginRegex = accessControlAllowOriginRegex;
    }

    public boolean isRunPolicies() {
        return runPolicies;
    }

    public void setRunPolicies(boolean runPolicies) {
        this.runPolicies = runPolicies;
    }
}
