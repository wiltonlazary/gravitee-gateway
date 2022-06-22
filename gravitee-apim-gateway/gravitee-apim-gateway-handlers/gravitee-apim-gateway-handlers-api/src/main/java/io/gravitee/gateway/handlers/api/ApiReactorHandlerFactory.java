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
package io.gravitee.gateway.handlers.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.gravitee.definition.model.ExecutionMode;
import io.gravitee.el.TemplateVariableProvider;
import io.gravitee.gateway.api.Invoker;
import io.gravitee.gateway.api.endpoint.resolver.EndpointResolver;
import io.gravitee.gateway.connector.ConnectorRegistry;
import io.gravitee.gateway.core.classloader.DefaultClassLoader;
import io.gravitee.gateway.core.component.ComponentProvider;
import io.gravitee.gateway.core.component.CompositeComponentProvider;
import io.gravitee.gateway.core.component.CustomComponentProvider;
import io.gravitee.gateway.core.endpoint.GroupManager;
import io.gravitee.gateway.core.endpoint.factory.EndpointFactory;
import io.gravitee.gateway.core.endpoint.factory.impl.EndpointFactoryImpl;
import io.gravitee.gateway.core.endpoint.lifecycle.GroupLifecycleManager;
import io.gravitee.gateway.core.endpoint.lifecycle.impl.DefaultGroupLifecycleManager;
import io.gravitee.gateway.core.endpoint.ref.ReferenceRegister;
import io.gravitee.gateway.core.endpoint.ref.impl.DefaultReferenceRegister;
import io.gravitee.gateway.core.endpoint.resolver.ProxyEndpointResolver;
import io.gravitee.gateway.core.invoker.InvokerFactory;
import io.gravitee.gateway.env.GatewayConfiguration;
import io.gravitee.gateway.flow.FlowPolicyResolverFactory;
import io.gravitee.gateway.flow.policy.PolicyChainFactory;
import io.gravitee.gateway.handlers.api.context.ApiTemplateVariableProvider;
import io.gravitee.gateway.handlers.api.context.ContentTemplateVariableProvider;
import io.gravitee.gateway.handlers.api.definition.Api;
import io.gravitee.gateway.handlers.api.policy.security.PlanBasedAuthenticationHandlerEnhancer;
import io.gravitee.gateway.handlers.api.processor.OnErrorProcessorChainFactory;
import io.gravitee.gateway.handlers.api.processor.RequestProcessorChainFactory;
import io.gravitee.gateway.handlers.api.processor.ResponseProcessorChainFactory;
import io.gravitee.gateway.jupiter.handlers.api.SyncApiReactor;
import io.gravitee.gateway.jupiter.handlers.api.adapter.invoker.InvokerAdapter;
import io.gravitee.gateway.jupiter.handlers.api.flow.FlowChainFactory;
import io.gravitee.gateway.jupiter.handlers.api.flow.resolver.FlowResolverFactory;
import io.gravitee.gateway.jupiter.handlers.api.processor.ApiProcessorChainFactory;
import io.gravitee.gateway.jupiter.policy.DefaultPolicyChainFactory;
import io.gravitee.gateway.platform.manager.OrganizationManager;
import io.gravitee.gateway.policy.PolicyChainProviderLoader;
import io.gravitee.gateway.policy.PolicyConfigurationFactory;
import io.gravitee.gateway.policy.PolicyFactory;
import io.gravitee.gateway.policy.PolicyManager;
import io.gravitee.gateway.policy.impl.CachedPolicyConfigurationFactory;
import io.gravitee.gateway.reactor.handler.ReactorHandler;
import io.gravitee.gateway.reactor.handler.ReactorHandlerFactory;
import io.gravitee.gateway.reactor.handler.context.ApiTemplateVariableProviderFactory;
import io.gravitee.gateway.resource.ResourceConfigurationFactory;
import io.gravitee.gateway.resource.ResourceLifecycleManager;
import io.gravitee.gateway.resource.internal.ResourceConfigurationFactoryImpl;
import io.gravitee.gateway.resource.internal.ResourceManagerImpl;
import io.gravitee.gateway.security.core.*;
import io.gravitee.node.api.Node;
import io.gravitee.node.api.configuration.Configuration;
import io.gravitee.plugin.core.api.ConfigurablePluginManager;
import io.gravitee.plugin.policy.PolicyClassLoaderFactory;
import io.gravitee.plugin.policy.PolicyPlugin;
import io.gravitee.plugin.resource.ResourceClassLoaderFactory;
import io.gravitee.plugin.resource.ResourcePlugin;
import io.gravitee.resource.api.ResourceManager;
import io.vertx.core.Vertx;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

