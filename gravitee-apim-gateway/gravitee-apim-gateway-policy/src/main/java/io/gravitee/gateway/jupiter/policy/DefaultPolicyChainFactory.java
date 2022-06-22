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
package io.gravitee.gateway.jupiter.policy;

import static io.gravitee.gateway.jupiter.api.ExecutionPhase.ASYNC_REQUEST;
import static io.gravitee.gateway.jupiter.api.ExecutionPhase.REQUEST;

import io.gravitee.definition.model.ExecutionMode;
import io.gravitee.definition.model.flow.Flow;
import io.gravitee.definition.model.flow.Step;
import io.gravitee.gateway.jupiter.api.ExecutionPhase;
import io.gravitee.gateway.jupiter.api.hook.Hook;
import io.gravitee.gateway.jupiter.api.policy.Policy;
import io.gravitee.gateway.jupiter.policy.tracing.TracingMessageHook;
import io.gravitee.gateway.jupiter.policy.tracing.TracingPolicyHook;
import io.gravitee.gateway.policy.PolicyMetadata;
import io.gravitee.node.api.cache.Cache;
import io.gravitee.node.api.cache.CacheConfiguration;
import io.gravitee.node.api.configuration.Configuration;
import io.gravitee.node.cache.standalone.StandaloneCache;
import io.netty.util.internal.StringUtil;
import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link PolicyChainFactory} that can be instantiated per-api or per-organization, and optimized to maximize the reuse of created {@link PolicyChain} thanks to a cache.
 *
 * <b>WARNING</b>: this factory must absolutely be created per api to ensure proper cache destruction when deploying / un-deploying the api.
 *
 * @author Jeoffrey HAEYAERT (jeoffrey.haeyaert at graviteesource.com)
 * @author GraviteeSource Team
 */
public class DefaultPolicyChainFactory implements PolicyChainFactory {

    public static final long CACHE_MAX_SIZE = 15;
    public static final long CACHE_TIME_TO_IDLE = 3600;
    private static final String ID_SEPARATOR = "-";
    private final PolicyManager policyManager;
    private final Cache<String, PolicyChain> policyChains;
    private final List<Hook> policyHooks = new ArrayList<>();

    public DefaultPolicyChainFactory(final String id, final Configuration configuration, final PolicyManager policyManager) {
        this.policyManager = policyManager;

        final CacheConfiguration cacheConfiguration = new CacheConfiguration();
        cacheConfiguration.setMaxSize(CACHE_MAX_SIZE);
        cacheConfiguration.setTimeToIdleSeconds(CACHE_TIME_TO_IDLE);

        this.policyChains = new StandaloneCache<>(id + "-policyChainFactory", cacheConfiguration);
        initPolicyHooks(configuration);
    }

    private void initPolicyHooks(final Configuration configuration) {
        boolean tracing = configuration.getProperty("services.tracing.enabled", Boolean.class, false);
        if (tracing) {
            policyHooks.add(new TracingPolicyHook());
            policyHooks.add(new TracingMessageHook());
        }
    }

    /**
     * {@inheritDoc}
     *
     * Once created, the policy chain is put in cache to avoid useless re-instantiations.
     */
    @Override
    public PolicyChain create(final String flowChainId, Flow flow, ExecutionPhase phase) {
        final String key = getFlowKey(flow, phase);
        PolicyChain policyChain = policyChains.get(key);

        if (policyChain == null) {
            final List<Step> steps = getSteps(flow, phase);

            final List<Policy> policies = steps
                .stream()
                .filter(Step::isEnabled)
                .map(this::buildPolicyMetadata)
                .map(policyMetadata -> policyManager.create(phase, policyMetadata))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            String policyChainId = getFlowId(flowChainId, flow);
            policyChain = new PolicyChain(policyChainId, policies, phase);
            policyChain.addHooks(policyHooks);
            policyChains.put(key, policyChain);
        }

        return policyChain;
    }

    private PolicyMetadata buildPolicyMetadata(Step step) {
        final PolicyMetadata policyMetadata = new PolicyMetadata(step.getPolicy(), step.getConfiguration(), step.getCondition());
        policyMetadata.metadata().put(PolicyMetadata.MetadataKeys.EXECUTION_MODE, ExecutionMode.JUPITER);

        return policyMetadata;
    }

    private List<Step> getSteps(Flow flow, ExecutionPhase phase) {
        final List<Step> steps;

        if (phase == REQUEST || phase == ASYNC_REQUEST) {
            steps = flow.getPre();
        } else {
            steps = flow.getPost();
        }

        return steps != null ? steps : Collections.emptyList();
    }

    private String getFlowKey(Flow flow, ExecutionPhase phase) {
        return flow.hashCode() + "-" + phase.name();
    }

    private String getFlowId(final String flowChainId, final Flow flow) {
        StringBuilder flowNameBuilder = new StringBuilder(flowChainId).append(ID_SEPARATOR);
        if (StringUtil.isNullOrEmpty(flow.getName())) {
            if (flow.getMethods().isEmpty()) {
                flowNameBuilder.append("ALL").append(ID_SEPARATOR);
            } else {
                flow.getMethods().forEach(httpMethod -> flowNameBuilder.append(httpMethod).append("-"));
            }
            flowNameBuilder.append(flow.getPath());
        } else {
            flowNameBuilder.append(flow.getName());
        }
        return flowNameBuilder.toString().toLowerCase(Locale.ROOT);
    }
}
