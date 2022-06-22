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
package io.gravitee.gateway.debug.reactor.handler.context.steps;

import io.gravitee.definition.model.PolicyScope;
import io.gravitee.gateway.api.Response;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.api.http.HttpHeaders;
import io.gravitee.gateway.policy.PolicyMetadata;
import io.gravitee.gateway.policy.StreamType;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public class DebugResponseStep extends DebugStep<Response> {

    public DebugResponseStep(String policyId, StreamType streamType, String uuid, PolicyScope policyScope, PolicyMetadata policyMetadata) {
        super(policyId, streamType, uuid, policyScope, policyMetadata);
    }

    @Override
    public void snapshotInputData(Response response, Map<String, Serializable> attributes) {
        policyInputContent.headers(response.headers()).statusCode(response.status()).reason(response.reason()).attributes(attributes);
    }

    @Override
    public void generateDiffMap(Response response, Map<String, Serializable> attributes, Buffer inputBuffer, Buffer outputBuffer) {
        if (!policyInputContent.getHeaders().deeplyEquals(response.headers())) {
            diffMap.put(DIFF_KEY_HEADERS, HttpHeaders.create(response.headers()));
        }
        if (policyInputContent.getStatusCode() != response.status()) {
            diffMap.put(DIFF_KEY_STATUS_CODE, response.status());
        }
        if (policyInputContent.getReason() != null && !policyInputContent.getReason().equals(response.reason())) {
            diffMap.put(DIFF_KEY_REASON, response.reason());
        }
        if (!policyInputContent.getAttributes().equals(attributes)) {
            diffMap.put(DIFF_KEY_ATTRIBUTES, new HashMap<>(attributes));
        }

        Buffer input = inputBuffer != null ? inputBuffer : Buffer.buffer();
        Buffer output = outputBuffer != null ? outputBuffer : Buffer.buffer();

        if (!input.getNativeBuffer().equals(output.getNativeBuffer())) {
            diffMap.put(DIFF_KEY_BODY_BUFFER, Buffer.buffer(output.getBytes()));
        }
    }
}