/**
 * @author David BRASSELY (david.brassely at graviteesource.com)
 * @author GraviteeSource Team
 */
public class ApiReactorHandlerFactory implements ReactorHandlerFactory<Api> {

    public static final String CLASSLOADER_LEGACY_ENABLED_PROPERTY = "classloader.legacy.enabled";
    public static final String REPORTERS_LOGGING_MAX_SIZE_PROPERTY = "reporters.logging.max_size";
    public static final String HANDLERS_REQUEST_HEADERS_X_FORWARDED_PREFIX_PROPERTY = "handlers.request.headers.x-forwarded-prefix";
    public static final String REPORTERS_LOGGING_EXCLUDED_RESPONSE_TYPES_PROPERTY = "reporters.logging.excluded_response_types";
    private static final String PENDING_REQUESTS_TIMEOUT_PROPERTY = "api.pending_requests_timeout";
    public static final String API_JUPITER_MODE_ENABLED = "api.jupiterMode.enabled";
    private final Logger logger = LoggerFactory.getLogger(ApiReactorHandlerFactory.class);
    private final Configuration configuration;
    private final Node node;
    private final io.gravitee.gateway.policy.PolicyFactoryCreator v3PolicyFactoryCreator;
    private final io.gravitee.gateway.jupiter.policy.PolicyFactory policyFactory;
    private final PolicyChainProviderLoader policyChainProviderLoader;
    private final ApiProcessorChainFactory apiProcessorChainFactory;
    private final FlowResolverFactory flowResolverFactory;
    private ApplicationContext applicationContext;
    protected final ContentTemplateVariableProvider contentTemplateVariableProvider;

    public ApiReactorHandlerFactory(
        ApplicationContext applicationContext,
        Configuration configuration,
        Node node,
        io.gravitee.gateway.policy.PolicyFactoryCreator v3PolicyFactoryCreator,
        io.gravitee.gateway.jupiter.policy.PolicyFactory policyFactory,
        PolicyChainProviderLoader policyChainProviderLoader,
        ApiProcessorChainFactory apiProcessorChainFactory,
        FlowResolverFactory flowResolverFactory
    ) {
        this.applicationContext = applicationContext;
        this.configuration = configuration;
        this.node = node;
        this.v3PolicyFactoryCreator = v3PolicyFactoryCreator;
        this.policyFactory = policyFactory;
        this.policyChainProviderLoader = policyChainProviderLoader;
        this.apiProcessorChainFactory = apiProcessorChainFactory;
        this.flowResolverFactory = flowResolverFactory;
        this.contentTemplateVariableProvider = new ContentTemplateVariableProvider();
    }

