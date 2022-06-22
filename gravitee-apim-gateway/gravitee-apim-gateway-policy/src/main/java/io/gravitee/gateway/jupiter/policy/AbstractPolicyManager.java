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

import com.fasterxml.jackson.databind.type.TypeFactory;
import io.gravitee.common.component.AbstractLifecycleComponent;
import io.gravitee.gateway.core.classloader.DefaultClassLoader;
import io.gravitee.gateway.core.component.ComponentProvider;
import io.gravitee.gateway.jupiter.api.ExecutionPhase;
import io.gravitee.gateway.jupiter.api.policy.Policy;
import io.gravitee.gateway.policy.PolicyConfigurationFactory;
import io.gravitee.gateway.policy.PolicyManifest;
import io.gravitee.gateway.policy.PolicyMetadata;
import io.gravitee.gateway.policy.impl.PolicyLoader;
import io.gravitee.plugin.core.api.ConfigurablePluginManager;
import io.gravitee.plugin.policy.PolicyClassLoaderFactory;
import io.gravitee.plugin.policy.PolicyPlugin;
import io.gravitee.policy.api.PolicyConfiguration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Guillaume LAMIRAND (guillaume.lamirand at graviteesource.com)
 * @author GraviteeSource Team
 */
public abstract class AbstractPolicyManager extends AbstractLifecycleComponent<PolicyManager> implements PolicyManager {

    protected final Map<String, PolicyManifest> manifests;
    protected final PolicyFactory policyFactory;
    protected final PolicyConfigurationFactory policyConfigurationFactory;
    protected final PolicyLoader policyLoader;

    protected AbstractPolicyManager(
        DefaultClassLoader classLoader,
        PolicyFactory policyFactory,
        PolicyConfigurationFactory policyConfigurationFactory,
        ConfigurablePluginManager<PolicyPlugin<?>> policyPluginManager,
        PolicyClassLoaderFactory policyClassLoaderFactory,
        ComponentProvider componentProvider
    ) {
        this.manifests = new ConcurrentHashMap<>();
        this.policyFactory = policyFactory;
        this.policyConfigurationFactory = policyConfigurationFactory;
        this.policyLoader = new PolicyLoader(classLoader, policyPluginManager, policyClassLoaderFactory, componentProvider);
    }

    @Override
    protected void doStart() throws Exception {
        // Load policies
        manifests.putAll(policyLoader.load(dependencies()));

        // Activate policy context
        policyLoader.activatePolicyContext(manifests);
    }

    @Override
    protected void doStop() throws Exception {
        // Deactivate policy context
        policyLoader.disablePolicyContext(manifests, policyFactory::cleanup);

        // Be sure to remove all references to policies.
        manifests.clear();

        // This action aims to avoid memory leak by making sure that no Gravitee ClassLoader is still referenced by Jackson TypeFactory.
        TypeFactory.defaultInstance().clearCache();
    }

    @Override
    public Policy create(final ExecutionPhase executionPhase, final PolicyMetadata policyMetadata) {
        PolicyManifest manifest = manifests.get(policyMetadata.getName());
        if (manifest != null) {
            PolicyConfiguration policyConfiguration = policyConfigurationFactory.create(
                manifest.configuration(),
                policyMetadata.getConfiguration()
            );

            return policyFactory.create(executionPhase, manifest, policyConfiguration, policyMetadata);
        }
        return null;
    }

    protected abstract Set<io.gravitee.definition.model.Policy> dependencies();
}
