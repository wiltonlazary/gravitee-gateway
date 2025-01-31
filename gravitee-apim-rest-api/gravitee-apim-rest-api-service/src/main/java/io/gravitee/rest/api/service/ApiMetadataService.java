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
package io.gravitee.rest.api.service;

import io.gravitee.rest.api.model.*;
import io.gravitee.rest.api.service.common.ExecutionContext;
import java.util.List;

/**
 * @author Azize ELAMRANI (azize at graviteesource.com)
 * @author GraviteeSource Team
 */
public interface ApiMetadataService {
    List<ApiMetadataEntity> findAllByApi(String apiId);

    ApiMetadataEntity findByIdAndApi(String metadataId, String apiId);

    List<ApiMetadataEntity> create(final ExecutionContext executionContext, List<ApiMetadataEntity> apiMetadata, String apiId);
    ApiMetadataEntity create(final ExecutionContext executionContext, NewApiMetadataEntity metadata);

    ApiMetadataEntity update(final ExecutionContext executionContext, UpdateApiMetadataEntity metadata);

    void delete(final ExecutionContext executionContext, String metadataId, String api);

    void deleteAllByApi(final ExecutionContext executionContext, String apiId);
}