    @Override
    public ReactorHandler create(Api api) {
        try {
            if (api.isEnabled()) {
                final ComponentProvider globalComponentProvider = applicationContext.getBean(ComponentProvider.class);
                final CustomComponentProvider customComponentProvider = new CustomComponentProvider();

                final ResourceLifecycleManager resourceLifecycleManager = resourceLifecycleManager(
                    api,
                    applicationContext.getBean(ResourceClassLoaderFactory.class),
                    resourceConfigurationFactory(),
                    applicationContext
                );

                customComponentProvider.add(ResourceManager.class, resourceLifecycleManager);
                customComponentProvider.add(io.gravitee.definition.model.Api.class, api);

                final CompositeComponentProvider apiComponentProvider = new CompositeComponentProvider(
                    customComponentProvider,
                    globalComponentProvider
                );

                final DefaultReferenceRegister referenceRegister = referenceRegister();
                final GroupLifecycleManager groupLifecycleManager = groupLifecyleManager(
                    api,
                    referenceRegister,
                    new EndpointFactoryImpl(),
                    applicationContext.getBean(GatewayConfiguration.class),
                    applicationContext.getBean(ConnectorRegistry.class),
                    configuration,
                    applicationContext.getBean(ObjectMapper.class)
                );

                final Invoker invoker = invokerFactory(
                    api,
                    applicationContext.getBean(Vertx.class),
                    endpointResolver(referenceRegister, groupLifecycleManager)
                )
                    .create();

                if (isV3ExecutionMode(api)) {
                    // Force creation of a dedicated PolicyFactory for each api as it may involve cache we want to be released when api is undeployed.
                    final PolicyFactory v3policyFactory = v3PolicyFactoryCreator.create();

                    final PolicyManager v3PolicyManager = v3PolicyManager(
                        api,
                        v3policyFactory,
                        policyConfigurationFactory(),
                        applicationContext.getBean(PolicyClassLoaderFactory.class),
                        resourceLifecycleManager,
                        apiComponentProvider
                    );

                    final ApiReactorHandler v3ApiReactor = getApiReactorHandler(api);
                    final FlowPolicyResolverFactory v3FlowPolicyResolverFactory = new FlowPolicyResolverFactory();
                    final PolicyChainFactory policyChainFactory = policyChainFactory(v3PolicyManager);
                    v3ApiReactor.setNode(node);
                    v3ApiReactor.setPendingRequestsTimeout(
                        configuration.getProperty(PENDING_REQUESTS_TIMEOUT_PROPERTY, Long.class, 10_000L)
                    );

                    final RequestProcessorChainFactory requestProcessorChainFactory = requestProcessorChainFactory(
                        api,
                        policyChainFactory,
                        v3PolicyManager,
                        policyChainProviderLoader,
                        authenticationHandlerSelector(
                            authenticationHandlerManager(securityProviderLoader(), authenticationHandlerEnhancer(api), apiComponentProvider)
                        ),
                        v3FlowPolicyResolverFactory
                    );

                    v3ApiReactor.setRequestProcessorChain(requestProcessorChainFactory);
                    v3ApiReactor.setResponseProcessorChain(
                        responseProcessorChainFactory(api, policyChainFactory, policyChainProviderLoader, v3FlowPolicyResolverFactory)
                    );
                    v3ApiReactor.setErrorProcessorChain(errorProcessorChainFactory(api, policyChainFactory));

                    v3ApiReactor.setInvoker(invoker);

                    v3ApiReactor.setPolicyManager(v3PolicyManager);
                    v3ApiReactor.setGroupLifecycleManager(groupLifecycleManager);
                    v3ApiReactor.setResourceLifecycleManager(resourceLifecycleManager);

                    v3ApiReactor.setExecutionContextFactory(v3ExecutionContextFactory(api, apiComponentProvider, referenceRegister));
                    return v3ApiReactor;
                } else {
                    final io.gravitee.gateway.jupiter.policy.PolicyManager policyManager = policyManager(
                        api,
                        policyFactory,
                        policyConfigurationFactory(),
                        applicationContext.getBean(PolicyClassLoaderFactory.class),
                        apiComponentProvider
                    );

                    final io.gravitee.gateway.jupiter.policy.PolicyChainFactory platformPolicyChainFactory = applicationContext.getBean(
                        "platformPolicyChainFactory",
                        io.gravitee.gateway.jupiter.policy.PolicyChainFactory.class
                    );

                    final io.gravitee.gateway.jupiter.policy.PolicyChainFactory apiPolicyChainFactory = new DefaultPolicyChainFactory(
                        api.getId(),
                        configuration,
                        policyManager
                    );

                    final OrganizationManager organizationManager = applicationContext.getBean(OrganizationManager.class);

                    FlowChainFactory flowChainFactory = new FlowChainFactory(
                        platformPolicyChainFactory,
                        apiPolicyChainFactory,
                        organizationManager,
                        configuration,
                        flowResolverFactory
                    );

                    return new SyncApiReactor(
                        api,
                        apiComponentProvider,
                        templateVariableProviders(api, referenceRegister),
                        new InvokerAdapter(invoker),
                        resourceLifecycleManager,
                        apiProcessorChainFactory,
                        policyManager,
                        flowChainFactory,
                        groupLifecycleManager,
                        configuration
                    );
                }
            } else {
                logger.warn("Api is disabled !");
            }
        } catch (Exception ex) {
            logger.error("Unexpected error while creating API handler", ex);
        }

        return null;
    }

    private boolean isV3ExecutionMode(Api api) {
        return (
            !configuration.getProperty(API_JUPITER_MODE_ENABLED, Boolean.class, false) ||
            api.getExecutionMode() == null ||
            api.getExecutionMode() == ExecutionMode.V3
        );
    }

    protected io.gravitee.gateway.reactor.handler.context.ExecutionContextFactory v3ExecutionContextFactory(
        Api api,
        ComponentProvider componentProvider,
        DefaultReferenceRegister referenceRegister
    ) {
        final io.gravitee.gateway.reactor.handler.context.ExecutionContextFactory executionContextFactory = new io.gravitee.gateway.reactor.handler.context.ExecutionContextFactory(
            componentProvider
        );

        executionContextFactory.addTemplateVariableProvider(new ApiTemplateVariableProvider(api));
        executionContextFactory.addTemplateVariableProvider(referenceRegister);
        applicationContext
            .getBean(ApiTemplateVariableProviderFactory.class)
            .getTemplateVariableProviders()
            .forEach(executionContextFactory::addTemplateVariableProvider);

        return executionContextFactory;
    }

