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
package io.gravitee.gateway.security.core;

import io.gravitee.gateway.api.ExecutionContext;
import io.gravitee.gateway.policy.AbstractPolicyResolver;
import io.gravitee.gateway.policy.Policy;
import io.gravitee.gateway.policy.PolicyManager;
import io.gravitee.gateway.policy.PolicyMetadata;
import io.gravitee.gateway.policy.StreamType;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class SecurityPolicyResolver extends AbstractPolicyResolver {

    protected static final String SECURITY_POLICY_STAGE = "SECURITY";
    private final AuthenticationHandlerSelector authenticationHandlerSelector;

    public SecurityPolicyResolver(final PolicyManager policyManager, final AuthenticationHandlerSelector authenticationHandlerSelector) {
        super(policyManager);
        this.authenticationHandlerSelector = authenticationHandlerSelector;
    }

    @Override
    public List<Policy> resolve(StreamType streamType, ExecutionContext context) {
        final AuthenticationHandler authenticationHandler = authenticationHandlerSelector.select(context.request());

        if (authenticationHandler == null) {
            // No authentication method selected, must send a 401
            logger.debug(
                "No authentication handler has been selected to process request {}. Returning an unauthorized status (401)",
                context.request().id()
            );

            // TODO: it's probably better to throw an exception ?
            return null;
        }

        logger.debug(
            "Authentication handler [{}] has been selected to secure incoming request {}",
            authenticationHandler.name(),
            context.request().id()
        );

        List<AuthenticationPolicy> policies = authenticationHandler.handle(context);
        return createAuthenticationChain(policies);
    }

    private List<Policy> createAuthenticationChain(List<AuthenticationPolicy> securityPolicies) {
        return securityPolicies
            .stream()
            .map(
                new Function<AuthenticationPolicy, Policy>() {
                    @Override
                    public Policy apply(AuthenticationPolicy securityPolicy) {
                        if (securityPolicy instanceof HookAuthenticationPolicy) {
                            try {
                                return createHookAuthenticationPolicy((HookAuthenticationPolicy) securityPolicy);
                            } catch (Exception ex) {
                                logger.error("Unexpected error while loading authentication policy", ex);
                            }
                        } else if (securityPolicy instanceof PluginAuthenticationPolicy) {
                            final PolicyMetadata policyMetadata = new PolicyMetadata(
                                ((PluginAuthenticationPolicy) securityPolicy).name(),
                                ((PluginAuthenticationPolicy) securityPolicy).configuration()
                            );
                            policyMetadata.metadata().put(PolicyMetadata.MetadataKeys.STAGE, SECURITY_POLICY_STAGE);
                            return create(StreamType.ON_REQUEST, policyMetadata);
                        }

                        return null;
                    }
                }
            )
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    protected Policy createHookAuthenticationPolicy(HookAuthenticationPolicy securityPolicy)
        throws InstantiationException, IllegalAccessException {
        return (Policy) securityPolicy.clazz().newInstance();
    }
}
