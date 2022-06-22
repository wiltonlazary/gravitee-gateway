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
package io.gravitee.gateway.policy.impl;

import io.gravitee.gateway.core.condition.ConditionEvaluator;
import io.gravitee.gateway.policy.PolicyFactory;
import io.gravitee.gateway.policy.PolicyFactoryCreator;
import io.gravitee.gateway.policy.PolicyPluginFactory;
import io.gravitee.gateway.policy.impl.tracing.TracingPolicyPluginFactory;
import io.gravitee.node.api.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class PolicyFactoryCreatorImpl implements PolicyFactoryCreator {

    private final Logger logger = LoggerFactory.getLogger(PolicyFactoryCreatorImpl.class);

    private final Configuration configuration;
    private final PolicyPluginFactory policyPluginFactory;
    private final ConditionEvaluator<String> conditionEvaluator;

    public PolicyFactoryCreatorImpl(
        final Configuration configuration,
        final PolicyPluginFactory policyPluginFactory,
        ConditionEvaluator<String> conditionEvaluator
    ) {
        this.configuration = configuration;
        this.policyPluginFactory = policyPluginFactory;
        this.conditionEvaluator = conditionEvaluator;
    }

    @Override
    public PolicyFactory create() {
        boolean tracing = configuration.getProperty("services.tracing.enabled", Boolean.class, false);

        if (tracing) {
            logger.debug("Tracing is enabled, looking to decorate all policies...");
        }

        final PolicyFactory policyFactory = tracing
            ? new TracingPolicyPluginFactory(policyPluginFactory, conditionEvaluator)
            : new PolicyFactoryImpl(policyPluginFactory, conditionEvaluator);
        return new CachedPolicyFactory(policyFactory);
    }
}