    private List<TemplateVariableProvider> templateVariableProviders(Api api, DefaultReferenceRegister referenceRegister) {
        List<TemplateVariableProvider> templateVariableProviders = new ArrayList<>();
        templateVariableProviders.add(new ApiTemplateVariableProvider(api));
        templateVariableProviders.add(contentTemplateVariableProvider);
        templateVariableProviders.add(referenceRegister);
        templateVariableProviders.addAll(
            applicationContext.getBean(ApiTemplateVariableProviderFactory.class).getTemplateVariableProviders()
        );

        return templateVariableProviders;
    }

    protected ApiReactorHandler getApiReactorHandler(Api api) {
        return new ApiReactorHandler(api);
    }

    public PolicyChainFactory policyChainFactory(PolicyManager policyManager) {
        return new PolicyChainFactory(policyManager);
    }

    public PolicyManager v3PolicyManager(
        Api api,
        PolicyFactory factory,
        PolicyConfigurationFactory policyConfigurationFactory,
        PolicyClassLoaderFactory policyClassLoaderFactory,
        ResourceLifecycleManager resourceLifecycleManager,
        ComponentProvider componentProvider
    ) {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(
            ResolvableType.forClassWithGenerics(ConfigurablePluginManager.class, PolicyPlugin.class)
        );

        ConfigurablePluginManager<PolicyPlugin<?>> ppm = (ConfigurablePluginManager<PolicyPlugin<?>>) applicationContext.getBean(
            beanNamesForType[0]
        );

        return new ApiPolicyManager(
            configuration.getProperty(CLASSLOADER_LEGACY_ENABLED_PROPERTY, Boolean.class, false),
            applicationContext.getBean(DefaultClassLoader.class),
            api,
            factory,
            policyConfigurationFactory,
            ppm,
            policyClassLoaderFactory,
            resourceLifecycleManager,
            componentProvider
        );
    }

    public io.gravitee.gateway.jupiter.policy.PolicyManager policyManager(
        Api api,
        io.gravitee.gateway.jupiter.policy.PolicyFactory factory,
        PolicyConfigurationFactory policyConfigurationFactory,
        PolicyClassLoaderFactory policyClassLoaderFactory,
        ComponentProvider componentProvider
    ) {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(
            ResolvableType.forClassWithGenerics(ConfigurablePluginManager.class, PolicyPlugin.class)
        );

        ConfigurablePluginManager<PolicyPlugin<?>> ppm = (ConfigurablePluginManager<PolicyPlugin<?>>) applicationContext.getBean(
            beanNamesForType[0]
        );

        return new io.gravitee.gateway.jupiter.handlers.api.ApiPolicyManager(
            applicationContext.getBean(DefaultClassLoader.class),
            api,
            factory,
            policyConfigurationFactory,
            ppm,
            policyClassLoaderFactory,
            componentProvider
        );
    }

    public PolicyConfigurationFactory policyConfigurationFactory() {
        return new CachedPolicyConfigurationFactory();
    }

    public ResourceLifecycleManager resourceLifecycleManager(
        Api api,
        ResourceClassLoaderFactory resourceClassLoaderFactory,
        ResourceConfigurationFactory resourceConfigurationFactory,
        ApplicationContext applicationContext
    ) {
        String[] beanNamesForType = applicationContext.getBeanNamesForType(
            ResolvableType.forClassWithGenerics(ConfigurablePluginManager.class, ResourcePlugin.class)
        );

        ConfigurablePluginManager<ResourcePlugin<?>> cpm = (ConfigurablePluginManager<ResourcePlugin<?>>) applicationContext.getBean(
            beanNamesForType[0]
        );

        return new ResourceManagerImpl(
            configuration.getProperty(CLASSLOADER_LEGACY_ENABLED_PROPERTY, Boolean.class, false),
            applicationContext.getBean(DefaultClassLoader.class),
            api,
            cpm,
            resourceClassLoaderFactory,
            resourceConfigurationFactory,
            applicationContext
        );
    }

    public ResourceConfigurationFactory resourceConfigurationFactory() {
        return new ResourceConfigurationFactoryImpl();
    }

    public SecurityProviderLoader securityProviderLoader() {
        return new SecurityProviderLoader();
    }

