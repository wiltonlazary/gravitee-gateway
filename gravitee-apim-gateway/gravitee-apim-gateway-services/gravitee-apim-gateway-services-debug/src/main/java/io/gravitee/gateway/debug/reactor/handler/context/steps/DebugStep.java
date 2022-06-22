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

import com.google.common.base.Stopwatch;
import io.gravitee.definition.model.PolicyScope;
import io.gravitee.definition.model.debug.DebugStepError;
import io.gravitee.definition.model.debug.DebugStepStatus;
import io.gravitee.gateway.api.buffer.Buffer;
import io.gravitee.gateway.debug.reactor.handler.context.AttributeHelper;
import io.gravitee.gateway.policy.PolicyMetadata;
import io.gravitee.gateway.policy.StreamType;
import io.gravitee.policy.api.PolicyResult;
import java.io.Serializable;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Yann TAVERNIER (yann.tavernier at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class DebugStep<T> {

    public static final String DIFF_KEY_HEADERS = "headers";
    public static final String DIFF_KEY_PARAMETERS = "parameters";
    public static final String DIFF_KEY_PATH = "path";
    public static final String DIFF_KEY_PATH_PARAMETERS = "pathParameters";
    public static final String DIFF_KEY_METHOD = "method";
    public static final String DIFF_KEY_CONTEXT_PATH = "contextPath";
    public static final String DIFF_KEY_ATTRIBUTES = "attributes";
    public static final String DIFF_KEY_BODY_BUFFER = "bodyBuffer";
    public static final String DIFF_KEY_BODY = "body";
    public static final String DIFF_KEY_STATUS_CODE = "statusCode";
    public static final String DIFF_KEY_REASON = "reason";

    protected final String policyId;
    protected final StreamType streamType;
    protected final String policyInstanceId;
    protected final PolicyScope policyScope;
    protected final Map<String, Object> diffMap = new HashMap<>();
    protected final Stopwatch stopwatch;
    protected DebugStepStatus status;
    protected String condition;
    protected DebugStepError error;
    protected boolean ended = false;
    private final PolicyMetadata policyMetadata;

    protected DebugStepContent policyInputContent;

    public DebugStep(String policyId, StreamType streamType, String uuid, PolicyScope policyScope, PolicyMetadata policyMetadata) {
        this.policyId = policyId;
        this.streamType = streamType;
        this.policyInstanceId = uuid;
        this.policyScope = policyScope;
        this.policyMetadata = policyMetadata;
        this.stopwatch = Stopwatch.createUnstarted();
        this.policyInputContent = new DebugStepContent();
    }

    public void before(T source, Map<String, Object> attributes) {
        snapshotInputData(source, AttributeHelper.filterAndSerializeAttributes(attributes));
        this.start();
    }

    protected abstract void snapshotInputData(T source, Map<String, Serializable> attributes);

    public void after(T source, Map<String, Object> attributes, Buffer inputBuffer, Buffer outputBuffer) {
        this.stop();
        Map<String, Serializable> cleanedAttributes = AttributeHelper.filterAndSerializeAttributes(attributes);
        generateDiffMap(source, cleanedAttributes, inputBuffer, outputBuffer);
        policyInputContent = null;
        this.status = this.status == null ? DebugStepStatus.COMPLETED : this.status;
    }

    protected abstract void generateDiffMap(T source, Map<String, Serializable> attributes, Buffer inputBuffer, Buffer outputBuffer);

    public Map<String, Object> getDebugDiffContent() {
        return diffMap;
    }

    public void start() {
        if (!stopwatch.isRunning()) {
            this.stopwatch.start();
        }
    }

    public void stop() {
        if (stopwatch.isRunning()) {
            this.stopwatch.stop();
        }
    }

    public void noTransformation() {
        this.status = DebugStepStatus.NO_TRANSFORMATION;
    }

    public void error(Throwable ex) {
        this.stop();
        this.status = DebugStepStatus.ERROR;
        this.error = new DebugStepError();
        this.error.setMessage(ex.getMessage());
    }

    public void error(PolicyResult policyResult) {
        this.stop();
        this.status = DebugStepStatus.ERROR;
        this.error = new DebugStepError();
        this.error.setMessage(policyResult.message());
        this.error.setKey(policyResult.key());
        this.error.setStatus(policyResult.statusCode());
        this.error.setContentType(policyResult.contentType());
    }

    public void onConditionEvaluation(String condition, Boolean isConditionTruthy) {
        this.condition = condition;
        if (isConditionTruthy != null && !isConditionTruthy) {
            this.status = DebugStepStatus.SKIPPED;
        }
    }

    public String getPolicyId() {
        return policyId;
    }

    public StreamType getStreamType() {
        return streamType;
    }

    public Duration elapsedTime() {
        return this.stopwatch.elapsed();
    }

    public String getPolicyInstanceId() {
        return policyInstanceId;
    }

    public PolicyScope getPolicyScope() {
        return policyScope;
    }

    public DebugStepStatus getStatus() {
        return status;
    }

    public String getCondition() {
        return condition;
    }

    public DebugStepError getError() {
        return error;
    }

    public boolean isEnded() {
        return ended;
    }

    public void ended() {
        this.ended = true;
    }

    public PolicyMetadata policyMetadata() {
        return policyMetadata;
    }

    @Override
    public String toString() {
        return (
            getClass().getSimpleName() +
            "{" +
            "policyId='" +
            policyId +
            '\'' +
            ", streamType=" +
            streamType +
            ", stopwatch=" +
            stopwatch.elapsed(TimeUnit.NANOSECONDS) +
            " ns" +
            ", diffMap='" +
            diffMap +
            '\'' +
            '}'
        );
    }
}