    public AuthenticationHandlerManager authenticationHandlerManager(
        SecurityProviderLoader securityProviderLoader,
        AuthenticationHandlerEnhancer authenticationHandlerEnhancer,
        ComponentProvider componentProvider
    ) {
        AuthenticationHandlerManager authenticationHandlerManager = new AuthenticationHandlerManager(
            securityProviderLoader,
            componentProvider
        );
        authenticationHandlerManager.setAuthenticationHandlerEnhancer(authenticationHandlerEnhancer);
        authenticationHandlerManager.afterPropertiesSet();
        return authenticationHandlerManager;
    }

    public AuthenticationHandlerEnhancer authenticationHandlerEnhancer(Api api) {
        return new PlanBasedAuthenticationHandlerEnhancer(api);
    }

    public AuthenticationHandlerSelector authenticationHandlerSelector(AuthenticationHandlerManager authenticationHandlerManager) {
        return new DefaultAuthenticationHandlerSelector(authenticationHandlerManager);
    }

    public InvokerFactory invokerFactory(Api api, Vertx vertx, EndpointResolver endpointResolver) {
        return new InvokerFactory(api, vertx, endpointResolver);
    }

    public DefaultReferenceRegister referenceRegister() {
        return new DefaultReferenceRegister();
    }

    public GroupLifecycleManager groupLifecyleManager(
        Api api,
        ReferenceRegister referenceRegister,
        EndpointFactory endpointFactory,
        GatewayConfiguration gatewayConfiguration,
        ConnectorRegistry connectorRegistry,
        Configuration configuration,
        ObjectMapper mapper
    ) {
        return new DefaultGroupLifecycleManager(
            api,
            referenceRegister,
            endpointFactory,
            connectorRegistry,
            configuration,
            mapper,
            gatewayConfiguration.tenant()
        );
    }

    public EndpointResolver endpointResolver(ReferenceRegister referenceRegister, GroupManager groupManager) {
        return new ProxyEndpointResolver(referenceRegister, groupManager);
    }

    public RequestProcessorChainFactory requestProcessorChainFactory(
        Api api,
        PolicyChainFactory policyChainFactory,
        PolicyManager policyManager,
        PolicyChainProviderLoader policyChainProviderLoader,
        AuthenticationHandlerSelector authenticationHandlerSelector,
        FlowPolicyResolverFactory flowPolicyResolverFactory
    ) {
        RequestProcessorChainFactory.RequestProcessorChainFactoryOptions options = new RequestProcessorChainFactory.RequestProcessorChainFactoryOptions();
        options.setMaxSizeLogMessage(configuration.getProperty(REPORTERS_LOGGING_MAX_SIZE_PROPERTY, String.class, null));
        options.setOverrideXForwardedPrefix(
            configuration.getProperty(HANDLERS_REQUEST_HEADERS_X_FORWARDED_PREFIX_PROPERTY, Boolean.class, false)
        );
        options.setExcludedResponseTypes(configuration.getProperty(REPORTERS_LOGGING_EXCLUDED_RESPONSE_TYPES_PROPERTY, String.class, null));

        return getRequestProcessorChainFactory(
            api,
            policyChainFactory,
            policyManager,
            policyChainProviderLoader,
            authenticationHandlerSelector,
            flowPolicyResolverFactory,
            options,
            new SecurityPolicyResolver(policyManager, authenticationHandlerSelector)
        );
    }

    protected RequestProcessorChainFactory getRequestProcessorChainFactory(
        Api api,
        PolicyChainFactory policyChainFactory,
        PolicyManager policyManager,
        PolicyChainProviderLoader policyChainProviderLoader,
        AuthenticationHandlerSelector authenticationHandlerSelector,
        FlowPolicyResolverFactory flowPolicyResolverFactory,
        RequestProcessorChainFactory.RequestProcessorChainFactoryOptions options,
        SecurityPolicyResolver securityPolicyResolver
    ) {
        return new RequestProcessorChainFactory(
            api,
            policyChainFactory,
            policyManager,
            options,
            policyChainProviderLoader,
            authenticationHandlerSelector,
            flowPolicyResolverFactory,
            securityPolicyResolver
        );
    }

    public ResponseProcessorChainFactory responseProcessorChainFactory(
        Api api,
        PolicyChainFactory policyChainFactory,
        PolicyChainProviderLoader policyChainProviderLoader,
        FlowPolicyResolverFactory flowPolicyResolverFactory
    ) {
        return new ResponseProcessorChainFactory(api, policyChainFactory, policyChainProviderLoader, node, flowPolicyResolverFactory);
    }

    public OnErrorProcessorChainFactory errorProcessorChainFactory(Api api, PolicyChainFactory policyChainFactory) {
        return new OnErrorProcessorChainFactory(api, policyChainFactory);
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
